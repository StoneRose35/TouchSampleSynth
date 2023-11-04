package ch.sr35.touchsamplesynth.audio

import android.graphics.drawable.Drawable

open class Instrument(var name: String): IInstrument {

    public open val voices: ArrayList<MusicalSoundGenerator>?=null

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
        return voices?.stream()?.map { a -> a as MusicalSoundGenerator}?.filter { v -> !v.isSounding() }?.findFirst()?.orElse(null)
    }

    fun hasVoice(msg: MusicalSoundGenerator?): Boolean
    {
        return voices?.contains(msg) ?: false
    }

    fun voicesCount(): Int
    {
        return voices?.size ?: 0
    }

    fun getPolyphonyDescription(): String
    {
        if (voicesCount()  < 2 )
        {
            return "M"
        }
        else
        {
            return "P " + voicesCount().toString()
        }
    }

    open fun generateVoices(cnt: Int)
    {

    }

    override fun generateInstance(nVoices: Int, n: String): Instrument
    {
        return Instrument("")
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

interface IInstrument
{
    fun generateInstance(nVoices: Int, n: String): Instrument
}