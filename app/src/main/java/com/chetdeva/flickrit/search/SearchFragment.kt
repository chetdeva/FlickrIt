package com.chetdeva.flickrit.search

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.SearchView
import android.util.Log
import android.view.*
import android.widget.TextView
import com.chetdeva.flickrit.Injection
import com.chetdeva.flickrit.R
import com.chetdeva.flickrit.network.dto.PhotoDto
import com.chetdeva.flickrit.search.SearchInteractor.Companion.VISIBLE_THRESHOLD
import com.chetdeva.flickrit.search.adapter.ProgressViewHolder
import com.chetdeva.flickrit.search.adapter.SearchResultsAdapter
import com.chetdeva.flickrit.util.extension.*
import com.chetdeva.flickrit.util.imagefetcher.ImageFetcher
import com.chetdeva.flickrit.util.recyclerview.InfiniteScrollListener


class SearchFragment : Fragment(), SearchContract.View {

    private lateinit var adapter: SearchResultsAdapter
    private lateinit var results: RecyclerView
    private lateinit var searching: TextView
    private var searchView: SearchView? = null
    private lateinit var imageFetcher: ImageFetcher

    private var infiniteScrollListener: InfiniteScrollListener? = null

    /**
     * helper to calculate number of spans in a Grid based on a [RecyclerView] viewType
     */
    private val spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
        override fun getSpanSize(position: Int): Int {
            val viewType = adapter.getItemViewType(position)
            return when (viewType) {
                ProgressViewHolder.VIEW_TYPE -> MAX_GRID_SPAN_COUNT
                else -> 1
            }
        }
    }

    /**
     * callback submit change in [SearchView] to presenter
     */
    private var onQueryTextListener: SearchView.OnQueryTextListener? = object : SearchView.OnQueryTextListener {
        override fun onQueryTextSubmit(query: String): Boolean {
            search(query)
            return true
        }

        override fun onQueryTextChange(newText: String): Boolean {
            // do nothing
            return true
        }
    }

    override var isActive: Boolean = false
        get() = isAdded

    override lateinit var presenter: SearchContract.Presenter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_search, container, false)
        setHasOptionsMenu(true)
        with(view) {
            results = findViewById(R.id.results)
            searching = findViewById(R.id.searching)
        }
        imageFetcher = Injection.provideImageFetcher(activity!!)
        setupList()
        return view
    }

    /**
     * setup results with [SearchResultsAdapter] and register [InfiniteScrollListener] callback
     */
    private fun setupList() {
        val layoutManager = GridLayoutManager(context, MAX_GRID_SPAN_COUNT)
        layoutManager.spanSizeLookup = spanSizeLookup
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        results.layoutManager = layoutManager
        results.setHasFixedSize(true)
        adapter = SearchResultsAdapter(presenter, imageFetcher)
        results.adapter = adapter
        addScrollCallback(layoutManager, VISIBLE_THRESHOLD)
    }

    private fun addScrollCallback(gridLayoutManager: GridLayoutManager, visibleThreshold: Int) {
        infiniteScrollListener = object : InfiniteScrollListener(gridLayoutManager, visibleThreshold) {
            override fun onLoadMore() {
                presenter.loadNextPage()
            }
        }
        results.addOnScrollListener(infiniteScrollListener)
    }

    /**
     * search a query string
     */
    private fun search(query: String) {
        presenter.search(query)
    }

    /**
     * render the [SearchState] on the View
     */
    override fun render(state: SearchState) {
        Log.i("SearchFragment", "state: $state")
        if (state.refresh) {
            hideKeyboard()
            clearList()
            showScreenLoader()
            return
        }
        if (state.showLoader) {
            showLoaderAndUpdate(state.photos)
        }
        if (state.hideLoader) {
            hideScreenLoaderIfShown()
            hideLoaderAndUpdate(state.photos)
        }
        if (state.error.isNotBlank()) {
            showError(state.error)
        }
    }

    /**
     * show loader on the screen
     */
    private fun showScreenLoader() {
        searching.visible()
    }

    /**
     * hide loader from the screen
     */
    private fun hideScreenLoaderIfShown() {
        if (searching.isVisible) {
            searching.gone()
        }
    }

    /**
     * hide keyboard from the screen
     */
    private fun hideKeyboard() {
        if (searchView?.hasFocus() == true) searchView?.clearFocus()
    }

    /**
     * clear the [PhotoDto] list
     */
    private fun clearList() {
        showList(emptyList())
    }

    /**
     * show loader at the bottom of the [PhotoDto] list and update it
     */
    private fun showLoaderAndUpdate(photos: List<PhotoDto?>) {
        val list = photos.toMutableList()
        list.add(null)
        showList(list)
    }

    /**
     * submit [PhotoDto] list to [SearchResultsAdapter]. This calculates diff and reflects changes
     */
    private fun showList(list: List<PhotoDto?>) {
        results.post { adapter.submitList(list) }
    }

    /**
     * hide loader from the bottom of the [PhotoDto] list and update it
     */
    private fun hideLoaderAndUpdate(photos: List<PhotoDto?>) {
        showList(photos)
    }

    /**
     * show error via Snackbar
     */
    private fun showError(message: String) {
        results.showSnackbar(message)
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater?.inflate(R.menu.menu_search, menu)
        searchView = searchView(menu)
        searchView?.queryHint = context?.getString(R.string.search)
        searchView?.setOnQueryTextListener(onQueryTextListener)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            R.id.action_search -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun searchView(menu: Menu?): SearchView? {
        val searchItem = menu?.findItem(R.id.action_search)
        return searchItem?.actionView as? SearchView
    }

    override fun onResume() {
        super.onResume()
        presenter.start()
        imageFetcher.setExitTasksEarly(false)
    }

    override fun onPause() {
        super.onPause()
        imageFetcher.setPauseWork(false)
        imageFetcher.setExitTasksEarly(true)
        imageFetcher.flushCache()
    }

    override fun onDestroy() {
        super.onDestroy()
        imageFetcher.closeCache()
        onQueryTextListener = null
        infiniteScrollListener = null
    }

    companion object {
        fun newInstance() = SearchFragment()
        private const val MAX_GRID_SPAN_COUNT = 3
    }
}
