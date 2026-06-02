package com.pclash

import android.app.Application
import android.content.Context
import android.content.Intent
import android.net.Uri
import com.pclash.common.Global
import com.pclash.common.utils.componentName
import com.pclash.remote.Broadcasts
import com.pclash.remote.Remote

@Suppress("unused")
class MainApplication : Application() {
    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        Global.init(this)
    }

    override fun onCreate() {
        super.onCreate()

        Global.openMainIntent = {
            Intent(Intent.ACTION_MAIN).apply {
                component = MainActivity::class.componentName
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
        }
        Global.openProfileIntent = {
            Intent(Intent.ACTION_MAIN).apply {
                component = ProfileEditActivity::class.componentName
                data = Uri.fromParts("id", it.toString(), null)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
        }

        Remote.init(this)
        Broadcasts.init(this)
    }
}
