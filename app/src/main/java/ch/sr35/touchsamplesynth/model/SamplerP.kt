package ch.sr35.touchsamplesynth.model

import androidx.core.net.toUri
import ch.sr35.touchsamplesynth.audio.Instrument
import ch.sr35.touchsamplesynth.audio.instruments.SamplerI
import java.io.File
import java.io.Serializable

class SamplerP(private var sampleStart: Int,
               private var sampleEnd: Int,
               private var loopStart: Int,
               private var loopEnd: Int,
               private var mode: Byte,
               private var sampleFile: String,
               private var actionAmountToVolume: Float,
               nVoices: Int,
               name: String
):PersistableInstrument(nVoices,name), Serializable, Cloneable  {

    override fun fromInstrument(i: Instrument) {
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
            actionAmountToVolume = i.getVolumeModulation()
        }
    }

    override fun toInstrument(i: Instrument) {
        if (i is SamplerI)
        {
            if (sampleFile.isNotEmpty() && File(sampleFile).exists()) {
                i.setSampleFile(File(sampleFile).toUri())
            }
            i.setLoopStartIndex(loopStart)
            i.setLoopEndIndex(loopEnd)
            i.setSampleStartIndex(sampleStart)
            i.setSampleEndIndex(sampleEnd)
            i.setMode(mode)
            i.setVolumeModulation(actionAmountToVolume)

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
        return "Sampler: %s, voices: %d".format(this.name, this.nVoices)
    }
    override fun clone(): Any {
        return SamplerP(this.sampleStart,this.sampleEnd,this.loopStart,this.loopEnd,this.mode,this.sampleFile,this.actionAmountToVolume,this.nVoices,this.name)
    }
}