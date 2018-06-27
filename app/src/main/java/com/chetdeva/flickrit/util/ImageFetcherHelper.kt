package com.chetdeva.flickrit.util

import android.support.v4.app.FragmentActivity
import com.chetdeva.flickrit.R
import com.chetdeva.flickrit.util.imagefetcher.ImageCache
import com.chetdeva.flickrit.util.imagefetcher.ImageFetcherParams

/**
 * @author chetansachdeva
 */

object ImageFetcherHelper {

    private const val IMAGE_CACHE_DIR = "flickr"

    fun getFlickrImageFetcherParams(activity: FragmentActivity): ImageFetcherParams {
        return ImageFetcherParams(
                imageThumbWidth = activity.resources.getDimensionPixelSize(R.dimen.image_thumbnail_size),
                imageThumbHeight = activity.resources.getDimensionPixelSize(R.dimen.image_thumbnail_height),
                loadingImageRes = R.drawable.ic_placeholder,
                cacheParams = getCacheParams(activity)
        )
    }

    private fun getCacheParams(activity: FragmentActivity): ImageCache.ImageCacheParams {
        return ImageCache.ImageCacheParams(activity, IMAGE_CACHE_DIR).apply {
            setMemCacheSizePercent(0.25f)
        }
    }


}