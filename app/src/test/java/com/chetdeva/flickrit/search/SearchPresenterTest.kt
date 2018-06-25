package com.chetdeva.flickrit.search

import com.chetdeva.flickrit.capture
import com.chetdeva.flickrit.network.ImageClient
import com.chetdeva.flickrit.search.SearchContract.Interactor.Callback
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers.any
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Captor
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations

/**
 * @author chetansachdeva
 */

class SearchPresenterTest {

    private lateinit var presenter: SearchContract.Presenter

    @Mock
    lateinit var interactor: SearchContract.Interactor
    @Mock
    lateinit var imageClient: ImageClient
    @Mock
    lateinit var view: SearchContract.View

    @Captor
    private lateinit var argumentCaptor: ArgumentCaptor<Callback>

    private val searchModel = SearchModel.Init

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        presenter = SearchPresenter(interactor, imageClient, view)
    }

    @Test
    fun testCreatePresenter() {
        presenter = SearchPresenter(interactor, imageClient, view)

        verify(view).presenter = presenter
    }

    @Test
    fun testSearch() {
        presenter.search("abc")

        verify(interactor).search(anyString(), capture(argumentCaptor))
        argumentCaptor.value.publish(SearchModel.Init)

        verify(view).render(SearchState.Init)
    }
}