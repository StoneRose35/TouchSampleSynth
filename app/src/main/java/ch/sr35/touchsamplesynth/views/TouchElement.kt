package ch.sr35.touchsamplesynth.views

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.os.Build
import android.text.method.Touch
import android.util.AttributeSet
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.constraintlayout.widget.ConstraintLayout
import ch.sr35.touchsamplesynth.MusicalPitch
import ch.sr35.touchsamplesynth.R
import ch.sr35.touchsamplesynth.TouchElementSelectedListener
import ch.sr35.touchsamplesynth.TouchSampleSynthMain
import ch.sr35.touchsamplesynth.audio.InstrumentI
import ch.sr35.touchsamplesynth.audio.MusicalSoundGenerator
import ch.sr35.touchsamplesynth.dialogs.EditTouchElementFragmentDialog
import ch.sr35.touchsamplesynth.graphics.Converter
import ch.sr35.touchsamplesynth.graphics.Point
import ch.sr35.touchsamplesynth.graphics.Rectangle
import com.google.android.material.color.MaterialColors
import java.io.Serializable
import kotlin.math.abs
import kotlin.math.sqrt

const val PADDING: Float = 32.0f
const val EDIT_CIRCLE_OFFSET = 24.0f
const val OUTLINE_STROKE_WIDTH_DEFAULT = 7.8f
const val OUTLINE_STROKE_WIDTH_ENGAGED = 20.4f
const val NO_DRAG_TOLERANCE = 20

