package nl.michiel.zenlyinterview

import android.graphics.drawable.GradientDrawable
import androidx.annotation.ColorInt
import androidx.viewpager.widget.ViewPager

class GradientAnimator(
    private val gradient: GradientDrawable,
    @ColorInt private val startColor1: Int,
    @ColorInt private val startColor2: Int,
    @ColorInt private val endColor1: Int,
    @ColorInt private val endColor2: Int
) {

    private val startHsv1 = Hsv(startColor1)
    private val startHsv2 = Hsv(startColor2)
    private val endHsv1 = Hsv(endColor1)
    private val endHsv2 = Hsv(endColor2)
    init {
        setAnimationValue(0f)
    }

    fun connect(pagerView: ViewPager) {
        val pageCount = pagerView.adapter?.count ?: return
        pagerView.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(
                position: Int, positionOffset: Float, positionOffsetPixels: Int
            ) {
                setAnimationValue(position + positionOffset / pageCount)
            }

            override fun onPageSelected(position: Int) {
            }

            override fun onPageScrollStateChanged(state: Int) {
            }
        })

    }

    private fun setAnimationValue(value: Float) {
        val color1 = blend(startHsv1, endHsv1, value)
        val color2 = blend(startHsv2, endHsv2, value)
        gradient.colors = intArrayOf(color1, color2)
    }

    private fun blend(a: Hsv, b: Hsv, value: Float): Int = a.mixToColor(b, value)
}
