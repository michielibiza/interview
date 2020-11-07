package nl.michiel.photogrid.data

import android.graphics.Bitmap
import kotlinx.coroutines.Deferred

class PhotoCache {
    fun get(url: String): Deferred<Bitmap>? = null
    fun put(url: String, image: Bitmap) = Unit
}
