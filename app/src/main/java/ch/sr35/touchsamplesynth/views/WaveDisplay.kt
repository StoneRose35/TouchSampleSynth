package ch.sr35.touchsamplesynth.views

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import ch.sr35.touchsamplesynth.R
import ch.sr35.touchsamplesynth.graphics.Converter
import kotlin.math.abs

val MARKER_SIZE= Converter.toPx(12)
val MARKER_LINE_WIDTH=Converter.toPx(3)

class WaveDisplay(context: Context, attributes: AttributeSet): View(context,attributes)   {
    private val waveColor:Paint = Paint()
    private val markersColor:Paint = Paint()
    private val backgroundColor:Paint = Paint()
    private var sampleData=ArrayList<Float>()
    var waveViewBuffer: Bitmap?=null
    private var waveViewbufferRect=Rect()
    var startMarkerPosition: Float=0.0f
    var endMarkerPosition: Float=1.0f
    var loopStartMarkerPosition: Float=0.23f
    var loopEndMarkerPosition: Float=0.74f
    private var startMarkerX=0
    private var startMarkerY=0
    private var endMarkerX=0
    private var endMarkerY=0
    private var loopMarkerStart=0
    private var loopMarkerEnd=0
    private var dragStart: WaveDisplayDragStart?=null
    var onChangeListener: WaveDisplayChangeListener?=null


