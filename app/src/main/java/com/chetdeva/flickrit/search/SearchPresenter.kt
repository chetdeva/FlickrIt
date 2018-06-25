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

    /**
     * publishes [SearchState] to the [SearchContract.View].
     */
    private val publisher = object : Publisher<SearchModel> {
        override fun publish(model: SearchModel) {
            view.render(model.state())
        }
    }

    /**
     * search for "kittens" on start
     */
    override fun start() {
        search("kittens")
    }

    /**
     * search Flickr for the [query] text
     */
    override fun search(query: String) {
        interactor.search(query, publisher)
    }

    /**
     * load next page with the previous query text
     */
    override fun loadNextPage() {
        interactor.nextPage(publisher)
    }

    /**
     * load image from [ImageService]
     */
    override fun loadImage(url: String, onDownloadComplete: (Bitmap?) -> Unit) {
        return imageService.downloadImage(url, onDownloadComplete)
    }

    /**
     * called when user taps on a [PhotoDto]
     */
    override fun onPhotoClicked(photo: PhotoDto) {

    }
}
