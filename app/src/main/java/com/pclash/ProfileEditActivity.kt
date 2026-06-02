package com.pclash

import android.app.Activity
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import com.pclash.databinding.ActivityProfileEditBinding
import com.pclash.fragment.ProfileEditFragment
import com.pclash.remote.withProfile
import com.pclash.service.model.Profile
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class ProfileEditActivity : BaseActivity() {
    private lateinit var binding: ActivityProfileEditBinding
    private var editor: ProfileEditFragment? = null
    private var processing = false
        set(value) {
            field = value
            if (value) {
                binding.saving.visibility = View.VISIBLE
                binding.save.visibility = View.INVISIBLE
            } else {
                binding.saving.visibility = View.INVISIBLE
                binding.save.visibility = View.VISIBLE
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileEditBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        binding.toolbar.setTitle(R.string.loading)

        launch {
            val id = intent.data?.schemeSpecificPart?.toLongOrNull() ?: return@launch finish()

            val metadata = withProfile {
                queryById(id)
            } ?: return@launch finish()

            when {
                metadata.lastModified > 0 ->
                    binding.toolbar.setTitle(R.string.edit_profile)
                metadata.name.isBlank() ->
                    binding.toolbar.setTitle(R.string.new_profile)
                else ->
                    binding.toolbar.setTitle(R.string.clone_profile)
            }

            val fragment = ProfileEditFragment(
                metadata.id,
                metadata.name, metadata.uri, metadata.interval,
                metadata.type, metadata.source
            )

            editor = fragment

            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment, fragment)
                .commit()

            binding.save.setOnClickListener {
                val name = fragment.name
                val uri = fragment.uri
                val interval = fragment.interval

                if (name.isBlank()) {
                    Snackbar.make(binding.rootView, R.string.empty_name, Snackbar.LENGTH_LONG).show()
                    return@setOnClickListener
                }

                val newMetadata = metadata.copy(
                    name = name,
                    uri = uri,
                    interval = interval
                )

                processing = true
                commit(newMetadata)
            }
        }
    }

    override fun onBackPressed() {
        if (processing) {
            Snackbar.make(binding.rootView, R.string.processing, Snackbar.LENGTH_LONG).show()
            return
        }

        if (editor?.isModified != true)
            return finish()

        AlertDialog.Builder(this)
            .setTitle(R.string.exit_without_save)
            .setMessage(R.string.exit_without_save_warning)
            .setNegativeButton(R.string.cancel) { _, _ -> }
            .setPositiveButton(R.string.ok) { _, _ -> finish() }
            .show()
    }

    override fun onDestroy() {
        runBlocking {
            withProfile {
                val id = editor?.id ?: return@withProfile
                release(id)
            }
        }
        super.onDestroy()
    }

    private fun commit(metadata: Profile) {
        launch {
            try {
                withProfile {
                    update(metadata.id, metadata)
                    commitAsync(metadata.id).await()
                }
                setResult(Activity.RESULT_OK)
                finish()
            } catch (e: Exception) {
                showSnackbarException(getString(R.string.download_failure), e.message)
            }
            processing = false
        }
    }
}
