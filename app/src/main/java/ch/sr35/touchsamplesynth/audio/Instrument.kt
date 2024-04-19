package ch.sr35.touchsamplesynth.audio

import android.graphics.drawable.Drawable

open class Instrument(var name: String) {

    open var voices=ArrayList<MusicalSoundGenerator>()

    open fun getType(): String
    {
        return ""
    }
    open fun getInstrumentIcon(): Drawable?
    {
        return null
    }
    fun getNextFreeVoice(): MusicalSoundGenerator?
    {
        return voices.stream().map { a -> a as MusicalSoundGenerator}?.filter { v -> !v.isSounding() }?.findFirst()?.orElse(null)
    }

    fun hasVoice(msg: MusicalSoundGenerator?): Boolean
    {
        return voices.contains(msg)
    }

    fun voicesCount(): Int
    {
        return voices.size
    }

    fun getPolyphonyDescription(): String
    {
        return if (voicesCount()  < 2 ) {
            "M"
        } else {
            "P " + voicesCount().toString()
        }
    }

    open fun generateVoices(cnt: Int)
    {

    }

    open fun setVolumeModulation(mod: Float)
    {
        for (voice in voices)
        {
            voice.actionAmountToVolume = mod
            voice.setMidiVelocityScaling(mod)
        }
    }

    open fun getVolumeModulation(): Float
    {
        if (voices.isNotEmpty())
        {
            return voices[0].actionAmountToVolume
        }
        return 0.0f
    }

    override fun equals(other: Any?): Boolean {
        if (other is Instrument)
        {
            return this.name == other.name && this.getType() == other.getType()
        }
        return false
    }

    override fun hashCode(): Int {
        return this.name.hashCode() + this.getType().hashCode()
    }
}
