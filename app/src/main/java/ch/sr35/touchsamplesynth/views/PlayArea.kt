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
import androidx.appcompat.widget.SwitchCompat
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.children
import androidx.core.view.size
import ch.sr35.touchsamplesynth.TouchSampleSynthMain
import com.google.android.material.color.MaterialColors
import ch.sr35.touchsamplesynth.R
import ch.sr35.touchsamplesynth.graphics.Point
import ch.sr35.touchsamplesynth.TAG
import ch.sr35.touchsamplesynth.TouchElementSelectedListener
import ch.sr35.touchsamplesynth.graphics.Line
import ch.sr35.touchsamplesynth.graphics.Overlap

const val CONNECTOR_LINE_BENDING = 0.25
class PlayArea(context: Context,attributeSet: AttributeSet): ConstraintLayout(context,attributeSet),
    TouchElementSelectedListener {
    var touchElementsSelection = ArrayList<TouchElement>()
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

    fun clearTouchElementSelection()
    {
        touchElementsSelection.clear()
    }

    override fun dispatchDraw(canvas: Canvas) {
        super.dispatchDraw(canvas)

        val appCtx = context as TouchSampleSynthMain
        if (appCtx.connectorDisplay) {
            instrumentChipContainer?.children?.filter { c ->
                c is InstrumentChip
            }?.forEach { ic ->
                appCtx.touchElements
                    .filter { te -> te.getSoundGenerator() == (ic as InstrumentChip).getInstrument() }
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
                currentPositions[event.getPointerId(event.actionIndex)] =
                    Point(event.getX(ptrIndex).toDouble(), event.getY(ptrIndex).toDouble())
                touchElements.find { te ->
                    te.isInside(
                        Point(
                            event.getX(ptrIndex).toDouble(),
                            event.getY(ptrIndex).toDouble()
                        )
                    ) && !te.isEditing() && !touchElementsOnPointer.containsValue(te)
                }?.let {
                    touchElementsOnPointer[event.getPointerId(ptrIndex)] = it
                    event.offsetLocation(
                        -(it.layoutParams as LayoutParams).leftMargin.toFloat(),
                        -(it.layoutParams as LayoutParams).topMargin.toFloat()
                    )
                    Log.i(TAG, "onTouchEvent, got event ${motionEventTypeToString(event)}")
                    Log.i(TAG, "added Pointer $event.getPointerId(event.actionIndex)")
                    it.dispatchTouchEvent(event)
                    return true
                }
                if (touchElementsSelection.size > 0 && touchElementsSelection.any { te ->
                        te.isInside(
                            Point(event.getX(ptrIndex).toDouble(), event.getY(ptrIndex).toDouble())
                        )
                    }) {
                    dispatchMotionEventOverSelection(event)
                    return true
                }
                touchElements.firstOrNull{ te -> te.isInside(Point(
                    event.getX(ptrIndex).toDouble(),
                    event.getY(ptrIndex).toDouble()
                ),0.0f)}?.let {
                    event.offsetLocation(
                        -(it.layoutParams as LayoutParams).leftMargin.toFloat(),
                        -(it.layoutParams as LayoutParams).topMargin.toFloat()
                    )
                    touchElementsOnPointer[event.getPointerId(ptrIndex)]=it
                    it.dispatchTouchEvent(event)
                    return true
                }

                if (touchElementsSelection.size > 0)
                {
                    touchElementsSelection.forEach {
                        te -> te.setEditmode(TouchElement.TouchElementState.EDITING)
                    }
                    touchElementsSelection.clear()
                    (context as TouchSampleSynthMain).setGraphicsToolbarItemsVisibility(false)
                    return true
                }
                if (isEditing())
                {
                    ((context as TouchSampleSynthMain).supportActionBar!!.customView.findViewById<SwitchCompat>(R.id.toolbar_edit)).isChecked = false
                }
                Log.i(TAG,"onTouchEvent, got event ${motionEventTypeToString(event)}")
                return true
            }

            MotionEvent.ACTION_MOVE ->
            {
                var processedMoveEvent=false
                for (pid in pointerIds) {
                    val pidx = event.findPointerIndex(pid)
                    if (!isEditing()) {
                        if (pidx >= 0) {
                            processedMoveEvent = if (touchElementsOnPointer[pid] != null) {
                                handleMoveWithAssociatedTouchElement(
                                    touchElementsOnPointer[pid]!!,
                                    event,
                                    pid,
                                    pidx
                                )
                            } else {
                                tryFindingNewTouchElement(event, pid, pidx)
                            }
                            handleSlideOverEvents(event, pid, pidx)
                        }
                    } else {
                        if (touchElementsOnPointer[pid] != null)
                        {
                            val copiedEvent = MotionEvent.obtain(event.downTime,event.eventTime,event.action,event.getX(pidx),event.getY(pidx),event.metaState)
                            copiedEvent.offsetLocation(
                                -(touchElementsOnPointer[pid]?.layoutParams as LayoutParams).leftMargin.toFloat(),
                                -(touchElementsOnPointer[pid]?.layoutParams as LayoutParams).topMargin.toFloat()
                            )
                            touchElementsOnPointer[pid]?.dispatchTouchEvent(copiedEvent)
                            return true
                        }
                        touchElements.firstOrNull { te ->
                            te.isInside(
                                Point(
                                    event.getX(pidx).toDouble(),
                                    event.getY(pidx).toDouble()
                                ),0.0f
                            )
                        }?.let {

                            if (touchElementsSelection.size > 0)
                            {
                                dispatchMotionEventOverSelection(event)
                            }
                            else
                            {
                                event.offsetLocation(
                                    -(it.layoutParams as LayoutParams).leftMargin.toFloat(),
                                    -(it.layoutParams as LayoutParams).topMargin.toFloat()
                                )
                                it.dispatchTouchEvent(event)
                            }

                            processedMoveEvent = true
                        }
                    }
                }
                return processedMoveEvent
                //if (touchElementsSelection.size > 1)
                //{
                //    dispatchMotionEventOverSelection(event)
                //    return true
                //}
                //return false
            }
            MotionEvent.ACTION_UP,MotionEvent.ACTION_POINTER_UP ->
            {
                val ptrId = event.getPointerId(event.actionIndex)
                val pidx = event.findPointerIndex(ptrId)
                val associatedTouchElement = touchElementsOnPointer[event.getPointerId(event.actionIndex)]
                if (associatedTouchElement!=null)
                {
                    event.offsetLocation(-(associatedTouchElement.layoutParams as LayoutParams).leftMargin.toFloat(),-(associatedTouchElement.layoutParams as LayoutParams).topMargin.toFloat())
                    associatedTouchElement.dispatchTouchEvent(event)
                    touchElementsOnPointer.remove(event.getPointerId(event.actionIndex))
                    Log.i(TAG,"removed Pointer $event.getPointerId(event.actionIndex)")
                }
                touchElements.firstOrNull { te -> te.isInside(Point(event.getX(pidx).toDouble(),event.getY(pidx).toDouble())) }?.let {
                    event.offsetLocation(-(it.layoutParams as LayoutParams).leftMargin.toFloat(),-(it.layoutParams as LayoutParams).topMargin.toFloat())
                    it.dispatchTouchEvent(event)
                    return true
                }
                if (touchElementsSelection.size > 0
                    && Line(currentPositions[ptrId]!!,Point(event.getX(pidx).toDouble(),event.getY(pidx).toDouble())).length() < NO_DRAG_TOLERANCE
                    )
                {
                    touchElementsSelection.firstOrNull {
                        te -> te.isInside(Point(event.getX(pidx).toDouble(),event.getY(pidx).toDouble()))
                    }?.let {
                        event.offsetLocation(-(it.layoutParams as LayoutParams).leftMargin.toFloat(),-(it.layoutParams as LayoutParams).topMargin.toFloat())
                        it.dispatchTouchEvent(event)
                    }
                }
                else if (touchElementsSelection.size > 0)
                {
                    dispatchMotionEventOverSelection(event)
                }
                pointerIds.remove(ptrId)
                oldPositions.remove(ptrId)
                currentPositions.remove(ptrId)
                return true
            }
        }
        return super.onTouchEvent(event)
    }


    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {

        when (ev!!.action.and(MotionEvent.ACTION_MASK))
        {
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_POINTER_DOWN -> {
                return !(isEditing() && (context as TouchSampleSynthMain).findViewById<LinearLayout>(R.id.playpage_instrument_chips).size > 0
                        && ev.y < (context as TouchSampleSynthMain).findViewById<LinearLayout>(R.id.playpage_instrument_chips).height)
            }
        }
        return false
    }

    override fun onTouchElementSelected(touchElement: TouchElement) {
        touchElementsSelection.add(touchElement)
        if (touchElementsSelection.size > 1)
        {
            (context as TouchSampleSynthMain).setGraphicsToolbarItemsVisibility(true)
        }

    }

    override fun onTouchElementDeselected(touchElement: TouchElement) {
        touchElementsSelection.remove(touchElement)
        if (touchElementsSelection.size < 2) {
            (context as TouchSampleSynthMain).setGraphicsToolbarItemsVisibility(false)
        }
    }

    private fun isEditing(): Boolean
    {
        return (context as TouchSampleSynthMain).findViewById<SwitchCompat>(R.id.toolbar_edit).isChecked
    }

    private fun handleMoveWithAssociatedTouchElement(touchElement: TouchElement,event: MotionEvent, pointerId: Int, pointerIndex: Int): Boolean
    {
        val copiedEvent = MotionEvent.obtain(event.downTime,event.eventTime,event.action,event.getX(pointerIndex),event.getY(pointerIndex),event.metaState)
        copiedEvent.offsetLocation(
            -(touchElement.layoutParams as LayoutParams).leftMargin.toFloat(),
            -(touchElement.layoutParams as LayoutParams).topMargin.toFloat()
        )
        touchElement.dispatchTouchEvent(copiedEvent)
        //Log.i(TAG,"dispatched to $associatedTouchElement")

        if (!touchElement.isInside(
                Point(
                    event.getX(pointerIndex).toDouble(),
                    event.getY(pointerIndex).toDouble()
                )
            )
        ) {
            Log.i(TAG,"Moved out of touchelement: $touchElement")
            touchElementsOnPointer.remove(pointerId)
            tryFindingNewTouchElement(event,pointerId,pointerIndex)
        }
        return true
    }

    private fun tryFindingNewTouchElement(event: MotionEvent,pointerId: Int,pointerIndex: Int): Boolean
    {
        touchElements.find { te ->
            te.isInside(
                Point(
                    event.getX(pointerIndex).toDouble(),
                    event.getY(pointerIndex).toDouble()
                )
            ) && !te.isEditing() && !touchElementsOnPointer.containsValue(te)
        }?.let {
            Log.i(TAG,"slided into new touchelement: $it")
            val eventForNewTE = MotionEvent.obtain(event.downTime,event.eventTime,event.action,event.getX(pointerIndex),event.getY(pointerIndex),event.metaState)
            touchElementsOnPointer[pointerId] = it
            eventForNewTE.offsetLocation(
                -(it.layoutParams as LayoutParams).leftMargin.toFloat(),
                -(it.layoutParams as LayoutParams).topMargin.toFloat()
            )
            eventForNewTE.action = MotionEvent.ACTION_DOWN
            it.dispatchTouchEvent(eventForNewTE)
            return true
        }
        return false
    }

    private fun dispatchMotionEventOverSelection(event: MotionEvent)
    {
        touchElementsSelection.firstOrNull {
                te -> te.isInside(Point(event.x.toDouble(),event.y.toDouble()))
        }?.let {
                firstTouchElement ->
            event.offsetLocation(
                -(firstTouchElement.layoutParams as LayoutParams).leftMargin.toFloat(),
                -(firstTouchElement.layoutParams as LayoutParams).topMargin.toFloat()
            )
            touchElementsSelection.forEach {
                val evt = MotionEvent.obtain(event)
                it.dispatchTouchEvent(evt)
            }
        }
    }

    private fun handleSlideOverEvents(event: MotionEvent,pointerId: Int,pointerIndex: Int)
    {
        val copiedEvent = MotionEvent.obtain(event.downTime,event.eventTime,event.action,event.getX(pointerIndex),event.getY(pointerIndex),event.metaState)
        if (currentPositions[pointerId] != null) {
            oldPositions[pointerId] = currentPositions[pointerId] as Point
            currentPositions[pointerId] =
                Point(copiedEvent.x.toDouble(), copiedEvent.y.toDouble())
        } else {
            currentPositions[pointerId] =
                Point(copiedEvent.x.toDouble(), copiedEvent.y.toDouble())
        }
        copiedEvent.recycle()
        val touchElementsSlidedOver = touchElements.filter { slidingOVerCandidate ->
            if (oldPositions[pointerId] != null && currentPositions[pointerId] != null) {
                return@filter slidingOVerCandidate.asRectangle().getIntersectingSides(
                    Line(
                        oldPositions[pointerId]!!,
                        currentPositions[pointerId]!!
                    )
                ).size == 2
            }
            return@filter false
        }
        touchElementsSlidedOver.forEach {
            Log.i(TAG, "Slideover Event for $it")
            if (it.asRectangle().getIntersectingSides(
                    Line(
                        oldPositions[pointerId]!!,
                        currentPositions[pointerId]!!
                    )
                ).contains(Overlap.TOP)
            ) {
                it.slideOverEvent(
                    MotionEvent.obtain(
                        event.downTime,
                        event.eventTime,
                        MotionEvent.ACTION_DOWN,
                        ((oldPositions[pointerId]!!.x + currentPositions[pointerId]!!.x) / 2).toFloat(),
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
                        ((oldPositions[pointerId]!!.y + currentPositions[pointerId]!!.y) / 2).toFloat(),
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