package com.chetdeva.flickrit.util.scroll

import android.support.v7.widget.RecyclerView

class RecyclerViewScrollCallback(
        private val visibleThreshold: Int,
        private val layoutManager: RecyclerView.LayoutManager
) : RecyclerView.OnScrollListener() {
    // The minimum amount of items to have below your current scroll position
    // before loading more.
    // The current offset index of data you have loaded
    private var currentPage = 0
    // The total number of items in the dataset after the last load
    private var previousTotalItemCount = 0
    // True if we are still waiting for the last set of data to load.
    private var loading = true
    // Sets the starting page index
    private val startingPageIndex = 0

    lateinit var layoutManagerType: LayoutManagerType
    lateinit var onScrolled: (Int) -> Unit

    constructor(builder: Builder) : this(builder.visibleThreshold, builder.layoutManager) {
        this.layoutManagerType = builder.layoutManagerType
        this.onScrolled = builder.onScrolledListener
        if (builder.resetLoadingState) {
            resetState()
        }
    }

    // This happens many times a second during a scroll, so be wary of the code you place here.
    // We are given a few useful parameters to help us work out if we need to load some more data,
    // but first we check if we are waiting for the previous load to finish.
    override fun onScrolled(view: RecyclerView?, dx: Int, dy: Int) {
        val lastVisibleItemPosition = RecyclerViewUtil.getLastVisibleItemPosition(layoutManager, layoutManagerType)
        val totalItemCount = layoutManager.itemCount

        // If the total item count is zero and the previous isn't, assume the
        // list is invalidated and should be reset back to initial state
        if (totalItemCount < previousTotalItemCount) {
            this.currentPage = this.startingPageIndex
            this.previousTotalItemCount = totalItemCount
            if (totalItemCount == 0) {
                this.loading = true
            }
        }
        // If it’s still loading, we check to see if the dataset count has
        // changed, if so we conclude it has finished loading and update the current page
        // number and total item count.
        if (loading && totalItemCount > previousTotalItemCount) {
            loading = false
            previousTotalItemCount = totalItemCount
        }

        // If it isn’t currently loading, we check to see if we have breached
        // the visibleThreshold and need to reload more data.
        // If we do need to reload some more data, we execute onLoadMore to fetch the data.
        // threshold should reflect how many total columns there are too
        if (!loading && lastVisibleItemPosition + visibleThreshold > totalItemCount) {
            currentPage++
            onScrolled(currentPage)
            loading = true
        }
    }

    // Call this method whenever performing new searches
    private fun resetState() {
        this.currentPage = this.startingPageIndex
        this.previousTotalItemCount = 0
        this.loading = true
    }

    class Builder(val layoutManager: RecyclerView.LayoutManager) {
        var visibleThreshold = 7
        var layoutManagerType = LayoutManagerType.LINEAR
        lateinit var onScrolledListener: (Int) -> Unit
        var resetLoadingState: Boolean = false

        fun visibleThreshold(value: Int): Builder {
            visibleThreshold = value
            return this
        }

        fun onScrolledListener(value: (Int) -> Unit): Builder {
            onScrolledListener = value
            return this
        }

        fun resetLoadingState(value: Boolean): Builder {
            resetLoadingState = value
            return this
        }

        fun build(): RecyclerViewScrollCallback {
            layoutManagerType = RecyclerViewUtil.computeLayoutManagerType(layoutManager)
            visibleThreshold = RecyclerViewUtil.computeVisibleThreshold(
                    layoutManager, layoutManagerType, visibleThreshold)
            return RecyclerViewScrollCallback(this)
        }
    }
}