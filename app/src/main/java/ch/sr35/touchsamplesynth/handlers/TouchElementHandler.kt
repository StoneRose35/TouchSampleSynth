package ch.sr35.touchsamplesynth.handlers

import android.app.AlertDialog
import android.content.Context
import android.view.MotionEvent
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import ch.sr35.touchsamplesynth.R
import ch.sr35.touchsamplesynth.TouchSampleSynthMain
import ch.sr35.touchsamplesynth.dialogs.EditTouchElementFragmentDialog
import ch.sr35.touchsamplesynth.graphics.Point
import ch.sr35.touchsamplesynth.views.NO_DRAG_TOLERANCE
import ch.sr35.touchsamplesynth.views.OUTLINE_STROKE_WIDTH_DEFAULT
import ch.sr35.touchsamplesynth.views.OUTLINE_STROKE_WIDTH_ENGAGED
import ch.sr35.touchsamplesynth.views.PADDING
import ch.sr35.touchsamplesynth.views.TouchElement
import ch.sr35.touchsamplesynth.views.TouchElement.ActionDir
import ch.sr35.touchsamplesynth.views.TouchElement.TouchElementDragAnchor
import ch.sr35.touchsamplesynth.views.TouchElement.TouchElementState
import kotlin.math.abs


class TouchAction(val startPoint: Point)
{
    var absoluteValue =0.0f
    var relativeValue=0.0f

}
open class TouchElementHandler(val touchElement: TouchElement) {

    private var dragStart: TouchElementDragAnchor? = null
    private var touchActionHorizontal: TouchAction= TouchAction(Point(0.0,0.0))
    private var touchActionVertical: TouchAction = TouchAction(Point(0.0,0.0))

    fun handleTouchEvent(event: MotionEvent?): Boolean
    {
        if (touchElement.elementState != TouchElementState.EDITING && touchElement.elementState != TouchElementState.EDITING_SELECTED) {
            if (event?.action?.and(MotionEvent.ACTION_MASK) == MotionEvent.ACTION_DOWN || event?.action?.and(MotionEvent.ACTION_MASK) == MotionEvent.ACTION_POINTER_DOWN) {
                handleActionDownInPlayMode(event)
            } else if (event?.action?.and(MotionEvent.ACTION_MASK) == MotionEvent.ACTION_UP || event?.action?.and(MotionEvent.ACTION_MASK) == MotionEvent.ACTION_POINTER_UP) {
                handleActionUpInPlayMode(event)
            } else if (event?.action?.and(MotionEvent.ACTION_MASK) == MotionEvent.ACTION_MOVE) {
                handleActionMoveInPlayMode(event)
            }
        } else {
            when (event?.action) {
                MotionEvent.ACTION_DOWN -> {
                    handleActionDownInEditMode(event)
                }
                MotionEvent.ACTION_UP -> {
                    handleActionUpInEditMode()
                }
                MotionEvent.ACTION_MOVE -> {
                    handleActionMoveInEditMode(event)
                }
            }
            return true
        }
        return false
    }


