package com.chetdeva.flickrit.search

import android.content.Context
import android.graphics.Bitmap
import com.chetdeva.flickrit.network.ImageDownloader
import com.chetdeva.flickrit.network.dto.PhotoDto
import com.chetdeva.flickrit.util.Publisher
import com.chetdeva.flickrit.util.image.ImageBitmapLoader

/**
 * @author chetansachdeva
 */

class SearchPresenter(
        private val interactor: SearchContract.Interactor,
        private val bitmapLoader: ImageBitmapLoader,
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
     * loadImageBitmap next page with the previous query text
     */
    override fun loadNextPage() {
        interactor.nextPage(publisher)
    }

    /**
     * loadImageBitmap image from [ImageDownloader]
     */
    override fun loadImage(context: Context, url: String, onLoadComplete: (Bitmap?) -> Unit) {
        return bitmapLoader.loadImageBitmap(url, onLoadComplete)
    }

    /**
     * called when user taps on a [PhotoDto]
     */
    override fun onPhotoClicked(photo: PhotoDto) {

    }
}
