package nl.michiel.photogrid.data

import android.graphics.Bitmap
import androidx.collection.LruCache
import kotlinx.coroutines.Deferred

interface PhotoCache {
    fun get(url: String): Bitmap?
    fun put(url: String, image: Bitmap)
}

class MemoryCache(sizeInItemCount: Int): PhotoCache {

    private val cache = LruCache<String, Bitmap>(sizeInItemCount)

    override fun get(url: String): Bitmap? = cache.get(url)

    override fun put(url: String, image: Bitmap) {
        cache.put(url, image)
    }

}