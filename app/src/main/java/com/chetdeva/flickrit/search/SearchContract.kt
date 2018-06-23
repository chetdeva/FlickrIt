package com.chetdeva.flickrit.search

import android.graphics.Bitmap
import com.chetdeva.flickrit.network.dto.PhotoDto
import com.chetdeva.flickrit.network.dto.SearchResultDto
import java.util.*

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
        fun downloadImage(url: String, onDownloadComplete: (Bitmap?) -> Unit)
        fun search(query: String, publish: (SearchState) -> Unit)
        fun nextPage(publish: (SearchState) -> Unit)
    }

    interface Adapter {
        fun downloadImage(url: String, onDownloadComplete: (Bitmap?) -> Unit)
        fun onResultClicked(photo: PhotoDto)
    }
}