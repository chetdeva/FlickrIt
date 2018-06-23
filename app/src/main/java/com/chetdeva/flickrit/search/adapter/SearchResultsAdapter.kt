package com.chetdeva.flickrit.search.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import com.chetdeva.flickrit.R
import com.chetdeva.flickrit.network.dto.PhotoDto
import com.chetdeva.flickrit.search.SearchContract

/**
 * @author chetansachdeva
 */

class SearchResultsAdapter(
        private val presenter: SearchContract.Adapter
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val photos: MutableList<PhotoDto> = mutableListOf()

    override fun getItemViewType(position: Int): Int {
        return if (photos[position].id == "-1") ITEM_PROGRESS
        else super.getItemViewType(position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return if (viewType == ITEM_PROGRESS) {
            val itemView = inflater.inflate(R.layout.item_progress, parent, false)
            PhotoViewHolder(itemView)
        } else {
            val itemView = inflater.inflate(R.layout.item_search_result, parent, false)
            return PhotoViewHolder(itemView)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder.itemViewType == ITEM_PROGRESS) {
            (holder as ProgressViewHolder).bind(true)
        } else {
            (holder as PhotoViewHolder).bind(photos[position])
        }
    }

    override fun getItemCount(): Int {
        return photos.count()
    }

    /**
     * add photos of items and notify
     */
    fun addAll(users: List<PhotoDto>) {
        photos.addAll(users)
        notifyItemRangeChanged(users.size, photos.size - 1)
    }

    /**
     * add an item and notify
     */
    fun add(user: PhotoDto) {
        photos.add(user)
        notifyItemInserted(photos.size - 1)
    }

    /**
     * remove an item and notify
     */
    fun remove(position: Int) {
        photos.removeAt(position)
        notifyItemRemoved(photos.size)
    }

    /**
     * clear all items and notify
     */
    fun clear() {
        photos.clear()
        notifyDataSetChanged()
    }

    inner class PhotoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

        private val title: TextView
            get() = itemView.findViewById(R.id.title)
        private val image: ImageView
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
            presenter.onResultClicked(photos[adapterPosition])
        }
    }

    /**
     * Progress PhotoViewHolder
     */
    inner class ProgressViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val progressBar: ProgressBar
            get() = itemView.findViewById(R.id.loader)

        fun bind(isIndeterminate: Boolean) {
            progressBar.isIndeterminate = isIndeterminate
        }
    }

    companion object {
        private val ITEM_PROGRESS = -1
    }
}