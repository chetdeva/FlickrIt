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

    private lateinit var publisher: (SearchModel) -> Unit
    private var model: SearchModel = SearchModel.Init

    override fun subscribe(publisher: (SearchModel) -> Unit) {
        this.publisher = publisher
    }

    override fun search(query: String) {

        if (query.isNotBlank() && query.length >= 3) {
            model = model.copy(loading = true, photos = emptyList(), query = query, page = 1)
            publisher(model)
            searchFlickr(query, model.page)
        }
    }

    private fun searchFlickr(query: String,
                             page: Int) {

        flickrApi.search(query, page, {
            onSearchSuccess(it)
        }, {
            onSearchError(it)
        })
    }

    private fun onSearchSuccess(response: SearchResponse) {
        if (response.photos?.photo?.isNotEmpty() == true) {
            val photos = updateList(searchMapper.mapFromEntity(response).photos)
            model = model.copy(loading = false, photos = photos, page = model.page + 1)
            publisher(model)
        } else {
            onSearchError("No Items Found")
        }
    }

    private fun updateList(photos: List<PhotoDto>): MutableList<PhotoDto> {
        return model.photos.toMutableList().apply { addAll(photos) }
    }

    private fun onSearchError(error: String) {
        model = model.copy(loading = false, error = error)
        publisher(model)
    }

    override fun nextPage() {
        if (model.loading) return
        model = model.copy(loading = true)
        publisher(model)
        searchFlickr(model.query, model.page)
    }

    companion object {
        const val VISIBLE_THRESHOLD: Int = 9
        const val MAX_PAGE_SIZE: Int = 12
    }
}