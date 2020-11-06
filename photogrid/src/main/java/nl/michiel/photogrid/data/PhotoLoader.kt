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

class PhotoLoader(context: Context) {

    private val errorBitmap =
        ContextCompat.getDrawable(context, R.drawable.load_error)?.toBitmap()
            ?: Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)

    suspend fun get(url: String): Bitmap =
        withContext(Dispatchers.IO) {
            try {
                BitmapFactory.decodeStream(URL(url).openConnection().getInputStream())
            } catch (error: Throwable) {
                error.printStackTrace()
                errorBitmap
            }
        }

}
