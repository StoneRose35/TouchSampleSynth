package ch.sr35.touchsamplesynth

class MusicalPitch {
    var value: Float = 0.0f
    var name: String = ""
    var index: Int = -1

    override fun equals(other: Any?): Boolean {
        if (other is MusicalPitch)
        {
            return other.name == this.name
        }
        return false
    }

    override fun hashCode(): Int {
        return this.name.hashCode()
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
                p.name = String.format("%s %d",noteNames[noteNameIdx], octaveNr)
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