    protected open fun handleActionDownInPlayMode(event: MotionEvent): Boolean
    {
        touchActionHorizontal = TouchAction(Point(event.x.toDouble(),event.y.toDouble()))
        touchActionVertical = TouchAction(Point(event.x.toDouble(),event.y.toDouble()))
        if ((touchElement.actionDir == ActionDir.HORIZONTAL_LR_VERTICAL_DU || touchElement.actionDir == ActionDir.HORIZONTAL_RL_VERTICAL_DU) && event.y >= PADDING && event.y <= touchElement.measuredHeight - PADDING) {
            touchActionVertical.absoluteValue = 1.0f - ((event.y- PADDING) / (touchElement.measuredHeight.toFloat()- 2* PADDING))
            touchActionVertical.relativeValue = ((touchActionVertical.startPoint.y.toFloat() - event.y) / (touchElement.measuredHeight.toFloat()- 2* PADDING))
        }
        else if (event.y >= PADDING && event.y <= touchElement.measuredHeight - PADDING) {
            touchActionVertical.absoluteValue = ((event.y- PADDING) / (touchElement.measuredHeight.toFloat()- 2* PADDING))
            touchActionVertical.relativeValue = ((event.y-touchActionVertical.startPoint.y.toFloat()) / (touchElement.measuredHeight.toFloat()- 2* PADDING))
        }
        if ((touchElement.actionDir == ActionDir.HORIZONTAL_LR_VERTICAL_DU || touchElement.actionDir == ActionDir.HORIZONTAL_LR_VERTICAL_UD) && event.x >= PADDING && event.x <= touchElement.measuredWidth - PADDING) {
            touchActionHorizontal.absoluteValue = (event.x- PADDING) / (touchElement.measuredWidth.toFloat()- 2* PADDING)
            touchActionHorizontal.relativeValue = (event.x-touchActionHorizontal.startPoint.x.toFloat()) / (touchElement.measuredWidth.toFloat()- 2* PADDING)
        }
        else if (event.x >= PADDING && event.x <= touchElement.measuredWidth - PADDING) {
            touchActionHorizontal.absoluteValue = 1.0f - (event.x- PADDING) / (touchElement.measuredWidth.toFloat()- 2* PADDING)
            touchActionHorizontal.relativeValue = (touchActionHorizontal.startPoint.x.toFloat() - event.x) / (touchElement.measuredWidth.toFloat()- 2* PADDING)
        }
        touchElement.performClick()
        touchElement.invalidate()
        touchElement.px = event.x
        touchElement.py = event.y

        return true
    }

    fun switchOnVoices(appContext: TouchSampleSynthMain) {
        var firstnote = true
        touchElement.notes.forEach { currentNote ->
            touchElement.soundGenerator?.getNextFreeVoice()?.let {
                touchElement.currentVoices.add(it)
                if (touchElement.soundGenerator!!.horizontalToActionB) {
                    it.applyTouchActionB(touchActionHorizontal.relativeValue)
                    it.applyTouchActionA(touchActionVertical.absoluteValue)
                } else {
                    it.applyTouchActionB(touchActionVertical.relativeValue)
                    it.applyTouchActionA(touchActionHorizontal.absoluteValue)
                }
                if (firstnote) {
                    it.setMidiChannel(touchElement.midiChannel)
                    val midiData = ByteArray(3)
                    midiData[0] = (0xB0 + touchElement.midiChannel).toByte()
                    midiData[1] = touchElement.midiCCA.toByte()
                    midiData[2] = (touchActionHorizontal.absoluteValue * 127.0f).toInt().toByte()
                    if (midiData[2] != touchElement.midiCCAOld) {
                        touchElement.appContext?.rtpMidiServer?.let {
                            if (it.isEnabled) {
                                var sentNotes = 0
                                while (sentNotes < (touchElement.appContext).rtpMidiNotesRepeat) {
                                    appContext.rtpMidiServer?.addToSendQueue(midiData)
                                    sentNotes += 1
                                }
                            }
                        }
                        it.sendMidiCC(
                            touchElement.midiCCA,
                            (touchActionHorizontal.absoluteValue * 127.0f).toInt()
                        )
                        touchElement.midiCCAOld = midiData[2]
                    }
                    midiData[1] = touchElement.midiCCB.toByte()
                    midiData[2] = (touchActionVertical.absoluteValue * 127.0f).toInt().toByte()
                    if (midiData[2] != touchElement.midiCCBOld) {
                        touchElement.appContext?.rtpMidiServer?.let {
                            if (it.isEnabled) {
                                var sentNotes = 0
                                while (sentNotes < (touchElement.appContext).rtpMidiNotesRepeat) {
                                    appContext.rtpMidiServer?.addToSendQueue(midiData)
                                    sentNotes += 1
                                }
                            }
                        }
                        it.sendMidiCC(
                            touchElement.midiCCB,
                            (touchActionVertical.absoluteValue * 127.0f).toInt()
                        )
                        touchElement.midiCCAOld = midiData[2]
                    }
                    firstnote = false
                }

                touchElement.appContext?.rtpMidiServer?.let { rtpMidiServer ->
                    if (rtpMidiServer.isEnabled) {
                        val midiData = ByteArray(3)
                        touchElement.setMidiNoteOn(midiData, currentNote)
                        var sentNotes = 0
                        while (sentNotes < (touchElement.appContext).rtpMidiNotesRepeat) {
                            appContext.rtpMidiServer?.addToSendQueue(midiData)
                            sentNotes += 1
                        }
                    }
                }

                it.setNote(currentNote.value)
                if (it.switchOn(1.0f)) {
                    touchElement.outLine.strokeWidth = OUTLINE_STROKE_WIDTH_ENGAGED
                }
            }
        }
    }

