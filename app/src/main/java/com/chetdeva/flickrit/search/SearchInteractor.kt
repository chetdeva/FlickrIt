package com.chetdeva.flickrit.search

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
        private val searchMapper: Mapper<SearchResponse, SearchResultDto>
) : SearchContract.Interactor {

    private var model: SearchModel = SearchModel.Init

    override fun search(query: String,
                        publish: (SearchState) -> Unit) {

        if (query.isNotBlank() && query.length >= 3) {
            model = model.copy(loading = true, photos = emptyList(), query = query, page = 1)
            val state = SearchState(showLoader = model.loading, photos = model.photos)
            publish(state)
            searchFlickr(query, model.page, publish)
        }
    }

    private fun searchFlickr(query: String,
                             page: Int,
                             publish: (SearchState) -> Unit) {

        flickrApi.search(query, page, {
            onSearchSuccess(it, publish)
        }, {
            onSearchError(it, publish)
        })
    }

    private fun onSearchSuccess(response: SearchResponse, publish: (SearchState) -> Unit) {
        if (response.photos?.photo?.isNotEmpty() == true) {
            val photos = updateList(searchMapper.mapFromEntity(response).photos)
            model = model.copy(loading = false, photos = photos, page = model.page + 1)
            val state = SearchState(hideLoader = !model.loading, photos = model.photos)
            publish(state)
        } else {
            onSearchError("No Items Found", publish)
        }
    }

    private fun updateList(photos: List<PhotoDto>): MutableList<PhotoDto> {
        return model.photos.toMutableList().apply { addAll(photos) }
    }

    private fun onSearchError(error: String, publish: (SearchState) -> Unit) {
        model = model.copy(loading = false, error = error)
        val state = SearchState(hideLoader = !model.loading, photos = model.photos, error = model.error)
        publish(state)
    }

    override fun nextPage(publish: (SearchState) -> Unit) {
        if (model.loading) return
        searchFlickr(model.query, model.page, publish)
    }

    companion object {
        const val VISIBLE_THRESHOLD: Int = 9
        const val MAX_PAGE_SIZE: Int = 12
    }
}