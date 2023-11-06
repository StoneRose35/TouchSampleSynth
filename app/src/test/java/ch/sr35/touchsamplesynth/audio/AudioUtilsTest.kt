package ch.sr35.touchsamplesynth.audio

import org.junit.Assert
import org.junit.Test
import kotlin.math.pow

class AudioUtilsTest {
    private val EPS=.000001f
    @Test
    fun noteToFreqTest()
    {
        val note1 = AudioUtils.FreqToNote(888.0f)
        val note2 = AudioUtils.FreqToNote(444.0f)
        Assert.assertTrue(note1-note2 -12.0f < EPS && note1-note2 -12.0f > -EPS)
    }

    @Test
    fun freqToNoteTest()
    {
        val freq1 = AudioUtils.NoteToFreq(6.0f)
        val freq2 = AudioUtils.NoteToFreq(7.0f)
        Assert.assertTrue(freq1* 2.0f.pow(1.0f / 12.0f) - freq2 < EPS && freq1* 2.0f.pow(1.0f / 12.0f) - freq2 > -EPS)
    }
}