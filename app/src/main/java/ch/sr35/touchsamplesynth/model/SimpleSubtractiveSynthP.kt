package ch.sr35.touchsamplesynth.model

import ch.sr35.touchsamplesynth.audio.Instrument
import ch.sr35.touchsamplesynth.audio.MusicalSoundGenerator
import ch.sr35.touchsamplesynth.audio.instruments.SimpleSubtractiveSynthI
import ch.sr35.touchsamplesynth.audio.voices.SimpleSubtractiveSynthK

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
): PersistableInstrument {
    override fun fromInstrument(i: Instrument) {
        if (i is SimpleSubtractiveSynthI)
        {
            attack = i.getAttack()
            decay = i.getDecay()
            sustain = i.getSustain()
            release = i.getRelease()
            initialCutoff = i.getCutoff()
            actionAmount = i.getActionAmount()
            resonance = i.getResonance()
        }
    }

    override fun toInstrument(msg: Instrument) {
        if (msg is SimpleSubtractiveSynthI)
        {
            msg.generateVoices(nVoices)
            msg.setAttack(attack)
            msg.setDecay(decay)
            msg.setSustain(sustain)
            msg.setRelease(release)
            msg.setCutoff(initialCutoff)
            msg.setActionAmount(actionAmount)
            msg.setResonance(resonance)
        }
    }

    override fun equals(other: Any?): Boolean {
        if (other is SimpleSubtractiveSynthP)
        {
            return this.hashCode() == other.hashCode()
        }
        return false
    }

    override fun hashCode(): Int {
        return this.actionAmount.toRawBits() +
                this.attack.toRawBits() +
                this.decay.toRawBits() +
                this.sustain.toRawBits() +
                this.release.toRawBits() +
                this.initialCutoff.toRawBits() +
                this.resonance.toRawBits()
    }
}