    init {
        waveColor.color = context.getColor(R.color.purple_700)
        waveColor.style = Paint.Style.STROKE
        waveColor.isAntiAlias = false

        markersColor.color = context.getColor(R.color.black)
        markersColor.style = Paint.Style.FILL
        markersColor.isAntiAlias = true

        backgroundColor.color = context.getColor(R.color.white)
        backgroundColor.style = Paint.Style.FILL
        backgroundColor.isAntiAlias= false

        sampleData.addAll(arrayOf(-0.23f,0.545f,0.7875f,-0.32f))
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        canvas.drawRect(0.0f,0.0f,width.toFloat(),height.toFloat(),backgroundColor)
        waveViewbufferRect.top=0
        waveViewbufferRect.left=0
        waveViewbufferRect.right=width
        waveViewbufferRect.bottom=height
        if (waveViewBuffer != null) {
            canvas.drawBitmap(waveViewBuffer!!, null, waveViewbufferRect, null)
        }
        startMarkerX =((width - 2*MARKER_SIZE)*startMarkerPosition + MARKER_SIZE).toInt()
        startMarkerY = height - MARKER_SIZE
        canvas.drawCircle(startMarkerX.toFloat(),startMarkerY.toFloat(),
            MARKER_SIZE.toFloat(),markersColor)
        endMarkerX=((width - 2*MARKER_SIZE)*endMarkerPosition + MARKER_SIZE).toInt()
        endMarkerY = height - MARKER_SIZE
        canvas.drawCircle(endMarkerX.toFloat(),endMarkerY.toFloat(),
            MARKER_SIZE.toFloat(),markersColor)

        loopMarkerStart = ((width-2* MARKER_SIZE)*loopStartMarkerPosition).toInt()
        canvas.drawRoundRect(MARKER_SIZE.toFloat()- MARKER_LINE_WIDTH.toFloat()/2 + loopMarkerStart.toFloat(),
            0.0f,MARKER_SIZE.toFloat()+ MARKER_LINE_WIDTH.toFloat()/2 + loopMarkerStart.toFloat(),
            height.toFloat()-2* MARKER_SIZE.toFloat(),
            MARKER_LINE_WIDTH.toFloat()/2.0f,MARKER_LINE_WIDTH.toFloat()/2.0f,markersColor)
        loopMarkerEnd = ((width-2* MARKER_SIZE)*loopEndMarkerPosition).toInt()
        canvas.drawRoundRect(MARKER_SIZE.toFloat()- MARKER_LINE_WIDTH.toFloat()/2 + loopMarkerEnd.toFloat(),
            0.0f,MARKER_SIZE.toFloat()+ MARKER_LINE_WIDTH.toFloat()/2 + loopMarkerEnd.toFloat(),
            height.toFloat()-2* MARKER_SIZE.toFloat(),
            MARKER_LINE_WIDTH.toFloat()/2.0f,MARKER_LINE_WIDTH.toFloat()/2.0f,markersColor)

    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when (event?.action)
        {
            MotionEvent.ACTION_DOWN -> {
                if ((event.x - startMarkerX)*(event.x - startMarkerX) + (event.y - startMarkerY)*(event.y - startMarkerY) < MARKER_SIZE* MARKER_SIZE)
                {
                    dragStart=WaveDisplayDragStart(event.x.toInt(),WaveDisplayDragAnchorType.SAMPLE_START)
                }
                else if ((event.x - endMarkerX)*(event.x - endMarkerX) + (event.y - endMarkerY)*(event.y - endMarkerY) < MARKER_SIZE* MARKER_SIZE)
                {
                    dragStart=WaveDisplayDragStart(event.x.toInt(),WaveDisplayDragAnchorType.SAMPLE_END)
                }
                else if ((event.y < height - 2* MARKER_SIZE) && abs(event.x - loopMarkerStart)< MARKER_SIZE)
                {
                    dragStart= WaveDisplayDragStart(event.x.toInt(),WaveDisplayDragAnchorType.LOOP_START)
                }
                else if ((event.y < height - 2* MARKER_SIZE) && abs(event.x - loopMarkerEnd)< MARKER_SIZE)
                {
                    dragStart = WaveDisplayDragStart(event.x.toInt(),WaveDisplayDragAnchorType.LOOP_END)
                }
                else
                {
                    dragStart=null
                }
                performClick()
            }
            MotionEvent.ACTION_MOVE -> {
                if (dragStart!=null)
                {
                    val newRelativePos = toRelativeWidth(event.x.toInt())
                    when (dragStart!!.anchorType)
                    {
                        WaveDisplayDragAnchorType.SAMPLE_START -> {
                            if (validateMarkers(newRelativePos,endMarkerPosition,loopStartMarkerPosition,loopEndMarkerPosition))
                            {
                                startMarkerPosition = newRelativePos
                                onChangeListener?.sampleStartMarkerChanged(startMarkerPosition)
                            }
                        }
                        WaveDisplayDragAnchorType.SAMPLE_END ->  {
                            if (validateMarkers(startMarkerPosition,newRelativePos,loopStartMarkerPosition,loopEndMarkerPosition))
                            {
                                endMarkerPosition = newRelativePos
                                onChangeListener?.sampleEndMarkerChanged(endMarkerPosition)
                            }
                        }
                        WaveDisplayDragAnchorType.LOOP_START -> {
                            if (validateMarkers(startMarkerPosition,endMarkerPosition,newRelativePos,loopEndMarkerPosition))
                            {
                                loopStartMarkerPosition = newRelativePos
                                onChangeListener?.loopStartMarkerChanged(loopStartMarkerPosition)
                            }
                        }
                        WaveDisplayDragAnchorType.LOOP_END -> {
                            if (validateMarkers(startMarkerPosition,endMarkerPosition,loopStartMarkerPosition,newRelativePos))
                            {
                                loopEndMarkerPosition = newRelativePos
                                onChangeListener?.loopEndMarkerChanged(loopEndMarkerPosition)
                            }
                        }
                    }
                    invalidate()
                }
            }
            MotionEvent.ACTION_UP ->
            {
                dragStart=null
            }
        }
        return true
    }

    override fun performClick(): Boolean {

        return super.performClick()
    }

    private fun validateMarkers(sampleStart:Float,sampleEnd:Float,loopStart:Float,loopEnd:Float):Boolean
    {
        return (sampleStart < sampleEnd) && (sampleStart <= loopEnd) && (loopStart < loopEnd) && (loopEnd <= sampleEnd)
    }

    private fun toRelativeWidth(pos: Int): Float
    {
        return (pos.toFloat() - MARKER_SIZE.toFloat())/(width-2* MARKER_SIZE.toFloat())
    }
}

interface WaveDisplayChangeListener
{
    fun sampleStartMarkerChanged(v: Float)
    fun sampleEndMarkerChanged(v: Float)
    fun loopStartMarkerChanged(v: Float)
    fun loopEndMarkerChanged(v: Float)
}

enum class WaveDisplayDragAnchorType
{
    SAMPLE_START,
    SAMPLE_END,
    LOOP_START,
    LOOP_END
}
class WaveDisplayDragStart(var positionStart: Int,var anchorType: WaveDisplayDragAnchorType)