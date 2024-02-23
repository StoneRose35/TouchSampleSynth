package ch.sr35.touchsamplesynth.audio

import android.content.Context

const val MIDI_MODE_OFF=0
const val MIDI_MODE_ON_POLY=4
const val MIDI_MODE_ON_MONO=6
open class MusicalSoundGenerator {
    open fun setNote(note: Float): Boolean {return false}
    open fun switchOn(vel: Float): Boolean {return false}
    open fun switchOff(vel: Float): Boolean {return false}
    open fun getInstance(): Byte{return -1}
    open fun generateAttachedInstance(context: Context): MusicalSoundGenerator {return MusicalSoundGenerator()}
    open fun bindToAudioEngine() {}
    open fun detachFromAudioEngine() {}
    open fun applyTouchAction(a: Float)
    {
    }
    external fun setMidiChannel(channel: Int)
    external fun sendMidiCC(ccNumber: Int,ccValue: Int)
    open fun isSounding(): Boolean {return false}
    open fun isEngaged(): Boolean {return false}
    open fun copyParamsTo(other: MusicalSoundGenerator) {}
    // 0: no midi, 2: midi available, 3: midi available, note change possible (monophonic)
    open fun setMidiMode(midiMode: Int) {}
    open fun getMidiMode(): Int {return 0}

}