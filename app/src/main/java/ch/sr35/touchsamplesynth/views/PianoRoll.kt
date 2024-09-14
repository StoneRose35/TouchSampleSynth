package ch.sr35.touchsamplesynth.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import ch.sr35.touchsamplesynth.MusicalPitch
import ch.sr35.touchsamplesynth.R
import ch.sr35.touchsamplesynth.graphics.Converter
import com.google.android.material.color.MaterialColors



class PianoRoll(context: Context, attributes: AttributeSet): View(context,attributes)   {

    enum class SelectionMode
    {
        SINGLE,
        MULTIPLE
    }

    // core properties
    var octave: Int = 0
    private var selectedKeys: ArrayList<MusicalPitch>
    var selectionMode: SelectionMode = SelectionMode.SINGLE
        get() = field
        set(value) {
            field = value
            if (value == SelectionMode.SINGLE)
            {
                if (selectedKeys.size > 1) {
                    selectedKeys.apply {
                        val f = first()
                        clear()
                        add(f)
                    }
                }
                else if (selectedKeys.isEmpty())
                {
                    selectedKeys.add(MusicalPitch(0,0))
                }
            }
        }



    //design properties
    private var smallKeyLengthPercent = 0.7f
    private var smallKeyWidthPercent = 0.87f
    private var dotSizePercent=0.32f
    private var octaveDotSizePercent = 0.68f
    private var octaveDotSizeSelectionPercent=0.38f
    private var outlineSize = Converter.toPx(2.0f)
    private var octaveIndicatorSize=Converter.toPx(32.0f)
    private var octaveArrowPercent = 0.78f

    // helper variables for onTouch events
    private val xSeparators = floatArrayOf(0.0f,0.0f,0.0f,0.0f,
                                            0.0f,0.0f,0.0f,0.0f,
                                            0.0f,0.0f,0.0f,0.0f,
                                            0.0f,0.0f,0.0f,0.0f,0.0f)
    private var ySeparator = 0.0f
    private val idxesOfLargeKeyXSeparators = intArrayOf(1,4,6,8,11,14)
    private val idxesToDistinguishYValue = intArrayOf(1,2,4,5,8,9,11,12,14,15)
    private val idxNoteMap: Map<KeyTouchIndex,Int> = mapOf(
        Pair(KeyTouchIndex(0,null),0),
        Pair(KeyTouchIndex(1,false),0),
        Pair(KeyTouchIndex(1,true),1),
        Pair(KeyTouchIndex(2,false),2),
        Pair(KeyTouchIndex(2,true),1),
        Pair(KeyTouchIndex(3,false),2),
        Pair(KeyTouchIndex(4,false),2),
        Pair(KeyTouchIndex(4,true),3),
        Pair(KeyTouchIndex(5,false),4),
        Pair(KeyTouchIndex(5,true),3),
        Pair(KeyTouchIndex(6,null),4),
        Pair(KeyTouchIndex(7,null),5),
        Pair(KeyTouchIndex(8,false),5),
        Pair(KeyTouchIndex(8,true),6),
        Pair(KeyTouchIndex(9,false),7),
        Pair(KeyTouchIndex(9,true),6),
        Pair(KeyTouchIndex(10,null),7),
        Pair(KeyTouchIndex(11,false),7),
        Pair(KeyTouchIndex(11,true),8),
        Pair(KeyTouchIndex(12,false),9),
        Pair(KeyTouchIndex(12,true),8),
        Pair(KeyTouchIndex(13,null),9),
        Pair(KeyTouchIndex(14,false),9),
        Pair(KeyTouchIndex(14,true),10),
        Pair(KeyTouchIndex(15,false),11),
        Pair(KeyTouchIndex(15,true),10),
        )

    private val bigKeyPaint: Paint
    private val bigKeySelectionPaint: Paint
    private val smallKeyPaint: Paint
    private val smallKeySelectionPaint: Paint
    private val outlinePaint: Paint
    private val octaveDotNoSelectionPaint: Paint
    private val octaveDotSelectionPaint: Paint
    private val octaveDotOutlinePaint: Paint

