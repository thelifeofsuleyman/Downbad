package com.thelifeofsuleyman.downbad.data

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Environment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URL
import java.text.SimpleDateFormat
import java.util.Locale

// This class defines the different states an update can be in
sealed class UpdateStatus {
    object UpToDate : UpdateStatus()
    data class Available(val version: String, val url: String) : UpdateStatus()
    data class Error(val message: String) : UpdateStatus()
}

class UpdateManager(private val context: Context) {

    // This points to the 'latest' tag created by your GitHub Action
    private val repoUrl = "https://api.github.com/repos/thelifeofsuleyman/Downbad/releases/tags/latest"

    suspend fun checkForUpdates(): UpdateStatus = withContext(Dispatchers.IO) {
        try {
            val response = URL(repoUrl).readText()
            val json = JSONObject(response)

            // Get the date this APK was published on GitHub
            val remoteDateString = json.getString("published_at")
            val downloadUrl = json.getJSONArray("assets").getJSONObject(0).getString("browser_download_url")

            if (isNewer(remoteDateString)) {
                UpdateStatus.Available("Latest Commit", downloadUrl)
            } else {
                UpdateStatus.UpToDate
            }
        } catch (e: Exception) {
            UpdateStatus.Error(e.message ?: "Check Failed")
        }
    }

    private fun isNewer(remoteDate: String): Boolean {
        return try {
            // Parses GitHub date format: 2026-01-08T15:00:00Z
            val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US)
            val remoteTime = sdf.parse(remoteDate)?.time ?: 0

            // Get the time YOUR app was last updated/installed on the phone
            val localTime = context.packageManager.getPackageInfo(context.packageName, 0).lastUpdateTime

            remoteTime > localTime
        } catch (e: Exception) {
            false
        }
    }

    fun downloadAndInstall(url: String) {
        val request = DownloadManager.Request(Uri.parse(url))
            .setTitle("Down Bad Update")
            .setDescription("Downloading latest version from GitHub...")
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "downbad_latest.apk")

        val manager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        manager.enqueue(request)
    }
}