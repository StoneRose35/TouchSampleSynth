package ch.sr35.touchsamplesynth.views

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import ch.sr35.touchsamplesynth.R


class TouchElement(context: Context, attributes: AttributeSet): View(context,attributes) {
    private val PADDING: Float = 32.0f
    private val EDIT_CIRCLE_OFFSET = 24.0f
    enum class ActionDir
    {
        HORIZONTAL,
        VERTICAL
    }
    enum class TouchElementState
    {
        PLAYING,
        EDITING
    }

    enum class TouchElementDragAnchor
    {
        BODY,
        TOP_LEFT,
        TOP_RIGHT,
        BOTTOM_LEFT,
        BOTTOM_RIGHT
    }

    var actionDir: ActionDir = ActionDir.HORIZONTAL
    var playerId: UInt = 0u
    val blackLine: Paint = Paint()
    val fillColor: Paint = Paint()
    val blackLineFat: Paint = Paint()
    val blackFill: Paint = Paint()
    var cornerRadius = 0.0f
    lateinit var dragStart: TouchElementDragAnchor
    var px: Float = 0.0f
    var py: Float = 0.0f
    var oldWidth: Int = 0
    var oldHeight: Int = 0
    private var elementState: TouchElementState = TouchElementState.EDITING

    init {
        blackLine.color = Color.BLACK
        blackLine.strokeWidth = 7.8f
        blackLine.style = Paint.Style.STROKE
        blackLine.isAntiAlias = true

        blackFill.color = Color.BLACK
        blackFill.style = Paint.Style.FILL
        blackFill.isAntiAlias = true

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
        var arrowSize: Float = 0.0f
        // draw oval
        canvas?.drawRoundRect(0.0f+PADDING,0.0f+PADDING,w-PADDING,h-PADDING ,cornerRadius,cornerRadius,fillColor)
        canvas?.drawRoundRect(0.0f+PADDING,0.0f+PADDING,w-PADDING,h-PADDING ,cornerRadius,cornerRadius,blackLine)

        // draw action arrow
        if (actionDir == ActionDir.HORIZONTAL)
        {
            arrowSize = if (0.6f*w < 0.11f*h) {
                0.6f*w
            } else {
                0.11f * h
            }
            canvas?.drawLine(0.2f*w+PADDING,0.8f*h-PADDING,0.8f*w-PADDING,0.8f*h-PADDING,blackLineFat)
            canvas?.drawLine(0.8f*w-PADDING-arrowSize,0.8f*h-PADDING-arrowSize,0.8f*w-PADDING,0.8f*h-PADDING,blackLineFat)
            canvas?.drawLine(0.8f*w-PADDING-arrowSize,0.8f*h-PADDING+arrowSize,0.8f*w-PADDING,0.8f*h-PADDING,blackLineFat)

        }
        else
        {
            arrowSize = 0.11f*w
            canvas?.drawLine(0.8f*w-PADDING,0.8f*h-PADDING,0.8f*w-PADDING,0.2f*h+PADDING,blackLineFat)
            canvas?.drawLine(0.8f*w-PADDING-arrowSize,0.2f*h+PADDING+arrowSize,0.8f*w-PADDING,0.2f*h+PADDING,blackLineFat)
            canvas?.drawLine(0.8f*w-PADDING+arrowSize,0.2f*h+PADDING+arrowSize,0.8f*w-PADDING,0.2f*h+PADDING,blackLineFat)
        }

        if (elementState == TouchElementState.EDITING)
        {
            canvas?.drawCircle(0.0f+EDIT_CIRCLE_OFFSET,0.0f+EDIT_CIRCLE_OFFSET,EDIT_CIRCLE_OFFSET,blackFill)
            canvas?.drawCircle(w-EDIT_CIRCLE_OFFSET,0.0f+EDIT_CIRCLE_OFFSET,EDIT_CIRCLE_OFFSET,blackFill)
            canvas?.drawCircle(w-EDIT_CIRCLE_OFFSET,h-EDIT_CIRCLE_OFFSET,EDIT_CIRCLE_OFFSET,blackFill)
            canvas?.drawCircle(0.0f+EDIT_CIRCLE_OFFSET,h-EDIT_CIRCLE_OFFSET,EDIT_CIRCLE_OFFSET,blackFill)
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
        if (elementState == TouchElementState.PLAYING) {
            if (event?.action == MotionEvent.ACTION_DOWN) {
                fillColor.color =
                    context.resources.getColor(R.color.touchelement_touched, context.theme)
            } else if (event?.action == MotionEvent.ACTION_UP) {
                fillColor.color =
                    context.resources.getColor(R.color.touchelement_not_touched, context.theme)
            }
            invalidate()
            return true //super.onTouchEvent(event)
        }
        else
        {
            if (event?.action == MotionEvent.ACTION_DOWN) {
                // start a corner drag if a corner has been hit, else move the whole element
                px = event.x
                py = event.y
                val layoutParams: ConstraintLayout.LayoutParams? =  this.layoutParams as ConstraintLayout.LayoutParams?
                if (layoutParams != null) {
                    oldHeight = layoutParams.height
                    oldWidth = layoutParams.width
                }
                dragStart = isInCorner(event.x ,event.y )
            }
            else if (event?.action == MotionEvent.ACTION_UP)
            {

            }
            else if (event?.action == MotionEvent.ACTION_MOVE)
            {
                val layoutParams: ConstraintLayout.LayoutParams? =  this.layoutParams as ConstraintLayout.LayoutParams?
                when(dragStart)
                {
                    TouchElementDragAnchor.TOP_LEFT -> {
                        layoutParams!!.leftMargin += event.x.minus(px).toInt()
                        layoutParams!!.width += px.minus(event.x).toInt()
                        layoutParams!!.topMargin += event.y.minus(py).toInt()
                        layoutParams!!.height += py.minus(event.y).toInt()

                    }
                    TouchElementDragAnchor.TOP_RIGHT ->
                    {
                        layoutParams!!.width =oldWidth + event.x.minus(px).toInt()
                        layoutParams.topMargin += event.y.minus(py).toInt()
                        layoutParams.height += py.minus(event.y).toInt()
                    }
                    TouchElementDragAnchor.BOTTOM_RIGHT ->
                    {
                        layoutParams!!.width =oldWidth + event.x.minus(px).toInt()
                        layoutParams.height = oldHeight + event.y.minus(py).toInt()
                    }
                    TouchElementDragAnchor.BOTTOM_LEFT ->
                    {
                        layoutParams!!.leftMargin += event.x.minus(px).toInt()
                        layoutParams!!.width += px.minus(event.x).toInt()
                        layoutParams!!.height = oldHeight + event.y.minus(py).toInt()
                    }
                    else -> {
                        layoutParams!!.leftMargin += event.x.minus(px).toInt()
                        layoutParams!!.topMargin += event.y.minus(py).toInt()
                    }
                }
                this.layoutParams = layoutParams
            }
            return true;
        }
        //return super.onTouchEvent(event);
    }

    private fun isInCorner(x: Float, y: Float): TouchElementDragAnchor
    {
        val w = (width ?: 0).toFloat()
        val h = (height ?: 0).toFloat()
        if (((x-(0.0f+EDIT_CIRCLE_OFFSET))*(x-(0.0f+EDIT_CIRCLE_OFFSET))
                    + (y-(0.0f+EDIT_CIRCLE_OFFSET))*(y-(0.0f+EDIT_CIRCLE_OFFSET)))
            <EDIT_CIRCLE_OFFSET*EDIT_CIRCLE_OFFSET)
        {
            return TouchElementDragAnchor.TOP_LEFT
        }
        else if (((x-(w-EDIT_CIRCLE_OFFSET))*(x-(w-EDIT_CIRCLE_OFFSET))
                    + (y-(0.0f+EDIT_CIRCLE_OFFSET))*(y-(0.0f+EDIT_CIRCLE_OFFSET)))
            <EDIT_CIRCLE_OFFSET*EDIT_CIRCLE_OFFSET)
        {
            return TouchElementDragAnchor.TOP_RIGHT;
        }
        else if (((x-(w-EDIT_CIRCLE_OFFSET))*(x-(w-EDIT_CIRCLE_OFFSET))
                    + (y-(h-EDIT_CIRCLE_OFFSET))*(y-(h-EDIT_CIRCLE_OFFSET)))
            <EDIT_CIRCLE_OFFSET*EDIT_CIRCLE_OFFSET)
        {
            return TouchElementDragAnchor.BOTTOM_RIGHT;
        }
        else if (((x-(0.0f+EDIT_CIRCLE_OFFSET))*(x-(0.0f+EDIT_CIRCLE_OFFSET))
                    + (y-(h-EDIT_CIRCLE_OFFSET))*(y-(h-EDIT_CIRCLE_OFFSET)))
            <EDIT_CIRCLE_OFFSET*EDIT_CIRCLE_OFFSET)
        {
            return TouchElementDragAnchor.BOTTOM_LEFT;
        }
        else
        {
            return TouchElementDragAnchor.BODY;
        }
    }
}