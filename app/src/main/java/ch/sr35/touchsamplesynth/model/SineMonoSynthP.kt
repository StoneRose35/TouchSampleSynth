package ch.sr35.touchsamplesynth.model

import ch.sr35.touchsamplesynth.audio.Instrument
import ch.sr35.touchsamplesynth.audio.MusicalSoundGenerator
import ch.sr35.touchsamplesynth.audio.instruments.SineMonoSynthI
import ch.sr35.touchsamplesynth.audio.voices.SineMonoSynthK
import java.io.Serializable

class SineMonoSynthP(private var attack: Float,
                     private var decay: Float,
                     private var sustain: Float,
                     private var release: Float,
                     override var nVoices: Int,
                     override var name: String
): PersistableInstrument,Serializable {
    override fun fromInstrument(msg: Instrument) {
        super.fromInstrument(msg)
        if (msg is SineMonoSynthI)
        {
            attack = msg.getAttack()
            decay = msg.getDecay()
            sustain = msg.getSustain()
            release = msg.getRelease()
        }
    }

    override fun toInstrument(msg: Instrument) {
        if (msg is SineMonoSynthI)
        {
            msg.generateVoices(nVoices)
            msg.setAttack(attack)
            msg.setDecay(decay)
            msg.setSustain(sustain)
            msg.setRelease(release)

        }
    }

    override fun equals(other: Any?): Boolean {
        if (other is SineMonoSynthP)
        {
            return this.hashCode() == other.hashCode()
        }
        return false
    }

    override fun hashCode(): Int {
        return this.attack.toRawBits() +
                this.decay.toRawBits() +
                this.sustain.toRawBits() +
                this.release.toRawBits()

    }

}
