package com.chetdeva.flickrit.search

import com.chetdeva.flickrit.network.FlickrApiService
import com.chetdeva.flickrit.network.dto.PhotoDto
import com.chetdeva.flickrit.network.dto.SearchResultDto
import com.chetdeva.flickrit.network.entities.SearchResponse
import com.chetdeva.flickrit.util.Mapper
import com.chetdeva.flickrit.util.Publisher
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
                        publisher: Publisher<SearchModel>) {

        if (query.isNotBlank() && query.length >= 3) {
            model = SearchModel.Init.copy(query = query)
            publisher.publish(model)
            searchFlickr(query, model.page, publisher)
        } else {
            onSearchError("Type at least 3 characters", publisher)
        }
    }

    private fun searchFlickr(query: String,
                             page: Int,
                             publisher: Publisher<SearchModel>) {
        appExecutors.networkIO.execute {
            flickrApi.search(query, page, {
                appExecutors.mainThread.execute {
                    onSearchSuccess(it, publisher)
                }
            }, {
                appExecutors.mainThread.execute {
                    onSearchError(it, publisher)
                }
            })
        }
    }

    private fun onSearchSuccess(response: SearchResponse, publisher: Publisher<SearchModel>) {
        if (response.photos?.photo?.isNotEmpty() == true) {
            val photos = updateList(searchMapper.mapFromEntity(response).photos)
            model = model.copy(showLoader = false, hideLoader = true, photos = photos, page = model.page + 1)
            publisher.publish(model)
        } else {
            onSearchError("No more items found", publisher)
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
    }
}