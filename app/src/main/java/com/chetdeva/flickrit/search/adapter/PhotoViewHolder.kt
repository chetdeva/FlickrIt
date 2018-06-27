package com.chetdeva.flickrit.search.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.chetdeva.flickrit.R
import com.chetdeva.flickrit.network.dto.PhotoDto
import com.chetdeva.flickrit.search.SearchContract
import com.chetdeva.flickrit.util.extension.gone
import com.chetdeva.flickrit.util.extension.visible
import com.chetdeva.flickrit.util.imagefetcher.ImageFetcher

class PhotoViewHolder(itemView: View,
                      private val imageFetcher: ImageFetcher,
                      private val adapter: SearchContract.Adapter
) : RecyclerView.ViewHolder(itemView) {

    private val title: TextView
        get() = itemView.findViewById(R.id.title)
    private val image: ImageView
        get() = itemView.findViewById(R.id.image)

    fun bind(photo: PhotoDto) {
        // set title
        if (photo.title.isNotBlank()) {
            title.visible()
            title.text = photo.title
        } else {
            title.gone()
        }
        // set image
        if (photo.url.isNotBlank()) {
            imageFetcher.loadImage(photo.url, image)
        } else {
            image.setImageResource(R.drawable.ic_placeholder)
        }

        itemView.setOnClickListener {
            adapter.onPhotoClicked(photo)
        }
    }

    companion object {
        fun create(parent: ViewGroup, imageFetcher: ImageFetcher, presenter: SearchContract.Adapter): PhotoViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val itemView = inflater.inflate(R.layout.item_photo, parent, false)
            return PhotoViewHolder(itemView, imageFetcher, presenter)
        }
    }
}