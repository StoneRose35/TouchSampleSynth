package ch.sr35.touchsamplesynth.views

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import ch.sr35.touchsamplesynth.R


class TouchElement(context: Context, attributes: AttributeSet): View(context,attributes) {
    private val PADDING: Float = 6.0f
    enum class ActionDir
    {
        HORIZONTAL,
        VERTICAL
    }
    var actionDir: ActionDir = ActionDir.HORIZONTAL
    var playerId: UInt = 0u
    val blackLine: Paint = Paint()
    val fillColor: Paint = Paint()
    val blackLineFat: Paint = Paint()
    var cornerRadius = 0.0f

    init {
        blackLine.color = Color.BLACK
        blackLine.strokeWidth = 7.8f
        blackLine.style = Paint.Style.STROKE
        blackLine.isAntiAlias = true

        blackLineFat.color = Color.BLACK
        blackLineFat.strokeWidth = 12.0f
        blackLineFat.style = Paint.Style.STROKE
        blackLineFat.strokeCap = Paint.Cap.ROUND
        blackLineFat.isAntiAlias = true

        fillColor.color = getContext().resources.getColor(R.color.touchelement_not_touched,getContext().theme)
        fillColor.style = Paint.Style.FILL

    }
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        val w = (width ?: 0).toFloat()
        val h = (height ?: 0).toFloat()
        // draw oval
        canvas?.drawRoundRect(0.0f+PADDING,0.0f+PADDING,w-PADDING,h-PADDING ,cornerRadius,cornerRadius,fillColor)
        canvas?.drawRoundRect(0.0f+PADDING,0.0f+PADDING,w-PADDING,h-PADDING ,cornerRadius,cornerRadius,blackLine)

        // draw action arrow
        if (actionDir == ActionDir.HORIZONTAL)
        {
            canvas?.drawLine(0.2f*w,0.8f*h,0.8f*w,0.8f*h,blackLineFat)
            canvas?.drawLine(0.8f*w-cornerRadius,0.8f*h-cornerRadius,0.8f*w,0.8f*h,blackLineFat)
            canvas?.drawLine(0.8f*w-cornerRadius,0.8f*h+cornerRadius,0.8f*w,0.8f*h,blackLineFat)
        }
        else
        {
            canvas?.drawLine(0.8f*w,0.8f*h,0.8f*w,0.2f*h,blackLineFat)
            canvas?.drawLine(0.8f*w-cornerRadius,0.2f*h+cornerRadius,0.8f*w,0.2f*h,blackLineFat)
            canvas?.drawLine(0.8f*w+cornerRadius,0.2f*h+cornerRadius,0.8f*w,0.2f*h,blackLineFat)
        }

    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {

    if (w > h)
    {
        cornerRadius =  (h/8.0).toFloat()
    }
    else
    {
        cornerRadius = (w/8.0).toFloat()
    }
        super.onSizeChanged(w, h, oldw, oldh)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {

        if (event?.action == MotionEvent.ACTION_DOWN) {
            fillColor.color = context.resources.getColor(R.color.touchelement_touched, context.theme)
        }
        else if (event?.action == MotionEvent.ACTION_UP)
        {
            fillColor.color = context.resources.getColor(R.color.touchelement_not_touched, context.theme)
        }
        invalidate()
        return true //super.onTouchEvent(event)
    }
}