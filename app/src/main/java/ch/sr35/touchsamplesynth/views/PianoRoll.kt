package ch.sr35.touchsamplesynth.views

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import ch.sr35.touchsamplesynth.MusicalPitch
import ch.sr35.touchsamplesynth.R
import ch.sr35.touchsamplesynth.graphics.Converter
import com.google.android.material.color.MaterialColors
import kotlin.math.roundToInt


class PianoRoll(context: Context, attributes: AttributeSet): View(context,attributes)   {

    enum class SelectionMode
    {
        SINGLE,
        MULTIPLE
    }

    // core properties
    var octave: Int = 0
    var selectedKeys: ArrayList<MusicalPitch>
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

    // animatable properties
    private var keysBitmapXAnchor = 0.0f

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
        Pair(KeyTouchIndex(3,null),2),
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
        Pair(KeyTouchIndex(16,null),11)
        )

    private val bigKeyPaint: Paint = Paint()
    private val bigKeySelectionPaint: Paint = Paint()
    private val smallKeyPaint: Paint = Paint()
    private val smallKeySelectionPaint: Paint = Paint()
    private val outlinePaint: Paint = Paint()
    private val octaveDotNoSelectionPaint: Paint = Paint()
    private val octaveDotSelectionPaint: Paint = Paint()
    private val octaveDotOutlinePaint: Paint = Paint()
    private var keysBitmap: Bitmap? = null
    private var keysBitmapCanvas: Canvas? = null
    private var oldCanvasWidth = 0
    private var oldCanvasHeight = 0
    private var updateKeyDisplay = true

    init {
        bigKeyPaint.color = MaterialColors.getColor(this, R.attr.pianoRollBigKeyColor)
        bigKeyPaint.style = Paint.Style.FILL

        bigKeySelectionPaint.let {
            it.color = MaterialColors.getColor(this, R.attr.pianoRollBigKeySelectionColor)
            it.style = Paint.Style.FILL
        }

        smallKeyPaint.color = MaterialColors.getColor(this, R.attr.pianoRollSmallKeyColor)
        smallKeyPaint.style = Paint.Style.FILL

        smallKeySelectionPaint.let {
            it.color = MaterialColors.getColor(this,R.attr.pianoRollSmallKeySelectionColor)
            it.style = Paint.Style.FILL
        }

        outlinePaint.color = MaterialColors.getColor(this,R.attr.pianoRollOutlineColor)
        outlinePaint.strokeWidth = outlineSize
        outlinePaint.style = Paint.Style.STROKE

        octaveDotNoSelectionPaint.let {
            it.color = MaterialColors.getColor(this,R.attr.pianoRollOctaveNoSelectionColor)
            it.style = Paint.Style.FILL
        }

        octaveDotSelectionPaint.let {
            it.color = MaterialColors.getColor(this,R.attr.pianoRollOctaveSelectionColor)
            it.style = Paint.Style.FILL
        }

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

        if (octaveIndicatorSize >= height)
        {
            return
        }
        if (width < 3 * octaveIndicatorSize)
        {
            return
        }
        if (oldCanvasWidth != width || oldCanvasHeight != (height- octaveIndicatorSize.toInt()) || keysBitmap == null)
        {
            keysBitmap=Bitmap.createBitmap(width,height-octaveIndicatorSize.toInt(),Bitmap.Config.ARGB_8888)
            keysBitmapCanvas = Canvas(keysBitmap!!)
            oldCanvasWidth = width
            oldCanvasHeight = height
            updateKeyDisplay = true
        }

        if (updateKeyDisplay) {
            // draw large keys
            var keyCnt = 0
            while (keyCnt < 7) {
                keysBitmapCanvas?.drawRect(
                    keyCnt.toFloat() * largeKeyWidth,
                    0.0f,
                    (keyCnt + 1).toFloat() * largeKeyWidth,
                    height.toFloat(),
                    bigKeyPaint
                )
                keysBitmapCanvas?.drawRect(
                    keyCnt.toFloat() * largeKeyWidth,
                    0.0f,
                    (keyCnt + 1).toFloat() * largeKeyWidth,
                    height.toFloat(),
                    outlinePaint
                )
                if (keyCnt > 0) {
                    xSeparators[idxesOfLargeKeyXSeparators[keyCnt - 1]] =
                        keyCnt.toFloat() * largeKeyWidth
                }
                keyCnt += 1
            }
            ySeparator = octaveIndicatorSize + smallKeyHeight
            // C#
            xSeparators[0] = largeKeyWidth - smallKeyWidthPercent * largeKeyWidth / 2.0f
            xSeparators[2] = largeKeyWidth + smallKeyWidthPercent * largeKeyWidth / 2.0f
            keysBitmapCanvas?.drawRect(
                xSeparators[0],
                0.0f,
                xSeparators[2],
                ySeparator - octaveIndicatorSize,
                smallKeyPaint
            )
            keysBitmapCanvas?.drawRect(
                xSeparators[0],
                0.0f,
                xSeparators[2],
                ySeparator - octaveIndicatorSize,
                outlinePaint
            )

            //D#
            xSeparators[3] = 2.0f * largeKeyWidth - smallKeyWidthPercent * largeKeyWidth / 2.0f
            xSeparators[5] = 2.0f * largeKeyWidth + smallKeyWidthPercent * largeKeyWidth / 2.0f
            keysBitmapCanvas?.drawRect(
                xSeparators[3],
                0.0f,
                xSeparators[5],
                ySeparator - octaveIndicatorSize,
                smallKeyPaint
            )
            keysBitmapCanvas?.drawRect(
                xSeparators[3],
                0.0f,
                xSeparators[5],
                ySeparator - octaveIndicatorSize,
                outlinePaint
            )

            //F#
            xSeparators[7] = 4.0f * largeKeyWidth - smallKeyWidthPercent * largeKeyWidth / 2.0f
            xSeparators[9] = 4.0f * largeKeyWidth + smallKeyWidthPercent * largeKeyWidth / 2.0f
            keysBitmapCanvas?.drawRect(
                xSeparators[7],
                0.0f,
                xSeparators[9],
                ySeparator - octaveIndicatorSize,
                smallKeyPaint
            )
            keysBitmapCanvas?.drawRect(
                xSeparators[7],
                0.0f,
                xSeparators[9],
                ySeparator - octaveIndicatorSize,
                outlinePaint
            )

            //G#
            xSeparators[10] = 5.0f * largeKeyWidth - smallKeyWidthPercent * largeKeyWidth / 2.0f
            xSeparators[12] = 5.0f * largeKeyWidth + smallKeyWidthPercent * largeKeyWidth / 2.0f
            keysBitmapCanvas?.drawRect(
                xSeparators[10],
                0.0f,
                xSeparators[12],
                ySeparator - octaveIndicatorSize,
                smallKeyPaint
            )
            keysBitmapCanvas?.drawRect(
                xSeparators[10],
                0.0f,
                xSeparators[12],
                ySeparator - octaveIndicatorSize,
                outlinePaint
            )

            //A#
            xSeparators[13] = 6.0f * largeKeyWidth - smallKeyWidthPercent * largeKeyWidth / 2.0f
            xSeparators[15] = 6.0f * largeKeyWidth + smallKeyWidthPercent * largeKeyWidth / 2.0f
            keysBitmapCanvas?.drawRect(
                xSeparators[13],
                0.0f,
                xSeparators[15],
                ySeparator - octaveIndicatorSize,
                smallKeyPaint
            )
            keysBitmapCanvas?.drawRect(
                xSeparators[13],
                0.0f,
                xSeparators[15],
                ySeparator - octaveIndicatorSize,
                outlinePaint
            )

            xSeparators[16] = width.toFloat()
            // draw the dots on selected keys
            val dotRadiusLarge = largeKeyWidth * dotSizePercent
            val dotRadiusSmall = smallKeyWidthPercent * largeKeyWidth * dotSizePercent
            selectedKeys.filter { k -> k.getOctave() == octave }.forEach { note ->
                note.getNoteWithinOctave()
                val selectionDotOffsetY =
                    keysBitmap!!.height.toFloat() - largeKeyWidth * (1 - 2 * dotSizePercent) / 2.0f
                val selectionDotOffsetYSmall =
                    smallKeyHeight - smallKeyWidthPercent * largeKeyWidth * (1 - 2 * dotSizePercent) / 2.0f
                when (note.getNoteWithinOctave()) {
                    0 -> {  // C
                        keysBitmapCanvas?.drawCircle(
                            largeKeyWidth / 2.0f, selectionDotOffsetY - dotRadiusLarge,
                            dotRadiusLarge,
                            bigKeySelectionPaint
                        )
                    }

                    1 -> { // C#
                        keysBitmapCanvas?.drawCircle(
                            largeKeyWidth,
                            selectionDotOffsetYSmall - dotRadiusSmall,
                            dotRadiusSmall,
                            smallKeySelectionPaint
                        )
                    }

                    2 -> { // D
                        keysBitmapCanvas?.drawCircle(
                            3.0f * largeKeyWidth / 2.0f, selectionDotOffsetY - dotRadiusLarge,
                            dotRadiusLarge,
                            bigKeySelectionPaint
                        )
                    }

                    3 -> { // D#
                        keysBitmapCanvas?.drawCircle(
                            2.0f * largeKeyWidth,
                            selectionDotOffsetYSmall - dotRadiusSmall,
                            dotRadiusSmall,
                            smallKeySelectionPaint
                        )
                    }

                    4 -> // E
                    {
                        keysBitmapCanvas?.drawCircle(
                            5.0f * largeKeyWidth / 2.0f, selectionDotOffsetY - dotRadiusLarge,
                            dotRadiusLarge,
                            bigKeySelectionPaint
                        )
                    }

                    5 -> // F
                    {
                        keysBitmapCanvas?.drawCircle(
                            7.0f * largeKeyWidth / 2.0f, selectionDotOffsetY - dotRadiusLarge,
                            dotRadiusLarge,
                            bigKeySelectionPaint
                        )
                    }

                    6 -> // F#
                    {
                        keysBitmapCanvas?.drawCircle(
                            4.0f * largeKeyWidth,
                            selectionDotOffsetYSmall - dotRadiusSmall,
                            dotRadiusSmall,
                            smallKeySelectionPaint
                        )
                    }

                    7 -> // G
                    {
                        keysBitmapCanvas?.drawCircle(
                            9.0f * largeKeyWidth / 2.0f, selectionDotOffsetY - dotRadiusLarge,
                            dotRadiusLarge,
                            bigKeySelectionPaint
                        )
                    }

                    8 -> // G#
                    {
                        keysBitmapCanvas?.drawCircle(
                            5.0f * largeKeyWidth,
                            selectionDotOffsetYSmall - dotRadiusSmall,
                            dotRadiusSmall,
                            smallKeySelectionPaint
                        )
                    }

                    9 -> // A
                    {
                        keysBitmapCanvas?.drawCircle(
                            11.0f * largeKeyWidth / 2.0f, selectionDotOffsetY - dotRadiusLarge,
                            dotRadiusLarge,
                            bigKeySelectionPaint
                        )
                    }

                    10 -> // A#
                    {
                        keysBitmapCanvas?.drawCircle(
                            6.0f * largeKeyWidth,
                            selectionDotOffsetYSmall - dotRadiusSmall,
                            dotRadiusSmall,
                            smallKeySelectionPaint
                        )
                    }

                    11 -> // B
                    {
                        keysBitmapCanvas?.drawCircle(
                            13.0f * largeKeyWidth / 2.0f, selectionDotOffsetY - dotRadiusLarge,
                            dotRadiusLarge,
                            bigKeySelectionPaint
                        )
                    }
                }
            }
        }

        canvas.drawBitmap(keysBitmap!!,keysBitmapXAnchor,octaveIndicatorSize,null)
        if (keysBitmapXAnchor > 0.0f)
        {
            canvas.drawBitmap(keysBitmap!!,keysBitmapXAnchor-keysBitmap!!.width.toFloat(),octaveIndicatorSize,null)
        }
        else if (keysBitmapXAnchor < 0.0)
        {
            canvas.drawBitmap(keysBitmap!!,keysBitmapXAnchor + keysBitmap!!.width.toFloat(),octaveIndicatorSize,null)
        }


        // draw octave indication dots
        // bigger dot around current selection
        canvas.drawCircle(width.toFloat()/2.0f + octave.toFloat() * octaveIndicatorSize,
            octaveIndicatorSize/2.0f,
            octaveIndicatorSize/2.0f*octaveDotSizePercent,octaveDotOutlinePaint)

        // all octave dots
        var octCnt=-4
        while (octCnt < 5)
        {
            val cx = width.toFloat()/2.0f + octCnt.toFloat() * octaveIndicatorSize
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
        canvas.drawRect(octaveIndicatorSize,0.0f,dx,octaveIndicatorSize,outlinePaint)

        canvas.drawRect(dx,0.0f,width.toFloat(),octaveIndicatorSize,outlinePaint)
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
                if (event.x < octaveIndicatorSize && octave > -4) // on octave down pseudobutton
                {
                    // octave down
                    ValueAnimator.ofFloat(0.0f,width.toFloat()).apply {
                        duration = 200
                        repeatMode = ValueAnimator.RESTART
                        repeatCount = 0
                        interpolator = AccelerateDecelerateInterpolator()
                        addUpdateListener {
                            keysBitmapXAnchor = this.animatedValue as Float
                            updateKeyDisplay = false
                            invalidate()
                        }
                        start()
                    }
                    octave -= 1
                    updateKeyDisplay =true
                    invalidate()
                    keysBitmapXAnchor=0.0f
                    return true
                }
                if (event.x > width - octaveIndicatorSize && octave < 4) // on octave up pseudobutton
                {
                    ValueAnimator.ofFloat(0.0f,-width.toFloat()).apply {
                        duration = 200
                        repeatMode = ValueAnimator.RESTART
                        repeatCount = 0
                        interpolator = AccelerateDecelerateInterpolator()
                        addUpdateListener {
                            keysBitmapXAnchor = this.animatedValue as Float
                            updateKeyDisplay = false
                            invalidate()
                        }
                        start()
                    }
                    octave += 1
                    updateKeyDisplay = true
                    invalidate()
                    keysBitmapXAnchor=0.0f
                    return true
                }
                val selectedOctave = ((event.x - width.toFloat()/2.0f)/octaveIndicatorSize).roundToInt()
                if (selectedOctave < octave)
                {
                    // shift down
                    ValueAnimator.ofFloat(0.0f,width.toFloat()).apply {
                        duration = 200
                        repeatMode = ValueAnimator.RESTART
                        repeatCount = 0
                        interpolator = AccelerateDecelerateInterpolator()
                        addUpdateListener {
                            keysBitmapXAnchor = this.animatedValue as Float
                            updateKeyDisplay = false
                            invalidate()
                        }
                        start()
                    }
                    octave = selectedOctave
                    updateKeyDisplay =true
                    invalidate()
                    keysBitmapXAnchor=0.0f
                    return true
                }
                else if (selectedOctave > octave) {
                    ValueAnimator.ofFloat(0.0f,-width.toFloat()).apply {
                        duration = 200
                        repeatMode = ValueAnimator.RESTART
                        repeatCount = 0
                        interpolator = AccelerateDecelerateInterpolator()
                        addUpdateListener {
                            keysBitmapXAnchor = this.animatedValue as Float
                            updateKeyDisplay = false
                            invalidate()
                        }
                        start()
                    }
                    octave = selectedOctave
                    updateKeyDisplay = true
                    invalidate()
                    keysBitmapXAnchor=0.0f
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