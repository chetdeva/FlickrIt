package com.chetdeva.flickrit.network

import android.graphics.Bitmap
import com.chetdeva.flickrit.util.image.ImageDownloadTask

/**
 * manages [Bitmap] images
 *
 * @author chetansachdeva
 */

class ImageDownloader(
        private val apiClient: ApiClient
) {
    /**
     * download image from [ApiClient]
     * publish callback as a [Bitmap]
     */
    fun downloadImage(url: String, onDownloadComplete: (Bitmap?) -> Unit) {
        ImageDownloadTask(apiClient, onDownloadComplete).execute(url)
    }
}