    protected open fun handleActionUpInPlayMode(event: MotionEvent): Boolean
    {
        touchElement.outLine.strokeWidth = OUTLINE_STROKE_WIDTH_DEFAULT
        if (touchElement.touchMode == TouchElement.TouchMode.MOMENTARY) {
            touchElement.appContext?.let { switchOffVoices(it) }
            touchElement.isEngaged = false
        }
        touchElement.invalidate()
        return true
    }

    fun switchOffVoices(appContext: TouchSampleSynthMain) {
        touchElement.currentVoices.forEach { it.switchOff(1.0f) }
        touchElement.appContext?.rtpMidiServer?.let {
            if (it.isEnabled) {
                touchElement.notes.forEach { currentNote ->
                    val midiData = ByteArray(3)
                    touchElement.setMidiNoteOff(midiData, currentNote)
                    var sentNotes = 0
                    while (sentNotes < appContext.rtpMidiNotesRepeat) {
                        appContext.rtpMidiServer?.addToSendQueue(midiData)
                        sentNotes += 1
                    }
                }
            }
        }
        touchElement.currentVoices.clear()
    }

    protected open fun handleActionMoveInPlayMode(event: MotionEvent) {
        if (event.y <= PADDING || event.y >= touchElement.measuredHeight - PADDING || event.x < PADDING || event.x >= touchElement.measuredWidth - PADDING) {
            if (touchElement.touchMode == TouchElement.TouchMode.MOMENTARY) {
                touchElement.outLine.strokeWidth = OUTLINE_STROKE_WIDTH_DEFAULT
                touchElement.appContext?.let {
                    switchOffVoices(it)
                }
            }
        } else if ((touchElement.actionDir == ActionDir.HORIZONTAL_LR_VERTICAL_DU || touchElement.actionDir == ActionDir.HORIZONTAL_RL_VERTICAL_DU) && event.y >= PADDING && event.y <= touchElement.measuredHeight - PADDING) {
            touchActionVertical.absoluteValue =
                1.0f - ((event.y - PADDING) / (touchElement.measuredHeight.toFloat() - 2 * PADDING))
            touchActionVertical.relativeValue =
                ((touchActionVertical.startPoint.y.toFloat() - event.y) / (touchElement.measuredHeight.toFloat() - 2 * PADDING))
        } else if (event.y >= PADDING && event.y <= touchElement.measuredHeight - PADDING) {
            touchActionVertical.absoluteValue =
                ((event.y - PADDING) / (touchElement.measuredHeight.toFloat() - 2 * PADDING))
            touchActionVertical.relativeValue =
                ((event.y - touchActionVertical.startPoint.y.toFloat()) / (touchElement.measuredHeight.toFloat() - 2 * PADDING))
        }
        if ((touchElement.actionDir == ActionDir.HORIZONTAL_LR_VERTICAL_DU || touchElement.actionDir == ActionDir.HORIZONTAL_LR_VERTICAL_UD) && event.x >= PADDING && event.x <= touchElement.measuredWidth - PADDING) {
            touchActionHorizontal.absoluteValue =
                (event.x - PADDING) / (touchElement.measuredWidth.toFloat() - 2 * PADDING)
            touchActionHorizontal.relativeValue =
                (event.x - touchActionHorizontal.startPoint.x.toFloat()) / (touchElement.measuredWidth.toFloat() - 2 * PADDING)
        } else if (event.x >= PADDING && event.x <= touchElement.measuredWidth - PADDING) {
            touchActionHorizontal.absoluteValue =
                1.0f - (event.x - PADDING) / (touchElement.measuredWidth.toFloat() - 2 * PADDING)
            touchActionHorizontal.relativeValue =
                (touchActionHorizontal.startPoint.x.toFloat() - event.x) / (touchElement.measuredWidth.toFloat() - 2 * PADDING)
        }
        var firstnote = true

        touchElement.currentVoices.forEach {
            if (touchElement.soundGenerator!!.horizontalToActionB) {
                it.applyTouchActionB(touchActionHorizontal.relativeValue)
                it.applyTouchActionA(touchActionVertical.absoluteValue)
            } else {
                it.applyTouchActionB(touchActionVertical.relativeValue)
                it.applyTouchActionA(touchActionHorizontal.absoluteValue)
            }

            if (firstnote) {
                val midiData = ByteArray(3)
                midiData[0] = (0xB0 + touchElement.midiChannel).toByte()
                midiData[1] = touchElement.midiCCA.toByte()
                midiData[2] = (touchActionHorizontal.absoluteValue * 127.0f).toInt().toByte()
                if (midiData[2] != touchElement.midiCCAOld) {
                    touchElement.appContext?.rtpMidiServer?.let {
                        if (it.isEnabled) {
                            var sentNotes = 0
                            while (sentNotes < (touchElement.appContext).rtpMidiNotesRepeat) {
                                touchElement.appContext.rtpMidiServer?.addToSendQueue(midiData)
                                sentNotes += 1
                            }
                        }
                    }
                    it.sendMidiCC(
                        touchElement.midiCCA,
                        (touchActionHorizontal.absoluteValue * 127.0f).toInt()
                    )
                    touchElement.midiCCAOld = midiData[2]
                }
                midiData[1] = touchElement.midiCCB.toByte()
                midiData[2] = (touchActionVertical.absoluteValue * 127.0f).toInt().toByte()
                if (midiData[2] != touchElement.midiCCBOld) {
                    touchElement.appContext?.rtpMidiServer?.let {
                        if (it.isEnabled) {
                            var sentNotes = 0
                            while (sentNotes < (touchElement.appContext).rtpMidiNotesRepeat) {
                                touchElement.appContext.rtpMidiServer?.addToSendQueue(midiData)
                                sentNotes += 1
                            }
                        }
                    }
                    it.sendMidiCC(
                        touchElement.midiCCB,
                        (touchActionVertical.absoluteValue * 127.0f).toInt()
                    )
                    touchElement.midiCCAOld = midiData[2]

                    firstnote = false
                }
            }
        }
    }

