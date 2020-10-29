package nl.michiel.zenlyinterview

import android.graphics.drawable.GradientDrawable
import androidx.annotation.ColorInt
import androidx.annotation.VisibleForTesting
import androidx.viewpager.widget.ViewPager
import timber.log.Timber

/**
 * Animate the gradient through Lab space
 *
 * The quiz stated either animate for 2 seconds,
 * or connect to [ViewPager.OnPageChangeListener.onPageScrolled].offset.
 * I wanted it to animate over multiple pages, so I combined position and offset.
 */
class GradientAnimator(
    private val gradient: GradientDrawable,
    @ColorInt private val startRgb1: Int,
    @ColorInt private val startRgb2: Int,
    @ColorInt private val endRgb1: Int,
    @ColorInt private val endRgb2: Int
) {

    private val startColor1 = ColorValue(startRgb1)
    private val startColor2 = ColorValue(startRgb2)
    private val endColor1 = ColorValue(endRgb1)
    private val endColor2 = ColorValue(endRgb2)

    init { setAnimationValue(0f) }

    fun connect(pagerView: ViewPager) {
        val pageCount = pagerView.adapter?.count ?: return
        pagerView.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageSelected(position: Int) = Unit
            override fun onPageScrollStateChanged(state: Int) = Unit

            override fun onPageScrolled(position: Int, offset: Float, pixels: Int) {
                setAnimationValue((position + offset) / (pageCount - 1))
            }
        })
    }

    @VisibleForTesting
    fun setAnimationValue(value: Float) {
        Timber.d("blend value = $value")
        val color1 = blend(startColor1, endColor1, value)
        val color2 = blend(startColor2, endColor2, value)
        gradient.colors = intArrayOf(color1, color2)
    }

    private fun blend(a: ColorValue, b: ColorValue, value: Float): Int = a.blendWithColorToRgb(b, value)
}
