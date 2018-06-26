package com.chetdeva.flickrit.util.image

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import com.chetdeva.flickrit.Injection
import com.chetdeva.flickrit.network.ImageDownloader
import com.chetdeva.flickrit.util.executor.AppExecutors

/**
 * helper class for loading image bitmaps
 */

class ImageBitmapLoader(private val memoryCache: BitmapCache,
                        private val diskCache: BitmapCache,
                        private val downloader: ImageDownloader,
                        private val executors: AppExecutors
) {

    fun loadImageBitmap(url: String, onLoadComplete: (Bitmap?) -> Unit) {
        loadCachedOrDownload(url) { bitmap ->
            executors.UI.execute {
                onLoadComplete(bitmap)
            }
        }
    }

    /**
     * load from memory [BitmapCache] or disk [BitmapCache] or download from [ImageDownloader]
     * save to memory and disk if necessary
     */
    private fun loadCachedOrDownload(url: String, onLoadComplete: (Bitmap?) -> Unit) {
        Log.d(TAG, "Loading image url: $url")

        // check memory cache and return bitmap if found

        executors.diskIO.execute {
            val memoryCacheBitmap = loadFromCache(url, memoryCache)
            if (memoryCacheBitmap != null) {
                onLoadComplete(memoryCacheBitmap)
            }

            // check disk, save to memory cache and return bitmap if found
            val diskCacheBitmap = loadFromCache(url, diskCache)
            if (diskCacheBitmap != null) {
                saveToCache(url, memoryCacheBitmap, memoryCache)
                onLoadComplete(diskCacheBitmap)
            }
        }

        // download, save to memory and disk cache and return bitmap

        executors.networkIO.execute {
            downloader.downloadImage(url) { bitmap ->
                saveToCache(url, bitmap, diskCache)
                saveToCache(url, bitmap, memoryCache)
                onLoadComplete(bitmap)
            }
        }
    }

    /**
     * save [Bitmap] to [BitmapCache]
     */
    private fun saveToCache(url: String, bitmap: Bitmap?, bitmapCache: BitmapCache) {
        bitmap?.let {
            bitmapCache.save(url, bitmap)
        }
    }

    /**
     * return cached bitmap from [whichBitmapCache]
     * the returned [Bitmap] can be null if cache does not have anything to offer
     */
    private fun loadFromCache(imageUrl: String, whichBitmapCache: BitmapCache): Bitmap? {
        return whichBitmapCache[imageUrl]
    }

    /**
     * deletes all cached Bitmaps from both memory and disk cache
     */
    fun clearCache() {
        diskCache.clear()
        memoryCache.clear()
    }

    companion object {

        private const val TAG = "ImageBitmapLoader"
        private val LOCK = Any()
        @Volatile
        private var sImageBitmapLoader: ImageBitmapLoader? = null

        fun getInstance(context: Context, appExecutors: AppExecutors): ImageBitmapLoader {
            if (sImageBitmapLoader == null) {
                synchronized(LOCK) {
                    if (sImageBitmapLoader == null) {
                        sImageBitmapLoader = ImageBitmapLoader(
                                MemoryBitmapCache.instance,
                                DiskBitmapCache.getInstance(context),
                                Injection.provideImageDownloader(),
                                appExecutors
                        )
                    }
                }
            }
            return sImageBitmapLoader!!
        }
    }
}