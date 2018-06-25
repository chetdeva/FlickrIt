package com.chetdeva.flickrit.network.mapper

import com.chetdeva.flickrit.network.dto.PhotoDto
import com.chetdeva.flickrit.network.dto.SearchResultDto
import com.chetdeva.flickrit.network.entities.Photo
import com.chetdeva.flickrit.network.entities.Photos
import com.chetdeva.flickrit.network.entities.SearchResponse
import com.chetdeva.flickrit.util.Mapper
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

/**
 * @author chetansachdeva
 */
class SearchMapperTest {

    private lateinit var mapper: Mapper<SearchResponse, SearchResultDto>

    @Before
    fun setUp() {
        mapper = SearchMapper()
    }

    @Test
    fun testMapFromEntityForList() {
        // when
        val searchResponse = SearchResponse().apply {
            photos = Photos().apply {
                photo = listOf(Photo())
            }
        }

        val expected = SearchResultDto(listOf(PhotoDto()))
        val actual = mapper.mapFromEntity(searchResponse)

        // then
        assertEquals(expected, actual)
    }

    @Test
    fun testMapFromEntityForEmptyList() {
        // when
        val searchResponse = SearchResponse()

        val expected = SearchResultDto(emptyList())
        val actual = mapper.mapFromEntity(searchResponse)

        // then
        assertEquals(expected, actual)
    }
}