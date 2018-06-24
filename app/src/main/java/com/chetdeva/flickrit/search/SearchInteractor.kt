package com.chetdeva.flickrit.search

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
                        publish: (SearchViewState) -> Unit) {

        if (query.isNotBlank() && query.length >= 3) {
            currentPage.set(1)
            photos.clear()
            publish(SearchViewState.Init)
            searchFlickr(query, currentPage.get(), publish)
        }
    }

    private fun searchFlickr(query: String,
                             page: Int,
                             publish: (SearchViewState) -> Unit) {
        Log.i("SearchInteractor", "searching flickr for query: $query page: $page")

        lastQuery.set(query)
        inFlight.set(true)
        publish(SearchViewState(showLoader = true, photos = photos))

        flickrApi.search(query, page, {
            onSuccess(it, publish)
        }, {
            onError(it, publish)
        })
    }

    private fun onSuccess(response: SearchResponse, publish: (SearchViewState) -> Unit) {
        if (response.photos?.photo?.isNotEmpty() == true) {
            val list = mapper.mapFromEntity(response).photos
            photos.addAll(list)
            val state = SearchViewState(hideLoader = true, photos = photos)
            inFlight.set(false)
            publish(state)
            currentPage.incrementAndGet()
        } else {
            onError("No Items Found", publish)
        }
    }

    private fun onError(error: String, publish: (SearchViewState) -> Unit) {
        val state = SearchViewState(hideLoader = true, photos = photos, error = error)
        inFlight.set(false)
        publish(state)
    }

    override fun nextPage(publish: (SearchViewState) -> Unit) {
        if (inFlight.get()) return
        searchFlickr(lastQuery.get(), currentPage.get(), publish)
    }

    companion object {
        const val VISIBILE_THRESHOLD: Int = 9
        const val MAX_PAGE_SIZE: Int = 12
    }
}