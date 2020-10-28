package nl.michiel.zenlyinterview

import android.graphics.drawable.GradientDrawable
import androidx.annotation.ColorInt
import androidx.viewpager.widget.ViewPager
import timber.log.Timber

class GradientAnimator(
    private val gradient: GradientDrawable,
    @ColorInt private val startColor1: Int,
    @ColorInt private val startColor2: Int,
    @ColorInt private val endColor1: Int,
    @ColorInt private val endColor2: Int
) {

    private val startHsv1 = ColorValue(startColor1)
    private val startHsv2 = ColorValue(startColor2)
    private val endHsv1 = ColorValue(endColor1)
    private val endHsv2 = ColorValue(endColor2)

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

    private fun setAnimationValue(value: Float) {
        Timber.d("blend value = $value")
        val color1 = blend(startHsv1, endHsv1, value)
        val color2 = blend(startHsv2, endHsv2, value)
        gradient.colors = intArrayOf(color1, color2)
    }

    private fun blend(a: ColorValue, b: ColorValue, value: Float): Int = a.blendWithColorToRgb(b, value)
}
