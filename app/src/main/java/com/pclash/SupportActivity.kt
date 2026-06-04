package com.pclash

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Html
import com.pclash.databinding.ActivitySupportBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SupportActivity : BaseActivity() {
    class UserRequestTrackException: Exception()

    private lateinit var binding: ActivitySupportBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySupportBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        binding.commonUi.build {
            tips {
                icon = getDrawable(R.drawable.ic_info)
                title = Html.fromHtml(getString(R.string.tips_support), Html.FROM_HTML_MODE_LEGACY)
            }

            category(text = getString(R.string.sources))

            option(
                title = getString(R.string.clash),
                summary = getString(R.string.clash_url)
            ) {
                onClick {
                    startActivity(
                        Intent(Intent.ACTION_VIEW)
                            .setData(Uri.parse(getString(R.string.clash_url)))
                    )
                }
            }
            option(
                title = getString(R.string.clash_for_android),
                summary = getString(R.string.clash_for_android_url)
            ) {
                onClick {
                    startActivity(
                        Intent(Intent.ACTION_VIEW)
                            .setData(Uri.parse(getString(R.string.clash_for_android_url)))
                    )
                }
            }

            category(text = getString(R.string.feedback))

            option(
                title = getString(R.string.upload_logcat),
                summary = getString(R.string.upload_logcat_summary)
            ) {
                onClick {
                    androidx.appcompat.app.AlertDialog.Builder(this@SupportActivity)
                        .setTitle(R.string.upload_logcat)
                        .setMessage(R.string.upload_logcat_warn)
                        .setNegativeButton(R.string.cancel) {_, _ -> }
                        .setPositiveButton(R.string.ok) {_, _ -> upload() }
                        .show()
                }
            }

            option(
                title = getString(R.string.github_issues),
                summary = getString(R.string.github_issues_url)
            ) {
                onClick {
                    startActivity(
                        Intent(Intent.ACTION_VIEW)
                            .setData(Uri.parse(getString(R.string.github_issues_url)))
                    )
                }
            }

            val firstLanguage = resources.configuration.locales.get(0).language

            if (firstLanguage.equals("zh", true)) {
                category(getString(R.string.donate))

                option(
                    title = getString(R.string.telegram_channel),
                    summary = getString(R.string.telegram_channel_url)
                ) {
                    onClick {
                        startActivity(
                            Intent(Intent.ACTION_VIEW)
                                .setData(Uri.parse(getString(R.string.telegram_channel_url)))
                        )
                    }
                }
            }
        }
    }

    private fun upload() {
        launch {
            withContext(Dispatchers.IO) {
                com.pclash.dump.LogcatDumper.dumpAll()
            }

            withContext(Dispatchers.Main) {
                com.google.android.material.snackbar.Snackbar.make(binding.root, R.string.uploaded, com.google.android.material.snackbar.Snackbar.LENGTH_LONG).show()
            }
        }
    }
}
