package nl.michiel.photogrid.domain

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.URL

class ImageLoader(context: Context) {

    suspend fun get(url: String): Bitmap =
        withContext(Dispatchers.IO) {
            try {
                BitmapFactory.decodeStream(URL(url).openConnection().getInputStream())
            } catch (error: Throwable) {
                error.printStackTrace()
                throw error
            }
        }

}