    init {
        bigKeyPaint = Paint()
        bigKeyPaint.color = MaterialColors.getColor(this, R.attr.pianoRollBigKeyColor)
        bigKeyPaint.style = Paint.Style.FILL

        bigKeySelectionPaint = Paint()
        bigKeySelectionPaint.let {
            it.color = MaterialColors.getColor(this, R.attr.pianoRollBigKeySelectionColor)
            it.style = Paint.Style.FILL
        }

        smallKeyPaint = Paint()
        smallKeyPaint.color = MaterialColors.getColor(this, R.attr.pianoRollSmallKeyColor)
        smallKeyPaint.style = Paint.Style.FILL

        smallKeySelectionPaint = Paint().also {
            it.color = MaterialColors.getColor(this,R.attr.pianoRollSmallKeySelectionColor)
            it.style = Paint.Style.FILL
        }

        outlinePaint = Paint()
        outlinePaint.color = MaterialColors.getColor(this,R.attr.pianoRollOutlineColor)
        outlinePaint.strokeWidth = outlineSize
        outlinePaint.style = Paint.Style.STROKE

        octaveDotNoSelectionPaint = Paint()
        octaveDotNoSelectionPaint.let {
            it.color = MaterialColors.getColor(this,R.attr.pianoRollOctaveNoSelectionColor)
            it.style = Paint.Style.FILL
        }

        octaveDotSelectionPaint = Paint()
        octaveDotSelectionPaint.let {
            it.color = MaterialColors.getColor(this,R.attr.pianoRollOctaveSelectionColor)
            it.style = Paint.Style.FILL
        }

        octaveDotOutlinePaint = Paint()

        octaveDotOutlinePaint.color = MaterialColors.getColor(this,R.attr.pianoRollOctaveSelectionOutlineColor)
        octaveDotOutlinePaint.style = Paint.Style.FILL


        selectedKeys = ArrayList<MusicalPitch>().apply {
            add(MusicalPitch(0,0))
        }
    }
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val largeKeyWidth = width.toFloat()/7.0f
        val smallKeyHeight = (height-octaveIndicatorSize)*smallKeyLengthPercent

        // draw large keys
        var keyCnt=0
        while (keyCnt < 7)
        {
            canvas.drawRect(keyCnt.toFloat()*largeKeyWidth,octaveIndicatorSize,(keyCnt+1).toFloat()*largeKeyWidth,height.toFloat(),bigKeyPaint)
            canvas.drawRect(keyCnt.toFloat()*largeKeyWidth,octaveIndicatorSize,(keyCnt+1).toFloat()*largeKeyWidth,height.toFloat(),outlinePaint)
            if (keyCnt > 0)
            {
                xSeparators[idxesOfLargeKeyXSeparators[keyCnt-1]]=keyCnt.toFloat()*largeKeyWidth
            }
            keyCnt += 1
        }
        ySeparator = octaveIndicatorSize+smallKeyHeight
        // C#
        xSeparators[0] = largeKeyWidth/2.0f + smallKeyWidthPercent*largeKeyWidth
        xSeparators[2] = 3.0f*largeKeyWidth/2.0f-smallKeyWidthPercent*largeKeyWidth
        canvas.drawRect( xSeparators[0],octaveIndicatorSize,xSeparators[2],ySeparator,smallKeyPaint)
        canvas.drawRect( xSeparators[0],octaveIndicatorSize,xSeparators[2],ySeparator,outlinePaint)

        //D#
        xSeparators[3] = 3.0f*largeKeyWidth/2.0f+ smallKeyWidthPercent*largeKeyWidth
        xSeparators[5] = 5.0f*largeKeyWidth/2.0f-smallKeyWidthPercent*largeKeyWidth
        canvas.drawRect( xSeparators[3],octaveIndicatorSize,xSeparators[5],ySeparator,smallKeyPaint)
        canvas.drawRect( xSeparators[3],octaveIndicatorSize,xSeparators[5],ySeparator,outlinePaint)

        //F#
        xSeparators[7] = 7.0f*largeKeyWidth/2.0f+ smallKeyWidthPercent*largeKeyWidth
        xSeparators[9] = 9.0f*largeKeyWidth/2.0f-smallKeyWidthPercent*largeKeyWidth
        canvas.drawRect( xSeparators[7],octaveIndicatorSize,xSeparators[9],ySeparator,smallKeyPaint)
        canvas.drawRect( xSeparators[7],octaveIndicatorSize,xSeparators[9],ySeparator,outlinePaint)

        //G#
        xSeparators[10] = 9.0f*largeKeyWidth/2.0f+ smallKeyWidthPercent*largeKeyWidth
        xSeparators[12] = 11.0f*largeKeyWidth/2.0f-smallKeyWidthPercent*largeKeyWidth
        canvas.drawRect( xSeparators[10],octaveIndicatorSize,xSeparators[12],ySeparator,smallKeyPaint)
        canvas.drawRect( xSeparators[10],octaveIndicatorSize,xSeparators[12],ySeparator,outlinePaint)

