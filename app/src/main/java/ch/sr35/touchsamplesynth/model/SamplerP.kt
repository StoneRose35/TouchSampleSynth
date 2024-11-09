package ch.sr35.touchsamplesynth.model

import androidx.core.net.toUri
import ch.sr35.touchsamplesynth.audio.InstrumentI
import ch.sr35.touchsamplesynth.audio.PolyphonyDefinition
import ch.sr35.touchsamplesynth.audio.instruments.SamplerI
import java.io.File
import java.io.Serializable

class SamplerP(private var sampleStart: Int,
               private var sampleEnd: Int,
               private var loopStart: Int,
               private var loopEnd: Int,
               private var mode: Byte,
               private var sampleFile: String,
               actionAmountToVolume: Float,
               polyphonyDefinition: PolyphonyDefinition,
               nVoices: Int,
               name: String
):InstrumentP(actionAmountToVolume, polyphonyDefinition,nVoices,name), Serializable, Cloneable  {

    override fun fromInstrument(i: InstrumentI) {
        super.fromInstrument(i)
        if (i is SamplerI)
        {
            sampleStart= i.getSampleStartIndex()
            sampleEnd = i.getSampleEndIndex()
            loopStart = i.getLoopStartIndex()
            loopEnd = i.getLoopEndIndex()
            mode = i.getMode()
            if (i.sampleUri!=null) {
                sampleFile = i.sampleUri!!.path.toString()
            }
        }
    }

    override fun toInstrument(i: InstrumentI) {
        if (i is SamplerI)
        {
            super.toInstrument(i)
            if (sampleFile.isNotEmpty() && File(sampleFile).exists()) {
                i.setSampleFile(File(sampleFile).toUri())
            }
            i.setLoopStartIndex(loopStart)
            i.setLoopEndIndex(loopEnd)
            i.setSampleStartIndex(sampleStart)
            i.setSampleEndIndex(sampleEnd)
            i.setMode(mode)
        }
    }

    override fun equals(other: Any?): Boolean {
        if (other is SamplerP)
        {
            return this.hashCode() == other.hashCode()
        }
        return false
    }

    override fun hashCode(): Int {
        return this.sampleStart+
                this.sampleEnd +
                this.loopStart +
                this.loopEnd + this.mode + super.hashCode()
    }

    override fun toString(): String
    {
        return "Sampler: %s, polyphonyDefinition: %s".format(this.name, this.polyphonyDefinition)
    }
    override fun clone(): Any {
        return SamplerP(this.sampleStart,this.sampleEnd,this.loopStart,this.loopEnd,this.mode,this.sampleFile,this.actionAmountToVolume,this.polyphonyDefinition,this.nVoices,this.name)
    }
}