package nl.michiel.zenlyinterview

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.annotation.VisibleForTesting
import androidx.appcompat.widget.AppCompatTextView
import timber.log.Timber
import java.lang.System.currentTimeMillis
import java.util.Random
import kotlin.math.absoluteValue
import kotlin.math.roundToInt

class CounterView(
    context: Context, attrs: AttributeSet? = null
) : AppCompatTextView(context, attrs) {

    private var animationRange = 0
    private var animationDuration = 0f
    private var animationStartTime = 0L
    private var textHeight = 0

    @VisibleForTesting
    var interpolator = AccelerateDecelerateInterpolator()

    @VisibleForTesting
    var number = 0
        set(value) {
            field = value
            stopAnimation()
//            invalidate()
        }

    init {
        val rng = Random()
        setOnClickListener { animateTo(100 + rng.nextInt(800)) }
        minLines = 2
        maxLines = 2
    }

    @VisibleForTesting
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

        val bounds = Rect()
        paint.getTextBounds(text, 0, text.length, bounds)
        textHeight = bounds.height()
    }

    private fun stopAnimation() {
        animationRange = 0
        animationStartTime
    }

    override fun onDraw(canvas: Canvas?) {
        Timber.d(".")
        val animationTime = currentTimeMillis() - animationStartTime
        val currentValue = propagateAnimation(animationTime)
        val currentNumber = currentValue.roundToInt()
        val offset = (currentValue - currentNumber) * lineHeight

        val lineHeight = paint.fontMetrics.let { it.ascent - it.descent }.absoluteValue

        val centeredTextY = (height + textHeight) / 2f
        canvas?.drawColor(Color.LTGRAY)

        drawOutlinedText(currentNumber.toString(), canvas, centeredTextY - offset)
        drawOutlinedText((currentNumber-1).toString(), canvas, centeredTextY - lineHeight - offset)
        drawOutlinedText((currentNumber+1).toString(), canvas, centeredTextY + lineHeight - offset)

        if (animationTime < animationDuration) {
            Timber.d("$animationTime   $animationDuration")
            invalidate()
        }
    }

    private fun drawOutlinedText(text: String, canvas: Canvas?, centeredTextY: Float) {
        paint.style = Paint.Style.FILL
        paint.color = Color.WHITE
        canvas?.drawText(text, 0f, centeredTextY, paint)

        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 1f
        paint.color = Color.BLACK
        canvas?.drawText(text, 0f, centeredTextY, paint)
    }

    private fun propagateAnimation(animationTime: Long): Float {
        return if (animationTime < animationDuration) {
            val animationValue = interpolator.getInterpolation(animationTime / animationDuration)
            Timber.d("anim = $animationValue")
            val amount = number + (animationRange * animationValue)
            amount
        } else {
            if (animationRange != 0) {
                number += animationRange
            }
            animationRange = 0
            number.toFloat()
        }
    }

}