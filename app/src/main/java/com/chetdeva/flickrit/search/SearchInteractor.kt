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
                        publish: (SearchModel) -> Unit) {

        if (query.isNotBlank() && query.length >= 3) {
            model = model.copy(loading = true, photos = emptyList(), query = query, page = 1)
            publish(model)
            searchFlickr(query, model.page, publish)
        }
    }

    private fun searchFlickr(query: String,
                             page: Int,
                             publish: (SearchModel) -> Unit) {

        flickrApi.search(query, page, {
            onSearchSuccess(it, publish)
        }, {
            onSearchError(it, publish)
        })
    }

    private fun onSearchSuccess(response: SearchResponse, publish: (SearchModel) -> Unit) {
        if (response.photos?.photo?.isNotEmpty() == true) {
            val photos = updateList(searchMapper.mapFromEntity(response).photos)
            model = model.copy(loading = false, photos = photos, page = model.page + 1)
            publish(model)
        } else {
            onSearchError("No Items Found", publish)
        }
    }

    private fun updateList(photos: List<PhotoDto>): MutableList<PhotoDto> {
        return model.photos.toMutableList().apply { addAll(photos) }
    }

    private fun onSearchError(error: String, publish: (SearchModel) -> Unit) {
        model = model.copy(loading = false, error = error)
        publish(model)
    }

    override fun nextPage(publish: (SearchModel) -> Unit) {
        if (model.loading) return
        model = model.copy(loading = true)
        publish(model)
        searchFlickr(model.query, model.page, publish)
    }

    companion object {
        const val VISIBLE_THRESHOLD: Int = 9
        const val MAX_PAGE_SIZE: Int = 12
    }
}