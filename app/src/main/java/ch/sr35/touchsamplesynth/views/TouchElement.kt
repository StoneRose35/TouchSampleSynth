package ch.sr35.touchsamplesynth.views

import android.app.Activity
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
import androidx.appcompat.content.res.AppCompatResources
import androidx.constraintlayout.widget.ConstraintLayout
import ch.sr35.touchsamplesynth.MusicalPitch
import ch.sr35.touchsamplesynth.R
import ch.sr35.touchsamplesynth.TouchElementSelectedListener
import ch.sr35.touchsamplesynth.TouchSampleSynthMain
import ch.sr35.touchsamplesynth.audio.instruments.InstrumentI
import ch.sr35.touchsamplesynth.audio.MusicalSoundGenerator
import ch.sr35.touchsamplesynth.graphics.AutoContraster
import ch.sr35.touchsamplesynth.graphics.Converter
import ch.sr35.touchsamplesynth.graphics.Point
import ch.sr35.touchsamplesynth.graphics.Rectangle
import ch.sr35.touchsamplesynth.graphics.RgbColor
import ch.sr35.touchsamplesynth.handlers.TouchAction
import ch.sr35.touchsamplesynth.handlers.TouchElementHandler
import com.google.android.material.color.MaterialColors
import java.io.Serializable
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

