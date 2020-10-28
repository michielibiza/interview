package nl.michiel.zenlyinterview

import android.graphics.Color
import android.graphics.ColorSpace
import android.graphics.ColorSpace.Named.CIE_LAB
import android.graphics.ColorSpace.Named.SRGB
import androidx.annotation.ColorInt

private val rgbToLab = ColorSpace.connect(ColorSpace.get(SRGB), ColorSpace.get(CIE_LAB))
private val labToRgb = ColorSpace.connect(ColorSpace.get(CIE_LAB), ColorSpace.get(SRGB))

class ColorValue(@ColorInt rgb: Int) {

    private val l: Float
    private val a: Float
    private val b: Float

    init {
        val packed = Color.pack(rgb)
        val values = rgbToLab.transform(Color.red(packed), Color.green(packed), Color.blue(packed))
        l = values[0]
        a = values[1]
        b = values[2]
    }

    @ColorInt fun blendWithColorToRgb(other: ColorValue, factor: Float): Int {
        val values = labToRgb.transform(
            mix(l, other.l, factor),
            mix(a, other.a, factor),
            mix(b, other.b, factor)
        )
        return Color.rgb(values[0], values[1], values[2])
    }

    private fun mix(a: Float, b: Float, factor: Float) = a + (b - a) * factor
}
