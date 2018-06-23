package com.chetdeva.flickrit.search

import android.graphics.Bitmap
import android.util.Log
import com.chetdeva.flickrit.network.FlickrApiService
import com.chetdeva.flickrit.network.dto.PhotoDto
import com.chetdeva.flickrit.network.dto.SearchResultDto
import com.chetdeva.flickrit.network.entities.SearchResponse
import com.chetdeva.flickrit.util.Mapper
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicReference

/**
 * @author chetansachdeva
 */

class SearchInteractor(
        private val flickrApi: FlickrApiService,
        private val mapper: Mapper<SearchResponse, SearchResultDto>
) : SearchContract.Interactor {

    private var currentPage: AtomicInteger = AtomicInteger(1)
    private var lastQuery: AtomicReference<String> = AtomicReference("")
    private var inFlight: AtomicBoolean = AtomicBoolean(false)
    private var photos: MutableList<PhotoDto> = mutableListOf()

    override fun search(query: String,
                        publish: (SearchState) -> Unit) {

        if (query.isNotBlank() && query.length >= 3) {
            currentPage.set(1)
            photos.clear()
            publish(SearchState(refresh = true))
            searchFlickr(query, currentPage.get(), publish)
        }
    }

    private fun searchFlickr(query: String,
                             page: Int,
                             publish: (SearchState) -> Unit) {
        Log.i("SearchInteractor", "searching flickr for query: $query page: $page")

        lastQuery.lazySet(query)
        inFlight.set(true)
        publish(SearchState(showLoader = true))

        flickrApi.search(query, page, {
            onSuccess(it, publish)
        }, {
            onError(it, publish)
        })
    }

    private fun onSuccess(response: SearchResponse, publish: (SearchState) -> Unit) {
        val list = mapper.mapFromEntity(response).photos
        photos.addAll(list)
        val state = SearchState(hideLoader = true, photos = photos)
        inFlight.set(false)
        publish(state)
        currentPage.incrementAndGet()
    }

    private fun onError(error: String, publish: (SearchState) -> Unit) {
        val state = SearchState(hideLoader = true, error = error)
        inFlight.set(false)
        publish(state)
    }

    override fun nextPage(publish: (SearchState) -> Unit) {
        if (inFlight.get()) return
        searchFlickr(lastQuery.get(), currentPage.get(), publish)
    }

    override fun downloadImage(url: String, onDownloadComplete: (Bitmap?) -> Unit) {
        return flickrApi.downloadImage(url, onDownloadComplete)
    }

    companion object {
        const val MAX_PAGE_SIZE: Int = 10
    }
}