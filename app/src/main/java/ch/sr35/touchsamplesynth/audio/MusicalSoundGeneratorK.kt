package ch.sr35.touchsamplesynth.audio

interface MusicalSoundGenerator {
    fun setNote(note: Float): Boolean
    fun switchOn(vel: Float): Boolean
    fun switchOff(vel: Float): Boolean
    fun getType(): String

    fun getInstance(): Int
}