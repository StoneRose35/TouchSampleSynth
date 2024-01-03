package ch.sr35.touchsamplesynth.views

import android.app.AlertDialog
import android.content.Context
import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import ch.sr35.touchsamplesynth.MusicalPitch
import ch.sr35.touchsamplesynth.R
import ch.sr35.touchsamplesynth.TouchSampleSynthMain
import ch.sr35.touchsamplesynth.audio.Instrument
import ch.sr35.touchsamplesynth.dialogs.EditTouchElementFragmentDialog
import ch.sr35.touchsamplesynth.graphics.Converter
import com.google.android.material.color.MaterialColors
import java.io.Serializable
import java.util.stream.IntStream
import kotlin.math.sqrt

const val PADDING: Float = 32.0f
const val EDIT_CIRCLE_OFFSET = 24.0f
const val OUTLINE_STROKE_WIDTH_DEFAULT = 7.8f
const val OUTLINE_STROKE_WIDTH_ENGAGED = 20.4f

class TouchElement(context: Context, attributeSet: AttributeSet?) :
    View(context, attributeSet) {

    enum class ActionDir: Serializable {
        HORIZONTAL,
        VERTICAL
    }

    enum class TouchElementState {
        PLAYING,
        PLAYING_VERBOSE,
        EDITING
    }

    enum class TouchElementDragAnchor {
        BODY,
        TOP_LEFT,
        TOP_RIGHT,
        BOTTOM_LEFT,
        BOTTOM_RIGHT
    }

    var actionDir: ActionDir = ActionDir.HORIZONTAL
    private val outLine: Paint = Paint()
    private val fillColor: Paint = Paint()
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
    private var elementState: TouchElementState = defaultState
    var soundGenerator: Instrument? = null
    var voiceNr: Int = -1
    var note: MusicalPitch? = null
    private var rotateRect: Rect = Rect()
    private var setSoundgenRect: Rect = Rect()
    private var deleteRect: Rect = Rect()
    private val boundsRotate = Rect()
    private val boundsSetSoundgen = Rect()
    private val boundsDelete = Rect()

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
        if (actionDir == ActionDir.HORIZONTAL) {
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

        } else {
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
                soundGenerator?.name.let { it ->
                    canvas.drawText(
                        it.toString(),
                        descriptionOffset.toFloat(),
                        descriptionOffset + (iconHeight * 0.7).toInt() + smallText.textSize,
                        smallText
                    )
                }
                note.let { it ->
                    canvas.drawText(
                        it.toString(),
                        descriptionOffset.toFloat(),
                        descriptionOffset + (iconHeight * 0.7).toInt()+ smallText.textSize * 2 + 10,
                        smallText
                    )
                }
            }


        }

        if (elementState == TouchElementState.EDITING) {
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
            val editRectangleWidth = IntStream.of(boundsRotate.width(),boundsSetSoundgen.width(),boundsDelete.width()).max().orElse(1)


            rotateRect.left = 20
            rotateRect.right = rotateRect.left + editRectangleWidth + Converter.toPx(3)
            rotateRect.top = EDIT_CIRCLE_OFFSET.toInt() + 50
            rotateRect.bottom = (editText.textSize.toInt() + 8 + rotateRect.top)


            canvas.drawRect(rotateRect, editBoxBackground)
            canvas.drawText(
                "Rotate",
                (rotateRect.left + 3).toFloat(),
                rotateRect.top.toFloat() + editText.textSize,
                editText
            )

            setSoundgenRect.left = 20
            setSoundgenRect.right = setSoundgenRect.left + editRectangleWidth + Converter.toPx(3)
            setSoundgenRect.top = rotateRect.bottom + 10
            setSoundgenRect.bottom = setSoundgenRect.top + editText.textSize.toInt() + 8

            canvas.drawRect(setSoundgenRect, editBoxBackground)
            canvas.drawText(
                "Set SoundGen",
                (setSoundgenRect.left + 3).toFloat(),
                setSoundgenRect.top.toFloat() + editText.textSize,
                editText
            )

            deleteRect.left = 20
            deleteRect.right = deleteRect.left + editRectangleWidth + Converter.toPx(3)
            deleteRect.top = setSoundgenRect.bottom + 10
            deleteRect.bottom = deleteRect.top + editText.textSize.toInt() + 8

            canvas.drawRect(deleteRect, editBoxBackground)
            canvas.drawText(
                "Delete",
                (deleteRect.left + 3).toFloat(),
                deleteRect.top.toFloat() + editText.textSize,
                editText
            )
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

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (elementState != TouchElementState.EDITING) {
            if (event?.action == MotionEvent.ACTION_DOWN) {
                performClick()
                px = event.x
                py = event.y
                return true
            } else if (event?.action == MotionEvent.ACTION_UP) {
                outLine.strokeWidth = OUTLINE_STROKE_WIDTH_DEFAULT
                soundGenerator?.voices?.get(voiceNr)?.switchOff(1.0f)
                invalidate()
                return true
            } else if (event?.action == MotionEvent.ACTION_MOVE) {
                if (event.y <= PADDING || event.y >= measuredHeight - PADDING || event.x < PADDING || event.x >= measuredWidth - PADDING) {
                    outLine.strokeWidth = OUTLINE_STROKE_WIDTH_DEFAULT
                    soundGenerator?.voices?.get(voiceNr)?.switchOff(1.0f)
                    invalidate()
                    return true
                } else if (actionDir == ActionDir.VERTICAL && event.y >= PADDING && event.y <= measuredHeight - PADDING) {
                    soundGenerator?.voices?.get(voiceNr)?.applyTouchAction((event.y) / measuredHeight.toFloat())
                    return true
                } else if (actionDir == ActionDir.HORIZONTAL && event.x >= PADDING && event.x <= measuredWidth - PADDING) {
                    soundGenerator?.voices?.get(voiceNr)?.applyTouchAction((event.x) / measuredWidth.toFloat())
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
                        if (this.actionDir == ActionDir.HORIZONTAL) {
                            this.actionDir = ActionDir.VERTICAL
                        } else {
                            actionDir = ActionDir.HORIZONTAL
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

                        val alertDlgBuilder = AlertDialog.Builder(context as TouchSampleSynthMain)
                            .setMessage(context.getString(R.string.alert_dialog_really_delete))
                            .setPositiveButton(context.getString(R.string.yes)) { _, _ ->
                                (context as TouchSampleSynthMain).touchElements.remove(this)
                                (parent as ViewGroup).removeView(this)
                            }
                            .setNegativeButton(context.getString(R.string.no)) { _, _ -> }
                        val alertDlg = alertDlgBuilder.create()
                        dragStart = null
                        alertDlg.show()
                        return true
                    } else {
                        val layoutParams: ConstraintLayout.LayoutParams? =
                            this.layoutParams as ConstraintLayout.LayoutParams?
                        if (layoutParams != null) {
                            oldHeight = layoutParams.height
                            oldWidth = layoutParams.width
                        }
                        dragStart = isInCorner(event.x, event.y)
                        return true
                    }
                }

                MotionEvent.ACTION_UP -> {

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
        note?.value?.let { soundGenerator?.voices?.get(voiceNr)?.setNote(it) }
        if (soundGenerator?.voices?.get(voiceNr)?.switchOn(1.0f)==true)
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


    fun setEditmode(mode: Boolean) {
        if (mode && elementState!=TouchElementState.EDITING) {
            this.elementState = TouchElementState.EDITING
            invalidate()
        } else if (!mode && elementState == TouchElementState.EDITING) {
            this.elementState = defaultState
            invalidate()
        }
    }

    fun setDefaultMode(mode: TouchElementState)
    {
        this.defaultState = mode
        if (this.elementState != TouchElementState.EDITING)
        {
            this.elementState = mode
        }
    }



}