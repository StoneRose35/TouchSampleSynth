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
            for (c in 0..88) {
                p = MusicalPitch()
                p.value = c.toFloat() - 39.0f
                p.name = String.format("%s %d",noteNames[noteNameIdx], (c - 39) / 12)
                p.index = c
                noteNameIdx += 1
                noteNameIdx %= 12
                res.add(p)
            }
            return res.toTypedArray()
        }
    }
}

