package ch.sr35.touchsamplesynth

import org.junit.Test


class MusicalPitchTest {

    @Test
    fun generateCorrectPitches()
    {
        val allNotes = MusicalPitch.generateAllNotes()
        assert(allNotes.size == 88)
    }
}