package ch.sr35.touchsamplesynth.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View


/**
 * TODO: document your custom view class.
 */
enum class WaveformType {
    SAW_TRIANGLE,
    RECTANGLE
}
class Waveform : View {

    var pulseWidth: Float = 0.0f
        set(value) {
            if (value >= -1.0f && value <= 1.0f)
            {
                field= value
                invalidate()
            }
        }
    var waveFormType: WaveformType = WaveformType.SAW_TRIANGLE
        set(value) {
            field = value
            invalidate()
        }

    private val linePaint = Paint()
    private val backgroundPaint = Paint()


    constructor(context: Context) : super(context) {
        init(null, 0)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(attrs, 0)
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    ) {
        init(attrs, defStyle)
    }

    private fun init(attrs: AttributeSet?, defStyle: Int) {
        val tv = TypedValue()
        context.theme.resolveAttribute(android.R.attr.colorBackground,tv,true)
        if (tv.isColorType)
        {
            backgroundPaint.color = tv.data
            backgroundPaint.style = Paint.Style.FILL
        }
        context.theme.resolveAttribute(android.R.attr.colorForeground,tv,true)
        if (tv.isColorType) {
            linePaint.color = tv.data
            linePaint.style = Paint.Style.STROKE
            linePaint.strokeWidth = 4.0f
            linePaint.isAntiAlias = true
        }

    }



    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val paddingLeft = paddingLeft
        val paddingTop = paddingTop
        val paddingRight = paddingRight
        val paddingBottom = paddingBottom

        val contentWidth = width - paddingLeft - paddingRight
        canvas.drawRect(0.0f,0.0f,width.toFloat(),height.toFloat(),backgroundPaint)
        if (waveFormType == WaveformType.SAW_TRIANGLE)
        {
            canvas.drawLine(paddingLeft.toFloat(),(height-paddingBottom).toFloat(),paddingLeft.toFloat() + contentWidth.toFloat()*(pulseWidth+1.0f)/2.0f,paddingTop.toFloat(),linePaint)
            canvas.drawLine(paddingLeft.toFloat() + contentWidth.toFloat()*(pulseWidth+1.0f)/2.0f,paddingTop.toFloat(),(width-paddingRight).toFloat(),(height-paddingBottom).toFloat(),linePaint)
        }
        else
        {
            canvas.drawLine(paddingLeft.toFloat(),paddingTop.toFloat(),paddingLeft.toFloat() + contentWidth.toFloat()*(pulseWidth+1.0f)/2.0f,paddingTop.toFloat(),linePaint)
            canvas.drawLine(paddingLeft.toFloat() + contentWidth.toFloat()*(pulseWidth+1.0f)/2.0f,paddingTop.toFloat(),paddingLeft.toFloat() + contentWidth.toFloat()*(pulseWidth+1.0f)/2.0f,(height - paddingBottom).toFloat(),linePaint)
            canvas.drawLine(paddingLeft.toFloat() + contentWidth.toFloat()*(pulseWidth+1.0f)/2.0f,(height - paddingBottom).toFloat(),(width - paddingRight).toFloat(),(height - paddingBottom).toFloat(),linePaint)
        }


    }
}