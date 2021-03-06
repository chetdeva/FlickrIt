package com.chetdeva.flickrit.search.adapter

import android.support.v7.recyclerview.extensions.ListAdapter
import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import com.chetdeva.flickrit.network.dto.PhotoDto
import com.chetdeva.flickrit.search.SearchContract
import com.chetdeva.flickrit.util.imagefetcher.ImageFetcher

/**
 * @author chetansachdeva
 */

class SearchResultsAdapter(
        private val adapter: SearchContract.Adapter,
        private val imageFetcher: ImageFetcher
) : ListAdapter<PhotoDto?, RecyclerView.ViewHolder>(DIFF_CALLBACK) {

    override fun getItemViewType(position: Int): Int {
        return if (null == getItem(position)) {
            ProgressViewHolder.VIEW_TYPE
        } else {
            super.getItemViewType(position)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == ProgressViewHolder.VIEW_TYPE) {
            ProgressViewHolder.create(parent)
        } else {
            PhotoViewHolder.create(parent, imageFetcher, adapter)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder.itemViewType == ProgressViewHolder.VIEW_TYPE) {
            (holder as ProgressViewHolder).bind(true)
        } else {
            (holder as PhotoViewHolder).bind(getItem(position)!!)
        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<PhotoDto?>() {

            override fun areItemsTheSame(oldItem: PhotoDto?, newItem: PhotoDto?): Boolean {
                return oldItem?.id == newItem?.id
            }

            override fun areContentsTheSame(oldItem: PhotoDto?, newItem: PhotoDto?): Boolean {
                return oldItem == newItem
            }
        }
    }
}