    private fun handleActionDownInEditMode(event: MotionEvent)
    {
        // start a corner drag if a corner has been hit, else move the whole element
        touchElement.px = event.x
        touchElement.py = event.y
        if (touchElement.setSoundgenRect.contains(touchElement.px.toInt(), touchElement.py.toInt())  && touchElement.elementState != TouchElementState.EDITING_SELECTED) {
            val editSoundgenerator = EditTouchElementFragmentDialog(
                touchElement,
                touchElement.appContext as Context
            )
            touchElement.dragStart = null
            editSoundgenerator.show()
            touchElement.invalidate()

        } else if (touchElement.deleteRect.contains(touchElement.px.toInt(), touchElement.py.toInt()) && touchElement.elementState != TouchElementState.EDITING_SELECTED) {

            touchElement.appContext?.let {
                AlertDialog.Builder(touchElement.appContext)
                    .setMessage(touchElement.context.getString(R.string.alert_dialog_really_delete))
                    .setPositiveButton(touchElement.context.getString(R.string.yes)) { _, _ ->
                        touchElement.appContext.touchElements.remove(touchElement)
                        (touchElement.parent as ViewGroup).removeView(touchElement)
                    }
                    .setNegativeButton(touchElement.context.getString(R.string.no)) { _, _ -> }.create().also {
                        touchElement.dragStart = null
                        it.show()
                    }
            }
        } else {
            val layoutParams: ConstraintLayout.LayoutParams? =
                touchElement.layoutParams as ConstraintLayout.LayoutParams?
            if (layoutParams != null) {
                touchElement.oldHeight = layoutParams.height
                touchElement.oldWidth = layoutParams.width
                touchElement.oldTopMargin = layoutParams.topMargin
                touchElement.oldLeftMargin = layoutParams.leftMargin
            }
            dragStart = touchElement.isInCorner(event.x, event.y)
        }
    }

