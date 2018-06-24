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
        interactor.search(query) { model ->
            view.render(model.state())
        }
    }

    override fun loadNextPage() {
        interactor.nextPage { model ->
            view.render(model.state())
        }
    }

    override fun downloadImage(url: String, onDownloadComplete: (Bitmap?) -> Unit) {
        return imageClient.downloadImage(url, onDownloadComplete)
    }

    override fun onResultClicked(photo: PhotoDto) {

    }
}
