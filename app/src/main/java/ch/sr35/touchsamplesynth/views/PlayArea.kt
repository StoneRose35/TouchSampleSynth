package ch.sr35.touchsamplesynth.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.util.Log
import android.util.TypedValue
import android.view.MotionEvent
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.children
import ch.sr35.touchsamplesynth.TouchSampleSynthMain
import com.google.android.material.color.MaterialColors
import ch.sr35.touchsamplesynth.R
import ch.sr35.touchsamplesynth.graphics.Point
import ch.sr35.touchsamplesynth.TAG
import ch.sr35.touchsamplesynth.graphics.Line
import ch.sr35.touchsamplesynth.graphics.Overlap

const val CONNECTOR_LINE_BENDING = 0.25
class PlayArea(context: Context,attributeSet: AttributeSet): ConstraintLayout(context,attributeSet) {

    private val pathPaint: Paint = Paint()
    var instrumentChipContainer: LinearLayout?=null
    var touchElementsOnPointer = HashMap<Int,TouchElement>()
    var pointerIds = ArrayList<Int>()
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
        when (event!!.actionMasked)
        {

            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_POINTER_DOWN -> {
                val ptrIndex = event.actionIndex
                pointerIds.add(event.getPointerId(event.actionIndex))
                currentPositions[event.getPointerId(event.actionIndex)] = Point(event.getX(ptrIndex).toDouble(),event.getY(ptrIndex).toDouble())
                touchElements.find { te -> te.isInside(Point(event.getX(ptrIndex).toDouble(),event.getY(ptrIndex).toDouble())) && !te.isEditing() && !touchElementsOnPointer.containsValue(te)}?.let {
                    touchElementsOnPointer[event.getPointerId(ptrIndex)] = it
                    event.offsetLocation(-(it.layoutParams as LayoutParams).leftMargin.toFloat(),-(it.layoutParams as LayoutParams).topMargin.toFloat())
                    Log.i(TAG,"onTouchEvent, got event ${motionEventTypeToString(event)}")
                    Log.i(TAG,"added Pointer $event.getPointerId(event.actionIndex)")
                    it.dispatchTouchEvent(event)
                    return true
                }
                Log.i(TAG,"onTouchEvent, got event ${motionEventTypeToString(event)}")
                return true
            }

            MotionEvent.ACTION_MOVE ->
            {
                var processedMoveEvent=false
                for (pid in pointerIds) {

                    val pidx = event.findPointerIndex(pid)
                    if (pidx >= 0)
                    {
                        val associatedTouchElement = touchElementsOnPointer[pid]
                        var copiedEvent = MotionEvent.obtain(event.downTime,event.eventTime,event.action,event.getX(pidx),event.getY(pidx),event.metaState)

                        if (associatedTouchElement != null) {


                            copiedEvent.offsetLocation(
                                -(associatedTouchElement.layoutParams as LayoutParams).leftMargin.toFloat(),
                                -(associatedTouchElement.layoutParams as LayoutParams).topMargin.toFloat()
                            )
                            associatedTouchElement.dispatchTouchEvent(copiedEvent)
                            //Log.i(TAG,"dispatched to $associatedTouchElement")
                            copiedEvent = MotionEvent.obtain(event.downTime,event.eventTime,event.action,event.getX(pidx),event.getY(pidx),event.metaState)

                            processedMoveEvent = true
                            if (!associatedTouchElement.isInside(
                                    Point(
                                        copiedEvent.x.toDouble(),
                                        copiedEvent.y.toDouble()
                                    )
                                )
                            ) {
                                Log.i(TAG,"Moved out of touchelement: $associatedTouchElement")
                                touchElementsOnPointer.remove(pid)
                                touchElements.find { te ->
                                    te.isInside(
                                        Point(
                                            copiedEvent.x.toDouble(),
                                            copiedEvent.y.toDouble()
                                        )
                                    ) && !te.isEditing() && !touchElementsOnPointer.containsValue(te)
                                }?.let {
                                    touchElementsOnPointer[pid] = it
                                    copiedEvent.offsetLocation(
                                        -(it.layoutParams as LayoutParams).leftMargin.toFloat(),
                                        -(it.layoutParams as LayoutParams).topMargin.toFloat()
                                    )
                                    copiedEvent.action=MotionEvent.ACTION_DOWN
                                    it.dispatchTouchEvent(copiedEvent)
                                    Log.i(TAG,"found new touchelement: $it")
                                }
                            }
                        } else {
                            touchElements.find { te ->
                                te.isInside(
                                    Point(
                                        copiedEvent.x.toDouble(),
                                        copiedEvent.y.toDouble()
                                    )
                                ) && !te.isEditing() && !touchElementsOnPointer.containsValue(te)
                            }?.let {
                                Log.i(TAG,"slided into new touchelement: $it")
                                touchElementsOnPointer[pid] = it
                                copiedEvent.offsetLocation(
                                    -(it.layoutParams as LayoutParams).leftMargin.toFloat(),
                                    -(it.layoutParams as LayoutParams).topMargin.toFloat()
                                )
                                copiedEvent.action = MotionEvent.ACTION_DOWN
                                it.dispatchTouchEvent(copiedEvent)
                                processedMoveEvent = true
                            }
                        }

                        copiedEvent = MotionEvent.obtain(event.downTime,event.eventTime,event.action,event.getX(pidx),event.getY(pidx),event.metaState)
                        if (currentPositions[pid] != null) {
                            oldPositions[pid] = currentPositions[pid] as Point
                            currentPositions[pid] =
                                Point(copiedEvent.x.toDouble(), copiedEvent.y.toDouble())
                        } else {
                            currentPositions[pid] =
                                Point(copiedEvent.x.toDouble(), copiedEvent.y.toDouble())
                        }

                        val touchElementsSlidedOver = touchElements.filter { slidingOVerCandidate ->
                            if (oldPositions[pid] != null && currentPositions[pid] != null) {
                                return@filter slidingOVerCandidate.asRectangle().getIntersectingSides(
                                    Line(
                                        oldPositions[pid]!!,
                                        currentPositions[pid]!!
                                    )
                                ).size == 2
                            }
                            return@filter false
                        }
                        touchElementsSlidedOver.forEach {
                            Log.i(TAG, "Slideover Event for $it")
                            if (it.asRectangle().getIntersectingSides(
                                    Line(
                                        oldPositions[pid]!!,
                                        currentPositions[pid]!!
                                    )
                                ).contains(Overlap.TOP)
                            ) {
                                it.slideOverEvent(
                                    MotionEvent.obtain(
                                        event.downTime,
                                        event.eventTime,
                                        MotionEvent.ACTION_DOWN,
                                        ((oldPositions[pid]!!.x + currentPositions[pid]!!.x) / 2).toFloat(),
                                        ((it.asRectangle().topLeft.y + it.asRectangle().bottomRight.y) / 2).toFloat(),
                                        event.pressure,
                                        event.size,
                                        event.metaState,
                                        event.xPrecision,
                                        event.yPrecision,
                                        event.deviceId,
                                        event.edgeFlags
                                    )
                                )
                            } else {
                                it.slideOverEvent(
                                    MotionEvent.obtain(
                                        event.downTime,
                                        event.eventTime,
                                        MotionEvent.ACTION_DOWN,
                                        ((oldPositions[pid]!!.y + currentPositions[pid]!!.y) / 2).toFloat(),
                                        ((it.asRectangle().topLeft.x + it.asRectangle().bottomRight.x) / 2).toFloat(),
                                        event.pressure,
                                        event.size,
                                        event.metaState,
                                        event.xPrecision,
                                        event.yPrecision,
                                        event.deviceId,
                                        event.edgeFlags
                                    )
                                )
                            }
                        }
                    }

                }
                return processedMoveEvent
            }
            MotionEvent.ACTION_UP,MotionEvent.ACTION_POINTER_UP ->
            {
                val ptrId = event.getPointerId(event.actionIndex)
                pointerIds.remove(ptrId)
                oldPositions.remove(ptrId)
                currentPositions.remove(ptrId)
                val associatedTouchElement = touchElementsOnPointer[event.getPointerId(event.actionIndex)]
                if (associatedTouchElement!=null)
                {
                    event.offsetLocation(-(associatedTouchElement.layoutParams as LayoutParams).leftMargin.toFloat(),-(associatedTouchElement.layoutParams as LayoutParams).topMargin.toFloat())
                    associatedTouchElement.dispatchTouchEvent(event)
                    touchElementsOnPointer.remove(event.getPointerId(event.actionIndex))
                    Log.i(TAG,"removed Pointer $event.getPointerId(event.actionIndex)")
                    return true
                }
            }
        }
        return super.onTouchEvent(event)
    }


    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {

        when (ev!!.action.and(MotionEvent.ACTION_MASK))
        {
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_POINTER_DOWN -> {
                val ptrIndex = ev.actionIndex
                pointerIds.add(ev.getPointerId(ev.actionIndex))
                currentPositions[ev.getPointerId(ev.actionIndex)] = Point(ev.getX(ptrIndex).toDouble(),ev.getY(ptrIndex).toDouble())
                touchElements.find { te -> te.isInside(Point(ev.getX(ptrIndex).toDouble(),ev.getY(ptrIndex).toDouble())) && !te.isEditing() && !touchElementsOnPointer.containsValue(te)}?.let {
                    touchElementsOnPointer[ev.getPointerId(ptrIndex)] = it
                    ev.offsetLocation(-(it.layoutParams as LayoutParams).leftMargin.toFloat(),-(it.layoutParams as LayoutParams).topMargin.toFloat())
                    Log.i(TAG,"Interceptor, got event ${motionEventTypeToString(ev)}")
                    //Log.i(TAG,"added Pointer $ev.getPointerId(ev.actionIndex)")
                    it.dispatchTouchEvent(ev)
                    return true
                }
                Log.i(TAG,"Interceptor, got event ${motionEventTypeToString(ev)}")
                return false
            }
            else -> {
                Log.i(TAG,"intercepting other event than action_down or action_pointer_down: ${ev.actionMasked}")
            }

        }
        return false
    }

    companion object  {

        fun motionEventTypeToString(event: MotionEvent): String
        {
            when(event.actionMasked)
            {
                MotionEvent.ACTION_DOWN -> {
                    return "MotionEvent.ACTION_DOWN"
                }
                MotionEvent.ACTION_POINTER_DOWN -> {
                    return "MotionEvent.ACTION_POINTER_DOWN"
                }
                MotionEvent.ACTION_UP -> {
                    return "MotionEvent.ACTION_UP"
                }
                MotionEvent.ACTION_POINTER_UP -> {
                    return "MotionEvent.ACTION_POINTER_UP"
                }
            }
            return ""
        }
    }
}