class TouchElement(context: Context, attributeSet: AttributeSet?) :
    View(context, attributeSet) {

    enum class ActionDir: Serializable {
        HORIZONTAL_LEFT_RIGHT,
        HORIZONTAL_RIGHT_LEFT,
        VERTICAL_UP_DOWN,
        VERTICAL_DOWN_UP
    }

    enum class TouchElementState {
        PLAYING,
        PLAYING_VERBOSE,
        EDITING,
        EDITING_SELECTED
    }

    enum class TouchElementDragAnchor {
        BODY,
        TOP_LEFT,
        TOP_RIGHT,
        BOTTOM_LEFT,
        BOTTOM_RIGHT
    }

    var actionDir: ActionDir = ActionDir.HORIZONTAL_LEFT_RIGHT
    private val outLine: Paint = Paint()
    val fillColor: Paint = Paint()
    private var px: Float = 0.0f
    private var py: Float = 0.0f
    private var defaultState = TouchElementState.PLAYING

    private val arrowLine: Paint = Paint()
    private val editDotFill: Paint = Paint()
    private val editBoxBackground: Paint = Paint()
    private val editText: Paint = Paint()
    private val smallText: Paint = Paint()
    private var cornerRadius = 0.0f
    private var dragStart: TouchElementDragAnchor? = null

    private var oldWidth: Int = 0
    private var oldHeight: Int = 0
    private var oldLeftMargin: Int = 0
    private var oldTopMargin: Int = 0
    private var elementState: TouchElementState = defaultState
    var soundGenerator: InstrumentI? = null
    private var currentVoice: MusicalSoundGenerator? = null
    var note: MusicalPitch? = null
    var midiChannel: Int=0
    var midiCC: Int=3
    private var midiCCOld: Byte=0
    private var rotateRect: Rect = Rect()
    private var setSoundgenRect: Rect = Rect()
    private var deleteRect: Rect = Rect()
    private val boundsRotate = Rect()
    private val boundsSetSoundgen = Rect()
    private val boundsDelete = Rect()
    private val appContext: TouchSampleSynthMain?

    private val rotateSymbol = AppCompatResources.getDrawable(context,R.drawable.rotatesymbol)
    private val editSymbol = AppCompatResources.getDrawable(context,R.drawable.editsymbol)
    private val deleteSymbol = AppCompatResources.getDrawable(context,R.drawable.deletesymbol)
    var onSelectedListener: TouchElementSelectedListener? = null
    init {
        outLine.color = MaterialColors.getColor(this,R.attr.touchElementLine)
        outLine.strokeWidth = OUTLINE_STROKE_WIDTH_DEFAULT
        outLine.style = Paint.Style.STROKE
        outLine.isAntiAlias = true

        editBoxBackground.color = MaterialColors.getColor(this,R.attr.touchElementEditBoxBgColor)
        editBoxBackground.style = Paint.Style.FILL
        editBoxBackground.isAntiAlias = true

        editDotFill.color = MaterialColors.getColor(this,R.attr.touchElementEditDot)
        editDotFill.style = Paint.Style.FILL
        editDotFill.isAntiAlias = true

        editText.color = MaterialColors.getColor(this,R.attr.touchElementEditTextColor)
        editText.style = Paint.Style.FILL
        editText.textSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 24.0f,Resources.getSystem().displayMetrics)
        editText.isAntiAlias = true

        smallText.color = MaterialColors.getColor(this,R.attr.touchElementEditTextColor)
        smallText.style = Paint.Style.FILL
        smallText.textSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12.0f,Resources.getSystem().displayMetrics)
        smallText.isAntiAlias = true

        arrowLine.color = MaterialColors.getColor(this,R.attr.touchElementLine)
        arrowLine.strokeWidth = 12.0f
        arrowLine.style = Paint.Style.STROKE
        arrowLine.strokeCap = Paint.Cap.ROUND
        arrowLine.isAntiAlias = true

        fillColor.color = MaterialColors.getColor(this,R.attr.touchElementColor)
        fillColor.style = Paint.Style.FILL
        appContext = if (context is TouchSampleSynthMain) {
            context
        } else {
            null
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val w = layoutParams.width.toFloat()
        val h = layoutParams.height.toFloat()
        val arrowSize: Float

        // draw oval
        canvas.drawRoundRect(
            0.0f + PADDING,
            0.0f + PADDING,
            w - PADDING,
            h - PADDING,
            cornerRadius,
            cornerRadius,
            fillColor
        )
        canvas.drawRoundRect(
            0.0f + PADDING,
            0.0f + PADDING,
            w - PADDING,
            h - PADDING,
            cornerRadius,
            cornerRadius,
            outLine
        )

        // draw action arrow
        when(actionDir) {
            ActionDir.HORIZONTAL_LEFT_RIGHT -> {
                arrowSize = if (0.6f * w < 0.11f * h) {
                    0.6f * w
                } else {
                    0.11f * h
                }
                canvas.drawLine(
                    0.2f * w + PADDING,
                    0.8f * h - PADDING,
                    0.8f * w - PADDING,
                    0.8f * h - PADDING,
                    arrowLine
                )
                canvas.drawLine(
                    0.8f * w - PADDING - arrowSize,
                    0.8f * h - PADDING - arrowSize,
                    0.8f * w - PADDING,
                    0.8f * h - PADDING,
                    arrowLine
                )
                canvas.drawLine(
                    0.8f * w - PADDING - arrowSize,
                    0.8f * h - PADDING + arrowSize,
                    0.8f * w - PADDING,
                    0.8f * h - PADDING,
                    arrowLine
                )
            }
            ActionDir.HORIZONTAL_RIGHT_LEFT -> {
                arrowSize = if (0.6f * w < 0.11f * h) {
                    0.6f * w
                } else {
                    0.11f * h
                }
                canvas.drawLine(
                    0.2f * w + PADDING,
                    0.8f * h - PADDING,
                    0.8f * w - PADDING,
                    0.8f * h - PADDING,
                    arrowLine
                )
                canvas.drawLine(
                    0.2f * w + PADDING + arrowSize,
                    0.8f * h - PADDING - arrowSize,
                    0.2f * w + PADDING,
                    0.8f * h - PADDING,
                    arrowLine
                )
                canvas.drawLine(
                    0.2f * w + PADDING + arrowSize,
                    0.8f * h - PADDING + arrowSize,
                    0.2f * w + PADDING,
                    0.8f * h - PADDING,
                    arrowLine
                )
            }
            ActionDir.VERTICAL_DOWN_UP -> {
                arrowSize = if (0.6f * h < 0.11f * w) {
                    0.6f * h
                } else {
                    0.11f * w
                }
                canvas.drawLine(
                    0.8f * w - PADDING,
                    0.8f * h - PADDING,
                    0.8f * w - PADDING,
                    0.2f * h + PADDING,
                    arrowLine
                )
                canvas.drawLine(
                    0.8f * w - PADDING - arrowSize,
                    0.2f * h + PADDING + arrowSize,
                    0.8f * w - PADDING,
                    0.2f * h + PADDING,
                    arrowLine
                )
                canvas.drawLine(
                    0.8f * w - PADDING + arrowSize,
                    0.2f * h + PADDING + arrowSize,
                    0.8f * w - PADDING,
                    0.2f * h + PADDING,
                    arrowLine
                )
            }
            ActionDir.VERTICAL_UP_DOWN -> {
                arrowSize = if (0.6f * h < 0.11f * w) {
                    0.6f * h
                } else {
                    0.11f * w
                }
                canvas.drawLine(
                    0.8f * w - PADDING,
                    0.8f * h - PADDING,
                    0.8f * w - PADDING,
                    0.2f * h + PADDING,
                    arrowLine
                )
                canvas.drawLine(
                    0.8f * w - PADDING - arrowSize,
                    0.8f * h - PADDING - arrowSize,
                    0.8f * w - PADDING,
                    0.8f * h - PADDING,
                    arrowLine
                )
                canvas.drawLine(
                    0.8f * w - PADDING + arrowSize,
                    0.8f * h - PADDING - arrowSize,
                    0.8f * w - PADDING,
                    0.8f * h - PADDING,
                    arrowLine
                )
            }
        }


        if (elementState==TouchElementState.PLAYING_VERBOSE)
        {
            val iconWidth = soundGenerator?.getInstrumentIcon()?.minimumWidth
            val iconHeight = soundGenerator?.getInstrumentIcon()?.minimumHeight
            val descriptionOffset = PADDING.toInt() + 10  + ((cornerRadius)*(1 - 1/ sqrt(2.0f))).toInt()
            if (iconWidth != null && iconHeight != null) {
                soundGenerator?.getInstrumentIcon()?.setBounds(
                    descriptionOffset,
                    descriptionOffset,
                    descriptionOffset + (iconWidth * 0.7).toInt(),
                    descriptionOffset + (iconHeight * 0.7).toInt()
                )
                soundGenerator?.getInstrumentIcon()?.draw(canvas)
                soundGenerator?.name.let {
                    canvas.drawText(
                        it.toString(),
                        descriptionOffset.toFloat(),
                        descriptionOffset + (iconHeight * 0.7).toInt() + smallText.textSize,
                        smallText
                    )
                }
                note.let {
                    canvas.drawText(
                        it.toString(),
                        descriptionOffset.toFloat(),
                        descriptionOffset + (iconHeight * 0.7).toInt()+ smallText.textSize * 2 + 10,
                        smallText
                    )
                }
            }


        }

        if (elementState == TouchElementState.EDITING || elementState == TouchElementState.EDITING_SELECTED) {
            canvas.drawCircle(
                0.0f + EDIT_CIRCLE_OFFSET,
                0.0f + EDIT_CIRCLE_OFFSET,
                EDIT_CIRCLE_OFFSET,
                editDotFill
            )
            canvas.drawCircle(
                w - EDIT_CIRCLE_OFFSET,
                0.0f + EDIT_CIRCLE_OFFSET,
                EDIT_CIRCLE_OFFSET,
                editDotFill
            )
            canvas.drawCircle(
                w - EDIT_CIRCLE_OFFSET,
                h - EDIT_CIRCLE_OFFSET,
                EDIT_CIRCLE_OFFSET,
                editDotFill
            )
            canvas.drawCircle(
                0.0f + EDIT_CIRCLE_OFFSET,
                h - EDIT_CIRCLE_OFFSET,
                EDIT_CIRCLE_OFFSET,
                editDotFill
            )


            editText.getTextBounds("Rotate",0,"Rotate".length,boundsRotate)
            editText.getTextBounds("Set SoundGen",0,"Set SoundGen".length,boundsSetSoundgen)
            editText.getTextBounds("Delete",0,"Delete".length,boundsDelete)
            val editRectangleWidth = 70//IntStream.of(boundsRotate.width(),boundsSetSoundgen.width(),boundsDelete.width()).max().orElse(1)

            rotateRect.left = 40
            rotateRect.right = rotateRect.left + editRectangleWidth + Converter.toPx(3)
            rotateRect.top = EDIT_CIRCLE_OFFSET.toInt() + 50
            rotateRect.bottom = (editText.textSize.toInt() + 8 + rotateRect.top)

            canvas.drawRect(rotateRect, editBoxBackground)
            rotateSymbol?.let {
                it.setBounds(
                    40,
                    EDIT_CIRCLE_OFFSET.toInt() + 50,
                    (40 +50/it.intrinsicHeight.toFloat()*it.intrinsicWidth.toFloat()).toInt(),
                    EDIT_CIRCLE_OFFSET.toInt() + 50 + 50
                )
                it.draw(canvas)
            }

            setSoundgenRect.left = 40
            setSoundgenRect.right = setSoundgenRect.left + editRectangleWidth + Converter.toPx(3)
            setSoundgenRect.top = rotateRect.bottom + 10
            setSoundgenRect.bottom = setSoundgenRect.top + editText.textSize.toInt() + 8

            canvas.drawRect(setSoundgenRect, editBoxBackground)

            editSymbol?.let {
                it.setBounds(
                    40,
                    rotateRect.bottom + 10,
                    (40 +50/it.intrinsicHeight.toFloat()*it.intrinsicWidth.toFloat()).toInt(),
                    rotateRect.bottom + 10 + 50
                )
                it.draw(canvas)
            }


            deleteRect.left = 40
            deleteRect.right = deleteRect.left + editRectangleWidth + Converter.toPx(3)
            deleteRect.top = setSoundgenRect.bottom + 10
            deleteRect.bottom = deleteRect.top + editText.textSize.toInt() + 8

            canvas.drawRect(deleteRect, editBoxBackground)
            deleteSymbol?.let {
                it.setBounds(
                    40,
                    setSoundgenRect.bottom + 10,
                    (40 +50/it.intrinsicHeight.toFloat()*it.intrinsicWidth.toFloat()).toInt(),
                    setSoundgenRect.bottom + 10 + 50
                )
                it.draw(canvas)
            }

        }


    }


    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        cornerRadius = if (w > h) {
            (h / 8.0).toFloat()
        } else {
            (w / 8.0).toFloat()
        }
        super.onSizeChanged(w, h, oldw, oldh)

    }

    fun slideOverEvent(event: MotionEvent)
    {

        var touchVal:Float=-2.0f
        if (actionDir == ActionDir.VERTICAL_DOWN_UP && event.y >= PADDING && event.y <= measuredHeight - PADDING) {
            touchVal = 1.0f - ((event.y- PADDING) / (measuredHeight.toFloat()- 2*PADDING))
        }
        else if (actionDir == ActionDir.VERTICAL_UP_DOWN && event.y >= PADDING && event.y <= measuredHeight - PADDING) {
            touchVal = ((event.y- PADDING) / (measuredHeight.toFloat()- 2*PADDING))
        }
        else if (actionDir == ActionDir.HORIZONTAL_LEFT_RIGHT && event.x >= PADDING && event.x <= measuredWidth - PADDING) {
            touchVal = (event.x- PADDING) / (measuredWidth.toFloat()- 2*PADDING)
        }
        else if (actionDir == ActionDir.HORIZONTAL_RIGHT_LEFT&& event.x >= PADDING && event.x <= measuredWidth - PADDING) {
            touchVal = 1.0f - ((event.x- PADDING) / (measuredWidth.toFloat()- 2*PADDING))
        }
        currentVoice = soundGenerator?.getNextFreeVoice()
        currentVoice?.setMidiChannel(midiChannel)
        if (touchVal>=-1.0f)
        {
            currentVoice?.applyTouchAction(touchVal)

            val midiData=ByteArray(3)
            midiData[0] = (0xB0 + midiChannel).toByte()
            midiData[1] = midiCC.toByte()
            midiData[2] = (touchVal*127.0f).toInt().toByte()
            if (midiData[2]!=midiCCOld) {
                appContext?.rtpMidiServer?.let {
                    if (it.isEnabled)
                    {
                        var sentNotes=0
                        while(sentNotes < (context as TouchSampleSynthMain).rtpMidiNotesRepeat) {
                            appContext.rtpMidiServer?.addToSendQueue(midiData)
                            sentNotes += 1
                        }
                    }
                }
                currentVoice?.sendMidiCC(midiCC,(touchVal*127.0f).toInt())
                midiCCOld=midiData[2]
            }
        }
        note?.value?.let { currentVoice?.setNote(it) }
        currentVoice?.trigger(1.0f)
        appContext?.rtpMidiServer?.let {
            if (it.isEnabled && this.note != null)
            {
                val midiData=ByteArray(3)
                setMidiNoteOn(midiData)
                var sentNotes=0
                while (sentNotes < (context as TouchSampleSynthMain).rtpMidiNotesRepeat)
                {
                    appContext.rtpMidiServer?.addToSendQueue(midiData)
                    sentNotes += 1
                }
                setMidiNoteOff(midiData)
                sentNotes=0
                while (sentNotes < (context as TouchSampleSynthMain).rtpMidiNotesRepeat)
                {
                    appContext.rtpMidiServer?.addToSendQueue(midiData)
                    sentNotes += 1
                }
            }
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (elementState != TouchElementState.EDITING && elementState != TouchElementState.EDITING_SELECTED) {
            if (event?.action?.and(MotionEvent.ACTION_MASK) == MotionEvent.ACTION_DOWN || event?.action?.and(MotionEvent.ACTION_MASK) == MotionEvent.ACTION_POINTER_DOWN) {
                var touchVal:Float=-2.0f
                if (actionDir == ActionDir.VERTICAL_DOWN_UP && event.y >= PADDING && event.y <= measuredHeight - PADDING) {
                    touchVal = 1.0f - ((event.y- PADDING) / (measuredHeight.toFloat()- 2*PADDING))
                }
                else if (actionDir == ActionDir.VERTICAL_UP_DOWN && event.y >= PADDING && event.y <= measuredHeight - PADDING) {
                    touchVal = ((event.y- PADDING) / (measuredHeight.toFloat()- 2*PADDING))
                }
                else if (actionDir == ActionDir.HORIZONTAL_LEFT_RIGHT && event.x >= PADDING && event.x <= measuredWidth - PADDING) {
                    touchVal = (event.x- PADDING) / (measuredWidth.toFloat()- 2*PADDING)
                }
                else if (actionDir == ActionDir.HORIZONTAL_RIGHT_LEFT&& event.x >= PADDING && event.x <= measuredWidth - PADDING) {
                    touchVal = 1.0f - ((event.x- PADDING) / (measuredWidth.toFloat()- 2*PADDING))
                }
                currentVoice = soundGenerator?.getNextFreeVoice()
                currentVoice?.setMidiChannel(midiChannel)
                if (touchVal>=-1.0f)
                {
                    currentVoice?.applyTouchAction(touchVal)

                    val midiData=ByteArray(3)
                    midiData[0] = (0xB0 + midiChannel).toByte()
                    midiData[1] = midiCC.toByte()
                    midiData[2] = (touchVal*127.0f).toInt().toByte()
                    if (midiData[2]!=midiCCOld) {
                        appContext?.rtpMidiServer?.let {
                            if (it.isEnabled)
                            {
                                var sentNotes=0
                                while(sentNotes < (context as TouchSampleSynthMain).rtpMidiNotesRepeat) {
                                    appContext.rtpMidiServer?.addToSendQueue(midiData)
                                    sentNotes += 1
                                }
                            }
                        }
                        currentVoice?.sendMidiCC(midiCC,(touchVal*127.0f).toInt())
                        midiCCOld=midiData[2]
                    }
                }
                performClick()
                appContext?.rtpMidiServer?.let {
                    if (it.isEnabled && this.note != null)
                    {
                        val midiData=ByteArray(3)
                        setMidiNoteOn(midiData)
                        var sentNotes=0
                        while (sentNotes < (context as TouchSampleSynthMain).rtpMidiNotesRepeat)
                        {
                            appContext.rtpMidiServer?.addToSendQueue(midiData)
                            sentNotes += 1
                        }
                    }
                }
                px = event.x
                py = event.y
                return true
            } else if (event?.action?.and(MotionEvent.ACTION_MASK) == MotionEvent.ACTION_UP || event?.action?.and(MotionEvent.ACTION_MASK) == MotionEvent.ACTION_POINTER_UP) {
                outLine.strokeWidth = OUTLINE_STROKE_WIDTH_DEFAULT
                currentVoice?.switchOff(1.0f)
                appContext?.rtpMidiServer?.let {
                    if (it.isEnabled && this.note != null)
                    {
                        val midiData=ByteArray(3)
                        setMidiNoteOff(midiData)
                        var sentNotes=0
                        while (sentNotes < (context as TouchSampleSynthMain).rtpMidiNotesRepeat) {
                            appContext.rtpMidiServer?.addToSendQueue(midiData)
                            sentNotes += 1
                        }

                    }
                }
                currentVoice = null
                invalidate()
                return true
            } else if (event?.action?.and(MotionEvent.ACTION_MASK) == MotionEvent.ACTION_MOVE) {
                var touchVal:Float=-2.0f
                if (event.y <= PADDING || event.y >= measuredHeight - PADDING || event.x < PADDING || event.x >= measuredWidth - PADDING) {
                    outLine.strokeWidth = OUTLINE_STROKE_WIDTH_DEFAULT
                    currentVoice?.let {
                        if (it.isEngaged())
                        {
                            it.switchOff(1.0f)
                            appContext?.rtpMidiServer?.let {midiserver->
                                if (midiserver.isEnabled)
                                {
                                    val midiData=ByteArray(3)
                                    setMidiNoteOff(midiData)
                                    var sentNotes = 0
                                    while (sentNotes < (context as TouchSampleSynthMain).rtpMidiNotesRepeat)
                                    {
                                        midiserver.addToSendQueue(midiData)
                                        sentNotes +=  1
                                    }
                                }
                            }
                            invalidate()
                        }
                    }
                    currentVoice=null
                    return true
                } else if (actionDir == ActionDir.VERTICAL_DOWN_UP && event.y >= PADDING && event.y <= measuredHeight - PADDING) {
                    touchVal = 1.0f - ((event.y- PADDING) / (measuredHeight.toFloat()- 2*PADDING))
                }
                else if (actionDir == ActionDir.VERTICAL_UP_DOWN && event.y >= PADDING && event.y <= measuredHeight - PADDING) {
                    touchVal = ((event.y- PADDING) / (measuredHeight.toFloat()- 2*PADDING))
                }
                else if (actionDir == ActionDir.HORIZONTAL_LEFT_RIGHT && event.x >= PADDING && event.x <= measuredWidth - PADDING) {
                    touchVal = (event.x- PADDING) / (measuredWidth.toFloat()- 2*PADDING)
                }
                else if (actionDir == ActionDir.HORIZONTAL_RIGHT_LEFT&& event.x >= PADDING && event.x <= measuredWidth - PADDING) {
                    touchVal = 1.0f - ((event.x- PADDING) / (measuredWidth.toFloat()- 2*PADDING))
                }

                if (touchVal>=-1.0f)
                {
                    currentVoice?.applyTouchAction(touchVal)

                    val midiData=ByteArray(3)
                    midiData[0] = (0xB0 + midiChannel).toByte()
                    midiData[1] = midiCC.toByte()
                    midiData[2] = (touchVal*127.0f).toInt().toByte()
                    if (midiData[2]!=midiCCOld) {
                        appContext?.rtpMidiServer?.let {
                            if (it.isEnabled)
                            {
                                appContext.rtpMidiServer?.addToSendQueue(
                                    midiData
                                )
                            }
                        }
                        currentVoice?.sendMidiCC(midiCC,(touchVal*127.0f).toInt())
                        midiCCOld=midiData[2]
                    }
                    return true
                }
            }
        } else {
            when (event?.action) {
                MotionEvent.ACTION_DOWN -> {
                    // start a corner drag if a corner has been hit, else move the whole element
                    px = event.x
                    py = event.y
                    if (rotateRect.contains(px.toInt(), py.toInt())) {
                        when(this.actionDir) {
                            ActionDir.HORIZONTAL_LEFT_RIGHT -> {
                                this.actionDir=ActionDir.HORIZONTAL_RIGHT_LEFT
                            }
                            ActionDir.HORIZONTAL_RIGHT_LEFT -> {
                                this.actionDir=ActionDir.VERTICAL_DOWN_UP
                            }
                            ActionDir.VERTICAL_DOWN_UP -> {
                                this.actionDir=ActionDir.VERTICAL_UP_DOWN
                            }
                            ActionDir.VERTICAL_UP_DOWN -> {
                                this.actionDir = ActionDir.HORIZONTAL_LEFT_RIGHT
                            }
                        }
                        dragStart = null
                        invalidate()
                        return true
                    } else if (setSoundgenRect.contains(px.toInt(), py.toInt())) {
                        val editSoundgenerator = EditTouchElementFragmentDialog(
                            this,
                            context
                        )
                        dragStart = null
                        editSoundgenerator.show()
                        this.invalidate()
                        return true
                    } else if (deleteRect.contains(px.toInt(), py.toInt())) {

                        appContext?.let {
                        AlertDialog.Builder(appContext)
                            .setMessage(context.getString(R.string.alert_dialog_really_delete))
                            .setPositiveButton(context.getString(R.string.yes)) { _, _ ->
                                appContext.touchElements.remove(this)
                                (parent as ViewGroup).removeView(this)
                            }
                            .setNegativeButton(context.getString(R.string.no)) { _, _ -> }.create().also {
                                dragStart = null
                                it.show()
                            }
                        }
                        return true
                    } else {
                        val layoutParams: ConstraintLayout.LayoutParams? =
                            this.layoutParams as ConstraintLayout.LayoutParams?
                        if (layoutParams != null) {
                            oldHeight = layoutParams.height
                            oldWidth = layoutParams.width
                            oldTopMargin = layoutParams.topMargin
                            oldLeftMargin = layoutParams.leftMargin
                        }
                        dragStart = isInCorner(event.x, event.y)
                        return true
                    }
                }

                MotionEvent.ACTION_UP -> {
                    if (dragStart != null)
                    {
                        (this.layoutParams as ConstraintLayout.LayoutParams).let {
                            if (abs(it.leftMargin - oldLeftMargin) + abs (it.topMargin - oldTopMargin) < NO_DRAG_TOLERANCE && it.height == oldHeight && it.width == oldWidth)
                            {
                                if (elementState == TouchElementState.EDITING)
                                {
                                    elementState = TouchElementState.EDITING_SELECTED
                                    outLine.strokeWidth = OUTLINE_STROKE_WIDTH_ENGAGED
                                    onSelectedListener?.onTouchElementSelected(this)
                                }
                                else if (elementState == TouchElementState.EDITING_SELECTED)
                                {
                                    elementState = TouchElementState.EDITING
                                    outLine.strokeWidth = OUTLINE_STROKE_WIDTH_DEFAULT
                                    onSelectedListener?.onTouchElementDeselected(this)
                                }
                                invalidate()
                            }
                        }

                        if (!validatePlacement())
                        {
                            val restoredlayoutParams: ConstraintLayout.LayoutParams? =
                                this.layoutParams as ConstraintLayout.LayoutParams?
                            restoredlayoutParams?.height = oldHeight
                            restoredlayoutParams?.width = oldWidth
                            restoredlayoutParams?.leftMargin = oldLeftMargin
                            restoredlayoutParams?.topMargin = oldTopMargin
                            layoutParams = restoredlayoutParams
                            invalidate()
                        }
                    }
                }

                MotionEvent.ACTION_MOVE -> {
                    val layoutParams: ConstraintLayout.LayoutParams? =
                        this.layoutParams as ConstraintLayout.LayoutParams?
                    if (dragStart != null) {
                        when (dragStart) {
                            TouchElementDragAnchor.TOP_LEFT -> {
                                layoutParams!!.leftMargin += event.x.minus(px).toInt()
                                layoutParams!!.width += px.minus(event.x).toInt()
                                layoutParams!!.topMargin += event.y.minus(py).toInt()
                                layoutParams!!.height += py.minus(event.y).toInt()

                            }

                            TouchElementDragAnchor.TOP_RIGHT -> {
                                layoutParams!!.width = oldWidth + event.x.minus(px).toInt()
                                layoutParams.topMargin += event.y.minus(py).toInt()
                                layoutParams.height += py.minus(event.y).toInt()
                            }

                            TouchElementDragAnchor.BOTTOM_RIGHT -> {
                                layoutParams!!.width = oldWidth + event.x.minus(px).toInt()
                                layoutParams.height = oldHeight + event.y.minus(py).toInt()
                            }

                            TouchElementDragAnchor.BOTTOM_LEFT -> {
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
                    return true
                }
            }
            return true
        }
        return false
    }


    override fun performClick(): Boolean {
        note?.value?.let { currentVoice?.setNote(it) }
        if (currentVoice?.switchOn(1.0f)==true)
        {
            outLine.strokeWidth = OUTLINE_STROKE_WIDTH_ENGAGED
        }
        invalidate()
        return super.performClick()
    }

    private fun isInCorner(x: Float, y: Float): TouchElementDragAnchor {
        val w = width.toFloat()
        val h = height.toFloat()
        if (((x - (0.0f + EDIT_CIRCLE_OFFSET)) * (x - (0.0f + EDIT_CIRCLE_OFFSET))
                    + (y - (0.0f + EDIT_CIRCLE_OFFSET)) * (y - (0.0f + EDIT_CIRCLE_OFFSET)))
            < EDIT_CIRCLE_OFFSET * EDIT_CIRCLE_OFFSET
        ) {
            return TouchElementDragAnchor.TOP_LEFT
        } else if (((x - (w - EDIT_CIRCLE_OFFSET)) * (x - (w - EDIT_CIRCLE_OFFSET))
                    + (y - (0.0f + EDIT_CIRCLE_OFFSET)) * (y - (0.0f + EDIT_CIRCLE_OFFSET)))
            < EDIT_CIRCLE_OFFSET * EDIT_CIRCLE_OFFSET
        ) {
            return TouchElementDragAnchor.TOP_RIGHT
        } else if (((x - (w - EDIT_CIRCLE_OFFSET)) * (x - (w - EDIT_CIRCLE_OFFSET))
                    + (y - (h - EDIT_CIRCLE_OFFSET)) * (y - (h - EDIT_CIRCLE_OFFSET)))
            < EDIT_CIRCLE_OFFSET * EDIT_CIRCLE_OFFSET
        ) {
            return TouchElementDragAnchor.BOTTOM_RIGHT
        } else if (((x - (0.0f + EDIT_CIRCLE_OFFSET)) * (x - (0.0f + EDIT_CIRCLE_OFFSET))
                    + (y - (h - EDIT_CIRCLE_OFFSET)) * (y - (h - EDIT_CIRCLE_OFFSET)))
            < EDIT_CIRCLE_OFFSET * EDIT_CIRCLE_OFFSET
        ) {
            return TouchElementDragAnchor.BOTTOM_LEFT
        } else {
            return TouchElementDragAnchor.BODY
        }
    }


    private fun setMidiNoteOn(midiData: ByteArray)
    {
        midiData[0] = (0x90 + midiChannel).toByte()
        midiData[1] = (this.note!!.value+69).toInt().toByte()
        midiData[2] = 0x7F.toByte()
    }

    private fun setMidiNoteOff(midiData: ByteArray)
    {
        midiData[0] = (0x80 + midiChannel).toByte()
        midiData[1] = (this.note!!.value+69).toInt().toByte()
        midiData[2] = 0x7F.toByte()
    }

    fun validatePlacement(): Boolean
    {
        val screenWidth: Int
        val screenHeight: Int
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.Q) {
            screenWidth =
                (context as Activity).windowManager.currentWindowMetrics.bounds.width()
            screenHeight =
                (context as Activity).windowManager.currentWindowMetrics.bounds.height()
        }
        else
        {
            screenWidth = (context as Activity).windowManager.defaultDisplay.width
            screenHeight = (context as Activity).windowManager.defaultDisplay.height
        }
        val layoutParams: ConstraintLayout.LayoutParams? =
            this.layoutParams as ConstraintLayout.LayoutParams?
        layoutParams?.let {
            return it.topMargin in 0..screenHeight - it.height
                    && it.leftMargin in 0 .. screenWidth - it.width
                    && it.width > 0 && it.height > 0
        }
        return false
    }

    fun setEditmode(mode: Boolean) {
        if (mode && elementState!=TouchElementState.EDITING && elementState != TouchElementState.EDITING_SELECTED) {
            this.elementState = TouchElementState.EDITING
            invalidate()
        } else if (!mode && (elementState == TouchElementState.EDITING || elementState == TouchElementState.EDITING_SELECTED)) {
            this.elementState = defaultState
            this.outLine.strokeWidth= OUTLINE_STROKE_WIDTH_DEFAULT
            invalidate()
        }
    }

    fun isEditing(): Boolean
    {
        return this.elementState == TouchElementState.EDITING
                || this.elementState == TouchElementState.EDITING_SELECTED
    }

    fun setDefaultMode(mode: TouchElementState)
    {
        this.defaultState = mode
        if (this.elementState != TouchElementState.EDITING)
        {
            this.elementState = mode
        }
    }

    fun asRectangle(): Rectangle
    {
        return Rectangle(
            Point(
                (layoutParams as ConstraintLayout.LayoutParams).leftMargin.toDouble(),
                (layoutParams as ConstraintLayout.LayoutParams).topMargin.toDouble()),
            Point((layoutParams as ConstraintLayout.LayoutParams).leftMargin.toDouble() + (layoutParams as ConstraintLayout.LayoutParams).width.toDouble(),
                (layoutParams as ConstraintLayout.LayoutParams).topMargin.toDouble()+ (layoutParams as ConstraintLayout.LayoutParams).height.toDouble())
        )
    }

    fun isInside(pt: Point): Boolean
    {
        return pt.x >  (layoutParams as ConstraintLayout.LayoutParams).leftMargin + PADDING &&
                pt.x < (layoutParams as ConstraintLayout.LayoutParams).leftMargin + (layoutParams as ConstraintLayout.LayoutParams).width - PADDING &&
                pt.y > (layoutParams as ConstraintLayout.LayoutParams).topMargin + PADDING &&
                pt.y < (layoutParams as ConstraintLayout.LayoutParams).topMargin + (layoutParams as ConstraintLayout.LayoutParams).height - PADDING

    }

    override fun equals(other: Any?): Boolean {
        if (other is TouchElement)
        {
            return this.asRectangle() == other.asRectangle() &&
                    this.soundGenerator?.name == other.soundGenerator?.name &&
                    this.soundGenerator?.getType() == other.soundGenerator?.getType() &&
                    this.note?.index == other.note?.index
        }
        return false
    }
}


