package ch.sr35.touchsamplesynth.views

import android.content.Context
import android.util.AttributeSet
import ch.sr35.touchsamplesynth.R
import android.widget.LinearLayout
import android.widget.TextView
import ch.sr35.touchsamplesynth.audio.Instrument

class InstrumentChip(context: Context,attributeSet: AttributeSet?): LinearLayout(context,attributeSet) {

    private var instrument: Instrument?=null
    init {
        inflate(context,R.layout.instrument_chip,this)
    }

    fun setInstrument(i: Instrument)
    {
        instrument = i
        instrument?.let {
            val nameAndType = findViewById<TextView>(R.id.instrument_chip_name)
            nameAndType.text = it.name
            nameAndType.setCompoundDrawables(null, it.getInstrumentIcon(),null,null)
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

    fun getInstrument(): Instrument?
    {
        return instrument
    }

}