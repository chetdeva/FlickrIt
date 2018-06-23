package com.chetdeva.flickrit.search.adapter

import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import com.chetdeva.flickrit.network.dto.PhotoDto
import com.chetdeva.flickrit.search.SearchContract

/**
 * @author chetansachdeva
 */

class SearchResultsAdapter(
        private val presenter: SearchContract.Adapter
) : RecyclerView.Adapter<RecyclerView.ViewHolder>(), Notifiable<PhotoDto?>, Loadable {

    private val photos: MutableList<PhotoDto?> = mutableListOf()

    override fun getItemViewType(position: Int): Int {
        return if (photos[position] == null) {
            ProgressViewHolder.VIEW_TYPE
        } else {
            super.getItemViewType(position)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == ProgressViewHolder.VIEW_TYPE) {
            ProgressViewHolder.create(parent)
        } else {
            PhotoViewHolder.create(parent, presenter)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder.itemViewType == ProgressViewHolder.VIEW_TYPE) {
            (holder as ProgressViewHolder).bind(true)
        } else {
            (holder as PhotoViewHolder).bind(photos[position]!!)
        }
    }

    override fun getItemCount(): Int {
        return photos.count()
    }

    override fun addAllNotify(list: List<PhotoDto?>) {
        this.photos.addAll(list)
        notifyItemRangeChanged(list.size, this.photos.size - 1)
    }

    override fun addNotify(item: PhotoDto?) {
        photos.add(item)
        notifyItemInserted(photos.size - 1)
    }

    override fun removeNotify(position: Int) {
        photos.removeAt(position)
        notifyItemRemoved(photos.size)
    }

    override fun clearNotify() {
        photos.clear()
        notifyDataSetChanged()
    }

    override fun addLoaderAtBottom() {
        addNotify(null)
    }

    override fun removeLoaderFromBottom() {
        if (photos.size > 0 && photos[photos.size - 1] == null) {
            removeNotify(photos.size - 1)
        }
    }
}