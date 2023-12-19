package ch.sr35.touchsamplesynth.model

import ch.sr35.touchsamplesynth.audio.Instrument
import ch.sr35.touchsamplesynth.audio.instruments.SimpleSubtractiveSynthI

class SimpleSubtractiveSynthP(
    private var attack: Float,
    private var decay: Float,
    private var sustain: Float,
    private var release: Float,
    private var initialCutoff: Float,
    private var actionAmount: Float,
    private var resonance: Float,
    override var nVoices: Int,
    override var name: String
): PersistableInstrument() {
    override fun fromInstrument(i: Instrument) {
        super.fromInstrument(i)
        if (i is SimpleSubtractiveSynthI)
        {
            attack = i.getAttack()
            decay = i.getDecay()
            sustain = i.getSustain()
            release = i.getRelease()
            initialCutoff = i.getInitialCutoff()
            actionAmount = i.getActionAmount()
            resonance = i.getResonance()
        }
    }

    override fun toInstrument(i: Instrument) {
        if (i is SimpleSubtractiveSynthI)
        {
            i.setAttack(attack)
            i.setDecay(decay)
            i.setSustain(sustain)
            i.setRelease(release)
            i.setInitialCutoff(initialCutoff)
            i.setActionAmount(actionAmount)
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
        return "SimpleSubtractiveSynth: %s, voices: %d".format(this.name, this.nVoices)
    }

    override fun hashCode(): Int {
        return this.actionAmount.toRawBits() +
                this.attack.toRawBits() +
                this.decay.toRawBits() +
                this.sustain.toRawBits() +
                this.release.toRawBits() +
                this.initialCutoff.toRawBits() +
                this.resonance.toRawBits() + super.hashCode()
    }

    override fun clone(): Any {
        val klon=SimpleSubtractiveSynthP(this.attack,this.decay,this.sustain,this.release,this.initialCutoff,this.actionAmount,this.resonance,this.nVoices,this.name)
        return klon
    }
}