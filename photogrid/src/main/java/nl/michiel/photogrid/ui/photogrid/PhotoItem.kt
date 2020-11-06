package nl.michiel.photogrid.ui.photogrid

import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import nl.michiel.photogrid.R
import nl.michiel.photogrid.data.Photo

class PhotoItem(photo: Photo) : Item() {
    override fun getLayout(): Int = R.layout.photo_grid_item
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
    }
}
