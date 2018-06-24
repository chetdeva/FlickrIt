package com.chetdeva.flickrit.search

import com.chetdeva.flickrit.network.ImageClient
import com.nhaarman.mockitokotlin2.mock
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner

/**
 * @author chetansachdeva
 */

@RunWith(MockitoJUnitRunner::class)
class SearchPresenterTest {

    lateinit var presenter: SearchContract.Presenter

    private val interactor: SearchContract.Interactor = mock()
    private val imageClient: ImageClient = mock()
    private val view: SearchContract.View = mock()

    @Before
    fun setUp() {
        presenter = SearchPresenter(interactor, imageClient, view)
    }

    @Test
    fun search() {
    }

    @Test
    fun loadNextPage() {
    }

    @Test
    fun downloadImage() {
    }

    @Test
    fun onResultClicked() {
    }
}

