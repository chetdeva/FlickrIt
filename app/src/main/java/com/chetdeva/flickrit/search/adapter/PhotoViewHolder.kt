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

class PhotoViewHolder(
        itemView: View,
        private val presenter: SearchContract.Adapter
) : RecyclerView.ViewHolder(itemView) {

    private val title: TextView
        get() = itemView.findViewById(R.id.title)
    private val image: ImageView
        get() = itemView.findViewById(R.id.image)

    fun bind(photo: PhotoDto) {
        title.text = photo.title
        image.setImageBitmap(null)
        presenter.downloadImage(photo.url) {
            image.setImageBitmap(null)
            image.setImageBitmap(it)
        }

        itemView.setOnClickListener {
            presenter.onResultClicked(photo)
        }
    }

    companion object {
        fun create(parent: ViewGroup, presenter: SearchContract.Adapter): PhotoViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val itemView = inflater.inflate(R.layout.item_photo, parent, false)
            return PhotoViewHolder(itemView, presenter)
        }
    }
}