
package ch.sr35.touchsamplesynth.model
import ch.sr35.touchsamplesynth.audio.instruments.InstrumentI
import ch.sr35.touchsamplesynth.audio.instruments.LooperI
import ch.sr35.touchsamplesynth.audio.instruments.PolyphonyDefinition
import java.io.Serializable


class LooperP(
    private var readPointer: Int=0,
    private var writePointer: Int=0,
    private var loopEnd: Int=0,
    @ExcludeFromJson var sample: FloatArray?=null,
    actionAmountToVolume: Float=0.0f,
    actionAmountToPitchBend: Float=0.0f,    
    polyphonyDefinition: PolyphonyDefinition=PolyphonyDefinition.MONOPHONIC,
    horizontalToActionB: Boolean=false,
    nVoices: Int=0,
    name: String=""
): InstrumentP(actionAmountToVolume,actionAmountToPitchBend,polyphonyDefinition,horizontalToActionB,nVoices,name),Serializable, Cloneable {
    private val className: String=this.javaClass.name
    override fun fromInstrument(i: InstrumentI) {
        super.fromInstrument(i)
        if (i is LooperI)
        {
            readPointer = i.getReadPointer()
            writePointer = i.getWritePointer()
            loopEnd = i.getLoopEnd()
            sample = i.getSample()
        }
    }

    override fun toInstrument(i: InstrumentI) {
        if (i is LooperI)
        {
            super.toInstrument(i)
            i.setReadPointer(this.readPointer)
            i.setWritePointer(this.writePointer)
            i.setLoopEnd(this.loopEnd)
            this.sample?.let { i.setSample(it) }
        }
    }

    override fun equals(other: Any?): Boolean {
        if (other is LooperP)
        {
            return this.hashCode() == other.hashCode()
        }
        return false
    }

    override fun hashCode(): Int {
        return super.hashCode() + 
        this.readPointer +
        this.writePointer +
        this.loopEnd  
    }

    override fun toString(): String
    {
        return "Looper: %s, voices: %b".format(this.name, this.polyphonyDefinition)
    }

    override fun clone(): Any {
        return LooperP(
            this.readPointer,
            this.writePointer,
            this.loopEnd,
            this.sample,
            this.actionAmountToVolume,
            this.actionAmountToPitchBend,
            this.polyphonyDefinition,
            this.horizontalToActionB,
            this.nVoices,
            this.name)
    }
}


