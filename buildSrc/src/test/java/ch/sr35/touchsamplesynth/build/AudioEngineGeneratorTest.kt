package ch.sr35.touchsamplesynth.build

import org.junit.Assert
import org.junit.Test

class AudioEngineGeneratorTest {

    @Test
    fun testAudioEngineGenerator() {
        val generator = AudioEngineGenerator()
        val result =generator.processAudioEngine(listOf("Sampler","Piano","Guitar","Harmonica"))
        Assert.assertNotNull(result)
    }
}