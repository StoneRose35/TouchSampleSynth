package ch.sr35.touchsamplesynth.model

import android.content.Context
import ch.sr35.touchsamplesynth.audio.Instrument
import ch.sr35.touchsamplesynth.audio.instruments.SamplerI
import ch.sr35.touchsamplesynth.audio.instruments.SimpleSubtractiveSynthI
import ch.sr35.touchsamplesynth.audio.instruments.SineMonoSynthI
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import java.io.Serializable
import java.lang.reflect.Type

class PersistableInstrumentDeserializer: JsonDeserializer<PersistableInstrument> {
    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): PersistableInstrument? {
        if (json?.asJsonObject?.has("sampleStart")==true)
        {
            return context?.deserialize(json,SamplerP::class.java)
        }
        else if (json?.asJsonObject?.has("resonance")==true)
        {
            return context?.deserialize(json,SimpleSubtractiveSynthP::class.java)
        }
        else if (json?.asJsonObject?.has("attack")==true)
        {
            return context?.deserialize(json,SineMonoSynthP::class.java)
        }
        return null
    }
}

open class PersistableInstrument(var actionAmountToVolume: Float=0.0f,var nVoices: Int=0,var name: String=""): Serializable, Cloneable {

    open fun fromInstrument(i: Instrument)
    {
        name = i.name
        nVoices = i.voicesCount()
        actionAmountToVolume = i.getVolumeModulation()
    }

    open fun toInstrument(i: Instrument)
    {
        i.name = name
        i.setVolumeModulation(actionAmountToVolume)
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
            when (msg) {
                is SimpleSubtractiveSynthI -> {
                    val pi = SimpleSubtractiveSynthP(0.0f,0.0f,0.0f,0.0f,0.0f,0.0f,0.0f,0.0f, 0, "")
                    pi.fromInstrument(msg)
                    return pi
                }

                is SineMonoSynthI -> {
                    val pi = SineMonoSynthP(0.0f,0.0f,0.0f,0.0f,0.0f,0,"")
                    pi.fromInstrument(msg)
                    return pi
                }

                is SamplerI -> {
                    val pi = SamplerP(0,0,0,0,0,"",0.0f,0,"")
                    pi.fromInstrument(msg)
                    return pi
                }

                else -> {
                    return null
                }
            }
        }
        fun toInstrument(pi: PersistableInstrument,context: Context): Instrument?
        {
            when (pi) {
                is SimpleSubtractiveSynthP -> {
                    val instr = SimpleSubtractiveSynthI(context,pi.name)
                    instr.generateVoices(pi.nVoices)
                    pi.toInstrument(instr)
                    return instr
                }

                is SineMonoSynthP -> {
                    val instr = SineMonoSynthI(context,pi.name)
                    instr.generateVoices(pi.nVoices)
                    pi.toInstrument(instr)
                    return instr
                }

                is SamplerP -> {
                    val instr = SamplerI(context,pi.name)
                    instr.generateVoices(pi.nVoices)
                    pi.toInstrument(instr)
                    return instr
                }

                else -> return null
            }
        }

    }
}