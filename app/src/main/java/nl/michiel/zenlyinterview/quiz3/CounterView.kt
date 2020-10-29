package nl.michiel.zenlyinterview.quiz3

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
import android.view.View
import androidx.annotation.VisibleForTesting
import androidx.appcompat.widget.AppCompatTextView
import nl.michiel.zenlyinterview.R
import timber.log.Timber
import java.lang.System.currentTimeMillis
import java.util.Random
import kotlin.math.absoluteValue
import kotlin.math.roundToInt

/**
 * A counter that animates like a 'slot machine'
 *
 * I chose to subclass TextView so we can still set the value in xml,
 * but another option would be to just subclass [View] and add the styleable attributes.
 *
 * This class has a custom [onDraw] method that renders all numbers to a bitmap,
 * then we overwrite the alpha values of that bitmap to get the fading effect at top and bottom.
 * If there is a more elegant way, I'm all ears ;)
 *
 * For now we animate to a random value on touch, but public methods would work in a normal setting.
 * The VisibleForTesting is to keep the public interface public without having warnings
 */
class CounterView(
    context: Context, attrs: AttributeSet? = null
) : AppCompatTextView(context, attrs) {

    private var animationRange = 0
    private var animationDuration = 0f
    private var animationStartTime = 0L

    private var textHeight = 0
    private var outlineStrokeWidth = 0f
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
        setOnClickListener { animateTo(100 + rng.nextInt(400)) }
        // this helps the view measure correctly
        minLines = 2
        maxLines = 2
        outlineStrokeWidth = resources.getDimension(R.dimen.counterStrokeWidth)
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

        // measure the height of a number once
        val bounds = Rect()
        paint.getTextBounds("123", 0, text.length, bounds)
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

            // setup alpha gradient to make smooth transitions at top and bottom
            shaderPaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.DST_IN)
            val colors = intArrayOf(Color.TRANSPARENT, Color.WHITE, Color.WHITE)
            val points = floatArrayOf(0f, 0.4f, 1f)
            shaderPaint.shader = LinearGradient(
                0f, 0f, 0f, height.toFloat() / 2,
                colors, points, Shader.TileMode.MIRROR)
        }
    }

    override fun onDraw(canvas: Canvas?) {
        val animationTime = currentTimeMillis() - animationStartTime
        val currentValue = propagateAnimation(animationTime)
        val currentNumber = currentValue.roundToInt()
        val offset = (currentValue - currentNumber) * lineHeight

        val lineHeight = paint.fontMetrics.let { it.ascent - it.descent }.absoluteValue
        val centerY = (height + textHeight) / 2f

        backingBmp.eraseColor(0)
        drawOutlinedText(currentNumber.toString(), centerY - offset)
        drawOutlinedText((currentNumber - 1).toString(), centerY - lineHeight - offset)
        drawOutlinedText((currentNumber + 1).toString(), centerY + lineHeight - offset)
        backingCanvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), shaderPaint)

        canvas?.drawBitmap(backingBmp, 0f, 0f, paint)

        if (animationTime < animationDuration) invalidate()
    }

    private fun drawOutlinedText(text: String, centeredTextY: Float) {
        paint.style = Paint.Style.FILL
        paint.color = Color.WHITE
        backingCanvas.drawText(text, 0f, centeredTextY, paint)

        paint.style = Paint.Style.STROKE
        paint.strokeWidth = outlineStrokeWidth
        paint.color = Color.BLACK
        backingCanvas.drawText(text, 0f, centeredTextY, paint)
    }

    /**
     * returns the current animated number as a float,
     * the fractional part signifies how much this number should scroll according to the animation
     */
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