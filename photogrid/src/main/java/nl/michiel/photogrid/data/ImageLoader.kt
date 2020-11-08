package nl.michiel.photogrid.data

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import nl.michiel.photogrid.R
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
