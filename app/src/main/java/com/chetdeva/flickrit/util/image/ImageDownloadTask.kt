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

        val response = try {
            apiClient.syncRequest(request)
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }

        return decodeBitmap(response)
    }

    private fun decodeBitmap(response: Response?): Bitmap? {
        return BitmapFactory.decodeStream(response?.body()?.byteStream())
    }

    override fun onPostExecute(result: Bitmap?) {
        super.onPostExecute(result)
        onDownloadComplete(result)
    }
}