package nl.michiel.photogrid.ui.photogrid

import android.net.Uri
import android.widget.ImageView
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import nl.michiel.photogrid.R
import nl.michiel.photogrid.data.Photo
import nl.michiel.photogrid.data.PhotoLoader
import timber.log.Timber

class PhotoItem(
    private val photo: Photo,
    private val loader: PhotoLoader,
    private val scope: CoroutineScope
) : Item() {
    override fun getLayout(): Int = R.layout.photo_grid_item
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        Timber.d("showing ${photo.url}")
        val imageView = viewHolder.itemView as ImageView
        scope.launch {
            imageView.setImageBitmap(loader.get(photo.url))
        }
    }
}
