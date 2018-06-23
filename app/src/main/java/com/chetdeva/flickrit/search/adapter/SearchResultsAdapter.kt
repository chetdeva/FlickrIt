package com.chetdeva.flickrit.search.adapter

import android.support.v7.recyclerview.extensions.ListAdapter
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.chetdeva.flickrit.R
import com.chetdeva.flickrit.network.dto.PhotoDto
import com.chetdeva.flickrit.search.SearchContract
import com.chetdeva.flickrit.search.adapter.PhotoItemCallBack.Companion.PHOTO_DIFF_CALLBACK

/**
 * @author chetansachdeva
 */

class SearchResultsAdapter(
        private val presenter: SearchContract.Adapter
) : ListAdapter<PhotoDto, SearchResultsAdapter.ViewHolder>(PHOTO_DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_search_result, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

        private val ViewHolder.title: TextView
            get() = itemView.findViewById(R.id.title)
        private val ViewHolder.image: ImageView
            get() = itemView.findViewById(R.id.image)

        init {
            itemView.setOnClickListener(this)
        }

        fun bind(photo: PhotoDto) {
            title.text = photo.title
            image.setImageBitmap(null)
            presenter.downloadImage(photo.url) {
                image.setImageBitmap(null)
                image.setImageBitmap(it)
            }
        }

        override fun onClick(v: View?) {
            presenter.onResultClicked(getItem(adapterPosition))
        }
    }
}