package com.chetdeva.flickrit.search

import android.util.Log
import com.chetdeva.flickrit.network.FlickrApiService
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

    private var model: SearchModel = SearchModel.Init

    override fun search(query: String,
                        publish: (SearchState) -> Unit) {

        if (query.isNotBlank() && query.length >= 3) {
            model.currentPage = 1
            model.photos.clear()
            publish(SearchState.Init)
            searchFlickr(query, model.currentPage, publish)
        }
    }

    private fun searchFlickr(query: String,
                             page: Int,
                             publish: (SearchState) -> Unit) {
        Log.i("SearchInteractor", "searching flickr for query: $query page: $page")

        model.lastQuery = query
        model.inFlight = true
        publish(SearchState(showLoader = true, photos = model.photos))

        flickrApi.search(query, page, {
            onSuccess(it, publish)
        }, {
            onError(it, publish)
        })
    }

    private fun onSuccess(response: SearchResponse, publish: (SearchState) -> Unit) {
        if (response.photos?.photo?.isNotEmpty() == true) {
            val list = mapper.mapFromEntity(response).photos
            model.photos.addAll(list)
            val state = SearchState(hideLoader = true, photos = model.photos)
            model.inFlight = false
            publish(state)
            model.currentPage++
        } else {
            onError("No Items Found", publish)
        }
    }

    private fun onError(error: String, publish: (SearchState) -> Unit) {
        val state = SearchState(hideLoader = true, photos = model.photos, error = error)
        model.inFlight = false
        publish(state)
    }

    override fun nextPage(publish: (SearchState) -> Unit) {
        if (model.inFlight) return
        searchFlickr(model.lastQuery, model.currentPage, publish)
    }

    companion object {
        const val VISIBILE_THRESHOLD: Int = 9
        const val MAX_PAGE_SIZE: Int = 12
    }
}