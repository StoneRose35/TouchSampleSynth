package ch.sr35.touchsamplesynth.model

import android.content.Context
import ch.sr35.touchsamplesynth.audio.Instrument
import ch.sr35.touchsamplesynth.audio.instruments.SamplerI
import ch.sr35.touchsamplesynth.audio.instruments.SimpleSubtractiveSynthI
import ch.sr35.touchsamplesynth.audio.instruments.SineMonoSynthI
import java.io.Serializable

open class PersistableInstrument(var nVoices: Int=0,var name: String=""): Serializable, Cloneable {

    open fun fromInstrument(i: Instrument)
    {
        name = i.name
        nVoices = i.voicesCount()
    }

    open fun toInstrument(i: Instrument)
    {
        i.name = name
    }

    override fun hashCode(): Int
    {
        return name.hashCode() + nVoices
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PersistableInstrument

        if (nVoices != other.nVoices) return false
        if (name != other.name) return false

        return true
    }

   public override fun clone(): Any {
        return super.clone()
    }

    override fun toString(): String
    {
        return "PersistableInstrument: %s, voices: %d".format(this.name, this.nVoices)
    }


}

class PersistableInstrumentFactory
{
    companion object
    {
        fun fromInstrument(msg: Instrument?): PersistableInstrument?
        {
            if (msg is SimpleSubtractiveSynthI)
            {
                val pi = SimpleSubtractiveSynthP(0.0f,0.0f,0.0f,0.0f,0.0f,0.0f,0.0f, 0, "")
                pi.fromInstrument(msg)
                return pi
            }
            else if (msg is SineMonoSynthI)
            {
                val pi = SineMonoSynthP(0.0f,0.0f,0.0f,0.0f,0,"")
                pi.fromInstrument(msg)
                return pi
            }
            else if (msg is SamplerI)
            {
                val pi = SamplerP(0,0,0,0,0,"",0,"")
                pi.fromInstrument(msg)
                return pi
            }
            else
            {
                return null
            }
        }
        fun toInstrument(pi: PersistableInstrument,context: Context): Instrument?
        {
            if (pi is SimpleSubtractiveSynthP) {
                val instr = SimpleSubtractiveSynthI(context,pi.name)
                instr.generateVoices(pi.nVoices)
                pi.toInstrument(instr)
                return instr
            }
            else if (pi is SineMonoSynthP)
            {
                val instr = SineMonoSynthI(context,pi.name)
                instr.generateVoices(pi.nVoices)
                pi.toInstrument(instr)
                return instr
            }
            else if (pi is SamplerP)
            {
                val instr = SamplerI(context,pi.name)
                instr.generateVoices(pi.nVoices)
                pi.toInstrument(instr)
                return instr
            }
            return null
        }

    }
}