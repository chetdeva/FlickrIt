package com.chetdeva.flickrit.search

import com.chetdeva.flickrit.network.FlickrApiService
import com.chetdeva.flickrit.network.dto.PhotoDto
import com.chetdeva.flickrit.network.dto.SearchResultDto
import com.chetdeva.flickrit.network.entities.SearchResponse
import com.chetdeva.flickrit.util.Mapper
import com.chetdeva.flickrit.util.NetworkResult
import com.chetdeva.flickrit.util.Publisher
import com.chetdeva.flickrit.util.executor.AppExecutors

/**
 * @author chetansachdeva
 */

class SearchInteractor(
        private val apiService: FlickrApiService,
        private val executors: AppExecutors,
        private val mapper: Mapper<SearchResponse, SearchResultDto>
) : SearchContract.Interactor {

    private var model: SearchModel = SearchModel.Init

    override fun search(query: String,
                        publisher: Publisher<SearchModel>) {

        if (query.isNotBlank() && query.length >= 3) {
            model = SearchModel.Init.copy(query = query)
            publisher.publish(model)
            searchFlickr(query, model.page, publisher)
        } else {
            onSearchError(TOO_SMALL_QUERY_ERROR, publisher)
        }
    }

    private fun searchFlickr(query: String,
                             page: Int,
                             publisher: Publisher<SearchModel>) {
        executors.networkIO.execute {
            apiService.search(query, page) { result ->
                executors.UI.execute {
                    when (result) {
                        is NetworkResult.Success -> onSearchSuccess(result.data, publisher)
                        is NetworkResult.Error -> onSearchError(result.error, publisher)
                    }
                }
            }
        }
    }

    private fun onSearchSuccess(response: SearchResponse, publisher: Publisher<SearchModel>) {
        if (response.photos?.photo?.isNotEmpty() == true) {
            val photos = updateList(mapper.mapFromEntity(response).photos)
            model = model.copy(showLoader = false, hideLoader = true, photos = photos, page = model.page + 1)
            publisher.publish(model)
        } else {
            onSearchError(NO_MORE_ITEMS_ERROR, publisher)
        }
    }

    private fun updateList(photos: List<PhotoDto>): MutableList<PhotoDto> {
        return model.photos.toMutableList().apply { addAll(photos) }
    }

    private fun onSearchError(error: String, publisher: Publisher<SearchModel>) {
        model = model.copy(showLoader = false, hideLoader = true, error = error)
        publisher.publish(model)
    }

    override fun nextPage(publisher: Publisher<SearchModel>) {
        if (model.showLoader) return
        model = model.copy(showLoader = true, hideLoader = false)
        publisher.publish(model)
        searchFlickr(model.query, model.page, publisher)
    }

    companion object {
        const val VISIBLE_THRESHOLD: Int = 9
        const val MAX_PAGE_SIZE: Int = 12
        const val TOO_SMALL_QUERY_ERROR = "Type at least 3 characters"
        const val NO_MORE_ITEMS_ERROR = "No more items found"
    }
}