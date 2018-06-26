package com.chetdeva.flickrit.search

import com.chetdeva.flickrit.capture
import com.chetdeva.flickrit.util.Publisher
import com.chetdeva.flickrit.util.image.ImageBitmapLoader
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentCaptor
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
    private lateinit var interactor: SearchContract.Interactor
    @Mock
    private lateinit var bitmapLoader: ImageBitmapLoader
    @Mock
    private lateinit var view: SearchContract.View

    @Captor
    private lateinit var publisher: ArgumentCaptor<Publisher<SearchModel>>

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        presenter = SearchPresenter(interactor, bitmapLoader, view)
    }

    @Test
    fun testCreatePresenter() {
        // when
        presenter = SearchPresenter(interactor, bitmapLoader, view)

        // then
        verify(view).presenter = presenter
    }

    @Test
    fun testRefreshState() {
        // when
        presenter.search("abc")

        // then
        verify(interactor).search(anyString(), capture(publisher))
        publisher.value.publish(SearchModel.Init.copy(refresh = true))

        verify(view).render(SearchState.Init.copy(refresh = true))
    }

    @Test
    fun testSearch() {
        // when
        presenter.search("abc")

        // then
        verify(interactor).search(anyString(), capture(publisher))
        publisher.value.publish(SearchModel.Init)

        verify(view).render(SearchState.Init)
    }

    @Test
    fun testNextPage() {
        // when
        presenter.loadNextPage()

        // then
        verify(interactor).nextPage(capture(publisher))
        publisher.value.publish(SearchModel.Init)

        verify(view).render(SearchState.Init)
    }
}