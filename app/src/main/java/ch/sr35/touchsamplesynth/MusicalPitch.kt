package ch.sr35.touchsamplesynth

import okhttp3.internal.notify
import java.util.Locale
import kotlin.math.floor

class MusicalPitchException(override val message: String): Exception()
{

}

class MusicalPitch() {
    var value: Float = 0.0f
    var name: String = ""
    var index: Int = -1

    constructor(octave: Int, indexWithinOctave: Int) : this() {
        if (octave < -4 || octave > 4 || indexWithinOctave < 0 || indexWithinOctave > 11)
        {
            throw MusicalPitchException("Note definitions, octave: %d, note: %d outside of valid range".format(octave,indexWithinOctave))
        }
        val noteNames = arrayOf("C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B")
        index = octave*12 + indexWithinOctave + 39
        value = index - 48.0f
        name = String.format(Locale.ROOT,"%s %d",noteNames[indexWithinOctave], octave)
    }

    fun getOctave(): Int
    {
        return floor((index - 39).toDouble()/12.0).toInt()
    }

    fun getNoteWithinOctave(): Int
    {
        return (index - 39) - getOctave()*12
    }

    override fun equals(other: Any?): Boolean {
        if (other is MusicalPitch)
        {
            return this.index == other.index
        }
        return false
    }

    override fun hashCode(): Int {
        return this.index
    }

    override fun toString(): String {
        return this.name
    }
    companion object {
        fun generateAllNotes(): Array<MusicalPitch> {
            val res = ArrayList<MusicalPitch>()
            val noteNames = arrayOf("C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B")
            var p: MusicalPitch
            var noteNameIdx = 9
            var octaveNr=0
            for (c in 0..87) {
                p = MusicalPitch()
                p.value = c.toFloat() - 48.0f
                p.name = String.format(Locale.ROOT,"%s %d",noteNames[noteNameIdx], octaveNr)
                p.index = c
                noteNameIdx += 1
                noteNameIdx %= 12
                if (noteNameIdx==0)
                {
                    octaveNr += 1
                }
                res.add(p)
            }
            return res.toTypedArray()
        }
    }
}

