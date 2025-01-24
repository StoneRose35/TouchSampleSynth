package ch.sr35.touchsamplesynth.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import ch.sr35.touchsamplesynth.graphics.Converter
import android.graphics.drawable.Drawable
import android.graphics.drawable.RotateDrawable
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.appcompat.content.res.AppCompatResources
import ch.sr35.touchsamplesynth.R
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin

private var PADDING_KNOB = Converter.toPx(3.0f)

class Knob : androidx.appcompat.widget.AppCompatSeekBar {



    var isDiscrete = false

    private var knobValueStart = progress
    private var startValY = 0.0f
    private var touchScale = Converter.toPx(240.0f)

    private var scalePaint: Paint = Paint()
    private var scalePaintThin: Paint = Paint()
    private lateinit var knobDrawable: Drawable
    private var onChangeListener: OnSeekBarChangeListener? = null
    private var linearity=1.0f



    constructor(context: Context) : super(context) {
        init(null, 0)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(attrs, 0)
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    ) {
        init(attrs, defStyle)
    }

    private fun init(attrs: AttributeSet?, defStyle: Int) {
        knobDrawable = AppCompatResources.getDrawable(context,R.drawable.knob)!!
        scalePaint.color = Color.BLACK
        scalePaint.strokeWidth = Converter.toPx(3.0f)
        scalePaint.isAntiAlias = true
        scalePaint.style = Paint.Style.STROKE
        scalePaintThin.color = Color.BLACK
        scalePaintThin.strokeWidth = Converter.toPx(1.0f)
        scalePaintThin.isAntiAlias = true
        scalePaintThin.style = Paint.Style.STROKE
        attrs?.let {
            context.obtainStyledAttributes(attrs, R.styleable.Knob).also {
                linearity = it.getFloat(R.styleable.Knob_linearity,1.0f)
                isDiscrete = it.getBoolean(R.styleable.Knob_isDiscrete,false)
                it.recycle()
            }
        }

    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                knobValueStart = progress
                parent.requestDisallowInterceptTouchEvent(true)
                startValY = event.y
                return true
            }
            MotionEvent.ACTION_MOVE -> {
                val interm = ((startValY - event.y)/touchScale)*(max-min)
                progress = knobValueStart + interm.toInt()
                progress = if (progress < min)
                {
                    min
                }
                else if (progress > max) {
                    max
                }  else
                {
                    progress
                }
                onChangeListener?.onProgressChanged(this,progress,true)
                postInvalidate()
                return true
            }
            MotionEvent.ACTION_UP ->
            {
                parent.requestDisallowInterceptTouchEvent(false)
                return true
            }
        }
        return false
    }


    override fun setOnSeekBarChangeListener(l: OnSeekBarChangeListener?) {
        onChangeListener = l
    }

    private fun progressSkew(): Int
    {
        return if (linearity == 1.0f)
        {
            progress
        }
        else
        {
            ((((progress.toFloat()-min)/(max - min)).pow(linearity))*(max-min) + min).toInt()
        }
    }
    override fun onDraw(canvas: Canvas) {
        //super.onDraw(canvas)

        canvas.drawArc(PADDING_KNOB, PADDING_KNOB,height-PADDING_KNOB,width-PADDING_KNOB,120f,300f,true,scalePaint)
        val knobValueAngle = -150.0f+300.0f * (progress - min) / (max - min)

        if (isDiscrete)
        {
            for (i in min..max)
            {
                val currentAngle = 120.0f + 300.0f * (i-min) / (max- min)
                canvas.drawLine(width.toFloat()/2.0f,height.toFloat()/2.0f,
                    (width.toFloat()/2.0f + (width.toFloat() - 2.0f*PADDING_KNOB)/2.0f*cos(PI/180.0f*currentAngle)).toFloat(),
                    (height.toFloat()/2.0f + (height.toFloat() - 2.0f*PADDING_KNOB)/2.0f*sin(PI/180.0f*currentAngle)).toFloat(),scalePaintThin)
            }
        }
        knobDrawable.setBounds(PADDING_KNOB.toInt()*3,PADDING_KNOB.toInt()*3,width-PADDING_KNOB.toInt()*3,height-PADDING_KNOB.toInt()*3)
        canvas.save()
        canvas.rotate(knobValueAngle,width.toFloat()/2.0f,height.toFloat()/2.0f)
        knobDrawable.draw(canvas)
        canvas.restore()

        }
    }