        //A#
        xSeparators[13] = 11.0f*largeKeyWidth/2.0f+ smallKeyWidthPercent*largeKeyWidth
        xSeparators[15] = 13.0f*largeKeyWidth/2.0f-smallKeyWidthPercent*largeKeyWidth
        canvas.drawRect( xSeparators[13],octaveIndicatorSize,xSeparators[15],ySeparator,smallKeyPaint)
        canvas.drawRect( xSeparators[13],octaveIndicatorSize,xSeparators[15],ySeparator,outlinePaint)

        xSeparators[16] = width.toFloat()
        // draw the dots on selected keys
        val dotRadiusLarge = largeKeyWidth*dotSizePercent
        val dotRadiusSmall = smallKeyWidthPercent*largeKeyWidth*dotSizePercent
        selectedKeys.filter { k -> k.getOctave() == octave  }.forEach {
            note -> note.getNoteWithinOctave()
            when (note.getNoteWithinOctave())
            {
                0 -> {  // C
                    canvas.drawCircle(largeKeyWidth/2.0f,
                        height.toFloat()-largeKeyWidth*(1-2*dotSizePercent) - dotRadiusLarge,
                        dotRadiusLarge,
                        bigKeySelectionPaint)
                }
                1 -> { // C#
                    canvas.drawCircle(largeKeyWidth,
                        octaveIndicatorSize + smallKeyHeight - smallKeyWidthPercent*largeKeyWidth*(1-2*dotSizePercent) - dotRadiusSmall,
                        dotRadiusSmall,
                        smallKeySelectionPaint)
                }
                2 -> { // D
                    canvas.drawCircle(3.0f*largeKeyWidth/2.0f,
                        height.toFloat()-largeKeyWidth*(1-2*dotSizePercent) - dotRadiusLarge,
                        dotRadiusLarge,
                        bigKeySelectionPaint)
                }
                3 -> { // D#
                    canvas.drawCircle(2.0f*largeKeyWidth,
                        octaveIndicatorSize + smallKeyHeight - smallKeyWidthPercent*largeKeyWidth*(1-2*dotSizePercent) - dotRadiusSmall,
                        dotRadiusSmall,
                        smallKeySelectionPaint)
                }
                4 -> // E
                {
                    canvas.drawCircle(5.0f*largeKeyWidth/2.0f,
                        height.toFloat()-largeKeyWidth*(1-2*dotSizePercent) - dotRadiusLarge,
                        dotRadiusLarge,
                        bigKeySelectionPaint)
                }
                5 -> // F
                {
                    canvas.drawCircle(7.0f*largeKeyWidth/2.0f,
                        height.toFloat()-largeKeyWidth*(1-2*dotSizePercent) - dotRadiusLarge,
                        dotRadiusLarge,
                        bigKeySelectionPaint)
                }
                6-> // F#
                {
                    canvas.drawCircle(4.0f*largeKeyWidth,
                        octaveIndicatorSize + smallKeyHeight - smallKeyWidthPercent*largeKeyWidth*(1-2*dotSizePercent) - dotRadiusSmall,
                        dotRadiusSmall,
                        smallKeySelectionPaint)
                }
                7 -> // G
                {
                    canvas.drawCircle(9.0f*largeKeyWidth/2.0f,
                        height.toFloat()-largeKeyWidth*(1-2*dotSizePercent) - dotRadiusLarge,
                        dotRadiusLarge,
                        bigKeySelectionPaint)
                }
                8 -> // G#
                {
                    canvas.drawCircle(5.0f*largeKeyWidth,
                        octaveIndicatorSize + smallKeyHeight - smallKeyWidthPercent*largeKeyWidth*(1-2*dotSizePercent) - dotRadiusSmall,
                        dotRadiusSmall,
                        smallKeySelectionPaint)
                }
                9 -> // A
                {
                    canvas.drawCircle(11.0f*largeKeyWidth/2.0f,
                        height.toFloat()-largeKeyWidth*(1-2*dotSizePercent) - dotRadiusLarge,
                        dotRadiusLarge,
                        bigKeySelectionPaint)
                }
                10 -> // A#
                {
                    canvas.drawCircle(6.0f*largeKeyWidth,
                        octaveIndicatorSize + smallKeyHeight - smallKeyWidthPercent*largeKeyWidth*(1-2*dotSizePercent) - dotRadiusSmall,
                        dotRadiusSmall,
                        smallKeySelectionPaint)
                }
                11 -> // B
                {
                    canvas.drawCircle(13.0f*largeKeyWidth/2.0f,
                        height.toFloat()-largeKeyWidth*(1-2*dotSizePercent) - dotRadiusLarge,
                        dotRadiusLarge,
                        bigKeySelectionPaint)
                }
            }
        }
        // draw octave indication dots

