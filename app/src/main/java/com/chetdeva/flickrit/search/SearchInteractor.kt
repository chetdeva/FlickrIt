package com.chetdeva.flickrit.search

import com.chetdeva.flickrit.network.FlickrApiService
import com.chetdeva.flickrit.network.dto.PhotoDto
import com.chetdeva.flickrit.network.dto.SearchResultDto
import com.chetdeva.flickrit.network.entities.SearchResponse
import com.chetdeva.flickrit.search.SearchContract.Interactor.Callback
import com.chetdeva.flickrit.util.Mapper
import com.chetdeva.flickrit.util.executor.AppExecutors

/**
 * @author chetansachdeva
 */

class SearchInteractor(
        private val flickrApi: FlickrApiService,
        private val appExecutors: AppExecutors,
        private val searchMapper: Mapper<SearchResponse, SearchResultDto>
) : SearchContract.Interactor {

    private var model: SearchModel = SearchModel.Init

    override fun search(query: String,
                        callback: Callback) {

        if (query.isNotBlank() && query.length >= 3) {
            model = SearchModel.Init.copy(query = query)
            callback.publish(model)
            searchFlickr(query, model.page, callback)
        } else {
            onSearchError("Type at least 3 characters", callback)
        }
    }

    private fun searchFlickr(query: String,
                             page: Int,
                             callback: Callback) {
        appExecutors.networkIO.execute {
            flickrApi.search(query, page, {
                appExecutors.mainThread.execute {
                    onSearchSuccess(it, callback)
                }
            }, {
                appExecutors.mainThread.execute {
                    onSearchError(it, callback)
                }
            })
        }
    }

    private fun onSearchSuccess(response: SearchResponse, callback: Callback) {
        if (response.photos?.photo?.isNotEmpty() == true) {
            val photos = updateList(searchMapper.mapFromEntity(response).photos)
            model = model.copy(showLoader = false, hideLoader = true, photos = photos, page = model.page + 1)
            callback.publish(model)
        } else {
            onSearchError("No more items found", callback)
        }
    }

    private fun updateList(photos: List<PhotoDto>): MutableList<PhotoDto> {
        return model.photos.toMutableList().apply { addAll(photos) }
    }

    private fun onSearchError(error: String, callback: Callback) {
        model = model.copy(showLoader = false, hideLoader = true, error = error)
        callback.publish(model)
    }

    override fun nextPage(callback: Callback) {
        if (model.showLoader) return
        model = model.copy(showLoader = true, hideLoader = false)
        callback.publish(model)
        searchFlickr(model.query, model.page, callback)
    }

    companion object {
        const val VISIBLE_THRESHOLD: Int = 9
        const val MAX_PAGE_SIZE: Int = 12
    }
}