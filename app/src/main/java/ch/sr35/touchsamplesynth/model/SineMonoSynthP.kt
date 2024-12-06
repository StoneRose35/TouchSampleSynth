package ch.sr35.touchsamplesynth.model

import ch.sr35.touchsamplesynth.audio.instruments.InstrumentI
import ch.sr35.touchsamplesynth.audio.instruments.PolyphonyDefinition
import ch.sr35.touchsamplesynth.audio.instruments.SineMonoSynthI
import java.io.Serializable

class SineMonoSynthP(
    private var attack: Float=0.0f,
    private var decay: Float=0.0f,
    private var sustain: Float=0.0f,
    private var release: Float=0.0f,
    actionAmountToVolume: Float=0.0f,
    actionAmountToPitchBend: Float=0.0f,
    polyphonyDefinition: PolyphonyDefinition = PolyphonyDefinition.MONOPHONIC,
    horizontalToActionB: Boolean=false,
    nVoices: Int=0,
    name: String=""
): InstrumentP(actionAmountToVolume, actionAmountToPitchBend,polyphonyDefinition,horizontalToActionB, nVoices,name),Serializable, Cloneable {
    private val className: String=this.javaClass.name
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

        if (i is SineMonoSynthI)
        {
            super.toInstrument(i)
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
        return super.hashCode() +
                 this.attack.toRawBits() +
                 this.decay.toRawBits() +
                 this.sustain.toRawBits() +
                 this.release.toRawBits()
    }

    override fun toString(): String
    {
        return "SineMonoSynth: %s, voices: %b".format(this.name, this.polyphonyDefinition)
    }

    override fun clone(): Any {
        return SineMonoSynthP(
            this.attack,
            this.decay,
            this.sustain,
            this.release,
            this.actionAmountToVolume,
            this.actionAmountToPitchBend,
            this.polyphonyDefinition,
            this.horizontalToActionB,
            this.nVoices,
            this.name)
    }

}
