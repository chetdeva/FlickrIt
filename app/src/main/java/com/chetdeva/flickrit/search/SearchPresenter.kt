package com.chetdeva.flickrit.search

import android.graphics.Bitmap
import com.chetdeva.flickrit.network.ImageClient
import com.chetdeva.flickrit.network.dto.PhotoDto

/**
 * @author chetansachdeva
 */

class SearchPresenter(
        private val interactor: SearchContract.Interactor,
        private val imageClient: ImageClient,
        private val view: SearchContract.View
) : SearchContract.Presenter {

    init {
        view.presenter = this
    }

    override fun start() {
        search("kittens")
    }

    override fun search(query: String) {
        interactor.search(query, object : SearchContract.Interactor.Callback {
            override fun publish(searchModel: SearchModel) {
                view.render(searchState(searchModel))
            }
        })
    }

    override fun loadNextPage() {
        interactor.nextPage(object : SearchContract.Interactor.Callback {
            override fun publish(searchModel: SearchModel) {
                view.render(searchState(searchModel))
            }
        })
    }

    private fun searchState(model: SearchModel): SearchState {
        return SearchState(
                showLoader = model.loading,
                hideLoader = !model.loading,
                photos = model.photos,
                error = model.error)
    }

    override fun downloadImage(url: String, onDownloadComplete: (Bitmap?) -> Unit) {
        return imageClient.downloadImage(url, onDownloadComplete)
    }

    override fun onResultClicked(photo: PhotoDto) {

    }
}
