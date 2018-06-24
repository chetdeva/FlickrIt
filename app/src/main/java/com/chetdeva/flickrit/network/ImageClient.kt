package com.chetdeva.flickrit.network

import android.graphics.Bitmap
import com.chetdeva.flickrit.util.image.DownloadImageTask

/**
 * @author chetansachdeva
 */

class ImageClient(
        private val apiClient: ApiClient
) {
    fun downloadImage(url: String, onDownloadComplete: (Bitmap?) -> Unit) {
        DownloadImageTask(apiClient, onDownloadComplete).execute(url)
    }
}