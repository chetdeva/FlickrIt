package com.chetdeva.flickrit.network

import android.graphics.Bitmap
import com.chetdeva.flickrit.util.image.DownloadImageTask

/**
 * manages [Bitmap] images
 *
 * @author chetansachdeva
 */

class ImageService(
        private val apiClient: ApiClient
) {
    /**
     * download image from [ApiClient]
     * publish callback as a [Bitmap]
     */
    fun downloadImage(url: String, onDownloadComplete: (Bitmap?) -> Unit) {
        DownloadImageTask(apiClient, onDownloadComplete).execute(url)
    }
}