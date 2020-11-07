package nl.michiel.photogrid.ui.photogrid

import android.widget.ImageView
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
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
        with(viewHolder.itemView as ImageView) {
            setImageDrawable(null)
            setTag(R.id.url, photo.url)
            scope.launch {
                val bitmap = loader.get(photo.url)
                if (getTag(R.id.url) == photo.url) {
                    setImageBitmap(bitmap)
                } else {
                    Timber.d("ignored out-of-date result")
                }
            }
        }
    }
}