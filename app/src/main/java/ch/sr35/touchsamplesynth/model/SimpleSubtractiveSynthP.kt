package ch.sr35.touchsamplesynth.model

import ch.sr35.touchsamplesynth.audio.MusicalSoundGenerator
import ch.sr35.touchsamplesynth.audio.instruments.SimpleSubtractiveSynthK

class SimpleSubtractiveSynthP(
    private var attack: Float,
    private var decay: Float,
    private var sustain: Float,
    private var release: Float,
    private var initialCutoff: Float,
    private var actionAmount: Float,
    private var resonance: Float
    ): PersistableInstrument {
    override fun fromInstrument(msg: MusicalSoundGenerator) {
        if (msg is SimpleSubtractiveSynthK)
        {
            attack = msg.getAttack()
            decay = msg.getDecay()
            sustain = msg.getSustain()
            release = msg.getRelease()
            initialCutoff = msg.initialCutoff
            actionAmount = msg.actionAmount
            resonance = msg.getResonance()
        }
    }

    override fun toInstrument(msg: MusicalSoundGenerator) {
        if (msg is SimpleSubtractiveSynthK)
        {
            if (msg.getInstance() < 0)
            {
                msg.bindToAudioEngine()
            }
            msg.setAttack(attack)
            msg.setDecay(decay)
            msg.setSustain(sustain)
            msg.setRelease(release)
            msg.initialCutoff = initialCutoff
            msg.actionAmount = actionAmount
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