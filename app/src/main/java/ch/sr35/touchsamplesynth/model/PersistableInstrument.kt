package ch.sr35.touchsamplesynth.model

import android.content.Context
import ch.sr35.touchsamplesynth.audio.Instrument
import ch.sr35.touchsamplesynth.audio.MusicalSoundGenerator
import ch.sr35.touchsamplesynth.audio.instruments.SimpleSubtractiveSynthI
import ch.sr35.touchsamplesynth.audio.instruments.SineMonoSynthI
import ch.sr35.touchsamplesynth.audio.voices.SimpleSubtractiveSynthK
import ch.sr35.touchsamplesynth.audio.voices.SineMonoSynthK
import java.io.Serializable

interface PersistableInstrument: Serializable {

    var nVoices: Int
    var name: String
    fun fromInstrument(i: Instrument)

    fun toInstrument(i: Instrument)

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
            else
            {
                return null
            }
        }
        fun toInstrument(pi: PersistableInstrument,context: Context): Instrument?
        {
            if (pi is SimpleSubtractiveSynthP) {
                val instr = SimpleSubtractiveSynthI.generateInstance(context,pi.nVoices,pi.name)
                pi.toInstrument(instr)
                return instr
            }
            else if (pi is SineMonoSynthP)
            {
                val instr = SineMonoSynthI.generateInstance(context,pi.nVoices,pi.name)
                pi.toInstrument(instr)
                return instr
            }
            return null
        }

    }
}