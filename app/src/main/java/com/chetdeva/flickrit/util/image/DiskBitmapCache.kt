package com.chetdeva.flickrit.util.image

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.example.android.architecture.blueprints.todoapp.util.SingletonHolderSingleArg
import java.io.*
import java.net.URLEncoder

/**
 * Persists Bitmaps in files in the cache directory (See [Context.getCacheDir]).
 */
class DiskBitmapCache(context: Context) : BitmapCache {

    private val cacheDirectory: File = context.cacheDir

    override val name: String
        get() = "Disk Cache"

    override fun containsKey(key: String): Boolean {
        synchronized(cacheDirectory) {
            val existingBitmap = get(key)
            return existingBitmap != null
        }
    }

    override fun get(key: String): Bitmap? {
        synchronized(cacheDirectory) {
            val cacheFileName = encodeKey(key)
            val foundCacheFiles = cacheDirectory.listFiles { _, filename -> filename == cacheFileName }

            return if (foundCacheFiles == null || foundCacheFiles.isEmpty()) {
                // No cached object found for this key
                null
            } else readBitmapFromFile(foundCacheFiles[0])

            // Read and return its contents
        }
    }

    override fun save(key: String, bitmapToSave: Bitmap) {
        val cacheFileName = encodeKey(key)
        val cacheFile = File(cacheDirectory, cacheFileName!!)

        try {
            val fileOutputStream = FileOutputStream(cacheFile)
            saveBitmapToFile(bitmapToSave, fileOutputStream)

        } catch (e: IOException) {
            e.printStackTrace()
        }

    }

    override fun clear() {
        synchronized(cacheDirectory) {
            val cachedFiles = cacheDirectory.listFiles()
            if (cachedFiles != null) {
                for (cacheFile in cachedFiles) {
                    cacheFile.delete()
                }
            }
            cacheDirectory.delete()
        }
    }

    /**
     * Escapes characters in a key (which may be a Url) so that it can be
     * safely used as a File name.
     *
     * This is required because otherwise keys having "\\" may be considered
     * as directory path separators.
     */
    private fun encodeKey(toEncodeString: String): String? {
        try {
            return URLEncoder.encode(toEncodeString, "UTF-8")
        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
        }
        return null
    }

    private fun readBitmapFromFile(foundCacheFile: File): Bitmap? {
        try {
            val fileInputStream = FileInputStream(foundCacheFile)
            return BitmapFactory.decodeStream(fileInputStream)

        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }
        return null
    }

    @Throws(IOException::class)
    private fun saveBitmapToFile(bitmapToSave: Bitmap, fileOutputStream: FileOutputStream) {
        fileOutputStream.use { outputStream ->
            bitmapToSave.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        }
    }

    companion object : SingletonHolderSingleArg<DiskBitmapCache, Context>(::DiskBitmapCache)
}