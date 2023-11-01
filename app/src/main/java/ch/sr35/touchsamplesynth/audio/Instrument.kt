package ch.sr35.touchsamplesynth.audio

import android.graphics.drawable.Drawable

open class Instrument(val name: String) {

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

    open fun generateVoices(cnt: Int)
    {

    }

    open fun generateInstance(nVoices: Int, n: String): Instrument
    {
        return Instrument("")
    }
}