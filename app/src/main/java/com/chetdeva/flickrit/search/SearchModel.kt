package com.chetdeva.flickrit.search

import com.chetdeva.flickrit.network.dto.PhotoDto

/**
 * @author chetansachdeva
 */

data class SearchModel(
        var currentPage: Int = 1,
        var lastQuery: String = "",
        var inFlight: Boolean = false,
        var photos: MutableList<PhotoDto> = mutableListOf<PhotoDto>()
) {
    companion object {
        val Init = SearchModel()
    }
}
