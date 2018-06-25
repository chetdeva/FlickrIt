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

    /**
     * validate [query] text and search [FlickrApiService]
     * publish [SearchModel] via [publisher]
     */
    override fun search(query: String,
                        publisher: Publisher<SearchModel>) {

        if (query.isNotBlank() && query.length >= 3) {
            model = SearchModel.Init.copy(
                    refresh = true,
                    showLoader = true,
                    query = query)
            publisher.publish(model)
            searchFlickr(query, model.page, publisher)
        } else {
            onSearchError(TOO_SMALL_QUERY_ERROR, publisher)
        }
    }

    /**
     * search [FlickrApiService] with given [query] text and [page] number
     * publish [SearchModel] via [publisher]
     */
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

    /**
     * called when search has successfully completed
     */
    private fun onSearchSuccess(response: SearchResponse, publisher: Publisher<SearchModel>) {
        if (response.photos?.photo?.isNotEmpty() == true) {
            val photos = updateList(mapper.mapFromEntity(response).photos)
            model = model.copy(
                    refresh = false,
                    showLoader = false,
                    hideLoader = true,
                    photos = photos,
                    page = model.page + 1,
                    error = "")
            publisher.publish(model)
        } else {
            onSearchError(NO_MORE_ITEMS_ERROR, publisher)
        }
    }

    /**
     * update the [PhotoDto] list in [SearchModel]
     */
    private fun updateList(photos: List<PhotoDto>): MutableList<PhotoDto> {
        return model.photos.toMutableList().apply { addAll(photos) }
    }

    /**
     * called when search errors out
     */
    private fun onSearchError(error: String, publisher: Publisher<SearchModel>) {
        model = model.copy(
                refresh = false,
                showLoader = false,
                hideLoader = true,
                error = error)
        publisher.publish(model)
    }

    /**
     * search next page with the previous query text held in [SearchModel]
     * publish [SearchModel] via [publisher]
     */
    override fun nextPage(publisher: Publisher<SearchModel>) {
        if (model.showLoader) return
        model = model.copy(
                refresh = false,
                showLoader = true,
                hideLoader = false,
                error = "")
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