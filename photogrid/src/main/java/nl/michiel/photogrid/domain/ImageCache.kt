package nl.michiel.photogrid.domain

import android.graphics.Bitmap
import androidx.collection.LruCache

interface ImageCache {
    fun get(url: String): Bitmap?
    fun put(url: String, image: Bitmap)
}

class MemoryCache(sizeInItemCount: Int): ImageCache {

    private val cache = LruCache<String, Bitmap>(sizeInItemCount)

    override fun get(url: String): Bitmap? = cache.get(url)

    override fun put(url: String, image: Bitmap) {
        cache.put(url, image)
    }

}