open class TouchElement(context: Context, attributeSet: AttributeSet?) :
    View(context, attributeSet) {

    enum class ActionDir: Serializable {
        HORIZONTAL_LR_VERTICAL_UD,
        HORIZONTAL_LR_VERTICAL_DU,
        HORIZONTAL_RL_VERTICAL_UD,
        HORIZONTAL_RL_VERTICAL_DU
    }

    enum class TouchElementState {
        PLAYING,
        PLAYING_VERBOSE,
        EDITING,
        EDITING_SELECTED
    }

    enum class TouchMode {
        MOMENTARY,
        TOGGLED
    }

    enum class TouchElementDragAnchor {
        BODY,
        TOP_LEFT,
        TOP_RIGHT,
        BOTTOM_LEFT,
        BOTTOM_RIGHT
    }

    class ArrowDefinition(val arrowB: Int, val arrowDr: Int, val onlyTriangle: Boolean)

    var actionDir: ActionDir = ActionDir.HORIZONTAL_LR_VERTICAL_DU
    val outLine: Paint = Paint()
    val fillColor: Paint = Paint()
    var px: Float = 0.0f
    var py: Float = 0.0f
    var padding: Float = PADDING
    private var defaultState = TouchElementState.PLAYING
    var touchMode = TouchMode.MOMENTARY
    var isEngaged = false

    private val arrowPath = Path()
    private val outlinePath = Path()
    val contrastingFill: Paint = Paint()
    private val editDotFill: Paint = Paint()
    private val editBoxBackground: Paint = Paint()
    private val editText: Paint = Paint()
    private val smallText: Paint = Paint()
    private var cornerRadius = 0.0f
    var dragStart: TouchElementDragAnchor? = null

    var oldWidth: Int = 0
    var oldHeight: Int = 0
    var oldLeftMargin: Int = 0
    var oldTopMargin: Int = 0
    var elementState: TouchElementState = defaultState
    private var soundGenerator: InstrumentI? = null
    var currentVoices = ArrayList<MusicalSoundGenerator>()
    var notes = ArrayList<MusicalPitch>()
    var midiChannel: Int=0
    var midiCCA: Int=3
    var midiCCAOld: Byte=0
    var midiCCB: Int=4
    var midiCCBOld: Byte=0
    var setSoundgenRect: Rect = Rect()
    var deleteRect: Rect = Rect()
    private val boundsRotate = Rect()
    private val boundsSetSoundgen = Rect()
    private val boundsDelete = Rect()
    val appContext: TouchSampleSynthMain?
    protected var touchElementHandler: TouchElementHandler

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

        contrastingFill.color = AutoContraster().generateContrastingColor(RgbColor.fromColorInt(fillColor.color)).toColorInt()
        contrastingFill.style = Paint.Style.FILL
        contrastingFill.isAntiAlias = true

        outLine.color = contrastingFill.color
        outLine.strokeWidth = OUTLINE_STROKE_WIDTH_DEFAULT
        outLine.style = Paint.Style.STROKE
        outLine.isAntiAlias = true

        context.obtainStyledAttributes(attributeSet, R.styleable.TouchElement).also {
            padding = it.getFloat(
                R.styleable.TouchElement_touchElementPadding,
                PADDING
            )
            it.recycle()
        }

        touchElementHandler = TouchElementHandler(this)
    }

    fun getMainColor(): RgbColor
    {
        return RgbColor.fromColorInt(fillColor.color)
    }


    fun setColor(color: RgbColor)
    {
        fillColor.color = color.toColorInt()
        contrastingFill.color = AutoContraster().generateContrastingColor(RgbColor.fromColorInt(fillColor.color)).toColorInt()
        outLine.color = contrastingFill.color
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val w = measuredWidth
        val h = measuredHeight

        // draw oval
        if (touchMode == TouchMode.MOMENTARY) {
            canvas.drawRoundRect(
                0.0f + padding,
                0.0f + padding,
                w - padding,
                h - padding,
                cornerRadius,
                cornerRadius,
                fillColor
            )
            canvas.drawRoundRect(
                0.0f + padding,
                0.0f + padding,
                w - padding,
                h - padding,
                cornerRadius,
                cornerRadius,
                outLine
            )
        } else if (touchMode == TouchMode.TOGGLED) {
            outlinePath.reset()
            outlinePath.moveTo(0.0f + padding + cornerRadius, 0.0f + padding)
            outlinePath.lineTo(w - padding - cornerRadius, 0.0f + padding)
            outlinePath.lineTo(w - padding, 0.0f + padding + cornerRadius)
            outlinePath.lineTo(w - padding, h - padding - cornerRadius)
            outlinePath.lineTo(w - padding - cornerRadius, h - padding)
            outlinePath.lineTo(0.0f + padding + cornerRadius, h - padding)
            outlinePath.lineTo(0.0f + padding, h - padding - cornerRadius)
            outlinePath.lineTo(0.0f + padding, 0.0f + padding + cornerRadius)
            outlinePath.lineTo(0.0f + padding + cornerRadius, 0.0f + padding)

            canvas.drawPath(outlinePath, fillColor)
            canvas.drawPath(outlinePath, outLine)
        }

        // draw action arrow
        when(actionDir) {
            ActionDir.HORIZONTAL_LR_VERTICAL_DU -> {
                drawArrowLeftRight(canvas)
                drawArrowDownUp(canvas)
            }
            ActionDir.HORIZONTAL_RL_VERTICAL_DU -> {
                drawArrowRightLeft(canvas)
                drawArrowDownUp(canvas)
            }
            ActionDir.HORIZONTAL_LR_VERTICAL_UD -> {
                drawArrowLeftRight(canvas)
                drawArrowUpDown(canvas)
            }
            ActionDir.HORIZONTAL_RL_VERTICAL_UD -> {
                drawArrowRightLeft(canvas)
                drawArrowUpDown(canvas)
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

    protected fun computeArrowDefinition(sizePerpendicularToArrow: Int,sizeAlongArrow: Int): ArrowDefinition
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
        val touchActionHorizontal = TouchAction(Point(event.x.toDouble(),event.y.toDouble()))
        val touchActionVertical = TouchAction(Point(event.x.toDouble(),event.y.toDouble()))

        if ((actionDir == ActionDir.HORIZONTAL_LR_VERTICAL_DU || actionDir == ActionDir.HORIZONTAL_RL_VERTICAL_DU) && event.y >= PADDING && event.y <= measuredHeight - PADDING) {
            touchActionVertical.absoluteValue = 1.0f - ((event.y- PADDING) / (measuredHeight.toFloat()- 2* PADDING))
            touchActionVertical.relativeValue = ((touchActionVertical.startPoint.y.toFloat() - event.y) / (measuredHeight.toFloat()- 2* PADDING))
        }
        else if (event.y >= PADDING && event.y <= measuredHeight - PADDING) {
            touchActionVertical.absoluteValue = ((event.y- PADDING) / (measuredHeight.toFloat()- 2* PADDING))
            touchActionVertical.relativeValue = ((event.y-touchActionVertical.startPoint.y.toFloat()) / (measuredHeight.toFloat()- 2* PADDING))
        }
        if ((actionDir == ActionDir.HORIZONTAL_LR_VERTICAL_DU || actionDir == ActionDir.HORIZONTAL_LR_VERTICAL_UD) && event.x >= PADDING && event.x <= measuredWidth - PADDING) {
            touchActionHorizontal.absoluteValue = (event.x- PADDING) / (measuredWidth.toFloat()- 2* PADDING)
            touchActionHorizontal.relativeValue = (event.x-touchActionHorizontal.startPoint.x.toFloat()) / (measuredWidth.toFloat()- 2* PADDING)
        }
        else if (event.x >= PADDING && event.x <= measuredWidth - PADDING) {
            touchActionHorizontal.absoluteValue = 1.0f - (event.x- PADDING) / (measuredWidth.toFloat()- 2* PADDING)
            touchActionHorizontal.relativeValue = (touchActionHorizontal.startPoint.x.toFloat() - event.x) / (measuredWidth.toFloat()- 2* PADDING)
        }

        currentVoices.clear()
        var firstnote = true
        notes.forEach { currentNote ->
            soundGenerator?.getNextFreeVoice()?.let {
                currentVoices.add(it)

                if (soundGenerator!!.horizontalToActionB)
                {
                    it.applyTouchActionB(touchActionHorizontal.relativeValue)
                    it.applyTouchActionA(touchActionHorizontal.absoluteValue)
                }
                else
                {
                    it.applyTouchActionB(touchActionVertical.relativeValue)
                    it.applyTouchActionA(touchActionVertical.absoluteValue)
                }

                if (firstnote)
                {
                    it.setMidiChannel(midiChannel)
                    it.setMidiChannel(midiChannel)
                    val midiData = ByteArray(3)
                    midiData[0] = (0xB0 + midiChannel).toByte()
                    midiData[1] = midiCCA.toByte()
                    midiData[2] = (touchActionHorizontal.absoluteValue * 127.0f).toInt().toByte()
                    if (midiData[2] != midiCCAOld) {
                        appContext?.rtpMidiServer?.let {
                            if (it.isEnabled) {
                                var sentNotes = 0
                                while (sentNotes < (appContext).rtpMidiNotesRepeat) {
                                    appContext.rtpMidiServer?.addToSendQueue(midiData)
                                    sentNotes += 1
                                }
                            }
                        }
                        it.sendMidiCC(midiCCA, (touchActionHorizontal.absoluteValue * 127.0f).toInt())
                        midiCCAOld = midiData[2]
                    }
                    midiData[1] = midiCCB.toByte()
                    midiData[2] = (touchActionVertical.absoluteValue * 127.0f).toInt().toByte()
                    if (midiData[2] != midiCCBOld) {
                        appContext?.rtpMidiServer?.let {
                            if (it.isEnabled) {
                                var sentNotes = 0
                                while (sentNotes < (appContext).rtpMidiNotesRepeat) {
                                    appContext.rtpMidiServer?.addToSendQueue(midiData)
                                    sentNotes += 1
                                }
                            }
                        }
                        it.sendMidiCC(midiCCB, (touchActionVertical.absoluteValue * 127.0f).toInt())
                        midiCCAOld = midiData[2]
                    }
                    firstnote = false
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
        return touchElementHandler.handleTouchEvent(event)
    }


    override fun performClick(): Boolean {
        if (!isEngaged) {
            touchElementHandler.switchOnVoices(appContext)
            isEngaged = true
        }
        else {
            touchElementHandler.switchOffVoices(appContext)
            isEngaged = false
        }
        return super.performClick()
    }

    fun isInCorner(x: Float, y: Float): TouchElementDragAnchor {
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

    private fun drawArrowLeftRight(canvas: Canvas)
    {
        val w = measuredWidth.toFloat()
        val h = measuredHeight.toFloat()
        val arrowDef = computeArrowDefinition(h.toInt(),w.toInt())
        arrowPath.reset()
        arrowPath.moveTo(w - padding - arrowDef.arrowDr - arrowDef.arrowB,h/2 - arrowDef.arrowB )
        arrowPath.lineTo(w - padding - arrowDef.arrowDr,h/2)
        arrowPath.lineTo(w - padding - arrowDef.arrowDr - arrowDef.arrowB,h/2 + arrowDef.arrowB )
        if (!arrowDef.onlyTriangle) {
            arrowPath.lineTo(padding + arrowDef.arrowDr,h / 2 + arrowDef.arrowB )
            arrowPath.lineTo(padding + arrowDef.arrowDr,h / 2 - arrowDef.arrowB )
        }
        arrowPath.close()
        canvas.drawPath(arrowPath,contrastingFill)
    }

    private fun drawArrowRightLeft(canvas: Canvas)
    {
        val w = measuredWidth.toFloat()
        val h = measuredHeight.toFloat()
        val arrowDef = computeArrowDefinition(h.toInt(),w.toInt())
        arrowPath.reset()
        arrowPath.moveTo(padding + arrowDef.arrowDr + arrowDef.arrowB,h/2 + arrowDef.arrowB)
        arrowPath.lineTo(padding + arrowDef.arrowDr, h/2 )
        arrowPath.lineTo(padding + arrowDef.arrowDr + arrowDef.arrowB,h/2 - arrowDef.arrowB)
        if (!arrowDef.onlyTriangle) {
            arrowPath.lineTo(w - padding - arrowDef.arrowDr,h / 2 - arrowDef.arrowB)
            arrowPath.lineTo( w - padding - arrowDef.arrowDr,h / 2 + arrowDef.arrowB)
        }
        arrowPath.close()
        canvas.drawPath(arrowPath,contrastingFill)
    }

    private fun drawArrowDownUp(canvas: Canvas)
    {
        val w = measuredWidth.toFloat()
        val h = measuredHeight.toFloat()
        val arrowDef = computeArrowDefinition(w.toInt(),h.toInt())
        arrowPath.reset()
        arrowPath.moveTo(w/2 - arrowDef.arrowB, padding + arrowDef.arrowDr + arrowDef.arrowB)
        arrowPath.lineTo(w/2, padding + arrowDef.arrowDr)
        arrowPath.lineTo(w/2 + arrowDef.arrowB, padding + arrowDef.arrowDr + arrowDef.arrowB)
        if (!arrowDef.onlyTriangle) {
            arrowPath.lineTo(w / 2 + arrowDef.arrowB, h - padding - arrowDef.arrowDr)
            arrowPath.lineTo(w / 2 - arrowDef.arrowB, h - padding - arrowDef.arrowDr)
        }
        arrowPath.close()
        canvas.drawPath(arrowPath,contrastingFill)
    }

    private fun drawArrowUpDown(canvas: Canvas)
    {
        val w = measuredWidth.toFloat()
        val h = measuredHeight.toFloat()
        val arrowDef = computeArrowDefinition(w.toInt(),h.toInt())
        arrowPath.reset()
        arrowPath.moveTo(w/2 + arrowDef.arrowB, h - padding - arrowDef.arrowDr - arrowDef.arrowB)
        arrowPath.lineTo(w/2, h - padding - arrowDef.arrowDr )
        arrowPath.lineTo(w/2 - arrowDef.arrowB, h - padding - arrowDef.arrowDr - arrowDef.arrowB)
        if (!arrowDef.onlyTriangle) {
            arrowPath.lineTo(w / 2 - arrowDef.arrowB, padding + arrowDef.arrowDr)
            arrowPath.lineTo(w / 2 + arrowDef.arrowB, padding + arrowDef.arrowDr)
        }
        arrowPath.close()
        canvas.drawPath(arrowPath,contrastingFill)
    }

    fun setMidiNoteOn(midiData: ByteArray,note: MusicalPitch)
    {
        midiData[0] = (0x90 + midiChannel).toByte()
        midiData[1] = (note.value+69).toInt().toByte()
        midiData[2] = 0x7F.toByte()
    }

    fun setMidiNoteOff(midiData: ByteArray,note: MusicalPitch)
    {
        midiData[0] = (0x80 + midiChannel).toByte()
        midiData[1] = (note.value+69).toInt().toByte()
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

    open fun setSoundGenerator(soundGenerator: InstrumentI?)
    {
        this.soundGenerator = soundGenerator
    }

    fun getSoundGenerator(): InstrumentI?
    {
        return soundGenerator
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