        // bigger dot around current selection
        canvas.drawCircle(width.toFloat()/2.0f - octave.toFloat() * octaveIndicatorSize,
            octaveIndicatorSize/2.0f,
            octaveIndicatorSize/2.0f*octaveDotSizePercent,octaveDotOutlinePaint)

        // all octave dots
        var octCnt=-4
        while (octCnt < 5)
        {
            val cx = width.toFloat()/2.0f - octCnt.toFloat() * octaveIndicatorSize
            val cy = octaveIndicatorSize/2.0f
            val radius = octaveIndicatorSize/2.0f*octaveDotSizePercent*(1.0f - octaveDotSizeSelectionPercent)
            if (selectedKeys.any {
                sel -> sel.getOctave() == octCnt
                })
            {
                canvas.drawCircle(cx, cy, radius,octaveDotSelectionPaint)
            }
            else
            {
                canvas.drawCircle(cx, cy, radius,octaveDotNoSelectionPaint)
            }
            octCnt += 1
        }

        // octave up / octave down pseudobuttons
        var dx = 0.0f
        canvas.drawRect(dx,0.0f,octaveIndicatorSize,octaveIndicatorSize,outlinePaint)
        val d =  (1.0f - octaveArrowPercent)*octaveIndicatorSize/2.0f
        var px1 = dx + octaveIndicatorSize/2.0f + (octaveIndicatorSize/2.0f - d)/2.0f
        var px2 = dx + octaveIndicatorSize/2.0f - (octaveIndicatorSize/2.0f - d)/2.0f
        var py1 = d
        var py2 = octaveIndicatorSize/2.0f
        var py3 = octaveIndicatorSize - d
        canvas.drawLine(px1,py1,px2,py2,outlinePaint)
        canvas.drawLine(px2,py2,px1,py3,outlinePaint)

        dx = width.toFloat() - octaveIndicatorSize
        canvas.drawRect(dx,0.0f,octaveIndicatorSize,octaveIndicatorSize,outlinePaint)
        px1 = dx + octaveIndicatorSize/2.0f + (octaveIndicatorSize/2.0f - d)/2.0f
        px2 = dx + octaveIndicatorSize/2.0f - (octaveIndicatorSize/2.0f - d)/2.0f
        py1 = d
        py2 = octaveIndicatorSize/2.0f
        py3 = octaveIndicatorSize - d
        canvas.drawLine(px2,py1,px1,py2,outlinePaint)
        canvas.drawLine(px1,py2,px2,py3,outlinePaint)

    }

    override fun performClick(): Boolean {

        return super.performClick()
    }
    override fun onTouchEvent(event: MotionEvent?): Boolean {

        if (event?.action == MotionEvent.ACTION_DOWN)
        {
            if (event.y < octaveIndicatorSize)
            {
                if (event.x < octaveIndicatorSize && octave > -4)
                {
                    // octave down
                    octave -= 1
                    invalidate()
                    return true
                }
                if (event.x > width - octaveIndicatorSize && octave < 4)
                {
                    octave += 1
                    invalidate()
                    return true
                }
            }
            else
            {
                var xInd= 0
                var isUpper: Boolean?=null
                while(xSeparators[xInd] < event.x && xInd < 17)
                {
                    xInd += 1
                }
                if (idxesToDistinguishYValue.contains(xInd))
                {
                    isUpper = event.y < ySeparator
                }
                val noteWithinOctaveToToggle = idxNoteMap[KeyTouchIndex(xInd,isUpper)]!!
                val noteToToggle = MusicalPitch(octave,noteWithinOctaveToToggle)
                if (selectionMode == SelectionMode.MULTIPLE) {
                    if (selectedKeys.contains(noteToToggle)) {
                        selectedKeys.remove(noteToToggle)
                    } else {
                        selectedKeys.add(noteToToggle)
                    }
                }
                else
                {
                    selectedKeys[0] = noteToToggle
                }
                invalidate()
                return true
            }
            performClick()
        }
        return super.onTouchEvent(event)
    }

    class KeyTouchIndex(val index: Int,val isUpper: Boolean?)
    {
        override fun equals(other: Any?): Boolean {
            if (other is KeyTouchIndex)
            {
                return this.index == index && this.isUpper == isUpper
            }
            return false
        }

        override fun hashCode(): Int {
            var result = index
            result = 31 * result + (isUpper?.hashCode() ?: 0)
            return result
        }
    }
}