package nl.michiel.zenlyinterview

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.Rect
import android.graphics.Shader
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
    private val shaderPaint = Paint()
    private lateinit var backingBmp: Bitmap
    private lateinit var backingCanvas: Canvas

    @VisibleForTesting
    var interpolator = AccelerateDecelerateInterpolator()

    @VisibleForTesting
    var number = 0
        set(value) {
            field = value
            stopAnimation()
            invalidate()
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

    @SuppressLint("DrawAllocation")
    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        if (changed) {
            backingBmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            backingCanvas = Canvas(backingBmp)
            shaderPaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
            val colors = intArrayOf(Color.TRANSPARENT, Color.WHITE, Color.WHITE)
            val points = floatArrayOf(0f, 0.4f, 1f)
            shaderPaint.shader = LinearGradient(
                0f, 0f, 0f, height.toFloat() / 2,
                colors, points, Shader.TileMode.MIRROR)
        }
    }

    override fun onDraw(canvas: Canvas?) {
        val animationTime = currentTimeMillis() - animationStartTime
        Timber.d("$animationTime")
        val currentValue = propagateAnimation(animationTime)
        val currentNumber = currentValue.roundToInt()
        val offset = (currentValue - currentNumber) * lineHeight

        val lineHeight = paint.fontMetrics.let { it.ascent - it.descent }.absoluteValue

        val centerY = (height + textHeight) / 2f

        backingBmp.eraseColor(0)
        backingCanvas.drawColor(0)
        drawOutlinedText(currentNumber.toString(), centerY - offset)
        drawOutlinedText((currentNumber - 1).toString(), centerY - lineHeight - offset)
        drawOutlinedText((currentNumber + 1).toString(), centerY + lineHeight - offset)
        backingCanvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), shaderPaint)

        canvas?.drawColor(0)
        canvas?.drawBitmap(backingBmp, 0f, 0f, paint)

        if (animationTime < animationDuration) {
            Timber.d("$animationTime   $animationDuration")
            invalidate()
        }
    }

    private fun drawOutlinedText(text: String, centeredTextY: Float) {
        paint.style = Paint.Style.FILL
        paint.color = Color.WHITE
        backingCanvas.drawText(text, 0f, centeredTextY, paint)

        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 1f
        paint.color = Color.BLACK
        backingCanvas.drawText(text, 0f, centeredTextY, paint)
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