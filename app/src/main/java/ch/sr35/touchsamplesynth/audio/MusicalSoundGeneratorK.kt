package ch.sr35.touchsamplesynth.audio

import android.content.Context

interface MusicalSoundGenerator {
    fun setNote(note: Float): Boolean
    fun switchOn(vel: Float): Boolean
    fun switchOff(vel: Float): Boolean
    fun getInstance(): Byte
    fun generateAttachedInstance(context: Context): MusicalSoundGenerator
    fun bindToAudioEngine()
    fun detachFromAudioEngine()
    fun applyTouchAction(a: Float)
    fun isSounding(): Boolean
    fun copyParamsTo(other: MusicalSoundGenerator)
    // 0: no midi, 2: midi available, 3: midi available, note change possible (monophonic)
    fun setMidiMode(midiMode: Int)

}