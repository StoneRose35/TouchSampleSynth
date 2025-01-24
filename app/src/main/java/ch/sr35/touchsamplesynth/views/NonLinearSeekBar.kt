package ch.sr35.touchsamplesynth.views

import android.content.Context
import android.util.AttributeSet
import android.widget.SeekBar
import ch.sr35.touchsamplesynth.R
import kotlin.math.pow

class NonLinearSeekBar: androidx.appcompat.widget.AppCompatSeekBar, SeekBar.OnSeekBarChangeListener {

    private var linearity = 1.0f
    private var nonLinearProgressChangeListener: OnSeekBarChangeListener?=null
    constructor(context: Context): super(context)
    {
        init(null,0)
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
        context.obtainStyledAttributes(attrs, R.styleable.Knob).also {
            linearity = it.getFloat(R.styleable.Knob_linearity, 1.0f)
            it.recycle()
        }
    }

    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
        nonLinearProgressChangeListener?.let {
            if (linearity == 1.0f) {
                it.onProgressChanged(this, progress, fromUser)
            }
            else
            {
                it.onProgressChanged(this, (((progress.toFloat() - min.toFloat())/(max.toFloat() - min.toFloat())).pow(linearity)*(max - min) + min) .toInt(), fromUser)
            }
        }
    }

    override fun setOnSeekBarChangeListener(l: OnSeekBarChangeListener?) {
        nonLinearProgressChangeListener = l
    }

    override fun onStartTrackingTouch(seekBar: SeekBar?) {
    }

    override fun onStopTrackingTouch(seekBar: SeekBar?) {
    }


}