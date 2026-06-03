package com.pclash

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.pclash.common.utils.intent
import com.pclash.common.utils.asBytesString
import com.pclash.common.utils.asSpeedString
import com.pclash.core.model.General
import com.pclash.databinding.ActivityMainBinding
import com.pclash.remote.withClash
import com.pclash.remote.withProfile
import com.pclash.service.util.startClashService
import com.pclash.service.util.stopClashService
import kotlinx.coroutines.*

class MainActivity : BaseActivity() {
    companion object {
        private const val REQUEST_CODE = 40000
    }

    private lateinit var binding: ActivityMainBinding
    private var bandwidthJob: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.status.setOnClickListener {
            if (clashRunning) {
                stopClashService()
            } else {
                val vpnRequest = startClashService()
                if (vpnRequest != null) {
                    val resolved = packageManager.resolveActivity(vpnRequest, 0)
                    if (resolved != null) {
                        startActivityForResult(vpnRequest, REQUEST_CODE)
                    } else {
                        showSnackbarException(getString(R.string.missing_vpn_component), null)
                    }
                }
            }
        }

        binding.proxies.setOnClickListener {
            startActivity(ProxiesActivity::class.intent)
        }

        binding.profiles.setOnClickListener {
            startActivity(ProfilesActivity::class.intent)
        }

        binding.logs.setOnClickListener {
            startActivity(LogsActivity::class.intent)
        }

        binding.settings.setOnClickListener {
            startActivity(SettingsActivity::class.intent)
        }

        binding.support.setOnClickListener {
            startActivity(SupportActivity::class.intent)
        }

        binding.about.setOnClickListener {
            showAboutDialog()
        }

        setupMenuItems()
    }

    private fun setupMenuItems() {
        binding.logs.apply {
            findViewById<View>(android.R.id.icon).setBackgroundResource(R.drawable.ic_logs)
            findViewById<TextView>(android.R.id.title).setText(R.string.logs)
        }
        binding.settings.apply {
            findViewById<View>(android.R.id.icon).setBackgroundResource(R.drawable.ic_settings)
            findViewById<TextView>(android.R.id.title).setText(R.string.settings)
        }
        binding.support.apply {
            findViewById<View>(android.R.id.icon).setBackgroundResource(R.drawable.ic_feedback)
            findViewById<TextView>(android.R.id.title).setText(R.string.support)
        }
        binding.about.apply {
            findViewById<View>(android.R.id.icon).setBackgroundResource(R.drawable.ic_about)
            findViewById<TextView>(android.R.id.title).setText(R.string.about)
        }
    }

    override fun onStart() {
        super.onStart()
        updateClashStatus()
    }

    override fun onStop() {
        super.onStop()
        stopBandwidthPolling()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK)
                startClashService()
            return
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override suspend fun onClashStarted() {
        updateClashStatus()
    }

    override suspend fun onClashStopped(reason: String?) {
        updateClashStatus()
        if (reason != null)
            showSnackbarException(getString(R.string.clash_start_failure), reason)
    }

    override suspend fun onClashProfileLoaded() {
        updateClashStatus()
    }

    private fun startBandwidthPolling() {
        if (bandwidthJob != null)
            return

        bandwidthJob = launch {
            withClash {
                try {
                    while (clashRunning && isActive) {
                        val traffic = querySpeed()
                        binding.downloadSpeed.text = traffic.download.asSpeedString()
                        binding.uploadSpeed.text = traffic.upload.asSpeedString()
                        delay(1000)
                    }
                } finally {
                    bandwidthJob = null
                }
            }
        }
    }

    private fun stopBandwidthPolling() {
        bandwidthJob?.cancel()
    }

    private fun updateClashStatus() {
        if (clashRunning) {
            startBandwidthPolling()

            binding.status.setCardBackgroundColor(getColor(R.color.primaryCardColorStarted))
            binding.status.icon = getDrawable(R.drawable.ic_started)
            binding.status.title = getText(R.string.running)

            binding.proxies.visibility = View.VISIBLE
        } else {
            stopBandwidthPolling()

            binding.downloadSpeed.text = "0.00 B/s"
            binding.uploadSpeed.text = "0.00 B/s"

            binding.status.setCardBackgroundColor(getColor(R.color.primaryCardColorStopped))
            binding.status.icon = getDrawable(R.drawable.ic_stopped)
            binding.status.title = getText(R.string.stopped)
            binding.status.summary = getText(R.string.tap_to_start)

            binding.proxies.visibility = View.GONE
        }

        launch {
            val general = withClash {
                queryGeneral()
            }
            val active = withProfile {
                queryActive()
            }

            val modeResId = when (general.mode) {
                General.Mode.DIRECT -> R.string.direct_mode
                General.Mode.GLOBAL -> R.string.global_mode
                General.Mode.RULE -> R.string.rule_mode
            }

            val profileString =
                if (active == null)
                    getText(R.string.not_selected)
                else
                    getString(R.string.format_profile_activated, active.name)

            binding.proxies.summary = getText(modeResId)
            binding.profiles.summary = profileString
        }
    }

    private fun showAboutDialog() {
        launch {
            val content = withContext(Dispatchers.Default) {
                val packageInfo = packageManager.getPackageInfo(packageName, 0)

                LayoutInflater.from(this@MainActivity)
                    .inflate(R.layout.dialog_abort, binding.root as ViewGroup?, false).apply {
                        findViewById<View>(android.R.id.icon).background =
                            getDrawable(R.drawable.ic_logo)
                        findViewById<TextView>(android.R.id.title).text =
                            getText(R.string.application_name)
                        findViewById<TextView>(android.R.id.summary).text = packageInfo.versionName
                    }
            }

            AlertDialog.Builder(this@MainActivity)
                .setView(content)
                .show()
        }
    }
}
