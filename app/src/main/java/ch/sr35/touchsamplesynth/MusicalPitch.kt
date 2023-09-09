package ch.sr35.touchsamplesynth

class MusicalPitch {
    var note: Float = 0.0f
    var name: String = ""


    companion object {
        fun generateAllNotes(): Array<MusicalPitch> {
            val res = ArrayList<MusicalPitch>()
            val noteNames = arrayOf("C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B")
            var p: MusicalPitch
            var noteNameIdx = 9
            for (c in 0..88) {
                p = MusicalPitch()
                p.note = c.toFloat() - 39.0f
                p.name = noteNames[noteNameIdx] + String.format("%d", (c - 39) / 12)
                noteNameIdx += 1
                noteNameIdx %= 12
            }
            return res.toTypedArray()
        }
    }
}

