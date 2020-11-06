package nl.michiel.photogrid.ui.photogrid

import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import nl.michiel.photogrid.R
import nl.michiel.photogrid.data.Photo
import timber.log.Timber

class PhotoItem(private val photo: Photo) : Item() {
    override fun getLayout(): Int = R.layout.photo_grid_item
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        Timber.d("showing ${photo.url}")
    }
}
