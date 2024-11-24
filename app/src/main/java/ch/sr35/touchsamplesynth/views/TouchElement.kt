package ch.sr35.touchsamplesynth.views

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Rect
import android.os.Build
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
import ch.sr35.touchsamplesynth.graphics.AutoContraster
import ch.sr35.touchsamplesynth.graphics.Converter
import ch.sr35.touchsamplesynth.graphics.Point
import ch.sr35.touchsamplesynth.graphics.Rectangle
import ch.sr35.touchsamplesynth.graphics.RgbColor
import ch.sr35.touchsamplesynth.model.importDoneFlag
import com.google.android.material.color.MaterialColors
import java.io.Serializable
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.sqrt

const val PADDING: Float = 32.0f
const val EDIT_CIRCLE_OFFSET = 24.0f
const val OUTLINE_STROKE_WIDTH_DEFAULT = 7.8f
const val OUTLINE_STROKE_WIDTH_ENGAGED = 20.4f
const val NO_DRAG_TOLERANCE = 5

val ARROW_DR = Converter.toPx(6)
val ARROW_Q = 7.0
val ARROW_BMAX = Converter.toPx(12)

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

    private class ArrowDefinition(val arrowB: Int, val arrowDr: Int, val onlyTriangle: Boolean)

    var actionDir: ActionDir = ActionDir.HORIZONTAL_LEFT_RIGHT
    private val outLine: Paint = Paint()
    private val fillColor: Paint = Paint()
    private var px: Float = 0.0f
    private var py: Float = 0.0f
    private var defaultState = TouchElementState.PLAYING

    private val arrowPath = Path()
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
    private var currentVoices = ArrayList<MusicalSoundGenerator>()
    var notes = ArrayList<MusicalPitch>()
    var midiChannel: Int=0
    var midiCC: Int=3
    private var midiCCOld: Byte=0
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

        fillColor.color = MaterialColors.getColor(this,R.attr.touchElementColor)
        fillColor.style = Paint.Style.FILL
        appContext = if (context is TouchSampleSynthMain) {
            context
        } else {
            null
        }

        arrowLine.color = AutoContraster().generateContrastingColor(RgbColor.fromColorInt(fillColor.color)).toColorInt() //MaterialColors.getColor(this,R.attr.touchElementLine)
        arrowLine.style = Paint.Style.FILL
        arrowLine.isAntiAlias = true

        outLine.color = arrowLine.color
        outLine.strokeWidth = OUTLINE_STROKE_WIDTH_DEFAULT
        outLine.style = Paint.Style.STROKE
        outLine.isAntiAlias = true
    }

    fun getMainColor(): RgbColor
    {
        return RgbColor.fromColorInt(fillColor.color)
    }

    fun setColor(color: RgbColor)
    {
        fillColor.color = color.toColorInt()
        arrowLine.color = AutoContraster().generateContrastingColor(RgbColor.fromColorInt(fillColor.color)).toColorInt()
        outLine.color = arrowLine.color
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val w = layoutParams.width.toFloat()
        val h = layoutParams.height.toFloat()

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

                val arrowDef = computeArrowDefinition(h.toInt(),w.toInt())
                arrowPath.reset()
                arrowPath.moveTo(w - PADDING - arrowDef.arrowDr - arrowDef.arrowB,h/2 - arrowDef.arrowB )
                arrowPath.lineTo(w - PADDING - arrowDef.arrowDr,h/2)
                arrowPath.lineTo(w - PADDING - arrowDef.arrowDr - arrowDef.arrowB,h/2 + arrowDef.arrowB )
                if (!arrowDef.onlyTriangle) {
                    arrowPath.lineTo(PADDING + arrowDef.arrowDr,h / 2 + arrowDef.arrowB )
                    arrowPath.lineTo(PADDING + arrowDef.arrowDr,h / 2 - arrowDef.arrowB )
                }
                arrowPath.close()
                canvas.drawPath(arrowPath,arrowLine)
            }
            ActionDir.HORIZONTAL_RIGHT_LEFT -> {
                val arrowDef = computeArrowDefinition(h.toInt(),w.toInt())
                arrowPath.reset()
                arrowPath.moveTo(PADDING + arrowDef.arrowDr + arrowDef.arrowB,h/2 + arrowDef.arrowB)
                arrowPath.lineTo(PADDING + arrowDef.arrowDr, h/2 )
                arrowPath.lineTo(PADDING + arrowDef.arrowDr + arrowDef.arrowB,h/2 - arrowDef.arrowB)
                if (!arrowDef.onlyTriangle) {
                    arrowPath.lineTo(w - PADDING - arrowDef.arrowDr,h / 2 - arrowDef.arrowB)
                    arrowPath.lineTo( w - PADDING - arrowDef.arrowDr,h / 2 + arrowDef.arrowB)
                }
                arrowPath.close()
                canvas.drawPath(arrowPath,arrowLine)
            }
            ActionDir.VERTICAL_DOWN_UP -> {
                val arrowDef = computeArrowDefinition(w.toInt(),h.toInt())
                arrowPath.reset()
                arrowPath.moveTo(w/2 - arrowDef.arrowB, PADDING + arrowDef.arrowDr + arrowDef.arrowB)
                arrowPath.lineTo(w/2, PADDING + arrowDef.arrowDr)
                arrowPath.lineTo(w/2 + arrowDef.arrowB, PADDING + arrowDef.arrowDr + arrowDef.arrowB)
                if (!arrowDef.onlyTriangle) {
                    arrowPath.lineTo(w / 2 + arrowDef.arrowB, h - PADDING - arrowDef.arrowDr)
                    arrowPath.lineTo(w / 2 - arrowDef.arrowB, h - PADDING - arrowDef.arrowDr)
                }
                arrowPath.close()
                canvas.drawPath(arrowPath,arrowLine)
            }
            ActionDir.VERTICAL_UP_DOWN -> {
                val arrowDef = computeArrowDefinition(w.toInt(),h.toInt())
                arrowPath.reset()
                arrowPath.moveTo(w/2 + arrowDef.arrowB, h - PADDING - arrowDef.arrowDr - arrowDef.arrowB)
                arrowPath.lineTo(w/2, h - PADDING - arrowDef.arrowDr )
                arrowPath.lineTo(w/2 - arrowDef.arrowB, h - PADDING - arrowDef.arrowDr - arrowDef.arrowB)
                if (!arrowDef.onlyTriangle) {
                    arrowPath.lineTo(w / 2 - arrowDef.arrowB, PADDING + arrowDef.arrowDr)
                    arrowPath.lineTo(w / 2 + arrowDef.arrowB, PADDING + arrowDef.arrowDr)
                }
                arrowPath.close()
                canvas.drawPath(arrowPath,arrowLine)
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
                var notetext = ""
                notes.forEach {
                    notetext += ",$it "
                }
                notetext = notetext.substring(1)
                canvas.drawText(
                    notetext,
                    descriptionOffset.toFloat(),
                    descriptionOffset + (iconHeight * 0.7).toInt()+ smallText.textSize * 2 + 10,
                    smallText
                )

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
            setSoundgenRect.top = EDIT_CIRCLE_OFFSET.toInt() + 50
            setSoundgenRect.bottom = setSoundgenRect.top + editText.textSize.toInt() + 8

            canvas.drawRect(setSoundgenRect, editBoxBackground)

            editSymbol?.let {
                it.setBounds(
                    40,
                    EDIT_CIRCLE_OFFSET.toInt() + 50,
                    (40 +50/it.intrinsicHeight.toFloat()*it.intrinsicWidth.toFloat()).toInt(),
                    EDIT_CIRCLE_OFFSET.toInt() + 50 + 50
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

    private fun computeArrowDefinition(sizePerpendicularToArrow: Int,sizeAlongArrow: Int): ArrowDefinition
    {
        val arrowDr: Int
        var arrowB: Int
        var onlyTriangle = false
        arrowB = if ((sizePerpendicularToArrow-2* PADDING) / ARROW_Q < ARROW_BMAX) {
            (((sizePerpendicularToArrow-2* PADDING) / ARROW_Q).toInt())
        } else {
            ARROW_BMAX
        }
        if ((sizeAlongArrow-2*PADDING) < 2* ARROW_DR + arrowB)
        {
            arrowDr = max((((sizeAlongArrow- 2* PADDING)-arrowB)/2).toInt(),0)
            if (arrowDr <= 0)
            {
                arrowB = (sizeAlongArrow-2* PADDING).toInt()
                onlyTriangle = true
            }
        }
        else
        {
            arrowDr = ARROW_DR
        }
        return ArrowDefinition(arrowB,arrowDr,onlyTriangle)
    }


    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        cornerRadius = if (w > h) {
            if (h < Converter.toPx(24)) {
                (h / 2.0).toFloat()
            }
            else
            {
                Converter.toPx(12).toFloat()
            }
        } else {
            if (w  < Converter.toPx(24)) {
                (w / 2.0).toFloat()
            }
            else {
                Converter.toPx(12).toFloat()
            }
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

        currentVoices.clear()
        var firstnote = true
        notes.forEach { currentNote ->
            soundGenerator?.getNextFreeVoice()?.let {
                currentVoices.add(it)

                if (firstnote)
                {
                    firstnote=false
                    it.setMidiChannel(midiChannel)

                    if (touchVal>=-1.0f)
                    {
                        it.applyTouchAction(touchVal)

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
                            it.sendMidiCC(midiCC,(touchVal*127.0f).toInt())
                            midiCCOld=midiData[2]
                        }
                    }
                }
                it.setNote(currentNote.value)
                it.trigger(1.0f)
                appContext?.rtpMidiServer?.let {
                    if (it.isEnabled) {
                        val midiData = ByteArray(3)
                        setMidiNoteOn(midiData,currentNote)
                        var sentNotes = 0
                        while (sentNotes < (context as TouchSampleSynthMain).rtpMidiNotesRepeat) {
                            appContext.rtpMidiServer?.addToSendQueue(midiData)
                            sentNotes += 1
                        }
                        setMidiNoteOff(midiData,currentNote)
                        sentNotes = 0
                        while (sentNotes < (context as TouchSampleSynthMain).rtpMidiNotesRepeat) {
                            appContext.rtpMidiServer?.addToSendQueue(midiData)
                            sentNotes += 1
                        }
                    }
                }
            }
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (elementState != TouchElementState.EDITING && elementState != TouchElementState.EDITING_SELECTED) {
            if (event?.action?.and(MotionEvent.ACTION_MASK) == MotionEvent.ACTION_DOWN || event?.action?.and(MotionEvent.ACTION_MASK) == MotionEvent.ACTION_POINTER_DOWN) {
                handleActionDownInPlayMode(event)
            } else if (event?.action?.and(MotionEvent.ACTION_MASK) == MotionEvent.ACTION_UP || event?.action?.and(MotionEvent.ACTION_MASK) == MotionEvent.ACTION_POINTER_UP) {
                handleActionUpInPlayMode()
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


    override fun performClick(): Boolean {
        /*currentVoices.clear()
        notes.forEach { currentNote ->
            soundGenerator?.getNextFreeVoice()?.let {
                it.setNote(currentNote.value)
                if (it.switchOn(1.0f)) {
                    outLine.strokeWidth = OUTLINE_STROKE_WIDTH_ENGAGED
                }
                currentVoices.add(it)
            }
        }
        invalidate()*/
        return super.performClick()
    }

    private fun isInCorner(x: Float, y: Float): TouchElementDragAnchor {
        val w = width.toFloat()
        val h = height.toFloat()
        if (elementState == TouchElementState.EDITING_SELECTED) {
            return TouchElementDragAnchor.BODY
        }
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

    private fun handleActionDownInPlayMode(event: MotionEvent): Boolean
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
        var firstnote = true
        notes.forEach { currentNote ->
            soundGenerator?.getNextFreeVoice()?.let {
                currentVoices.add(it)
                it.setMidiChannel(midiChannel)

                if (touchVal >= -1.0f) {
                    it.applyTouchAction(touchVal)
                    if (firstnote) {
                        val midiData = ByteArray(3)
                        midiData[0] = (0xB0 + midiChannel).toByte()
                        midiData[1] = midiCC.toByte()
                        midiData[2] = (touchVal * 127.0f).toInt().toByte()
                        if (midiData[2] != midiCCOld) {
                            appContext?.rtpMidiServer?.let {
                                if (it.isEnabled) {
                                    var sentNotes = 0
                                    while (sentNotes < (context as TouchSampleSynthMain).rtpMidiNotesRepeat) {
                                        appContext.rtpMidiServer?.addToSendQueue(midiData)
                                        sentNotes += 1
                                    }
                                }
                            }
                            it.sendMidiCC(midiCC, (touchVal * 127.0f).toInt())
                            midiCCOld = midiData[2]
                        }
                        firstnote = false
                    }
                }

                appContext?.rtpMidiServer?.let { rtpMidiServer ->
                    if (rtpMidiServer.isEnabled) {
                        val midiData=ByteArray(3)
                        setMidiNoteOn(midiData,currentNote)
                        var sentNotes=0
                        while (sentNotes < (context as TouchSampleSynthMain).rtpMidiNotesRepeat)
                        {
                            appContext.rtpMidiServer?.addToSendQueue(midiData)
                            sentNotes += 1
                        }
                    }
                }

                it.setNote(currentNote.value)
                if (it.switchOn(1.0f)) {
                    outLine.strokeWidth = OUTLINE_STROKE_WIDTH_ENGAGED
                }
            }
        }
        performClick()
        invalidate()
        px = event.x
        py = event.y

        return true
    }

    private fun handleActionUpInPlayMode(): Boolean
    {
        outLine.strokeWidth = OUTLINE_STROKE_WIDTH_DEFAULT
        currentVoices.forEach { it.switchOff(1.0f) }
        appContext?.rtpMidiServer?.let {
            if (it.isEnabled )
            {
                notes.forEach { currentNote ->
                    val midiData = ByteArray(3)
                    setMidiNoteOff(midiData,currentNote)
                    var sentNotes = 0
                    while (sentNotes < (context as TouchSampleSynthMain).rtpMidiNotesRepeat) {
                        appContext.rtpMidiServer?.addToSendQueue(midiData)
                        sentNotes += 1
                    }
                }
            }
        }
        currentVoices.clear()
        invalidate()
        return true
    }

    private fun handleActionMoveInPlayMode(event: MotionEvent): Boolean
    {
        var touchVal:Float=-2.0f
        if (event.y <= PADDING || event.y >= measuredHeight - PADDING || event.x < PADDING || event.x >= measuredWidth - PADDING) {
            outLine.strokeWidth = OUTLINE_STROKE_WIDTH_DEFAULT
            currentVoices.forEach  {
                if (it.isEngaged())
                {
                    it.switchOff(1.0f)

                    invalidate()
                }
            }
            appContext?.rtpMidiServer?.let {midiserver->
                if (midiserver.isEnabled) {
                    notes.forEach { currentNote ->
                        val midiData = ByteArray(3)
                        setMidiNoteOff(midiData,currentNote)
                        var sentNotes = 0
                        while (sentNotes < (context as TouchSampleSynthMain).rtpMidiNotesRepeat) {
                            midiserver.addToSendQueue(midiData)
                            sentNotes += 1
                        }
                    }
                }
            }
            currentVoices.clear()
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
        var firstnote = true
        if (touchVal>=-1.0f)
        {
            currentVoices.forEach {
                it.applyTouchAction(touchVal)

                if (firstnote) {
                    val midiData = ByteArray(3)
                    midiData[0] = (0xB0 + midiChannel).toByte()
                    midiData[1] = midiCC.toByte()
                    midiData[2] = (touchVal * 127.0f).toInt().toByte()
                    if (midiData[2] != midiCCOld) {
                        appContext?.rtpMidiServer?.let {
                            if (it.isEnabled) {
                                appContext.rtpMidiServer?.addToSendQueue(
                                    midiData
                                )
                            }
                        }
                        it.sendMidiCC(midiCC, (touchVal * 127.0f).toInt())
                        midiCCOld = midiData[2]
                    }
                    firstnote = false
                }
            }
            return true
        }
        return false
    }

        private fun handleActionDownInEditMode(event: MotionEvent)
        {
            // start a corner drag if a corner has been hit, else move the whole element
            px = event.x
            py = event.y
            if (setSoundgenRect.contains(px.toInt(), py.toInt())  && elementState != TouchElementState.EDITING_SELECTED) {
                val editSoundgenerator = EditTouchElementFragmentDialog(
                    this,
                    context
                )
                dragStart = null
                editSoundgenerator.show()
                this.invalidate()

            } else if (deleteRect.contains(px.toInt(), py.toInt()) && elementState != TouchElementState.EDITING_SELECTED) {

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
            }
        }

    private fun handleActionUpInEditMode(): Boolean
    {
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
        return true
    }

    private fun handleActionMoveInEditMode(event: MotionEvent): Boolean
    {
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

    private fun setMidiNoteOn(midiData: ByteArray,note: MusicalPitch)
    {
        midiData[0] = (0x90 + midiChannel).toByte()
        midiData[1] = (note.value+69).toInt().toByte()
        midiData[2] = 0x7F.toByte()
    }

    private fun setMidiNoteOff(midiData: ByteArray,note: MusicalPitch)
    {
        midiData[0] = (0x80 + midiChannel).toByte()
        midiData[1] = (note.value+69).toInt().toByte()
        midiData[2] = 0x7F.toByte()
    }

    private fun validatePlacement(): Boolean
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

    fun setEditmode(mode: TouchElementState) {
        if (mode == TouchElementState.EDITING ) {
            this.elementState = mode
            this.outLine.strokeWidth= OUTLINE_STROKE_WIDTH_DEFAULT
        }
        else if (mode == TouchElementState.EDITING_SELECTED){
            this.elementState = mode
            this.outLine.strokeWidth= OUTLINE_STROKE_WIDTH_ENGAGED
        }
        else if (elementState == TouchElementState.EDITING || elementState == TouchElementState.EDITING_SELECTED) {
            this.elementState = defaultState
            this.outLine.strokeWidth= OUTLINE_STROKE_WIDTH_DEFAULT
        }
        invalidate()
    }

    fun setDefaultmode()
    {
        this.elementState = defaultState
        this.outLine.strokeWidth= OUTLINE_STROKE_WIDTH_DEFAULT
        invalidate()
    }

    fun isEditing(): Boolean
    {
        return this.elementState == TouchElementState.EDITING
                || this.elementState == TouchElementState.EDITING_SELECTED
    }

    fun defineDefaultMode(mode: TouchElementState)
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

    fun isInside(pt: Point, padding: Float = PADDING): Boolean
    {
        return pt.x >  (layoutParams as ConstraintLayout.LayoutParams).leftMargin + padding &&
                pt.x < (layoutParams as ConstraintLayout.LayoutParams).leftMargin + (layoutParams as ConstraintLayout.LayoutParams).width - padding &&
                pt.y > (layoutParams as ConstraintLayout.LayoutParams).topMargin + padding &&
                pt.y < (layoutParams as ConstraintLayout.LayoutParams).topMargin + (layoutParams as ConstraintLayout.LayoutParams).height - padding
    }

    override fun equals(other: Any?): Boolean {
        if (other is TouchElement)
        {
            return this.asRectangle() == other.asRectangle() &&
                    this.soundGenerator?.name == other.soundGenerator?.name &&
                    this.soundGenerator?.getType() == other.soundGenerator?.getType() &&
                    this.notes.all { other.notes.contains(it) }
        }
        return false
    }

    override fun hashCode(): Int {
        return this.soundGenerator?.name.hashCode() + this.notes.hashCode() + this.soundGenerator?.getType().hashCode()
    }
}


