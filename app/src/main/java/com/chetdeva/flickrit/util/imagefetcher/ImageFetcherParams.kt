package com.chetdeva.flickrit.util.imagefetcher

import android.support.annotation.DrawableRes

data class ImageFetcherParams(val imageThumbWidth: Int,
                              val imageThumbHeight: Int,
                              @DrawableRes val loadingImageRes: Int,
                              val cacheParams: ImageCache.ImageCacheParams)