package com.chetdeva.flickrit.util.image

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import com.chetdeva.flickrit.network.ApiClient
import okhttp3.Response
import java.io.IOException

/**
 * downloads image from [ApiClient]
 *
 * @author chetansachdeva
 */

class ImageDownloadTask(
        private val apiClient: ApiClient,
        private val onDownloadComplete: (Bitmap?) -> Unit
) : AsyncTask<String, Unit, Bitmap?>() {

    override fun doInBackground(vararg params: String): Bitmap? {
        val request = ApiClient.buildRequest(params.first())

        return try {
            val response = apiClient.syncRequest(request)
            decodeBitmap(response)
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

    private fun decodeBitmap(response: Response?): Bitmap? {
        return response?.body()?.let {
            BitmapFactory.decodeStream(it.byteStream())
        }
    }

    override fun onPostExecute(result: Bitmap?) {
        super.onPostExecute(result)
        onDownloadComplete(result)
    }
}