package com.chetdeva.flickrit.network.mapper

import com.chetdeva.flickrit.network.dto.PhotoDto
import com.chetdeva.flickrit.network.dto.SearchResultDto
import com.chetdeva.flickrit.network.entities.Photo
import com.chetdeva.flickrit.network.entities.SearchResponse
import com.chetdeva.flickrit.util.Mapper
import java.util.*

/**
 * map [SearchResponse] to [SearchResultDto] and back
 *
 * @author chetansachdeva
 */

class SearchMapper(
        private val photoMapper: Mapper<Photo, PhotoDto> = PhotoMapper()
) : Mapper<SearchResponse, SearchResultDto> {

    override fun mapFromEntity(entity: SearchResponse): SearchResultDto {
        val photos = entity.photos?.photo?.map { photoMapper.mapFromEntity(it) }
        return SearchResultDto(photos ?: Collections.emptyList())
    }

    override fun mapToEntity(dto: SearchResultDto): SearchResponse {
        throw UnsupportedOperationException("SearchMapper.mapToEntity not supported yet")
    }
}