package com.chetdeva.flickrit.util.imagefetcher

import android.support.v4.app.FragmentActivity
import com.chetdeva.flickrit.R

/**
 * @author chetansachdeva
 */

object ImageFetcherHelper {

    private const val IMAGE_CACHE_DIR = "flickr"

    fun getFlickrImageFetcher(activity: FragmentActivity): ImageFetcher {

        val imageThumbWidth = activity.resources.getDimensionPixelSize(R.dimen.image_thumbnail_size)
        val imageThumbHeight = activity.resources.getDimensionPixelSize(R.dimen.image_thumbnail_height)

        val cacheParams = getCacheParams(activity)

        val imageFetcher = ImageFetcher.getInstance(activity, imageThumbWidth, imageThumbHeight)
        imageFetcher.setLoadingImage(R.drawable.ic_placeholder)
        imageFetcher.addImageCache(activity.supportFragmentManager, cacheParams)

        return imageFetcher
    }

    private fun getCacheParams(activity: FragmentActivity): ImageCache.ImageCacheParams {
        val cacheParams = ImageCache.ImageCacheParams(activity, IMAGE_CACHE_DIR)
        cacheParams.setMemCacheSizePercent(0.25f) // Set memory cache to 25% of app memory
        return cacheParams
    }
}