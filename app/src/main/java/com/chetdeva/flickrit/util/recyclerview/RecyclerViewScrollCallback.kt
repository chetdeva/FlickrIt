package com.chetdeva.flickrit.util.recyclerview

import android.support.v7.widget.RecyclerView
import com.chetdeva.flickrit.util.scroll.LayoutManagerType
import com.chetdeva.flickrit.util.scroll.RecyclerViewHelper

class RecyclerViewScrollCallback(
        private val visibleThreshold: Int,
        private val layoutManager: RecyclerView.LayoutManager
) : RecyclerView.OnScrollListener() {

    lateinit var layoutManagerType: LayoutManagerType
    lateinit var onScrolled: (Int) -> Unit

    constructor(builder: Builder) : this(builder.visibleThreshold, builder.layoutManager) {
        this.layoutManagerType = builder.layoutManagerType
        this.onScrolled = builder.onScrolledListener
    }

    override fun onScrolled(view: RecyclerView, dx: Int, dy: Int) {
        // bail out if scrolling upward or already loading data
        if (dy < 0) return

        val visibleItemCount = view.childCount
        val totalItemCount = layoutManager.itemCount
        val firstVisibleItem = RecyclerViewHelper.getFirstVisibleItemPosition(layoutManager, layoutManagerType)

        if (totalItemCount - visibleItemCount <= firstVisibleItem + visibleThreshold) {
            onScrolled(0)
        }
    }

    class Builder(val layoutManager: RecyclerView.LayoutManager) {
        var visibleThreshold = 7
        var layoutManagerType = LayoutManagerType.LINEAR
        lateinit var onScrolledListener: (Int) -> Unit

        fun visibleThreshold(value: Int): Builder {
            visibleThreshold = value
            return this
        }

        fun onScrolledListener(value: (Int) -> Unit): Builder {
            onScrolledListener = value
            return this
        }

        fun build(): RecyclerViewScrollCallback {
            layoutManagerType = RecyclerViewHelper.computeLayoutManagerType(layoutManager)
            visibleThreshold = RecyclerViewHelper.computeVisibleThreshold(layoutManager, layoutManagerType, visibleThreshold)
            return RecyclerViewScrollCallback(this)
        }
    }
}