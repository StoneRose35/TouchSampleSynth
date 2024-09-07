package ch.sr35.touchsamplesynth.views

import android.content.Context
import android.graphics.drawable.VectorDrawable
import android.util.AttributeSet
import ch.sr35.touchsamplesynth.R
import android.widget.LinearLayout
import android.widget.TextView
import ch.sr35.touchsamplesynth.audio.InstrumentI

class InstrumentChip(context: Context,attributeSet: AttributeSet?): LinearLayout(context,attributeSet) {

    private var instrumentI: InstrumentI?=null
    init {
        inflate(context,R.layout.instrument_chip,this)
    }

    fun setInstrument(i: InstrumentI)
    {
        instrumentI = i
        instrumentI?.let { it ->
            val nameAndType = findViewById<TextView>(R.id.instrument_chip_name)
            nameAndType.text = it.name
            nameAndType.setCompoundDrawables(null, it.getInstrumentIcon().also { drawable ->
                drawable?.setBounds(0,0,(drawable as VectorDrawable).minimumWidth,drawable.minimumHeight)
            },null,null)
            if (it.isMonophonic) {
                findViewById<TextView>(R.id.instrument_chip_monopoly).text =
                    context.getString(R.string.monophonic)
            }
            else
            {
                findViewById<TextView>(R.id.instrument_chip_monopoly).text =
                    context.getString(R.string.polyphonic)
            }
        }

    }

    fun getInstrument(): InstrumentI?
    {
        return instrumentI
    }

}