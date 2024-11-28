package ch.sr35.touchsamplesynth.audio.voices

import android.graphics.Color
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class SimpleSubtractiveSynthKTest {

    @Test
    fun addToAudioEngineTest()
    {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val synth=SimpleSubtractiveSynthK(context)
        synth.bindToAudioEngine()
        Assert.assertTrue(synth.instanceNr() > -1)
    }

    @Test
    fun detachFromAudioEngineTest()
    {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val synth=SimpleSubtractiveSynthK(context)
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
        val synth1=SimpleSubtractiveSynthK(context)
        val synth2=SimpleSubtractiveSynthK(context)
        Assert.assertEquals(synth1,synth2)
    }

    @Test
    fun unequalityTest()
    {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val synth1=SimpleSubtractiveSynthK(context)
        val synth2=SimpleSubtractiveSynthK(context)
        synth1.bindToAudioEngine()
        synth2.bindToAudioEngine()
        Assert.assertNotEquals(synth1,synth2)
        synth1.detachFromAudioEngine()
        synth2.detachFromAudioEngine()
    }

    @Test
    fun unequalityTest2()
    {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val synth1=SimpleSubtractiveSynthK(context)
        val obj2 = Color.BLACK
        synth1.bindToAudioEngine()
        Assert.assertNotEquals(synth1,obj2)
        synth1.detachFromAudioEngine()
    }
}