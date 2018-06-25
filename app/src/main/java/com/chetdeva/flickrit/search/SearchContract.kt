package com.chetdeva.flickrit.search

import android.graphics.Bitmap
import com.chetdeva.flickrit.mvp.BasePresenter
import com.chetdeva.flickrit.mvp.BaseView
import com.chetdeva.flickrit.network.dto.PhotoDto

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
        fun search(query: String, publish: (SearchModel) -> Unit)
        fun nextPage(publish: (SearchModel) -> Unit)
    }

    interface Adapter {
        fun downloadImage(url: String, onDownloadComplete: (Bitmap?) -> Unit)
        fun onResultClicked(photo: PhotoDto)
    }
}