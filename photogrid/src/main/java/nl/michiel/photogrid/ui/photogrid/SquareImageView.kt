package nl.michiel.photogrid.ui.photogrid

import android.content.Context
import android.util.AttributeSet
import android.widget.ImageView

class SquareImageView(
    context: Context,
    attrs: AttributeSet? = null
) : androidx.appcompat.widget.AppCompatImageView(context, attrs) {

    // override measurement to always match the height to the width
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        setMeasuredDimension(measuredWidth, measuredWidth)
    }
}