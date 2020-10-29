package nl.michiel.zenlyinterview

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.appcompat.widget.AppCompatTextView
import java.lang.System.currentTimeMillis
import java.util.Random

class CounterView(
    context: Context,
    attrs: AttributeSet? = null
) : AppCompatTextView(context, attrs) {

    private var animationRange = 0
    private var animationDuration = 0f
    private var animationStartTime = 0L

    var interpolator = AccelerateDecelerateInterpolator()

    var number = 0
        set(value) {
            field = value
            stopAnimation()
            invalidate()
        }

    init {
        val rng = Random()
        setOnClickListener { animateTo(rng.nextInt(1000)) }
    }

    fun animateTo(number: Int, time: Int = 1500) {
        animationRange = number - this.number
        animationStartTime = currentTimeMillis()
        animationDuration = time.toFloat()
        invalidate()
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        number = try {
            text.toString().toInt()
        } catch (error: Throwable) {
            0
        }
    }

    private fun stopAnimation() {
        animationRange = 0
        animationStartTime
    }

    override fun onDraw(canvas: Canvas?) {
        val animationTime = currentTimeMillis() - animationStartTime
        text = if (animationTime < animationDuration) {
            val animationValue = interpolator.getInterpolation(animationTime / animationDuration)
            val amount = number + (animationRange * animationValue)
            amount.toInt().toString()
        } else {
            number += animationRange
            animationRange = 0
            number.toString()
        }

        super.onDraw(canvas)

        if (animationTime < animationDuration) invalidate()
    }

}