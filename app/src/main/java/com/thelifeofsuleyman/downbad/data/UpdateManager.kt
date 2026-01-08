package com.thelifeofsuleyman.downbad.data

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Environment
import com.thelifeofsuleyman.downbad.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URL

sealed class UpdateStatus {
    object UpToDate : UpdateStatus()
    data class Available(val version: String, val url: String) : UpdateStatus()
    data class Error(val message: String) : UpdateStatus()
}

class UpdateManager(private val context: Context) {

    private val repoUrl = "https://api.github.com/repos/thelifeofsuleyman/Downbad/releases/latest"

    suspend fun checkForUpdates(): UpdateStatus = withContext(Dispatchers.IO) {
        try {
            val response = URL(repoUrl).readText()
            val json = JSONObject(response)

            // 1. Extract the version number (e.g., 1.0.5) from the Release Title
            val releaseName = json.getString("name")
            val remoteVersion = Regex("""\d+\.\d+\.\d+""").find(releaseName)?.value ?: "0.0.0"

            // 2. Get your app's local version (e.g., 1.0.0)
            val localVersion = BuildConfig.VERSION_NAME

            // 3. Find the APK download link
            val assets = json.getJSONArray("assets")
            if (assets.length() == 0) return@withContext UpdateStatus.Error("No APK found in release")
            val downloadUrl = assets.getJSONObject(0).getString("browser_download_url")

            // 4. Use the smart comparison logic
            if (isNewer(remoteVersion, localVersion)) {
                UpdateStatus.Available(remoteVersion, downloadUrl)
            } else {
                UpdateStatus.UpToDate
            }
        } catch (e: Exception) {
            UpdateStatus.Error("Check failed: ${e.message}")
        }
    }

    /**
     * Compares two version strings (e.g., "1.0.10" and "1.0.9")
     * Returns true if remote is strictly higher than local.
     */
    private fun isNewer(remote: String, local: String): Boolean {
        if (remote == "0.0.0" || remote == local) return false

        return try {
            val remoteParts = remote.split(".").map { it.toIntOrNull() ?: 0 }
            val localParts = local.split(".").map { it.toIntOrNull() ?: 0 }

            val maxLength = maxOf(remoteParts.size, localParts.size)
            for (i in 0 until maxLength) {
                val r = remoteParts.getOrNull(i) ?: 0
                val l = localParts.getOrNull(i) ?: 0
                if (r > l) return true
                if (r < l) return false
            }
            false
        } catch (e: Exception) {
            false
        }
    }

    fun downloadAndInstall(url: String) {
        val request = DownloadManager.Request(Uri.parse(url))
            .setTitle("Down Bad Update")
            .setDescription("Downloading latest build from GitHub...")
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "downbad_latest.apk")

        val manager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        manager.enqueue(request)
    }
}