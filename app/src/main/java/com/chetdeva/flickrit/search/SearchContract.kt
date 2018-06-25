package com.chetdeva.flickrit.search

import android.graphics.Bitmap
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
        fun search(query: String)
        fun loadNextPage()
    }

    interface Interactor {
        fun search(query: String, publisher: Publisher<SearchModel>)
        fun nextPage(publisher: Publisher<SearchModel>)
    }

    interface Adapter {
        fun loadImage(url: String, onDownloadComplete: (Bitmap?) -> Unit)
        fun onResultClicked(photo: PhotoDto)
    }
}