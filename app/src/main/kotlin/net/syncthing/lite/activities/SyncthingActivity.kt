package net.syncthing.lite.activities

import android.app.AlertDialog
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import net.syncthing.java.core.beans.FolderInfo
import net.syncthing.lite.BuildConfig
import net.syncthing.lite.R
import net.syncthing.lite.databinding.DialogLoadingBinding
import net.syncthing.lite.library.LibraryHandler
import org.jetbrains.anko.contentView
import org.slf4j.impl.HandroidLoggerAdapter

abstract class SyncthingActivity : AppCompatActivity() {

    var libraryHandler: LibraryHandler? = null
        private set
    private var loadingDialog: AlertDialog? = null
    private var snackBar: Snackbar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        HandroidLoggerAdapter.DEBUG = BuildConfig.DEBUG

        val binding = DataBindingUtil.inflate<DialogLoadingBinding>(
                LayoutInflater.from(this), R.layout.dialog_loading, null, false)
        binding.loadingText.text = getString(R.string.loading_config_starting_syncthing_client)
        loadingDialog = AlertDialog.Builder(this)
                .setCancelable(false)
                .setView(binding.root)
                .show()
        LibraryHandler(this, this::onLibraryLoadedInternal,
                                        this::onIndexUpdateProgress, this::onIndexUpdateComplete)
    }

    override fun onDestroy() {
        super.onDestroy()
        libraryHandler?.close()
        loadingDialog?.dismiss()
    }

    private fun onLibraryLoadedInternal(libraryHandler: LibraryHandler) {
        this.libraryHandler = libraryHandler
        if (!isDestroyed) {
            loadingDialog?.dismiss()
        }
        onLibraryLoaded()
    }

    open fun onIndexUpdateProgress(folderInfo: FolderInfo, percentage: Int) {
        val message = getString(R.string.index_update_progress_label, folderInfo.label, percentage)
        snackBar?.setText(message) ?: run {
            snackBar = Snackbar.make(contentView!!, message, Snackbar.LENGTH_INDEFINITE)
            snackBar?.show()
        }
    }

    open fun onIndexUpdateComplete(folderInfo: FolderInfo) {
        snackBar?.dismiss()
        snackBar = null
    }

    open fun onLibraryLoaded() {}
}
