package com.pclash

import android.content.pm.ApplicationInfo
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.pclash.adapter.PackagesAdapter
import com.pclash.databinding.ActivityAccessControlPackagesBinding
import com.pclash.service.settings.ServiceSettings
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlin.streams.toList

class PackagesActivity : BaseActivity() {
    private lateinit var binding: ActivityAccessControlPackagesBinding
    private val activity: PackagesActivity
        get() = this
    private val adapter: PackagesAdapter?
        get() = binding.mainList.adapter as PackagesAdapter?
    private val refreshChannel = Channel<Unit>(Channel.CONFLATED)

    private var keyword: String = ""
    private var sort: PackagesAdapter.Sort = PackagesAdapter.Sort.NAME
    private var decrease: Boolean = false
    private var systemApp: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAccessControlPackagesBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        launch {
            val appsDeferred = async {
                withContext(Dispatchers.IO) {
                    val pm = packageManager
                    val packages = pm.getInstalledPackages(0)
                    packages.parallelStream()
                        .map {
                            PackagesAdapter.AppInfo(
                                it.packageName,
                                it.applicationInfo.loadLabel(pm).toString(),
                                it.applicationInfo.loadIcon(pm),
                                it.firstInstallTime, it.lastUpdateTime,
                                it.applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM != 0
                            )
                        }
                        .toList()
                }
            }

            val selectedDeferred = async {
                withContext(Dispatchers.IO) {
                    ServiceSettings(activity).get(ServiceSettings.ACCESS_CONTROL_PACKAGES)
                }
            }

            val apps = appsDeferred.await()
            val selected = selectedDeferred.await()

            val adapter = PackagesAdapter(activity, apps)
            binding.mainList.adapter = adapter
            binding.mainList.layoutManager = LinearLayoutManager(activity)

            adapter.selectedPackages.addAll(selected)
            binding.progress.visibility = View.GONE

            refreshChannel.trySend(Unit)

            while (isActive) {
                refreshChannel.receive()
                adapter.applyFilter(keyword, sort, decrease, systemApp)
                binding.mainList.scrollToPosition(0)
                delay(200)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        if (!super.onCreateOptionsMenu(menu))
            return false

        menuInflater.inflate(R.menu.packages, menu)
        menu?.apply {
            (findItem(R.id.search).actionView as SearchView).apply {
                setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                    override fun onQueryTextSubmit(query: String?) = false
                    override fun onQueryTextChange(newText: String?): Boolean {
                        keyword = newText ?: ""
                        refreshChannel.trySend(Unit)
                        return true
                    }
                })
            }
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (super.onOptionsItemSelected(item))
            return true

        when (item.itemId) {
            R.id.systemApps -> { item.isChecked = !item.isChecked; systemApp = item.isChecked }
            R.id.sortReverse -> { item.isChecked = !item.isChecked; decrease = item.isChecked }
            R.id.sortName -> { item.isChecked = true; sort = PackagesAdapter.Sort.NAME }
            R.id.sortPackageName -> { item.isChecked = true; sort = PackagesAdapter.Sort.PACKAGE }
            R.id.sortUpdateTime -> { item.isChecked = true; sort = PackagesAdapter.Sort.UPDATE_TIME }
            R.id.sortInstallTime -> { item.isChecked = true; sort = PackagesAdapter.Sort.INSTALL_TIME }
            else -> return false
        }
        refreshChannel.trySend(Unit)
        return true
    }

    override fun onStop() {
        super.onStop()
        val packageList = adapter?.selectedPackages ?: return
        GlobalScope.launch {
            withContext(Dispatchers.IO) {
                ServiceSettings(activity).commit {
                    put(ServiceSettings.ACCESS_CONTROL_PACKAGES, packageList)
                }
            }
        }
    }
}
