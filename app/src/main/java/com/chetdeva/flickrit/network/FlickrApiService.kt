package com.chetdeva.flickrit.network

import com.chetdeva.flickrit.network.entities.SearchResponse
import com.chetdeva.flickrit.search.SearchInteractor
import com.chetdeva.flickrit.util.NetworkResult
import com.chetdeva.flickrit.util.extension.fromJson
import com.example.android.architecture.blueprints.todoapp.util.SingletonHolderDoubleArg
import com.google.gson.Gson
import java.util.*


/**
 * manages Flickr API HTTP calls
 *
 * @author chetansachdeva
 */

class FlickrApiService(private val apiClient: ApiClient,
                       private val gson: Gson) {

    /**
     * search Flickr API for given [query] text and [page] number
     * publish a [NetworkResult] of type [SearchResponse]
     */
    fun search(query: String,
               page: Int,
               onResult: (NetworkResult<SearchResponse>) -> Unit) {

        val params = getDefaultParams()
        params["text"] = query
        params["page"] = page.toString()
        params["per_page"] = SearchInteractor.MAX_PAGE_SIZE.toString()
        params["method"] = SEARCH_PHOTOS_METHOD

        val request = ApiClient.buildRequest(baseUrl = FLICKR_API_BASE_URL, params = params)

        apiClient.asyncRequest(request, { response ->
            val searchResponse = gson.fromJson<SearchResponse>(response.string())
            onResult(NetworkResult.Success(searchResponse))
        }, { error ->
            onResult(NetworkResult.Error(error))
        })
    }

    companion object : SingletonHolderDoubleArg<FlickrApiService, ApiClient, Gson>(::FlickrApiService) {

        private const val FLICKR_API_KEY: String = "6dea34e991808ad909d861a07ffd223c"
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