package com.chetdeva.flickrit.search

import com.chetdeva.flickrit.network.dto.PhotoDto

/**
 * @author chetansachdeva
 */

data class SearchState(val refresh: Boolean = false,
                       val showLoader: Boolean = false,
                       val hideLoader: Boolean = false,
                       val photos: List<PhotoDto> = emptyList(),
                       val error: String = "") {

    companion object {
        val Init = SearchState()
    }
}