    private fun handleActionUpInEditMode(): Boolean
    {
        if (dragStart != null)
        {
            (touchElement.layoutParams as ConstraintLayout.LayoutParams).let {
                if (abs(it.leftMargin - touchElement.oldLeftMargin) + abs (it.topMargin - touchElement.oldTopMargin) < NO_DRAG_TOLERANCE && it.height == touchElement.oldHeight && it.width == touchElement.oldWidth)
                {
                    if (touchElement.elementState == TouchElementState.EDITING)
                    {
                        touchElement.elementState = TouchElementState.EDITING_SELECTED
                        touchElement.outLine.strokeWidth = OUTLINE_STROKE_WIDTH_ENGAGED
                        touchElement.onSelectedListener?.onTouchElementSelected(touchElement)
                    }
                    else if (touchElement.elementState == TouchElementState.EDITING_SELECTED)
                    {
                        touchElement.elementState = TouchElementState.EDITING
                        touchElement.outLine.strokeWidth = OUTLINE_STROKE_WIDTH_DEFAULT
                        touchElement.onSelectedListener?.onTouchElementDeselected(touchElement)
                    }
                    touchElement.invalidate()
                }
            }

            if (!touchElement.validatePlacement())
            {
                val restoredlayoutParams: ConstraintLayout.LayoutParams? =
                    touchElement.layoutParams as ConstraintLayout.LayoutParams?
                restoredlayoutParams?.height = touchElement.oldHeight
                restoredlayoutParams?.width = touchElement.oldWidth
                restoredlayoutParams?.leftMargin = touchElement.oldLeftMargin
                restoredlayoutParams?.topMargin = touchElement.oldTopMargin
                touchElement.layoutParams = restoredlayoutParams
                touchElement.invalidate()
            }
        }
        return true
    }

    private fun handleActionMoveInEditMode(event: MotionEvent): Boolean
    {
        val layoutParams: ConstraintLayout.LayoutParams? =
            this.touchElement.layoutParams as ConstraintLayout.LayoutParams?
        if (dragStart != null) {
            when (dragStart) {
                TouchElementDragAnchor.TOP_LEFT -> {
                    layoutParams!!.leftMargin += event.x.minus(this.touchElement.px).toInt()
                    layoutParams!!.width += this.touchElement.px.minus(event.x).toInt()
                    layoutParams!!.topMargin += event.y.minus(this.touchElement.py).toInt()
                    layoutParams!!.height += this.touchElement.py.minus(event.y).toInt()

                }

                TouchElementDragAnchor.TOP_RIGHT -> {
                    layoutParams!!.width = this.touchElement.oldWidth + event.x.minus(this.touchElement.px).toInt()
                    layoutParams.topMargin += event.y.minus(this.touchElement.py).toInt()
                    layoutParams.height += this.touchElement.py.minus(event.y).toInt()
                }

                TouchElementDragAnchor.BOTTOM_RIGHT -> {
                    layoutParams!!.width = this.touchElement.oldWidth + event.x.minus(this.touchElement.px).toInt()
                    layoutParams.height = this.touchElement.oldHeight + event.y.minus(this.touchElement.py).toInt()
                }

                TouchElementDragAnchor.BOTTOM_LEFT -> {
                    layoutParams!!.leftMargin += event.x.minus(this.touchElement.px).toInt()
                    layoutParams!!.width += this.touchElement.px.minus(event.x).toInt()
                    layoutParams!!.height = this.touchElement.oldHeight + event.y.minus(this.touchElement.py).toInt()
                }

                else -> {
                    layoutParams!!.leftMargin += event.x.minus(this.touchElement.px).toInt()
                    layoutParams!!.topMargin += event.y.minus(this.touchElement.py).toInt()
                }
            }
            this.touchElement.layoutParams = layoutParams
        }
        return true
    }
}