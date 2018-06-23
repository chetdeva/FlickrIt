package com.chetdeva.flickrit.search

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.SearchView
import android.util.Log
import android.view.*
import com.chetdeva.flickrit.Injector
import com.chetdeva.flickrit.R
import com.chetdeva.flickrit.extensions.showToast
import com.chetdeva.flickrit.network.dto.PhotoDto
import com.chetdeva.flickrit.search.adapter.ProgressViewHolder
import com.chetdeva.flickrit.search.adapter.SearchResultsAdapter
import com.chetdeva.flickrit.util.mainThread
import com.chetdeva.flickrit.util.scroll.RecyclerViewScrollCallback

class SearchFragment : Fragment(), SearchContract.View {

    private lateinit var adapter: SearchResultsAdapter
    private lateinit var results: RecyclerView
    private var searchView: SearchView? = null
    private lateinit var presenter: SearchContract.Presenter
    private val spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
        override fun getSpanSize(position: Int): Int {
            val viewType = adapter.getItemViewType(position)
            return when (viewType) {
                ProgressViewHolder.VIEW_TYPE -> MAX_GRID_SPAN_COUNT
                else -> 1
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        presenter = SearchPresenter(Injector.provideSearchInteractor(), this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_search, container, false)
        results = view.findViewById(R.id.results)
        setupList()
        return view
    }

    private fun setupList() {
        val layoutManager = GridLayoutManager(context, MAX_GRID_SPAN_COUNT)
        layoutManager.spanSizeLookup = spanSizeLookup
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        results.layoutManager = layoutManager
        adapter = SearchResultsAdapter(presenter)
        results.adapter = adapter
        results.addOnScrollListener(scrollCallback(layoutManager) {
            presenter.loadNextPage()
        })
    }

    private fun scrollCallback(layoutManager: RecyclerView.LayoutManager,
                               onScrolled: (Int) -> Unit): RecyclerViewScrollCallback {
        return RecyclerViewScrollCallback.Builder(layoutManager)
                .visibleThreshold(7)
                .resetLoadingState(true)
                .onScrolledListener(onScrolled)
                .build()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        search("kittens")
    }

    private fun search(query: String) {
        presenter.search(query)
    }

    override fun render(state: SearchState) = mainThread {
        Log.i("SearchFragment", "state: $state")

        if (state.error.isNotBlank()) {
            showError(state.error)
        }
        if (state.refresh) {
            clearSearchFocus()
            refreshAdapter()
        }
        if (state.showLoader) {
            showLoader()
        }
        if (state.hideLoader) {
            hideLoader()
        }
        if (state.photos.isNotEmpty()) {
            showResults(state.photos)
        }
    }

    private fun clearSearchFocus() {
        searchView?.clearFocus()
    }

    private fun refreshAdapter() {
        results.post { adapter.clearNotify() }
    }

    private fun showLoader() {
        results.post { adapter.addLoaderAtBottom() }
    }

    private fun hideLoader() {
        results.post { adapter.removeLoaderFromBottom() }
    }

    private fun showResults(photos: List<PhotoDto>) {
        results.post { adapter.addAllNotify(photos) }
    }

    private fun showError(message: String) {
        activity?.showToast(message)
    }

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

    override fun onDestroy() {
        super.onDestroy()
        onQueryTextListener = null
    }

    companion object {
        private const val MAX_GRID_SPAN_COUNT = 3
    }
}
