package ch.sr35.touchsamplesynth.audio

import kotlin.math.ln
import kotlin.math.log2
import kotlin.math.pow

class AudioUtils {

    companion object {
    fun NoteToFreq(note: Float): Float
    {
        return (2.0f.pow(note / 12.0f) *440.0f)
    }

    fun FreqToNote(freq: Float): Float
    {
        return log2(freq/440.0f)*12.0f
    }
    }
}