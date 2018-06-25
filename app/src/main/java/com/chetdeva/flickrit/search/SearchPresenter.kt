package com.chetdeva.flickrit.search

import android.graphics.Bitmap
import com.chetdeva.flickrit.network.ImageService
import com.chetdeva.flickrit.network.dto.PhotoDto
import com.chetdeva.flickrit.util.Publisher

/**
 * @author chetansachdeva
 */

class SearchPresenter(
        private val interactor: SearchContract.Interactor,
        private val imageService: ImageService,
        private val view: SearchContract.View
) : SearchContract.Presenter {

    init {
        view.presenter = this
    }

    private val publisher = object : Publisher<SearchModel> {
        override fun publish(model: SearchModel) {
            view.render(searchState(model))
        }
    }

    override fun start() {
        search("kittens")
    }

    override fun search(query: String) {
        interactor.search(query, publisher)
    }

    override fun loadNextPage() {
        interactor.nextPage(publisher)
    }

    private fun searchState(model: SearchModel): SearchState {
        return SearchState(
                refresh = model.refresh,
                showLoader = model.showLoader,
                hideLoader = model.hideLoader,
                photos = model.photos,
                error = model.error)
    }

    override fun loadImage(url: String, onDownloadComplete: (Bitmap?) -> Unit) {
        return imageService.downloadImage(url, onDownloadComplete)
    }

    override fun onResultClicked(photo: PhotoDto) {

    }
}
