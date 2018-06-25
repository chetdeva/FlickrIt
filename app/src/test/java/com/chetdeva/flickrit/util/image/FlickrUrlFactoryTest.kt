package com.chetdeva.flickrit.util.image

import com.chetdeva.flickrit.network.entities.Photo
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

/**
 * @author chetansachdeva
 */
class FlickrUrlFactoryTest {

    private lateinit var urlFactory: UrlFactory<Photo>

    @Before
    fun setUp() {
        urlFactory = FlickrUrlFactory()
    }

    @Test
    fun testCreateUrl() {
        // when
        val photo = Photo()
        photo.farm = 1
        photo.server = "578"
        photo.id = "23451156376"
        photo.secret = "8983a8ebc7"

        val expected = "http://farm${photo.farm}.static.flickr.com/${photo.server}/${photo.id}_${photo.secret}.jpg"
        val actual = urlFactory.createUrl(photo)

        // then
        assertEquals(expected, actual)
    }

    @Test
    fun testNullCreateUrl() {
        // when
        val photo = Photo()
        photo.farm = null
        photo.server = "578"
        photo.id = "23451156376"
        photo.secret = "8983a8ebc7"

        val actual = urlFactory.createUrl(photo)

        // then
        assertEquals(null, actual)
    }
}