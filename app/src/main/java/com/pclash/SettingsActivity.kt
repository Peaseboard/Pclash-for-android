package com.pclash

import android.os.Bundle
import com.pclash.common.utils.intent
import com.pclash.databinding.ActivitySettingsBinding

class SettingsActivity : BaseActivity() {
    private lateinit var binding: ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        binding.commonUi.build {
            option(
                icon = getDrawable(R.drawable.ic_settings_applications),
                title = getString(R.string.behavior)
            ) {
                paddingHeight = true

                onClick {
                    startActivity(SettingsBehaviorActivity::class.intent)
                }
            }
            option(
                icon = getDrawable(R.drawable.ic_network),
                title = getString(R.string.network)
            ) {
                paddingHeight = true

                onClick {
                    startActivity(SettingsNetworkActivity::class.intent)
                }
            }
            option(
                icon = getDrawable(R.drawable.ic_interface),
                title = getString(R.string.interface_)
            ) {
                paddingHeight = true

                onClick {
                    startActivity(SettingsInterfaceActivity::class.intent)
                }
            }
        }
    }
}
