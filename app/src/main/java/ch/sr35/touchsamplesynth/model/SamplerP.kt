package ch.sr35.touchsamplesynth.model

import android.net.Uri
import ch.sr35.touchsamplesynth.audio.Instrument
import ch.sr35.touchsamplesynth.audio.instruments.SamplerI
import java.io.Serializable

class SamplerP(private var sampleStart: Int,
               private var sampleEnd: Int,
               private var loopStart: Int,
               private var loopEnd: Int,
               private var mode: Byte,
               private var sampleFile: String,
               override var nVoices: Int,
               override var name: String
):PersistableInstrument(), Serializable, Cloneable  {

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
                sampleFile = i.sampleUri!!.toString()
            }
        }
    }

    override fun toInstrument(i: Instrument) {
        if (i is SamplerI)
        {
            if (sampleFile.isNotEmpty()) {
                i.setSampleFile(Uri.parse(sampleFile))
            }
            i.setSampleStartIndex(sampleStart)
            i.setSampleEndIndex(sampleEnd)
            i.setLoopStartIndex(loopStart)
            i.setLoopEndIndex(loopEnd)
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
        return "Sampler: %s, voices: %d".format(this.name, this.nVoices)
    }
    override fun clone(): Any {
        return SamplerP(this.sampleStart,this.sampleEnd,this.loopStart,this.loopEnd,this.mode,this.sampleFile,this.nVoices,this.name)
    }
}