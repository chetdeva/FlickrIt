package com.chetdeva.flickrit.search

import com.chetdeva.flickrit.mvp.BasePresenter
import com.chetdeva.flickrit.mvp.BaseView
import com.chetdeva.flickrit.network.dto.PhotoDto
import com.chetdeva.flickrit.util.Publisher

/**
 * @author chetansachdeva
 */

interface SearchContract {

    interface View : BaseView<Presenter> {
        fun render(state: SearchState)
    }

    interface Presenter : BasePresenter, Adapter {
        var lastQuery: String
        fun search(query: String)
        fun searchLastQuery()
        fun loadNextPage()
    }

    interface Interactor {
        var lastQuery: String
        fun search(query: String, publisher: Publisher<SearchModel>)
        fun nextPage(publisher: Publisher<SearchModel>)
        fun searchLastQuery(publisher: Publisher<SearchModel>)
    }

    interface Adapter {
        fun onPhotoClicked(photo: PhotoDto)
    }
}