package com.chetdeva.flickrit.util.image

import com.chetdeva.flickrit.network.entities.Photo
import java.util.*

/**
 * create [String] url from [Photo] object
 *
 * @author chetansachdeva
 */

class FlickrUrlFactory : UrlFactory<Photo> {

    companion object {
        /**
         * Flickr image url pattern http://farm{farm}.static.flickr.com/{server}/{id}_{secret}.jpg
         */
        private const val FLICKR_IMAGE_URL_FORMAT = "http://farm%1\$s.static.flickr.com/%2\$s/%3\$s_%4\$s.jpg"
    }

    override fun createUrl(photo: Photo): String? {
        if (photo.farm != null && !photo.server.isNullOrBlank() &&
                !photo.id.isNullOrBlank() && !photo.secret.isNullOrBlank()) {
            val farm = photo.farm!!
            val server = photo.server!!
            val id = photo.id!!
            val secret = photo.secret!!
            return String.format(Locale.getDefault(), FLICKR_IMAGE_URL_FORMAT,
                    farm, server, id, secret)
        }
        return null
    }
}