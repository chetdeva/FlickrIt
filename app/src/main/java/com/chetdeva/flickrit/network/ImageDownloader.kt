package com.chetdeva.flickrit.network

import android.graphics.Bitmap
import com.chetdeva.flickrit.util.image.ImageDownloadTask
import com.example.android.architecture.blueprints.todoapp.util.SingletonHolderSingleArg

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

    companion object: SingletonHolderSingleArg<ImageDownloader, ApiClient>(::ImageDownloader)
}