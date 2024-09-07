package ch.sr35.touchsamplesynth.model

import ch.sr35.touchsamplesynth.audio.InstrumentI
import ch.sr35.touchsamplesynth.audio.instruments.SimpleSubtractiveSynthI

class SimpleSubtractiveSynthP(
    private var attack: Float,
    private var decay: Float,
    private var sustain: Float,
    private var release: Float,
    private var initialCutoff: Float,
    private var actionAmountToFilter: Float,
    actionAmountToVolume: Float,
    private var resonance: Float,
    isMono: Boolean,
    name: String
): PersistableInstrument(actionAmountToVolume,isMono,name) {
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
        super.toInstrument(i)
        if (i is SimpleSubtractiveSynthI)
        {
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
        return "SimpleSubtractiveSynth: %s, monophonic: %b".format(this.name, this.isMonophonic)
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
        val klon=SimpleSubtractiveSynthP(this.attack,this.decay,this.sustain,this.release,this.initialCutoff,this.actionAmountToFilter, this.actionAmountToVolume,this.resonance,this.isMonophonic,this.name)
        return klon
    }
}