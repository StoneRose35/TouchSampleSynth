package ch.sr35.touchsamplesynth

import org.junit.Assert
import org.junit.Test


class MusicalPitchTest {

    @Test
    fun generateCorrectPitches()
    {
        val allNotes = MusicalPitch.generateAllNotes()
        assert(allNotes.size == 88)
    }

    @Test
    fun checkOctaveAssignment()
    {
        val allNotes = MusicalPitch.generateAllNotes()

        Assert.assertTrue(allNotes[0].getOctave()==-4)
        Assert.assertTrue(allNotes[0].getNoteWithinOctave()==9)

        Assert.assertTrue(allNotes.last().getOctave()==4)
        Assert.assertTrue(allNotes.last().getNoteWithinOctave()==0)
    }

    @Test
    fun generateValidMusicalPitch()
    {
        val note = MusicalPitch(-1,3)
        Assert.assertTrue(note.index == -12+39+3)
    }

    @Test
    fun generateInvalidMusicalPitch()
    {
        try {
            val note = MusicalPitch(5, 20)
            Assert.fail()
        }
        catch (_: MusicalPitchException)
        {

        }

    }

}