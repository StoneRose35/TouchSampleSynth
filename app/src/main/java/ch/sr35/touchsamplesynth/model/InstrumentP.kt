package ch.sr35.touchsamplesynth.model

import android.content.Context
import ch.sr35.touchsamplesynth.audio.InstrumentI
import ch.sr35.touchsamplesynth.audio.PolyphonyDefinition
import ch.sr35.touchsamplesynth.audio.instruments.SamplerI
import ch.sr35.touchsamplesynth.audio.instruments.SimpleSubtractiveSynthI
import ch.sr35.touchsamplesynth.audio.instruments.SineMonoSynthI
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import java.io.Serializable
import java.lang.reflect.Type

class PersistableInstrumentDeserializer: JsonDeserializer<InstrumentP> {
    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): InstrumentP? {
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

open class InstrumentP(var actionAmountToVolume: Float=0.0f, var polyphonyDefinition: PolyphonyDefinition=PolyphonyDefinition.MONOPHONIC, var name: String="", var id: String=""): Serializable, Cloneable {

    open fun fromInstrument(i: InstrumentI)
    {
        name = i.name
        polyphonyDefinition = i.polyphonyDefinition
        actionAmountToVolume = i.getVolumeModulation()
    }

    open fun toInstrument(i: InstrumentI)
    {
        i.name = name
        i.polyphonyDefinition = polyphonyDefinition
        i.setVolumeModulation(actionAmountToVolume)
    }


   public override fun clone(): Any {
        return super.clone()
    }

    override fun equals(other: Any?):
            Boolean
    {
        return if (other !is InstrumentP) {
            false
        } else {
            this.name == other.name && this.id == other.id && this.polyphonyDefinition == other.polyphonyDefinition
        }
    }



    override fun toString(): String
    {
        return "PersistableInstrument: %s, polyphonyDefinit: %b".format(this.name, this.polyphonyDefinition)
    }

    override fun hashCode(): Int {
        var result = actionAmountToVolume.hashCode()
        result = 31 * result + polyphonyDefinition.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + id.hashCode()
        return result
    }


}

class PersistableInstrumentFactory
{
    companion object
    {
        fun fromInstrument(msg: InstrumentI?): InstrumentP?
        {
            val pi: InstrumentP = when (msg) {
                is SimpleSubtractiveSynthI -> {
                    SimpleSubtractiveSynthP(0.0f,0.0f,0.0f,0.0f,0.0f,0.0f,0.0f,0.0f, PolyphonyDefinition.MONOPHONIC, "")
                }
                is SineMonoSynthI -> {
                    SineMonoSynthP(0.0f,0.0f,0.0f,0.0f,0.0f,PolyphonyDefinition.MONOPHONIC,"")
                }
                is SamplerI -> {
                    SamplerP(0,0,0,0,0,"",0.0f,PolyphonyDefinition.MONOPHONIC,"")
                }
                else -> {
                    return null
                }
            }
            pi.fromInstrument(msg)
            return pi
        }
        fun toInstrument(pi: InstrumentP, context: Context): InstrumentI?
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
            if (pi.polyphonyDefinition == PolyphonyDefinition.MONOPHONIC) {
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