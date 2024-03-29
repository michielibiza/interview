package nl.michiel.photogrid.view

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

/**
 * adds margins to items in a grid
 */
class GridInnerMarginDecoration(
    private val margin: Int,
    private val columnCount: Int = 3
) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        outRect.set(0, 0, 0, margin)
        val position = parent.getChildLayoutPosition(view)
        val column = position % columnCount.toFloat()
        outRect.left = (margin * column / columnCount).toInt()
        outRect.right = (margin * (columnCount - column) / columnCount).toInt()
    }
}
