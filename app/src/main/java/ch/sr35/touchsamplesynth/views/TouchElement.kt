package ch.sr35.touchsamplesynth.views

import android.app.AlertDialog
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import ch.sr35.touchsamplesynth.MusicalPitch
import ch.sr35.touchsamplesynth.R
import ch.sr35.touchsamplesynth.TouchSampleSynthMain
import ch.sr35.touchsamplesynth.audio.MusicalSoundGenerator
import ch.sr35.touchsamplesynth.fragments.EditTouchElementFragmentDialog

const val PADDING: Float = 32.0f
const val EDIT_CIRCLE_OFFSET = 24.0f

open class TouchElement(context: Context,attributeSet: AttributeSet?): View(context,attributeSet) {

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

    private var actionDir: ActionDir = ActionDir.HORIZONTAL
    private val blackLine: Paint = Paint()
    private val fillColor: Paint = Paint()
    private val blackLineFat: Paint = Paint()
    private val blackFill: Paint = Paint()
    private val grayFill: Paint = Paint()
    private val blackText: Paint = Paint()
    private var cornerRadius = 0.0f
    private var dragStart: TouchElementDragAnchor?=null
    private var px: Float = 0.0f
    private var py: Float = 0.0f
    private var oldWidth: Int = 0
    private var oldHeight: Int = 0
    private var elementState: TouchElementState = TouchElementState.PLAYING
    var soundGenerator: MusicalSoundGenerator? = null
    var note: MusicalPitch?=null
    private var rotateRect:Rect=Rect()
    private var setSoundgenRect:Rect=Rect()
    private var deleteRect:Rect=Rect()



    init {
        blackLine.color = Color.BLACK
        blackLine.strokeWidth = 7.8f
        blackLine.style = Paint.Style.STROKE
        blackLine.isAntiAlias = true

        grayFill.color = Color.LTGRAY
        grayFill.style = Paint.Style.FILL
        grayFill.isAntiAlias = true

        blackFill.color = Color.BLACK
        blackFill.style = Paint.Style.FILL
        blackFill.isAntiAlias = true

        blackText.color = Color.BLACK
        blackText.style = Paint.Style.FILL
        blackText.textSize = 28.0f
        blackText.isAntiAlias = true

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

        val w = width.toFloat()
        val h = height.toFloat()
        val arrowSize: Float
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
            arrowSize = if (0.6f*h < 0.11f*w) {
                0.6f*h
            } else {
                0.11f * w
            }
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


            rotateRect.left =20
            rotateRect.right=rotateRect.left+200
            rotateRect.top=EDIT_CIRCLE_OFFSET.toInt() + 50
            rotateRect.bottom=(blackText.textSize.toInt()+8+rotateRect.top)

            canvas?.drawRect(rotateRect,grayFill)
            canvas?.drawText("Rotate",(rotateRect.left + 3).toFloat(),rotateRect.top.toFloat()+ blackText.textSize,blackText)

            setSoundgenRect.left = 20
            setSoundgenRect.right = setSoundgenRect.left + 200
            setSoundgenRect.top = rotateRect.bottom + 10
            setSoundgenRect.bottom = setSoundgenRect.top + blackText.textSize.toInt() + 8

            canvas?.drawRect(setSoundgenRect,grayFill)
            canvas?.drawText("Set SoundGen",(setSoundgenRect.left + 3).toFloat(),setSoundgenRect.top.toFloat()+ blackText.textSize,blackText)

            deleteRect.left = 20
            deleteRect.right = deleteRect.left + 200
            deleteRect.top = setSoundgenRect.bottom + 10
            deleteRect.bottom = deleteRect.top + blackText.textSize.toInt() + 8

            canvas?.drawRect(deleteRect,grayFill)
            canvas?.drawText("Delete",(deleteRect.left + 3).toFloat(),deleteRect.top.toFloat()+ blackText.textSize,blackText)
        }

    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {

        cornerRadius = if (w > h)
        {
            (h/8.0).toFloat()
        }
        else
        {
            (w/8.0).toFloat()
        }
        super.onSizeChanged(w, h, oldw, oldh)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (elementState == TouchElementState.PLAYING) {
            if (event?.action == MotionEvent.ACTION_DOWN) {
                performClick()
                px = event.x
                py = event.y
                return true
            } else if (event?.action == MotionEvent.ACTION_UP) {
                fillColor.color =
                    context.resources.getColor(R.color.touchelement_not_touched, context.theme)
                soundGenerator?.switchOff(1.0f)
                return false
            }
            else if (event?.action == MotionEvent.ACTION_MOVE)
            {
                if (actionDir ==ActionDir.VERTICAL)
                {
                    soundGenerator?.applyTouchAction ((event.y )/height.toFloat())
                }
                else
                {
                    soundGenerator?.applyTouchAction ((event.x )/width.toFloat())
                }
                return true
            }
            invalidate()
            return true //super.onTouchEvent(event)
        }
        else
        {
            when (event?.action) {
                MotionEvent.ACTION_DOWN -> {
                    // start a corner drag if a corner has been hit, else move the whole element
                    px = event.x
                    py = event.y
                    if (rotateRect.contains(px.toInt(),py.toInt()))
                    {
                        if (this.actionDir == ActionDir.HORIZONTAL)
                        {
                            this.actionDir = ActionDir.VERTICAL
                        }
                        else
                        {
                            actionDir = ActionDir.HORIZONTAL
                        }
                        invalidate()
                    }
                    else if (setSoundgenRect.contains(px.toInt(),py.toInt()))
                    {
                        val editSoundgenerator  = EditTouchElementFragmentDialog()
                        editSoundgenerator.setData(this,(context as TouchSampleSynthMain).soundGenerators)

                        (context as TouchSampleSynthMain).supportFragmentManager
                            .beginTransaction()
                            .add(editSoundgenerator,null)
                            .commit()
                        editSoundgenerator.dialog?.window?.setLayout(300, 600)
                    }
                    else if (deleteRect.contains(px.toInt(),py.toInt()))
                    {

                        val alertDlgBuilder =  AlertDialog.Builder(context as TouchSampleSynthMain)
                            .setMessage(context.getString(R.string.alert_dialog_really_delete))
                            .setPositiveButton(context.getString(R.string.yes)) { _, _ ->
                                (context as TouchSampleSynthMain).touchElements.remove(this)
                                (parent as ViewGroup).removeView(this)
                            }
                            .setNegativeButton(context.getString(R.string.no)) {_, _ -> }
                        val alertDlg = alertDlgBuilder.create()
                        alertDlg.show()
                    }
                    else {
                        val layoutParams: ConstraintLayout.LayoutParams? =
                            this.layoutParams as ConstraintLayout.LayoutParams?
                        if (layoutParams != null) {
                            oldHeight = layoutParams.height
                            oldWidth = layoutParams.width
                        }
                        dragStart = isInCorner(event.x, event.y)
                    }
                }
                MotionEvent.ACTION_UP -> {

                }
                MotionEvent.ACTION_MOVE -> {
                    val layoutParams: ConstraintLayout.LayoutParams? =  this.layoutParams as ConstraintLayout.LayoutParams?
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
                }
            }
            return true
        }
        //return super.onTouchEvent(event);
    }

