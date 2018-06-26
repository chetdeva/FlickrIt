package com.chetdeva.flickrit.util.image

import android.graphics.Bitmap
import android.util.LruCache

/**
 * Holds objects temporarily — until the app gets killed.
 * The methods of this Cache are thread safe.
 */
class MemoryBitmapCache : BitmapCache {

    private val cache = LruCache<String, Bitmap>(CACHE_SIZE_BYTES)

    override val name: String
        get() = "Memory Cache"

    override fun containsKey(key: String): Boolean {
        synchronized(cache) {
            val existingBitmap = get(key)
            return existingBitmap != null
        }
    }

    override fun get(key: String): Bitmap? {
        return cache.get(key)
    }

    override fun save(key: String, bitmapToSave: Bitmap) {
        cache.put(key, bitmapToSave)
    }

    override fun clear() {
        cache.evictAll()
    }

    companion object {
        // Use 1/8th of the available memory for this memory cache.
        val CACHE_SIZE_BYTES: Int = (Runtime.getRuntime().maxMemory() / 1024 / 8).toInt()
        val instance: MemoryBitmapCache by lazy { MemoryBitmapCache() }
    }
}