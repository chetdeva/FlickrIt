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

    override fun search(query: String) {
        interactor.search(query) {
            view.render(searchState(it))
        }
    }

    override fun loadNextPage() {
        interactor.nextPage {
            view.render(searchState(it))
        }
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
