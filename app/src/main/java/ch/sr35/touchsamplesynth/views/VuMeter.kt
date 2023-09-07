package ch.sr35.touchsamplesynth.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import ch.sr35.touchsamplesynth.R

class VuMeter(context: Context, attributes: AttributeSet): View(context,attributes)  {

    private var level: Float = 0.0f
        set(l) {
            field = if (l < 0.0f) {
                0.0f
            } else if (l > 1.0f) {
                1.0f
            } else {
                l
            }
        }
    private val fillColor: Paint = Paint()
    private val meterColor: Paint = Paint()

    init {
        fillColor.color = context.getColor(R.color.vu_meter_background)
        fillColor.style=Paint.Style.FILL

        meterColor.color = context.getColor(R.color.vu_meter_green)
        meterColor.style = Paint.Style.FILL

    }
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        val w = width.toFloat()
        val h = height.toFloat()
        canvas?.drawRect(0.0f,0.0f,w,h,fillColor)
        canvas?.drawRect(4.0f,4.0f,(w-4.0f)*level,h-4.0f,meterColor)
    }

    fun updateVuLevel(lvl: Float)
    {
        level = lvl
        invalidate()
    }
}