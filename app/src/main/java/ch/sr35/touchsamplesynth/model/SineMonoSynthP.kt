package ch.sr35.touchsamplesynth.model

import ch.sr35.touchsamplesynth.audio.Instrument
import ch.sr35.touchsamplesynth.audio.instruments.SineMonoSynthI
import java.io.Serializable

class SineMonoSynthP(private var attack: Float,
                     private var decay: Float,
                     private var sustain: Float,
                     private var release: Float,
                     private var actionAmountToVolume: Float,
                     nVoices: Int,
                     name: String
): PersistableInstrument(nVoices,name),Serializable, Cloneable {
    override fun fromInstrument(i: Instrument) {
        super.fromInstrument(i)
        if (i is SineMonoSynthI)
        {

            attack = i.getAttack()
            decay = i.getDecay()
            sustain = i.getSustain()
            release = i.getRelease()
            actionAmountToVolume =i.getVolumeModulation()
        }
    }

    override fun toInstrument(i: Instrument) {
        if (i is SineMonoSynthI)
        {
            i.setAttack(attack)
            i.setDecay(decay)
            i.setSustain(sustain)
            i.setRelease(release)
            i.setVolumeModulation(actionAmountToVolume)
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
                this.release.toRawBits() + super.hashCode()

    }

    override fun toString(): String
    {
        return "SineMonoSynth: %s, voices: %d".format(this.name, this.nVoices)
    }

    override fun clone(): Any {
        return SineMonoSynthP(this.attack,this.decay,this.sustain,this.release,this.actionAmountToVolume,this.nVoices,this.name)
    }

}
