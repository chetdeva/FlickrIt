package com.chetdeva.flickrit.network

import com.example.android.architecture.blueprints.todoapp.util.SingletonHolderSingleArg
import okhttp3.*
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

/**
 * @author chetansachdeva
 */

class ApiClient(private val client: OkHttpClient) {

    /**
     * make an asyn buildRequest via [OkHttpClient]
     */
    fun asyncRequest(request: Request,
                     onSuccess: (ResponseBody) -> Unit,
                     onError: (String) -> Unit) {

        client.newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful && response.body() != null) {
                    onSuccess(response.body()!!)
                } else {
                    onError(response.message())
                }
            }

            override fun onFailure(call: Call, e: IOException?) {
                onError(resolveException(e))
            }
        })
    }

    /**
     * make a sync buildRequest via [OkHttpClient]
     */
    fun syncRequest(request: Request): Response {
        return client.newCall(request).execute()
    }

    companion object : SingletonHolderSingleArg<ApiClient, OkHttpClient>(::ApiClient) {

        private val NO_INTERNET_MESSAGE = "No internet connection."
        private val REMOTE_SERVER_FAILED_MESSAGE = "Application server could not respond."
        private val UNEXPECTED_ERROR_OCCURRED = "Something went wrong."

        /**
         * build a [Request] with the given
         * [baseUrl], [path], [headers] and query [params]
         */
        fun buildRequest(baseUrl: String,
                         path: String = "",
                         headers: Map<String, String> = emptyMap(),
                         params: Map<String, String> = emptyMap()): Request {

            val url = buildUrl(baseUrl, path, params).toString()

            return buildRequest(url, headers)
        }

        /**
         * build [HttpUrl] with the given
         * [baseUrl], [path] and query [params]
         */
        private fun buildUrl(baseUrl: String, path: String, params: Map<String, String>): HttpUrl {
            val urlBuilder = HttpUrl.parse(baseUrl)!!.newBuilder()
            if (path.isNotBlank()) {
                urlBuilder.addPathSegment(path)
            }
            params.map { urlBuilder.addQueryParameter(it.key, it.value) }
            return urlBuilder.build()
        }

        /**
         * build request with the given [url] and [headers]
         */
        private fun buildRequest(url: String, headers: Map<String, String>): Request {
            val requestBuilder = Request.Builder()
            headers.map { requestBuilder.addHeader(it.key, it.value) }
            return requestBuilder
                    .url(url)
                    .build()
        }

        /**
         * resolve HTTP [IOException] to a [String]
         */
        fun resolveException(cause: Exception?): String {
            return when (cause) {
                is UnknownHostException -> NO_INTERNET_MESSAGE
                is SocketTimeoutException -> REMOTE_SERVER_FAILED_MESSAGE
                is ConnectException -> NO_INTERNET_MESSAGE
                else -> UNEXPECTED_ERROR_OCCURRED
            }
        }
    }
}