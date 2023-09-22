package ch.sr35.touchsamplesynth.model

import android.content.Context
import ch.sr35.touchsamplesynth.audio.MusicalSoundGenerator
import ch.sr35.touchsamplesynth.audio.instruments.SimpleSubtractiveSynthK
import ch.sr35.touchsamplesynth.audio.instruments.SineMonoSynthK
import java.io.Serializable

interface PersistableInstrument: Serializable {

    fun fromInstrument(msg: MusicalSoundGenerator)

    fun toInstrument(msg: MusicalSoundGenerator)

}

class PersistableInstrumentFactory
{
    companion object
    {
        fun fromInstrument(msg: MusicalSoundGenerator?): PersistableInstrument?
        {
            if(msg is SimpleSubtractiveSynthK)
            {
                val pi = SimpleSubtractiveSynthP(0.0f,0.0f,0.0f,0.0f,0.0f,0.0f,0.0f)
                pi.fromInstrument(msg)
                return pi
            }
            else if (msg is SineMonoSynthK)
            {
                val pi = SineMonoSynthP(0.0f,0.0f,0.0f,0.0f)
                pi.fromInstrument(msg)
                return pi
            }
            else
            {
                return null
            }
        }
        fun toInstrument(pi: PersistableInstrument,context: Context): MusicalSoundGenerator?
        {
            if (pi is SimpleSubtractiveSynthP) {
                val instr = SimpleSubtractiveSynthK(context)
                pi.toInstrument(instr)
                return instr
            }
            else if (pi is SineMonoSynthP)
            {
                val instr = SineMonoSynthK(context)
                pi.toInstrument(instr)
                return instr
            }
            return null
        }

    }
}