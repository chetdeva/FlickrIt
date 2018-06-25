package com.chetdeva.flickrit.search

import com.chetdeva.flickrit.SingleExecutors
import com.chetdeva.flickrit.argumentCaptor
import com.chetdeva.flickrit.capture
import com.chetdeva.flickrit.captureValue
import com.chetdeva.flickrit.network.FlickrApiService
import com.chetdeva.flickrit.network.dto.SearchResultDto
import com.chetdeva.flickrit.network.entities.SearchResponse
import com.chetdeva.flickrit.util.Mapper
import com.chetdeva.flickrit.util.NetworkResult
import com.chetdeva.flickrit.util.Publisher
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations

/**
 * @author chetansachdeva
 */
class SearchInteractorTest {

    private lateinit var interactor: SearchContract.Interactor

    @Mock
    lateinit var apiService: FlickrApiService
    @Mock
    private lateinit var mapper: Mapper<SearchResponse, SearchResultDto>

    @Mock
    private lateinit var publisher: Publisher<SearchModel>

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        interactor = SearchInteractor(apiService, SingleExecutors(), mapper)
    }

    @Test
    fun searchForTooSmallQuery() {
        val mockQuery = "q"
        interactor.search(mockQuery, publisher)

        verify(publisher).publish(SearchModel.Init.copy(hideLoader = true, error = SearchInteractor.TOO_SMALL_QUERY_ERROR))
    }

    @Test
    fun searchForSuccess() {
        val mockQuery = "query"
        interactor.search(mockQuery, publisher)

        val apiResultCaptor = argumentCaptor<(NetworkResult<SearchResponse>) -> Unit>()

        verify(apiService).search(anyString(), anyInt(), capture(apiResultCaptor))
        apiResultCaptor.value.invoke(NetworkResult.Success(SearchResponse()))

        verify(publisher).publish(SearchModel.Init.copy(query = mockQuery))
    }

    @Test
    fun nextPageForSuccess() {
        interactor.nextPage(publisher)

        val apiResultCaptor = argumentCaptor<(NetworkResult<SearchResponse>) -> Unit>()

        verify(apiService).search(anyString(), anyInt(), capture(apiResultCaptor))
        apiResultCaptor.value.invoke(NetworkResult.Success(SearchResponse()))

        verify(publisher).publish(SearchModel.Init.copy(showLoader = true, query = ""))
    }
}