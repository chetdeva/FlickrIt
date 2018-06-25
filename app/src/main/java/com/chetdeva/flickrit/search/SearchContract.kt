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
        interface Callback {
            fun publish(searchModel: SearchModel)
        }

        fun search(query: String, callback: Callback)
        fun nextPage(callback: Callback)
    }

    interface Adapter {
        fun downloadImage(url: String, onDownloadComplete: (Bitmap?) -> Unit)
        fun onResultClicked(photo: PhotoDto)
    }
}