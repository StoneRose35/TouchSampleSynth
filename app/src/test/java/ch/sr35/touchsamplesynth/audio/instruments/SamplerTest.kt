package ch.sr35.touchsamplesynth.audio.instruments

import org.junit.Assert
import org.junit.Test

class SamplerTest {

    @Test
    fun loadWaveFileTest()
    {
        val exampleWavResource = javaClass.classLoader?.getResource("exampleWav.wav")
        Assert.assertNotNull(exampleWavResource)
    }
}