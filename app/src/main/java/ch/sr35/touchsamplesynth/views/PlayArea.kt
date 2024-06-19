package ch.sr35.touchsamplesynth.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.util.TypedValue
import android.view.MotionEvent
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.children
import ch.sr35.touchsamplesynth.TouchSampleSynthMain
import com.google.android.material.color.MaterialColors
import ch.sr35.touchsamplesynth.R
import ch.sr35.touchsamplesynth.graphics.Point

const val CONNECTOR_LINE_BENDING = 0.25
class PlayArea(context: Context,attributeSet: AttributeSet): ConstraintLayout(context,attributeSet) {

    private val pathPaint: Paint = Paint()
    var instrumentChipContainer: LinearLayout?=null
    var touchElementsOnPointer = HashMap<TouchElement,Int>()
    val touchElements = (context as TouchSampleSynthMain).touchElements

    private val path = Path()
    init {
        pathPaint.color = MaterialColors.getColor(this, R.attr.touchElementLinkColor)
        val typedVal = TypedValue()
        if (context.theme.resolveAttribute(R.attr.touchElementLinkSize,typedVal,false))
        {
            pathPaint.strokeWidth = typedVal.float
        }
        pathPaint.style = Paint.Style.STROKE
        pathPaint.isAntiAlias = true

    }


    override fun dispatchDraw(canvas: Canvas) {
        super.dispatchDraw(canvas)

        val appCtx = context as TouchSampleSynthMain
        if (appCtx.connectorDisplay) {
            instrumentChipContainer?.children?.filter { c ->
                c is InstrumentChip
            }?.forEach { ic ->
                appCtx.touchElements
                    .filter { te -> te.soundGenerator == (ic as InstrumentChip).getInstrument() }
                    .forEach { touchEl ->
                        path.reset()
                        val sourceX = ic.x + ic.width / 2
                        val sourceY = ic.y + ic.height
                        path.moveTo(sourceX, sourceY)
                        val targetX = touchEl.x + touchEl.width / 2
                        val targetY = touchEl.y + EDIT_CIRCLE_OFFSET
                        if (targetY < sourceY) {
                            path.lineTo(targetX, targetY)
                        } else {
                            val interm1Y = sourceY + CONNECTOR_LINE_BENDING * (targetY - sourceY)
                            val interm2Y = targetY - CONNECTOR_LINE_BENDING * (targetY - sourceY)
                            path.cubicTo(
                                sourceX,
                                interm1Y.toFloat(),
                                targetX,
                                interm2Y.toFloat(),
                                targetX,
                                targetY
                            )
                        }
                        canvas.drawPath(path, pathPaint)
                    }
            }
        }
    }

