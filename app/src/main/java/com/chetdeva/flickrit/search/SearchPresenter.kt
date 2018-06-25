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
        interactor.subscribe {
            view.render(it.state())
        }
    }

    override fun search(query: String) {
        interactor.search(query)
    }

    override fun loadNextPage() {
        interactor.nextPage()
    }

    override fun downloadImage(url: String, onDownloadComplete: (Bitmap?) -> Unit) {
        return imageClient.downloadImage(url, onDownloadComplete)
    }

    override fun onResultClicked(photo: PhotoDto) {

    }
}
