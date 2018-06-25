package com.chetdeva.flickrit.search

import com.chetdeva.flickrit.network.dto.PhotoDto

/**
 * @author chetansachdeva
 */

data class SearchModel(
        val refresh: Boolean = false,
        val hideLoader: Boolean = false,
        val showLoader: Boolean = false,
        val photos: List<PhotoDto> = emptyList(),
        val query: String = "",
        val page: Int = 1,
        val error: String = ""
) {
    companion object {
        val Init = SearchModel()
    }
}
