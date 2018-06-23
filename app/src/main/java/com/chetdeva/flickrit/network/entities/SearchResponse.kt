package com.chetdeva.flickrit.network.entities

import com.google.gson.annotations.SerializedName

class SearchResponse {
    @SerializedName("photos")
    var photos: Photos? = null
    @SerializedName("stat")
    var stat: String? = null
}