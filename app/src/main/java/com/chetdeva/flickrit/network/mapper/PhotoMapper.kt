package com.chetdeva.flickrit.network.mapper

import com.chetdeva.flickrit.network.dto.PhotoDto
import com.chetdeva.flickrit.network.entities.Photo
import com.chetdeva.flickrit.util.image.FlickrUrlFactory
import com.chetdeva.flickrit.util.Mapper
import com.chetdeva.flickrit.util.image.UrlFactory

/**
 * @author chetansachdeva
 */

class PhotoMapper(
        private val urlFactory: UrlFactory<Photo> = FlickrUrlFactory()
) : Mapper<Photo, PhotoDto> {

    override fun mapFromEntity(photo: Photo): PhotoDto {
        return PhotoDto(id = photo.id ?: "",
                title = photo.title ?: "",
                url = urlFactory.create(photo) ?: "")
    }

    override fun mapToEntity(dto: PhotoDto): Photo {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}