    override fun performClick(): Boolean {
        return super.performClick()
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when (event!!.action.and(MotionEvent.ACTION_MASK))
        {
            MotionEvent.ACTION_DOWN ->
            {
                performClick()
            }

            MotionEvent.ACTION_MOVE ->
            {
                val ptrIndex = event.actionIndex
                val pointerId = event.getPointerId(ptrIndex)
                touchElements.firstOrNull {
                        te -> te.isInside(Point(event.x.toDouble(),event.y.toDouble())) && !te.isEditing()
                }.let {
                    if (it != null)
                    {
                        if (touchElementsOnPointer[it] == pointerId)
                        {
                            // transpose coordinates and manually propagate actionEvent to "it"
                            event.offsetLocation(-(it.layoutParams as LayoutParams).leftMargin.toFloat(),-(it.layoutParams as LayoutParams).topMargin.toFloat())

                            it.dispatchTouchEvent(event)
                            return true
                        }
                        else
                        {
                            // has hopped from one touch element to the other

                            // find the touchElement which is associated with this pointerId,
                            // call MotionEvent move with the transposed coordinates (moves out of the area, thus switches off)
                            val teToRemove = touchElementsOnPointer.filterValues { v -> v==pointerId }.keys.firstOrNull()

                            if (teToRemove != null) {
                                event.offsetLocation(-(teToRemove.layoutParams as LayoutParams).leftMargin.toFloat(),-(teToRemove.layoutParams as LayoutParams).topMargin.toFloat())
                                teToRemove.dispatchTouchEvent(event)
                                touchElementsOnPointer.remove(teToRemove)
                            }

                            // cal MotionEvent action down with the transposed coordinates
                            event.action = MotionEvent.ACTION_DOWN
                            event.offsetLocation(-(it.layoutParams as LayoutParams).leftMargin.toFloat(),-(it.layoutParams as LayoutParams).topMargin.toFloat())
                            it.dispatchTouchEvent(event)
                            touchElementsOnPointer[it]=pointerId
                            return true
                        }
                    }
                    else
                    {
                        // find the touchElement which is associated with this pointerId,
                        // call MotionEvent move with the transposed coordinates (moves out of the area, thus switches off)
                        val teToRemove = touchElementsOnPointer.filterValues { v -> v==pointerId }.keys.firstOrNull()
                        if (teToRemove != null) {
                            event.offsetLocation(-(teToRemove.layoutParams as LayoutParams).leftMargin.toFloat(),-(teToRemove.layoutParams as LayoutParams).topMargin.toFloat())
                            teToRemove.dispatchTouchEvent(event)
                            touchElementsOnPointer.remove(teToRemove)
                            return true
                        }

                    }
                }
            }
        }
        return super.onTouchEvent(event)
    }
    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {

        when (ev!!.action.and(MotionEvent.ACTION_MASK))
        {
            MotionEvent.ACTION_DOWN -> {
// check on which touch element (if any the action went down and store the touch element a pointer  index 0
                touchElements.find { te -> te.isInside(Point(ev.x.toDouble(),ev.y.toDouble())) && !te.isEditing() }?.let {
                    touchElementsOnPointer.put(it,0)
                }
                return false
            }
            MotionEvent.ACTION_POINTER_DOWN -> {

                val ptrIndex = ev.actionIndex
                touchElements.find { te -> te.isInside(Point(ev.x.toDouble(),ev.y.toDouble())) && !te.isEditing()}?.let {
                    touchElementsOnPointer.put(it,ev.getPointerId(ptrIndex))
                }
                return false
            }
            MotionEvent.ACTION_UP ->
            {
                touchElements.find { te -> te.isInside(Point(ev.x.toDouble(),ev.y.toDouble()))  && !te.isEditing()}?.let {
                    touchElementsOnPointer.remove(it)
                }
            }
            MotionEvent.ACTION_POINTER_UP ->
            {
                touchElements.find { te -> te.isInside(Point(ev.x.toDouble(),ev.y.toDouble()))  && !te.isEditing()}?.let {
                    touchElementsOnPointer.remove(it)
                }
                return false
            }
            /*
            MotionEvent.ACTION_MOVE ->
            {
                val ptrIndex = ev.actionIndex
                val pointerId = ev.getPointerId(ptrIndex)
                touchElements.firstOrNull {
                    te -> te.isInside(Point(ev.x.toDouble(),ev.y.toDouble())) && !te.isEditing()
                }.let {
                    if (it != null)
                    {
                        if (touchElementsOnPointer[it] == pointerId)
                        {
                            // transpose coordinates and manually propagate actionEvent to "it"
                            ev.offsetLocation(-(it.layoutParams as LayoutParams).leftMargin.toFloat(),-(it.layoutParams as LayoutParams).topMargin.toFloat())

                            it.dispatchTouchEvent(ev)
                            return true
                        }
                        else
                        {
                            // has hopped from one touch element to the other

                            // find the touchElement which is associated with this pointerId,
                            // call MotionEvent move with the transposed coordinates (moves out of the area, thus switches off)
                            val teToRemove = touchElementsOnPointer.filterValues { v -> v==pointerId }.keys.firstOrNull()

                            if (teToRemove != null) {
                                ev.offsetLocation(-(teToRemove.layoutParams as LayoutParams).leftMargin.toFloat(),-(teToRemove.layoutParams as LayoutParams).topMargin.toFloat())
                                teToRemove.dispatchTouchEvent(ev)
                                touchElementsOnPointer.remove(teToRemove)
                            }

                            // cal MotionEvent action down with the transposed coordinates
                            ev.action = MotionEvent.ACTION_DOWN
                            ev.offsetLocation(-(it.layoutParams as LayoutParams).leftMargin.toFloat(),-(it.layoutParams as LayoutParams).topMargin.toFloat())
                            it.dispatchTouchEvent(ev)
                            touchElementsOnPointer[it]=pointerId
                            return true
                        }
                    }
                    else
                    {
                        // find the touchElement which is associated with this pointerId,
                        // call MotionEvent move with the transposed coordinates (moves out of the area, thus switches off)
                        val teToRemove = touchElementsOnPointer.filterValues { v -> v==pointerId }.keys.firstOrNull()
                        if (teToRemove != null) {
                            ev.offsetLocation(-(teToRemove.layoutParams as LayoutParams).leftMargin.toFloat(),-(teToRemove.layoutParams as LayoutParams).topMargin.toFloat())
                            teToRemove.dispatchTouchEvent(ev)
                            touchElementsOnPointer.remove(teToRemove)
                            return true
                        }

                    }
                }
            }*/
        }
        return false
    }


}