package ch.sr35.touchsamplesynth.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import ch.sr35.touchsamplesynth.MusicalPitch
import ch.sr35.touchsamplesynth.R
import com.google.android.material.color.MaterialColors

class PianoRoll(context: Context, attributes: AttributeSet): View(context,attributes)   {

    // core properties
    var octave: Int
    var selectedKeys: ArrayList<MusicalPitch>

    //design properties
    var smallKeyLengthPercent = 0.7f
    var smallKeyWidthPercent = 0.87f
    var outlineSize = 4.0f
    var octaveIndicatorSize=64.0f

    private val bigKeyPaint: Paint
    private val smallKeyPaint: Paint
    private val outlinePaint: Paint

    init {
        octave = 0
        bigKeyPaint = Paint()
        bigKeyPaint.color = MaterialColors.getColor(this, R.attr.pianoRollBigKeyColor)
        bigKeyPaint.style = Paint.Style.FILL

        smallKeyPaint = Paint()
        smallKeyPaint.color = MaterialColors.getColor(this, R.attr.pianoRollSmallKeyColor)
        smallKeyPaint.style = Paint.Style.FILL

        outlinePaint = Paint()
        outlinePaint.color = MaterialColors.getColor(this,R.attr.pianoRollOutlineColor)
        outlinePaint.strokeWidth = outlineSize
        outlinePaint.style = Paint.Style.STROKE

        selectedKeys = ArrayList()
    }
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val largeKeyWidth = width.toFloat()/7.0f
        val smallKeyHeight = (height-octaveIndicatorSize).toFloat()*smallKeyLengthPercent

        // draw large keys
        var keyCnt=0
        while (keyCnt < 7)
        {
            canvas.drawRect(keyCnt.toFloat()*largeKeyWidth,octaveIndicatorSize,(keyCnt+1).toFloat()*largeKeyWidth,height.toFloat(),bigKeyPaint)
            canvas.drawRect(keyCnt.toFloat()*largeKeyWidth,octaveIndicatorSize,(keyCnt+1).toFloat()*largeKeyWidth,height.toFloat(),outlinePaint)
            keyCnt += 1
        }
        // C#
        canvas.drawRect( largeKeyWidth/2.0f + smallKeyWidthPercent*largeKeyWidth,octaveIndicatorSize,3.0f*largeKeyWidth/2.0f-smallKeyWidthPercent*largeKeyWidth,octaveIndicatorSize+smallKeyHeight,smallKeyPaint)
        canvas.drawRect( largeKeyWidth/2.0f + smallKeyWidthPercent*largeKeyWidth,octaveIndicatorSize,3.0f*largeKeyWidth/2.0f-smallKeyWidthPercent*largeKeyWidth,octaveIndicatorSize+smallKeyHeight,outlinePaint)

        //D#
        canvas.drawRect( 3.0f*largeKeyWidth/2.0f+ smallKeyWidthPercent*largeKeyWidth,octaveIndicatorSize,5.0f*largeKeyWidth/2.0f-smallKeyWidthPercent*largeKeyWidth,octaveIndicatorSize+smallKeyHeight,smallKeyPaint)
        canvas.drawRect( 3.0F*largeKeyWidth/2.0f+ smallKeyWidthPercent*largeKeyWidth,octaveIndicatorSize,5.0f*largeKeyWidth/2.0f-smallKeyWidthPercent*largeKeyWidth,octaveIndicatorSize+smallKeyHeight,outlinePaint)

        //F#
        canvas.drawRect( 7.0f*largeKeyWidth/2.0f+ smallKeyWidthPercent*largeKeyWidth,octaveIndicatorSize,9.0f*largeKeyWidth/2.0f-smallKeyWidthPercent*largeKeyWidth,octaveIndicatorSize+smallKeyHeight,smallKeyPaint)
        canvas.drawRect( 7.0F*largeKeyWidth/2.0f+ smallKeyWidthPercent*largeKeyWidth,octaveIndicatorSize,9.0f*largeKeyWidth/2.0f-smallKeyWidthPercent*largeKeyWidth,octaveIndicatorSize+smallKeyHeight,outlinePaint)

        //G#
        canvas.drawRect( 9.0f*largeKeyWidth/2.0f+ smallKeyWidthPercent*largeKeyWidth,octaveIndicatorSize,11.0f*largeKeyWidth/2.0f-smallKeyWidthPercent*largeKeyWidth,octaveIndicatorSize+smallKeyHeight,smallKeyPaint)
        canvas.drawRect( 9.0F*largeKeyWidth/2.0f+ smallKeyWidthPercent*largeKeyWidth,octaveIndicatorSize,11.0f*largeKeyWidth/2.0f-smallKeyWidthPercent*largeKeyWidth,octaveIndicatorSize+smallKeyHeight,outlinePaint)

        //A#
        canvas.drawRect( 11.0f*largeKeyWidth/2.0f+ smallKeyWidthPercent*largeKeyWidth,octaveIndicatorSize,13.0f*largeKeyWidth/2.0f-smallKeyWidthPercent*largeKeyWidth,octaveIndicatorSize+smallKeyHeight,smallKeyPaint)
        canvas.drawRect( 11.0F*largeKeyWidth/2.0f+ smallKeyWidthPercent*largeKeyWidth,octaveIndicatorSize,13.0f*largeKeyWidth/2.0f-smallKeyWidthPercent*largeKeyWidth,octaveIndicatorSize+smallKeyHeight,outlinePaint)

    }
}