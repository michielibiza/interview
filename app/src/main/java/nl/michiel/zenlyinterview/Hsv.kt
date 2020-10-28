package nl.michiel.zenlyinterview

import android.graphics.Color
import androidx.annotation.ColorInt

class Hsv(@ColorInt rgba: Int) {

    private val h: Float
    private val s: Float
    private val v: Float

    init {
        val hsv = FloatArray(3)
        Color.colorToHSV(rgba, hsv)
        h = hsv[0]
        s = hsv[1]
        v = hsv[2]
    }

    fun mixToColor(other: Hsv, factor: Float): Int {
        val hueDiff = h.degreesTo(other.h)
        val mixed = floatArrayOf(
            h + hueDiff * factor,
            mix(s, other.s, factor),
            mix(v, other.v, factor)
        )
        return Color.HSVToColor(mixed)
    }

    private fun mix(a: Float, b: Float, factor: Float) = a + (b - a) * factor
}

private fun Float.degreesTo(other: Float): Float {
    val diff = other - this
    if (diff > 180) return 360 - diff
    if (diff < -180) return -360 - diff
    return diff
}
