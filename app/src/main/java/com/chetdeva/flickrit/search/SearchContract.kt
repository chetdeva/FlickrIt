package com.chetdeva.flickrit.search

import android.graphics.Bitmap
import com.chetdeva.flickrit.network.dto.PhotoDto

/**
 * @author chetansachdeva
 */

interface SearchContract {

    interface View {
        fun render(state: SearchViewState)
    }

    interface Presenter : Adapter {
        fun search(query: String)
        fun loadNextPage()
    }

    interface Interactor {
        fun search(query: String, publish: (SearchViewState) -> Unit)
        fun nextPage(publish: (SearchViewState) -> Unit)
    }

    interface Adapter {
        fun downloadImage(url: String, onDownloadComplete: (Bitmap?) -> Unit)
        fun onResultClicked(photo: PhotoDto)
    }
}