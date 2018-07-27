package com.chetdeva.flickrit.search

import com.chetdeva.flickrit.SingleExecutors
import com.chetdeva.flickrit.any
import com.chetdeva.flickrit.argumentCaptor
import com.chetdeva.flickrit.capture
import com.chetdeva.flickrit.network.FlickrApiService
import com.chetdeva.flickrit.network.dto.PhotoDto
import com.chetdeva.flickrit.network.dto.SearchResultDto
import com.chetdeva.flickrit.network.entities.Photo
import com.chetdeva.flickrit.network.entities.Photos
import com.chetdeva.flickrit.network.entities.SearchResponse
import com.chetdeva.flickrit.util.Mapper
import com.chetdeva.flickrit.util.NetworkResult
import com.chetdeva.flickrit.util.Publisher
import com.chetdeva.flickrit.whenever
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations


/**
 * @author chetansachdeva
 */
class SearchInteractorTest {

    private lateinit var interactor: SearchContract.Interactor

    @Mock
    private lateinit var apiService: FlickrApiService
    @Mock
    private lateinit var mapper: Mapper<SearchResponse, SearchResultDto>
    @Mock
    private lateinit var publisher: Publisher<SearchModel>

    private val mockSearchResponse: SearchResponse
        get() {
            return SearchResponse().apply {
                photos = Photos().apply {
                    photo = listOf(Photo())
                }
            }
        }

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        interactor = SearchInteractor(apiService, SingleExecutors(), mapper)
    }

    @Test
    fun searchForTooSmallQuery() {
        // given
        val mockQuery = "q"

        // when
        interactor.search(mockQuery, publisher)

        // then
        verify(publisher).publish(SearchModel.Init.copy(hideLoader = true, error = SearchInteractor.TOO_SMALL_QUERY_ERROR))
    }

    @Test
    fun searchForSuccess() {
        // given
        val mockQuery = "query"
        whenever(mapper.mapFromEntity(any())).thenReturn(SearchResultDto(listOf(PhotoDto())))

        // when
        interactor.search(mockQuery, publisher)

        val apiResultCaptor = argumentCaptor<(NetworkResult<SearchResponse>) -> Unit>()
        verify(apiService).search(anyString(), anyInt(), capture(apiResultCaptor))
        apiResultCaptor.value.invoke(NetworkResult.Success(mockSearchResponse))

        // then
        val model = SearchModel.Init.copy(refresh = true, showLoader = true, query = mockQuery)
        verify(publisher).publish(model)
        verify(publisher).publish(model.copy(refresh = false, hideLoader = true, showLoader = false, photos = listOf(PhotoDto()), page = 2))
    }

    @Test
    fun searchForError() {
        // given
        val mockQuery = "query"
        val mockError = "error"

        // when
        interactor.search(mockQuery, publisher)

        val apiResultCaptor = argumentCaptor<(NetworkResult<SearchResponse>) -> Unit>()
        verify(apiService).search(anyString(), anyInt(), capture(apiResultCaptor))
        apiResultCaptor.value.invoke(NetworkResult.Error(mockError))

        // then
        val model = SearchModel.Init.copy(refresh = true, showLoader = true, query = mockQuery)
        verify(publisher).publish(model)
        verify(publisher).publish(model.copy(refresh = false, hideLoader = true, showLoader = false, error = mockError))
    }

    @Test
    fun searchForSuccessNoMoreItems() {
        // given
        val mockQuery = "query"

        // when
        interactor.search(mockQuery, publisher)

        val apiResultCaptor = argumentCaptor<(NetworkResult<SearchResponse>) -> Unit>()
        verify(apiService).search(anyString(), anyInt(), capture(apiResultCaptor))
        apiResultCaptor.value.invoke(NetworkResult.Success(SearchResponse()))

        // then
        val model = SearchModel.Init.copy(refresh = true, showLoader = true, query = mockQuery)
        verify(publisher).publish(model)
        verify(publisher).publish(model.copy(refresh = false, hideLoader = true, showLoader = false, error = "No more items found"))
    }

    @Test
    fun nextPageForSuccess() {
        // given
        whenever(mapper.mapFromEntity(any())).thenReturn(SearchResultDto(listOf(PhotoDto())))

        // when
        interactor.nextPage(publisher)

        val apiResultCaptor = argumentCaptor<(NetworkResult<SearchResponse>) -> Unit>()
        verify(apiService).search(anyString(), anyInt(), capture(apiResultCaptor))
        apiResultCaptor.value.invoke(NetworkResult.Success(mockSearchResponse))

        // then
        val model = SearchModel.Init.copy(refresh = false, showLoader = true)
        verify(publisher).publish(model)
        verify(publisher).publish(model.copy(refresh = false, hideLoader = true, showLoader = false, photos = listOf(PhotoDto()), page = 2))
    }

    @Test
    fun nextPageForError() {
        // given
        val mockError = "error"
        // when
        interactor.nextPage(publisher)

        val apiResultCaptor = argumentCaptor<(NetworkResult<SearchResponse>) -> Unit>()
        verify(apiService).search(anyString(), anyInt(), capture(apiResultCaptor))
        apiResultCaptor.value.invoke(NetworkResult.Error(mockError))

        // then
        val model = SearchModel.Init.copy(refresh = false, showLoader = true)
        verify(publisher).publish(model)
        verify(publisher).publish(model.copy(refresh = false, hideLoader = true, showLoader = false, error = mockError))
    }
}