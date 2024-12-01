package ch.sr35.touchsamplesynth.model

import android.content.Context
import ch.sr35.touchsamplesynth.audio.instruments.InstrumentI
import ch.sr35.touchsamplesynth.audio.instruments.PolyphonyDefinition
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
        val className = json?.asJsonObject?.get("className")?.asString
        return context?.deserialize(json,Class.forName(className!!))
    }
}

open class InstrumentP(var actionAmountToVolume: Float=0.0f, var polyphonyDefinition: PolyphonyDefinition = PolyphonyDefinition.MONOPHONIC, var nVoices:Int=0, var name: String="", var id: String=""): Serializable, Cloneable {

    init {
        val className = this.javaClass.name
    }
    open fun fromInstrument(i: InstrumentI)
    {
        name = i.name
        polyphonyDefinition = i.polyphonyDefinition
        actionAmountToVolume = i.getVolumeModulation()
        nVoices = i.voicesCount()
    }

    open fun toInstrument(i: InstrumentI)
    {
        i.name = name
        i.polyphonyDefinition = polyphonyDefinition
        i.generateVoices(nVoices)
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
        return "PersistableInstrument: %s, polyphonyDefinition: %b".format(this.name, this.polyphonyDefinition)
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
        fun fromInstrument(msg: InstrumentI?): InstrumentP
        {
            val className = msg?.javaClass?.name!!.split(".").last()
            val pi = Class.forName("ch.sr35.touchsamplesynth.model.${className.substring(0,className.length-1)+"P"}").constructors[0].newInstance() as InstrumentP
            pi.fromInstrument(msg)
            return pi
        }
        fun toInstrument(pi: InstrumentP, context: Context): InstrumentI
        {
            val className = pi.javaClass.name.split(".").last()
            val instr = Class.forName("ch.sr35.touchsamplesynth.audio.instruments.${className.substring(0,className.length-1)+"I"}").constructors[0].newInstance(context,pi.name) as InstrumentI
            pi.toInstrument(instr)
            return instr
        }

    }
}