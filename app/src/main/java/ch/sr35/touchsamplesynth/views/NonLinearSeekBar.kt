package ch.sr35.touchsamplesynth.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.graphics.drawable.RotateDrawable
import android.graphics.drawable.VectorDrawable
import android.util.AttributeSet
import android.util.TypedValue
import android.view.MotionEvent
import android.widget.GridLayout.HORIZONTAL
import android.widget.GridLayout.VERTICAL
import android.widget.SeekBar
import androidx.core.content.res.ResourcesCompat
import ch.sr35.touchsamplesynth.R
import kotlin.math.pow

class NonLinearSeekBar: androidx.appcompat.widget.AppCompatSeekBar, SeekBar.OnSeekBarChangeListener {

    private var linearity = 1.0f
    var orientation = VERTICAL


    private var sliderCenterVal: Double = 0.0
    private var touchStartVal: Float = 0.0f
    private var sliderHeight:Int = 0
    private var sliderWidth:Int = 0
    private var lineEnd = 0.0f
    private var lineStart = 0.0f
    private var progressStart = 0
    private lateinit var sliderDrawable: Drawable
    private lateinit var sliderDrawableVertical: RotateDrawable
    private lateinit var linePaint: Paint
    private var nonLinearProgressChangeListener: OnSeekBarChangeListener?=null
    constructor(context: Context): super(context)
    {
        init(null,0)
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
        val tv= TypedValue()
        context.obtainStyledAttributes(attrs, R.styleable.NonLinearSeekBar).also {
            linearity = it.getFloat(R.styleable.Knob_linearity, 1.0f)
            orientation = it.getInt(R.styleable.NonLinearSeekBar_android_orientation, HORIZONTAL)

            it.recycle()
        }
        context.theme.resolveAttribute(R.attr.sliderDrawable,tv,true)
        sliderDrawable = ResourcesCompat.getDrawable(context.resources, tv.resourceId,context.theme)!!
        sliderWidth = (sliderDrawable as VectorDrawable).minimumWidth
        sliderHeight = (sliderDrawable as VectorDrawable).minimumHeight
        sliderDrawableVertical = RotateDrawable()
        sliderDrawableVertical.drawable = sliderDrawable

        sliderDrawableVertical.fromDegrees = 0.0f
        sliderDrawableVertical.toDegrees = 90.0f
        sliderDrawableVertical.level = 10000
        linePaint = Paint().also {
            it.style = Paint.Style.STROKE
            context.theme.resolveAttribute(R.attr.seekBarLineColor,tv,true)
            it.color = ResourcesCompat.getColor(context.resources,tv.resourceId,context.theme)
            it.strokeWidth = 3.0f
        }
        if (orientation == HORIZONTAL)
        {
            minWidth = sliderWidth
            minHeight = sliderHeight
        }
        else if (orientation == VERTICAL)
        {
            minWidth = sliderHeight
            minHeight = sliderWidth
        }
    }

    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
        nonLinearProgressChangeListener?.let {
            if (linearity == 1.0f) {
                it.onProgressChanged(this, progress, fromUser)
            }
            else
            {
                it.onProgressChanged(this, (((progress.toFloat() - min.toFloat())/(max.toFloat() - min.toFloat())).pow(linearity)*(max - min) + min) .toInt(), fromUser)
            }
        }
    }

    override fun setOnSeekBarChangeListener(l: OnSeekBarChangeListener?) {
        nonLinearProgressChangeListener = l
    }


    override fun onDraw(canvas: Canvas) {
        if (orientation == HORIZONTAL) {
            val lineYPos = height / 2.0f
            lineStart = sliderWidth/2.0f
            lineEnd = width - sliderWidth/2.0f
            if (lineStart < lineEnd)
            {
                canvas.drawLine(lineStart, lineYPos, lineEnd, lineYPos, linePaint)
                sliderCenterVal = (progress.toFloat() - min.toFloat())/(max.toFloat() - min.toFloat())*(lineEnd.toDouble() - lineStart.toDouble()) + lineStart.toDouble()
                sliderDrawable.setBounds(
                    sliderCenterVal.toInt() - sliderWidth/2,
                    lineYPos.toInt() - sliderHeight/2,
                    sliderCenterVal.toInt() + sliderWidth/2,
                    lineYPos.toInt() + sliderHeight/2)
                sliderDrawable.draw(canvas)
            }
        }
        else
        {
            val lineXPos = width / 2.0f
            lineStart =sliderWidth/2.0f
            lineEnd = height - sliderWidth/2.0f
            if (lineStart < lineEnd)
            {
                canvas.drawLine(lineXPos, lineStart, lineXPos, lineEnd, linePaint)
                sliderCenterVal = (min - progress.toFloat())/(max.toFloat() - min.toFloat())*(lineEnd.toDouble() - lineStart.toDouble()) + lineEnd.toInt()
                sliderDrawable.setBounds(
                    lineXPos.toInt() - sliderWidth/2,
                    sliderCenterVal.toInt() - sliderHeight/2,
                    lineXPos.toInt() + sliderWidth/2,
                    sliderCenterVal.toInt() + sliderHeight/2)
                sliderDrawableVertical.draw(canvas)
            }
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when(event?.action)
        {
            MotionEvent.ACTION_DOWN ->
            {
                if (orientation == HORIZONTAL && sliderDrawable.bounds.contains(event.x.toInt(),event.y.toInt()))
                {
                    touchStartVal = event.x
                    progressStart = progress
                    parent.requestDisallowInterceptTouchEvent(true)
                    return true
                }
                else if (orientation == VERTICAL && sliderDrawableVertical.bounds.contains(event.x.toInt(),event.y.toInt()))
                {
                    touchStartVal = event.y
                    progressStart = progress
                    parent.requestDisallowInterceptTouchEvent(true)
                    return true
                }
                touchStartVal = -1.0f
            }
            MotionEvent.ACTION_MOVE ->
            {
                if (touchStartVal >= 0.0f)
                {
                    if (orientation == HORIZONTAL)
                    {

                        progress = progressStart + ((event.x - touchStartVal)/(lineEnd - lineStart)*(max-min)).toInt()
                        nonLinearProgressChangeListener?.onProgressChanged(this,progress,true)
                        postInvalidate()
                    }
                    else
                    {
                        progress = progressStart - ((event.y - touchStartVal)/(lineEnd - lineStart)*(max-min)).toInt()
                        nonLinearProgressChangeListener?.onProgressChanged(this,progress,true)
                        postInvalidate()
                    }
                    return true
                }

            }
            MotionEvent.ACTION_UP -> {
                if (touchStartVal >= 0.0f) {
                    parent.requestDisallowInterceptTouchEvent(false)
                    return true
                }
            }
        }
        return super.onTouchEvent(event)
    }

    override fun getMinWidth(): Int {
        if (orientation == HORIZONTAL)
        {
            return sliderWidth
        }
        else
        {
            return sliderHeight
        }
    }

    override fun getMinHeight(): Int {
        if (orientation == HORIZONTAL)
        {
            return sliderHeight
        }
        else
        {
            return sliderWidth
        }
    }

    override fun onStartTrackingTouch(seekBar: SeekBar?) {
    }

    override fun onStopTrackingTouch(seekBar: SeekBar?) {
    }


}