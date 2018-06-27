package com.chetdeva.flickrit.search

import com.chetdeva.flickrit.network.dto.PhotoDto
import com.chetdeva.flickrit.util.Publisher

/**
 * @author chetansachdeva
 */

class SearchPresenter(
        private val interactor: SearchContract.Interactor,
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

    override fun start() {
    }

    /**
     * search Flickr for the [query] text
     */
    override fun search(query: String) {
        interactor.search(query, publisher)
    }

    /**
     * search Flickr for the last [query] text
     */
    override fun searchLastQuery() {
        interactor.searchLastQuery(publisher)
    }

    /**
     * loadImageBitmap next page with the previous query text
     */
    override fun loadNextPage() {
        interactor.nextPage(publisher)
    }

    override var lastQuery: String
        get() = interactor.lastQuery
        set(value) {
            interactor.lastQuery = value
        }

    /**
     * called when user taps on a [PhotoDto]
     */
    override fun onPhotoClicked(photo: PhotoDto) {

    }
}
