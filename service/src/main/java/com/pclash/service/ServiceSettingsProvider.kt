package com.pclash.service

import android.content.Context
import android.content.SharedPreferences
import dev.rikka.rikkax.multiprocess.MultiProcessSharedPreferences
import dev.rikka.rikkax.multiprocess.PreferenceProvider

class ServiceSettingsProvider : PreferenceProvider() {
    override fun onCreatePreference(context: Context?): SharedPreferences {
        return context!!.getSharedPreferences(
            Constants.SERVICE_SETTING_FILE_NAME,
            Context.MODE_PRIVATE
        )
    }

    companion object {
        fun createSharedPreferencesFromContext(context: Context): SharedPreferences {
            return when (context) {
                is BaseService, is TunService ->
                    context.getSharedPreferences(
                        Constants.SERVICE_SETTING_FILE_NAME,
                        Context.MODE_PRIVATE
                    )
                else ->
                    MultiProcessSharedPreferences(
                        context,
                        context.packageName + Constants.SETTING_PROVIDER_SUFFIX
                    )
            }
        }
    }
}