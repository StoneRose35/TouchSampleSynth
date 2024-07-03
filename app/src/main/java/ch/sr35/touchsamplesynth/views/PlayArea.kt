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
import ch.sr35.touchsamplesynth.graphics.Line
import ch.sr35.touchsamplesynth.graphics.Overlap
import ch.sr35.touchsamplesynth.graphics.Point

const val CONNECTOR_LINE_BENDING = 0.25
class PlayArea(context: Context,attributeSet: AttributeSet): ConstraintLayout(context,attributeSet) {

    private val pathPaint: Paint = Paint()
    var instrumentChipContainer: LinearLayout?=null
    var touchElementsOnPointer = HashMap<TouchElement,Int>()
    var oldPositions = HashMap<Int,Point>()
    var currentPositions = HashMap<Int,Point>()
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
                touchElements.find { te -> te.isInside(Point(event.x.toDouble(),event.y.toDouble())) && !te.isEditing() }?.let {
                    touchElementsOnPointer.put(it,0)
                    oldPositions[0]=Point(event.x.toDouble(),event.y.toDouble())
                    // transpose coordinates and manually propagate actionEvent to "it"
                    event.offsetLocation(-(it.layoutParams as LayoutParams).leftMargin.toFloat(),-(it.layoutParams as LayoutParams).topMargin.toFloat())

                    it.dispatchTouchEvent(event)
                    return true
                }
            }

            MotionEvent.ACTION_POINTER_DOWN ->
            {
                val ptrIndex = event.actionIndex
                touchElements.find { te -> te.isInside(Point(event.getX(ptrIndex).toDouble(),event.getY(ptrIndex).toDouble())) && !te.isEditing()}?.let {
                    touchElementsOnPointer.put(it,event.getPointerId(ptrIndex))
                    val touchElementEvent = MotionEvent.obtain(event.downTime,
                        event.eventTime,
                        MotionEvent.ACTION_DOWN,
                        event.getX(ptrIndex),
                        event.getY(ptrIndex),
                        event.pressure,
                        event.size,
                        event.metaState,
                        event.xPrecision,
                        event.yPrecision,
                        event.deviceId,
                        event.edgeFlags)
                    oldPositions[ptrIndex] = Point(event.getX(ptrIndex).toDouble(),event.getY(ptrIndex).toDouble())
                    // transpose coordinates and manually propagate actionEvent to "it"
                    touchElementEvent.offsetLocation(-(it.layoutParams as LayoutParams).leftMargin.toFloat(),-(it.layoutParams as LayoutParams).topMargin.toFloat())
                    it.dispatchTouchEvent(touchElementEvent)
                    return true
                }
            }

            MotionEvent.ACTION_MOVE ->
            {
                val ptrIndex = event.actionIndex
                val pointerId = event.getPointerId(ptrIndex)
                if (currentPositions[ptrIndex]!=null)
                {
                    oldPositions[ptrIndex] = currentPositions[ptrIndex] as Point
                    currentPositions[ptrIndex] = Point(event.getX(ptrIndex).toDouble(),event.getY(ptrIndex).toDouble())
                }
                else
                {
                    currentPositions[ptrIndex] = Point(event.getX(ptrIndex).toDouble(),event.getY(ptrIndex).toDouble())
                }
                touchElements.firstOrNull {
                        te -> te.isInside(Point(event.getX(ptrIndex).toDouble(),event.getY(ptrIndex).toDouble())) && !te.isEditing()
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
                // find all touch element which might have been intersected by the line going from oldposition to currentposition
                touchElements.filter {
                    it.asRectangle().getIntersectingSides(Line(oldPositions[ptrIndex]!!,currentPositions[ptrIndex]!!)).size == 2
                }.forEach {
                    if (it.asRectangle().getIntersectingSides(Line(oldPositions[ptrIndex]!!,currentPositions[ptrIndex]!!)).contains(Overlap.TOP))
                    {
                        it.slideOverEvent(MotionEvent.obtain(
                            event.downTime,
                            event.eventTime,
                            MotionEvent.ACTION_DOWN,
                            ((oldPositions[ptrIndex]!!.x + currentPositions[ptrIndex]!!.x)/2).toFloat(),
                            ((it.asRectangle().topLeft.y + it.asRectangle().bottomRight.y)/2).toFloat(),
                            event.pressure,
                            event.size,
                            event.metaState,
                            event.xPrecision,
                            event.yPrecision,
                            event.deviceId,
                            event.edgeFlags))
                    }
                    else
                    {
                        it.slideOverEvent(MotionEvent.obtain(
                            event.downTime,
                            event.eventTime,
                            MotionEvent.ACTION_DOWN,
                            ((oldPositions[ptrIndex]!!.y + currentPositions[ptrIndex]!!.y)/2).toFloat(),
                            ((it.asRectangle().topLeft.x + it.asRectangle().bottomRight.x)/2).toFloat(),
                            event.pressure,
                            event.size,
                            event.metaState,
                            event.xPrecision,
                            event.yPrecision,
                            event.deviceId,
                            event.edgeFlags))
                    }
                }
            }
            MotionEvent.ACTION_UP ->
            {
                oldPositions.remove(0)
                currentPositions.remove(0)
                touchElements.find { te -> te.isInside(Point(event.x.toDouble(),event.y.toDouble()))  && !te.isEditing()}?.let {
                    touchElementsOnPointer.remove(it)
                    event.offsetLocation(-(it.layoutParams as LayoutParams).leftMargin.toFloat(),-(it.layoutParams as LayoutParams).topMargin.toFloat())
                    it.dispatchTouchEvent(event)
                    return true
                }
            }
            MotionEvent.ACTION_POINTER_UP ->
            {
                val ptrIndex = event.actionIndex
                oldPositions.remove(ptrIndex)
                currentPositions.remove(ptrIndex)
                touchElements.find { te -> te.isInside(Point(event.getX(ptrIndex).toDouble(),event.getY(ptrIndex).toDouble()))  && !te.isEditing()}?.let {
                    touchElementsOnPointer.remove(it)
                    val touchElementEvent = MotionEvent.obtain(event.downTime,
                        event.eventTime,
                        MotionEvent.ACTION_UP,
                        event.getX(ptrIndex),
                        event.getY(ptrIndex),
                        event.pressure,
                        event.size,
                        event.metaState,
                        event.xPrecision,
                        event.yPrecision,
                        event.deviceId,
                        event.edgeFlags)
                    touchElementEvent.offsetLocation(
                        -(it.layoutParams as LayoutParams).leftMargin.toFloat(),
                        -(it.layoutParams as LayoutParams).topMargin.toFloat()
                    )
                    it.dispatchTouchEvent(touchElementEvent)
                    return true
                }
            }
        }
        return super.onTouchEvent(event)
    }
    /*
    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {

        when (ev!!.action.and(MotionEvent.ACTION_MASK))
        {

            MotionEvent.ACTION_DOWN -> {
// check on which touch element (if any the action went down and store the touch element a pointer  index 0
                touchElements.find { te -> te.isInside(Point(ev.x.toDouble(),ev.y.toDouble())) && !te.isEditing() }?.let {
                    touchElementsOnPointer.put(it,0)
                    ev.offsetLocation(-(it.layoutParams as LayoutParams).leftMargin.toFloat(),-(it.layoutParams as LayoutParams).topMargin.toFloat())
                    it.dispatchTouchEvent(ev)
                    return true
                }
                return false
            }
            MotionEvent.ACTION_POINTER_DOWN -> {

                val ptrIndex = ev.actionIndex
                touchElements.find { te -> te.isInside(Point(ev.x.toDouble(),ev.y.toDouble())) && !te.isEditing()}?.let {
                    touchElementsOnPointer.put(it,ev.getPointerId(ptrIndex))
                    ev.offsetLocation(-(it.layoutParams as LayoutParams).leftMargin.toFloat(),-(it.layoutParams as LayoutParams).topMargin.toFloat())
                    it.dispatchTouchEvent(ev)
                    return true
                }
                return false
            }
            MotionEvent.ACTION_UP ->
            {
                touchElements.find { te -> te.isInside(Point(ev.x.toDouble(),ev.y.toDouble()))  && !te.isEditing()}?.let {
                    touchElementsOnPointer.remove(it)
                    ev.offsetLocation(-(it.layoutParams as LayoutParams).leftMargin.toFloat(),-(it.layoutParams as LayoutParams).topMargin.toFloat())
                    it.dispatchTouchEvent(ev)
                    return true
                }
            }
            MotionEvent.ACTION_POINTER_UP ->
            {
                touchElements.find { te -> te.isInside(Point(ev.x.toDouble(),ev.y.toDouble()))  && !te.isEditing()}?.let {
                    touchElementsOnPointer.remove(it)
                    ev.offsetLocation(-(it.layoutParams as LayoutParams).leftMargin.toFloat(),-(it.layoutParams as LayoutParams).topMargin.toFloat())

                    it.dispatchTouchEvent(ev)
                    return true
                }
                return false
            }


        }
        return false
    }
*/

}