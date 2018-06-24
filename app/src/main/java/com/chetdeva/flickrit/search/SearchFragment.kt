package com.chetdeva.flickrit.search

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.SearchView
import android.util.Log
import android.view.*
import android.widget.ProgressBar
import com.chetdeva.flickrit.Injector
import com.chetdeva.flickrit.R
import com.chetdeva.flickrit.util.extension.showToast
import com.chetdeva.flickrit.network.dto.PhotoDto
import com.chetdeva.flickrit.search.SearchInteractor.Companion.VISIBILE_THRESHOLD
import com.chetdeva.flickrit.search.adapter.ProgressViewHolder
import com.chetdeva.flickrit.search.adapter.SearchResultsAdapter
import com.chetdeva.flickrit.util.extension.gone
import com.chetdeva.flickrit.util.extension.isVisible
import com.chetdeva.flickrit.util.extension.visible
import com.chetdeva.flickrit.util.mainThread
import com.chetdeva.flickrit.util.scroll.RecyclerViewScrollCallback


class SearchFragment : Fragment(), SearchContract.View {

    private lateinit var adapter: SearchResultsAdapter
    private lateinit var results: RecyclerView
    private lateinit var loader: ProgressBar
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        injectDependencies()
    }

    private fun injectDependencies() {
        val interactor = Injector.provideSearchInteractor()
        val imageClient = Injector.provideImageClient()
        presenter = SearchPresenter(interactor, imageClient, this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_search, container, false)
        results = view.findViewById(R.id.results)
        loader = view.findViewById(R.id.loader)
        setupList()
        return view
    }

    private fun setupList() {
        val layoutManager = GridLayoutManager(context, MAX_GRID_SPAN_COUNT)
        layoutManager.spanSizeLookup = spanSizeLookup
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        results.layoutManager = layoutManager
        results.setHasFixedSize(true)
        adapter = SearchResultsAdapter(presenter)
        results.adapter = adapter
        results.addOnScrollListener(scrollCallback(layoutManager) {
            presenter.loadNextPage()
        })
    }

    private fun scrollCallback(layoutManager: RecyclerView.LayoutManager,
                               onScrolled: (Int) -> Unit): RecyclerViewScrollCallback {
        return RecyclerViewScrollCallback.Builder(layoutManager)
                .visibleThreshold(VISIBILE_THRESHOLD)
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
            hideScreenLoader()
            showError(state.error)
        }
        if (state.showLoader) {
            if (state.photos.isEmpty()) {
                clearSearchFocus()
                clearList()
                showScreenLoader()
            } else {
                showListLoader(state.photos)
            }
        }
        if (state.hideLoader) {
            hideScreenLoader()
            hideListLoader(state.photos)
        }
    }

    private fun showScreenLoader() {
        loader.visible()
    }

    private fun hideScreenLoader() {
        if (loader.isVisible()) {
            loader.gone()
        }
    }

    private fun clearSearchFocus() {
        searchView?.clearFocus()
    }

    private fun clearList() {
        showList(emptyList())
    }

    private fun showListLoader(photos: List<PhotoDto?>) {
        val list = photos.toMutableList()
        list.add(null)
        showList(list)
    }

    private fun showList(list: List<PhotoDto?>) {
        results.post { adapter.submitList(list) }
    }

    private fun hideListLoader(photos: List<PhotoDto?>) {
        showList(photos)
    }

    private fun showError(message: String) {
        activity?.showToast(message)
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
