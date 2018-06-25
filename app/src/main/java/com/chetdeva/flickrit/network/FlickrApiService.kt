package com.chetdeva.flickrit.network

import com.chetdeva.flickrit.BuildConfig
import com.chetdeva.flickrit.network.entities.SearchResponse
import com.chetdeva.flickrit.search.SearchInteractor
import com.chetdeva.flickrit.util.extension.fromJson
import com.example.android.architecture.blueprints.todoapp.util.SingletonHolderDoubleArg
import com.google.gson.Gson
import java.util.*


/**
 * @author chetansachdeva
 */

class FlickrApiService(private val apiClient: ApiClient,
                       private val gson: Gson) {

    fun search(query: String,
               page: Int,
               onSuccess: (SearchResponse) -> Unit,
               onError: (String) -> Unit) {

        val params = getDefaultParams()
        params["text"] = query
        params["page"] = page.toString()
        params["per_page"] = SearchInteractor.MAX_PAGE_SIZE.toString()
        params["method"] = SEARCH_PHOTOS_METHOD

        val request = ApiClient.request(baseUrl = FLICKR_API_BASE_URL, params = params)

        apiClient.asyncRequest(request, {
            onSuccess(gson.fromJson(it.string()))
        }, onError)
    }

    companion object : SingletonHolderDoubleArg<FlickrApiService, ApiClient, Gson>(::FlickrApiService) {

        private const val FLICKR_API_KEY: String = BuildConfig.FLICKR_API_KEY
        private const val FLICKR_API_BASE_URL: String = "https://api.flickr.com/services/rest"
        private const val SEARCH_PHOTOS_METHOD = "flickr.photos.search"

        private fun getDefaultParams(): HashMap<String, String> {
            return hashMapOf(
                    "api_key" to FLICKR_API_KEY,
                    "format" to "json",
                    "nojsoncallback" to "1",
                    "safe_search" to "1"
            )
        }
    }
}