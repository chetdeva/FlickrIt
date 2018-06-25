package com.chetdeva.flickrit.search

import android.graphics.Bitmap
import com.chetdeva.flickrit.network.dto.PhotoDto

/**
 * @author chetansachdeva
 */

interface SearchContract {

    interface View {
        fun render(state: SearchState)
    }

    interface Presenter : Adapter {
        fun search(query: String)
        fun loadNextPage()
    }

    interface Interactor {
        fun subscribe(publisher: (SearchModel) -> Unit)
        fun search(query: String)
        fun nextPage()
    }

    interface Adapter {
        fun downloadImage(url: String, onDownloadComplete: (Bitmap?) -> Unit)
        fun onResultClicked(photo: PhotoDto)
    }
}