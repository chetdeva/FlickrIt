package com.chetdeva.flickrit.network.entities

import com.google.gson.annotations.SerializedName

class Photos {
    @SerializedName("page")
    var page: Int? = null
    @SerializedName("pages")
    var pages: Int? = null
    @SerializedName("perpage")
    var perpage: Int? = null
    @SerializedName("total")
    var total: String? = null
    @SerializedName("photo")
    var photo: List<Photo>? = null
}