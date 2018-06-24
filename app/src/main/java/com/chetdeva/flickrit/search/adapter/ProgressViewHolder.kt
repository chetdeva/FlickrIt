package com.chetdeva.flickrit.search.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import com.chetdeva.flickrit.R

class ProgressViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val progressBar: ProgressBar
        get() = itemView.findViewById(R.id.loader)

    fun bind(isIndeterminate: Boolean) {
        progressBar.isIndeterminate = isIndeterminate
    }

    companion object {
        const val VIEW_TYPE = -1

        fun create(parent: ViewGroup): ProgressViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val itemView = inflater.inflate(R.layout.item_progress, parent, false)
            return ProgressViewHolder(itemView)
        }
    }
}