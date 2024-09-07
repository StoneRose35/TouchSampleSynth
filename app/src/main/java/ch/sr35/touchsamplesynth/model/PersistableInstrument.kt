package ch.sr35.touchsamplesynth.model

import android.content.Context
import ch.sr35.touchsamplesynth.audio.InstrumentI
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

open class PersistableInstrument(var actionAmountToVolume: Float=0.0f,var isMonophonic: Boolean=true,var name: String="",var id: String=""): Serializable, Cloneable {

    open fun fromInstrument(i: InstrumentI)
    {
        name = i.name
        isMonophonic = i.isMonophonic
        actionAmountToVolume = i.getVolumeModulation()
    }

    open fun toInstrument(i: InstrumentI)
    {
        i.name = name
        i.isMonophonic = isMonophonic
        i.setVolumeModulation(actionAmountToVolume)
    }


   public override fun clone(): Any {
        return super.clone()
    }

    override fun equals(other: Any?):
            Boolean
    {
        return if (other !is PersistableInstrument) {
            false
        } else {
            this.name == other.name && this.id == other.id && this.isMonophonic == other.isMonophonic
        }
    }



    override fun toString(): String
    {
        return "PersistableInstrument: %s, monophonic: %b".format(this.name, this.isMonophonic)
    }

    override fun hashCode(): Int {
        var result = actionAmountToVolume.hashCode()
        result = 31 * result + isMonophonic.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + id.hashCode()
        return result
    }


}

class PersistableInstrumentFactory
{
    companion object
    {
        fun fromInstrument(msg: InstrumentI?): PersistableInstrument?
        {
            val pi: PersistableInstrument = when (msg) {
                is SimpleSubtractiveSynthI -> {
                    SimpleSubtractiveSynthP(0.0f,0.0f,0.0f,0.0f,0.0f,0.0f,0.0f,0.0f, true, "")
                }
                is SineMonoSynthI -> {
                    SineMonoSynthP(0.0f,0.0f,0.0f,0.0f,0.0f,true,"")
                }
                is SamplerI -> {
                    SamplerP(0,0,0,0,0,"",0.0f,true,"")
                }
                else -> {
                    return null
                }
            }
            pi.fromInstrument(msg)
            return pi
        }
        fun toInstrument(pi: PersistableInstrument,context: Context): InstrumentI?
        {
            val instr: InstrumentI = when (pi) {
                is SimpleSubtractiveSynthP -> {
                    SimpleSubtractiveSynthI(context,pi.name)
                }

                is SineMonoSynthP -> {
                    SineMonoSynthI(context,pi.name)
                }

                is SamplerP -> {
                    SamplerI(context,pi.name)
                }
                else -> return null
            }
            if (pi.isMonophonic) {
                instr.generateVoices(1)
            }
            else
            {
                instr.generateVoices(InstrumentI.DEFAULT_POLYPHONY)
            }
            pi.toInstrument(instr)
            return instr
        }

    }
}