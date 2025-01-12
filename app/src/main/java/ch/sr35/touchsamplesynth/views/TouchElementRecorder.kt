package ch.sr35.touchsamplesynth.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import ch.sr35.touchsamplesynth.audio.SoundRecorder
import ch.sr35.touchsamplesynth.audio.instruments.InstrumentI
import ch.sr35.touchsamplesynth.audio.instruments.LooperI
import ch.sr35.touchsamplesynth.handlers.TouchElementRecorderHandler
import kotlin.math.sqrt


class TouchElementRecorder(context: Context, attributeSet: AttributeSet?) :
    TouchElement(context, attributeSet) {

        var isRecording = false
        var hasRecordedContent = false
        private val playButtonPadding: Float = 12.0f
        private val symbolPath: Path = Path()
        private val outlineDeleteSymbol: Paint = Paint()

    init {

        touchElementHandler = TouchElementRecorderHandler(this)
        outlineDeleteSymbol.color = outLine.color
        outlineDeleteSymbol.strokeWidth = OUTLINE_STROKE_WIDTH_ENGAGED
        outlineDeleteSymbol.style = Paint.Style.STROKE
        outlineDeleteSymbol.isAntiAlias = true
    }
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        outlineDeleteSymbol.color = outLine.color
        if (isRecording) {
            drawPlayButton(canvas)
            drawStopButton(canvas)
        }
        else
        {
            drawRecordButton(canvas)
        }

        if (hasRecordedContent) {
            drawDeleteButton(canvas)
        }
    }

    override fun performClick(): Boolean {
        super.performClick()
        return true
    }

    override fun setSoundGenerator(soundGenerator: InstrumentI?) {
        soundGenerator?.let {
            if (it is LooperI) {
                hasRecordedContent = it.hasRecordedContent()
            }
            super.setSoundGenerator(it)
        }
    }

    private fun drawPlayButton(canvas: Canvas) {
        val w = measuredWidth.toFloat()
        val h = measuredHeight.toFloat()
        val arrowDefs = computeArrowDefinition(measuredWidth,measuredHeight)

        if (w > h)
        {
            val triangleHeight = h/2 - arrowDefs.arrowB - padding - playButtonPadding
            if (triangleHeight > 0)
            {
                val triangleWidth = (sqrt(3.0)/2*triangleHeight).toFloat()
                symbolPath.reset()
                symbolPath.moveTo(w/2 + (w/2-padding - arrowDefs.arrowB)/2,padding + playButtonPadding/2 )
                symbolPath.lineTo(w/2 + (w/2-padding - arrowDefs.arrowB)/2 + triangleWidth,padding + playButtonPadding/2  + triangleHeight/2)
                symbolPath.lineTo(w/2 + (w/2-padding - arrowDefs.arrowB)/2,padding + playButtonPadding/2  + triangleHeight)
                symbolPath.close()
                canvas.drawPath(symbolPath, contrastingFill)
            }
        }
        else
        {
            val triangleWidth = w/2 - arrowDefs.arrowB - padding - playButtonPadding
            if (triangleWidth > 0)
            {
                val triangleHeight = (2.0/sqrt(5.0)*triangleWidth).toFloat()
                symbolPath.reset()
                symbolPath.moveTo(w/2 + (w/2-padding - arrowDefs.arrowB)/2,padding + playButtonPadding/2 )
                symbolPath.lineTo(w/2 + (w/2-padding - arrowDefs.arrowB)/2 + triangleWidth,padding + playButtonPadding/2  + triangleHeight/2)
                symbolPath.lineTo(w/2 + (w/2-padding - arrowDefs.arrowB)/2,padding + playButtonPadding/2  + triangleHeight)
                symbolPath.close()
                canvas.drawPath(symbolPath, contrastingFill)
            }
        }
    }

    private fun drawRecordButton(canvas: Canvas) {
        val w = measuredWidth.toFloat()
        val h = measuredHeight.toFloat()
        val radius: Float
        val arrowDefs = computeArrowDefinition(measuredWidth,measuredHeight)
        if (w > h)
        {
            radius = (h/2 - arrowDefs.arrowB - padding - playButtonPadding)/2
        }
        else
        {
            radius = (w/2 - arrowDefs.arrowB - padding - playButtonPadding)/2
        }
        canvas.drawCircle(w*3/4 ,h/4,radius,contrastingFill)
    }

    private fun drawStopButton(canvas: Canvas) {
        val w = measuredWidth.toFloat()
        val h = measuredHeight.toFloat()
        val arrowDefs = computeArrowDefinition(measuredWidth,measuredHeight)
        val sidelength: Float
        val centerX = w/2 + arrowDefs.arrowB + (w/2-padding - arrowDefs.arrowB)/2
        val centerY = h/2 + arrowDefs.arrowB + (h/2 - padding -arrowDefs.arrowB)/2
        if (w > h)
        {
            sidelength = h/2 - arrowDefs.arrowB - padding - playButtonPadding*2
        }
        else
        {
            sidelength = w/2 - arrowDefs.arrowB - padding - playButtonPadding*2
        }
        if (sidelength > 0) {
            canvas.drawRect(
                centerX - sidelength / 2,
                centerY - sidelength / 2 ,
                centerX + sidelength / 2,
                centerY + sidelength / 2,
                contrastingFill
            )
        }
    }

    private fun drawDeleteButton(canvas: Canvas) {
        val w = measuredWidth.toFloat()
        val h = measuredHeight.toFloat()
        val arrowDefs = computeArrowDefinition(measuredWidth,measuredHeight)
        val sidelength: Float
        val centerX = padding + (w/2-padding - arrowDefs.arrowB)/2
        val centerY = h/2 + arrowDefs.arrowB + (h/2 - padding -arrowDefs.arrowB)/2
        if (w > h)
        {
            sidelength = h/2 - arrowDefs.arrowB - padding - playButtonPadding*2
        }
        else
        {
            sidelength = w/2 - arrowDefs.arrowB - padding - playButtonPadding*2
        }
        if (sidelength > 0) {
            canvas.drawLine(centerX - sidelength/2,centerY - sidelength/2,centerX + sidelength/2,centerY + sidelength/2,outlineDeleteSymbol)
            canvas.drawLine(centerX + sidelength/2,centerY - sidelength/2,centerX - sidelength/2,centerY + sidelength/2,outlineDeleteSymbol)
        }
    }
}