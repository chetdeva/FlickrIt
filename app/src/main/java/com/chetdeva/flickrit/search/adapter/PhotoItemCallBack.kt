package com.chetdeva.flickrit.search.adapter

import android.support.v7.util.DiffUtil
import com.chetdeva.flickrit.network.dto.PhotoDto

/**
 * @author chetansachdeva
 */

class PhotoItemCallBack : DiffUtil.ItemCallback<PhotoDto>() {

    override fun areItemsTheSame(oldItem: PhotoDto?, newItem: PhotoDto?): Boolean {
        return oldItem?.id == newItem?.id
    }

    override fun areContentsTheSame(oldItem: PhotoDto?, newItem: PhotoDto?): Boolean {
        return oldItem == newItem
    }

    companion object {
        val PHOTO_DIFF_CALLBACK by lazy { PhotoItemCallBack() }
    }
}