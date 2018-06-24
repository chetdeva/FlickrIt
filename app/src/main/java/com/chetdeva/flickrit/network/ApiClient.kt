package com.chetdeva.flickrit.network

import com.chetdeva.flickrit.util.SingletonHolderSingleArg
import okhttp3.*
import java.io.IOException

/**
 * @author chetansachdeva
 */

class ApiClient(private val client: OkHttpClient) {

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
                onError(e?.localizedMessage ?: "")
            }
        })
    }

    fun syncRequest(request: Request): Response {
        return client.newCall(request).execute()
    }

    companion object : SingletonHolderSingleArg<ApiClient, OkHttpClient>(::ApiClient) {

        fun request(baseUrl: String,
                    path: String = "",
                    headers: Map<String, String> = emptyMap(),
                    params: Map<String, String> = emptyMap()): Request {

            val url = buildUrl(baseUrl, path, params).toString()

            return buildRequest(url, headers)
        }

        private fun buildUrl(baseUrl: String, path: String, params: Map<String, String>): HttpUrl {
            val urlBuilder = HttpUrl.parse(baseUrl)!!.newBuilder()
            if (path.isNotBlank()) { urlBuilder.addPathSegment(path) }
            params.map { urlBuilder.addQueryParameter(it.key, it.value) }
            return urlBuilder.build()
        }

        private fun buildRequest(url: String, headers: Map<String, String>): Request {
            val requestBuilder = Request.Builder()
            headers.map { requestBuilder.addHeader(it.key, it.value) }
            return requestBuilder
                    .url(url)
                    .build()
        }

    }
}