package ch.sr35.touchsamplesynth.audio

import android.content.Context
import android.graphics.drawable.Drawable

interface MusicalSoundGenerator {
    fun setNote(note: Float): Boolean
    fun switchOn(vel: Float): Boolean
    fun switchOff(vel: Float): Boolean
    fun getType(): String
    fun getInstrumentIcon(): Drawable?
    fun getInstance(): Int
    fun generateAttachedInstance(context: Context): MusicalSoundGenerator

    fun bindToAudioEngine()

    fun detachFromAudioEngine()
}