    override fun performClick(): Boolean {
        fillColor.color =
            context.resources.getColor(R.color.touchelement_touched, context.theme)
        note?.value?.let { soundGenerator?.setNote(it) }
        soundGenerator?.switchOn(1.0f)
        return super.performClick()
    }

    private fun isInCorner(x: Float, y: Float): TouchElementDragAnchor
    {
        val w = width.toFloat()
        val h = height.toFloat()
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
            return TouchElementDragAnchor.TOP_RIGHT
        }
        else if (((x-(w-EDIT_CIRCLE_OFFSET))*(x-(w-EDIT_CIRCLE_OFFSET))
                    + (y-(h-EDIT_CIRCLE_OFFSET))*(y-(h-EDIT_CIRCLE_OFFSET)))
            <EDIT_CIRCLE_OFFSET*EDIT_CIRCLE_OFFSET)
        {
            return TouchElementDragAnchor.BOTTOM_RIGHT
        }
        else if (((x-(0.0f+EDIT_CIRCLE_OFFSET))*(x-(0.0f+EDIT_CIRCLE_OFFSET))
                    + (y-(h-EDIT_CIRCLE_OFFSET))*(y-(h-EDIT_CIRCLE_OFFSET)))
            <EDIT_CIRCLE_OFFSET*EDIT_CIRCLE_OFFSET)
        {
            return TouchElementDragAnchor.BOTTOM_LEFT
        }
        else
        {
            return TouchElementDragAnchor.BODY
        }
    }


    fun setEditmode(mode: Boolean)
    {
        if (mode && this.elementState==TouchElementState.PLAYING)
        {
            this.elementState=TouchElementState.EDITING
            invalidate()
        }
        else if (!mode && elementState == TouchElementState.EDITING)
        {
            this.elementState=TouchElementState.PLAYING
            invalidate()
        }

    }

}