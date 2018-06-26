package com.chetdeva.flickrit.util.image

import android.graphics.Bitmap

/**
 * Interface for a fixed-size local storage.
 * Implemented by [MemoryBitmapCache] and [DiskBitmapCache].
 */
interface BitmapCache {

    /**
     * For debugging
     */
    val name: String

    /**
     * Whether any object with <var>key</var> exists
     */
    fun containsKey(key: String): Boolean

    /**
     * Gets the object mapped against <var>key</var>.
     */
    operator fun get(key: String): Bitmap?

    /**
     * Saves <var>bitmapToSave</var> against <var>key</var>.
     */
    fun save(key: String, bitmapToSave: Bitmap)

    /**
     * Deletes everything in this cache.
     */
    fun clear()

}