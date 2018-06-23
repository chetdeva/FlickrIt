package com.chetdeva.flickrit.search

import android.graphics.Bitmap
import android.util.Log
import com.chetdeva.flickrit.network.FlickrApiService
import com.chetdeva.flickrit.network.dto.PhotoDto
import com.chetdeva.flickrit.network.dto.SearchResultDto
import com.chetdeva.flickrit.network.entities.SearchResponse
import com.chetdeva.flickrit.util.Mapper

/**
 * @author chetansachdeva
 */

class SearchInteractor(
        private val flickrApi: FlickrApiService,
        private val mapper: Mapper<SearchResponse, SearchResultDto>
) : SearchContract.Interactor {

    private var currentPage: Int = 1
    private var lastQuery: String = ""
    private var inFlight: Boolean = false
    private var photos: MutableList<PhotoDto> = mutableListOf()

    override fun search(query: String,
                        publish: (SearchState) -> Unit) {

        if (query.isNotBlank() && query.length >= 3) {
            currentPage = 1
            photos.clear()
            publish(SearchState.Refresh)
            searchFlickr(query, currentPage, publish)
        }
    }

    private fun searchFlickr(query: String,
                             page: Int,
                             publish: (SearchState) -> Unit) {
        Log.i("SearchInteractor", "searching flickr for query: $query page: $page")

        lastQuery = query
        inFlight = true
        publish(SearchState.Loading)

        flickrApi.search(query, page, {
            onSuccess(it, publish)
        }, {
            onError(it, publish)
        })
    }

    private fun onSuccess(response: SearchResponse, publish: (SearchState) -> Unit) {
        val list = mapper.mapFromEntity(response).photos
        photos.addAll(list)
        val state = SearchState(loading = false, photos = photos)
        inFlight = false
        publish(state)
        currentPage++
    }

    private fun onError(error: String, publish: (SearchState) -> Unit) {
        val state = SearchState(error = error)
        inFlight = false
        publish(state)
    }

    override fun nextPage(publish: (SearchState) -> Unit) {
        if (inFlight) return
        searchFlickr(lastQuery, currentPage, publish)
    }

    override fun downloadImage(url: String, onDownloadComplete: (Bitmap?) -> Unit) {
        return flickrApi.downloadImage(url, onDownloadComplete)
    }

    companion object {
        const val MAX_PAGE_SIZE: Int = 10
    }
}