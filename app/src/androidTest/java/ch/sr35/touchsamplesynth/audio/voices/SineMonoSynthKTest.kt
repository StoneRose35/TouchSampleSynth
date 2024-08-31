package ch.sr35.touchsamplesynth.audio.voices

import android.graphics.Color
import androidx.test.platform.app.InstrumentationRegistry
import ch.sr35.touchsamplesynth.AudioTest
import org.junit.Assert
import org.junit.Test

class SineMonoSynthKTest: AudioTest() {


    @Test
    fun addToAudioEngineTest()
    {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val synth=SineMonoSynthK(context)
        synth.bindToAudioEngine()
        Assert.assertTrue(synth.getInstance() > -1)
    }

    @Test
    fun detachFromAudioEngineTest()
    {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val synth=SineMonoSynthK(context)
        synth.bindToAudioEngine()
        try {
            synth.detachFromAudioEngine()
        }
        catch (_: Exception)
        {
            Assert.fail()
        }
    }

    @Test
    fun equalityTest()
    {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val synth1=SineMonoSynthK(context)
        val synth2=SineMonoSynthK(context)
        Assert.assertEquals(synth1,synth2)
    }

    @Test
    fun unequalityTest()
    {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val synth1=SineMonoSynthK(context)
        val synth2=SineMonoSynthK(context)
        synth1.bindToAudioEngine()
        synth2.bindToAudioEngine()
        Assert.assertNotEquals(synth1,synth2)

    }

    @Test
    fun unequalityTest2()
    {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val synth1=SineMonoSynthK(context)
        val obj2 = Color.BLACK
        synth1.bindToAudioEngine()
        Assert.assertNotEquals(synth1,obj2)
    }
}