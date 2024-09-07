package ch.sr35.touchsamplesynth.model

import ch.sr35.touchsamplesynth.audio.InstrumentI
import ch.sr35.touchsamplesynth.audio.instruments.SineMonoSynthI
import java.io.Serializable

class SineMonoSynthP(private var attack: Float,
                     private var decay: Float,
                     private var sustain: Float,
                     private var release: Float,
                     actionAmountToVolume: Float,
                     isMono: Boolean,
                     name: String
): PersistableInstrument(actionAmountToVolume,isMono,name),Serializable, Cloneable {
    override fun fromInstrument(i: InstrumentI) {
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

    override fun toInstrument(i: InstrumentI) {
        super.toInstrument(i)
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
        return "SineMonoSynth: %s, voices: %b".format(this.name, this.isMonophonic)
    }

    override fun clone(): Any {
        return SineMonoSynthP(this.attack,this.decay,this.sustain,this.release,this.actionAmountToVolume,this.isMonophonic,this.name)
    }

}
