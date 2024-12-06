package ch.sr35.touchsamplesynth.model

import ch.sr35.touchsamplesynth.audio.instruments.InstrumentI
import ch.sr35.touchsamplesynth.audio.instruments.PolyphonyDefinition
import ch.sr35.touchsamplesynth.audio.instruments.SimpleSubtractiveSynthI

class SimpleSubtractiveSynthP(
    private var attack: Float=0.0f,
    private var decay: Float=0.0f,
    private var sustain: Float=0.0f,
    private var release: Float=0.0f,
    private var initialCutoff: Float=0.0f,
    private var resonance: Float=0.0f,
    private var actionAmountToFilter: Float=0.0f,
    actionAmountToVolume: Float=0.0f,
    actionAmountToPitchBend: Float=0.0f,
    polyphonyDefinition: PolyphonyDefinition = PolyphonyDefinition.MONOPHONIC,
    horizontalToActionB: Boolean=false,
    nVoices: Int=0,
    name: String=""
): InstrumentP(actionAmountToVolume,actionAmountToPitchBend, polyphonyDefinition,horizontalToActionB,nVoices,name) {
    private val className: String=this.javaClass.name
    override fun fromInstrument(i: InstrumentI) {
        super.fromInstrument(i)
        if (i is SimpleSubtractiveSynthI)
        {
            attack = i.getAttack()
            decay = i.getDecay()
            sustain = i.getSustain()
            release = i.getRelease()
            initialCutoff = i.getInitialCutoff()
            actionAmountToFilter = i.getActionAmountToFilter()
            actionAmountToVolume = i.getVolumeModulation()
            resonance = i.getResonance()
        }
    }

    override fun toInstrument(i: InstrumentI) {

        if (i is SimpleSubtractiveSynthI)
        {
            super.toInstrument(i)
            i.setAttack(attack)
            i.setDecay(decay)
            i.setSustain(sustain)
            i.setRelease(release)
            i.setInitialCutoff(initialCutoff)
            i.setActionAmountToFilter(actionAmountToFilter)
            i.setVolumeModulation(actionAmountToVolume)
            i.setResonance(resonance)
        }
    }

    override fun equals(other: Any?): Boolean {
        if (other is SimpleSubtractiveSynthP)
        {
            return this.hashCode() == other.hashCode()
        }
        return false
    }

    override fun toString(): String
    {
        return "SimpleSubtractiveSynth: %s, polyphonyDefinition: %b".format(this.name, this.polyphonyDefinition)
    }

    override fun hashCode(): Int {
        return this.actionAmountToFilter.toRawBits() +
                this.attack.toRawBits() +
                this.decay.toRawBits() +
                this.sustain.toRawBits() +
                this.release.toRawBits() +
                this.initialCutoff.toRawBits() +
                this.resonance.toRawBits() + super.hashCode()
    }

    override fun clone(): Any {
        val klon=SimpleSubtractiveSynthP(
            this.attack,
            this.decay,
            this.sustain,
            this.release,
            this.initialCutoff,
            this.resonance,
            this.actionAmountToFilter,
            this.actionAmountToVolume,
            this.actionAmountToPitchBend,
            this.polyphonyDefinition,
            this.horizontalToActionB,
            this.nVoices,
            this.name
        )
        return klon
    }
}