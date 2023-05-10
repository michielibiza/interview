package nl.michiel.photogrid.view

import android.widget.ImageView
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import nl.michiel.photogrid.R
import nl.michiel.photogrid.domain.Photo
import nl.michiel.photogrid.domain.SmartImageLoader
import timber.log.Timber

class PhotoItem(
    private val photo: Photo,
    private val loader: SmartImageLoader
) : Item() {

    override fun getLayout(): Int = R.layout.photo_grid_item

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        Timber.d("showing ${photo.url}")
        val imageView = viewHolder.itemView as ImageView
        imageView.setImageDrawable(null)
        loader.load(photo.url, imageView)
    }

}
