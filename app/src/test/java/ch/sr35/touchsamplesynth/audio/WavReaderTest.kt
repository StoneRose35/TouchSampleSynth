package ch.sr35.touchsamplesynth.audio

import org.junit.Assert
import org.junit.Test
import java.io.File

class WavReaderTest {

    @Test
    fun basicWavReaderTest()
    {
        val wr = WavReader()
        val  file = File("./src/test/resources/exampleWav.wav")
        Assert.assertTrue(file.isFile)
        val wavFile = wr.readWaveFile(file.inputStream())
        Assert.assertNotNull(wavFile)
        Assert.assertEquals(wavFile.header.sampleRate,48000)
        Assert.assertEquals(wavFile.header.nChannels, 2)
        Assert.assertEquals(wavFile.header.bitDepth,16)

    }

    @Test
    fun basicWavReaderTest2()
    {
        val wr = WavReader()
        val  file = File("./src/test/resources/exampleWav24.wav")
        Assert.assertTrue(file.isFile)
        val wavFile = wr.readWaveFile(file.inputStream())
        Assert.assertNotNull(wavFile)
        Assert.assertEquals(wavFile.header.sampleRate,48000)
        Assert.assertEquals(wavFile.header.nChannels, 2)
        Assert.assertEquals(wavFile.header.bitDepth,24)
    }

    @Test
    fun basicWavReaderTest3()
    {
        val wr = WavReader()
        val  file = File("./src/test/resources/exampleWav32.wav")
        Assert.assertTrue(file.isFile)
        val wavFile = wr.readWaveFile(file.inputStream())
        Assert.assertNotNull(wavFile)
        Assert.assertEquals(wavFile.header.sampleRate,48000)
        Assert.assertEquals(wavFile.header.nChannels, 2)
        Assert.assertEquals(wavFile.header.bitDepth,32)
    }

    @Test
    fun basicWavReaderTest4()
    {
        val wr = WavReader()
        val  file = File("./src/test/resources/exampleWav44100.wav")
        Assert.assertTrue(file.isFile)
        val wavFile = wr.readWaveFile(file.inputStream())
        Assert.assertNotNull(wavFile)
        Assert.assertEquals(wavFile.header.sampleRate,44100)
        Assert.assertEquals(wavFile.header.nChannels, 2)
        Assert.assertEquals(wavFile.header.bitDepth,24)
    }

    @Test
    fun basicWavReaderTest5()
    {
        val wr = WavReader()
        val  file = File("./src/test/resources/exampleWavU8.wav")
        Assert.assertTrue(file.isFile)
        val wavFile = wr.readWaveFile(file.inputStream())
        Assert.assertNotNull(wavFile)
        Assert.assertEquals(wavFile.header.sampleRate,48000)
        Assert.assertEquals(wavFile.header.nChannels, 2)
        Assert.assertEquals(wavFile.header.bitDepth,8)
    }

    @Test
    fun basicWavReaderTest6()
    {
        val wr = WavReader()
        val  file = File("./src/test/resources/Acoustic Snare 02.wav")
        Assert.assertTrue(file.isFile)
        val wavFile = wr.readWaveFile(file.inputStream())
        wavFile.getFloatData(44100,WaveFileChannel.LEFT)
        Assert.assertNotNull(wavFile)
    }

    @Test
    fun getFloatDataTest()
    {
        val wr = WavReader()
        val  file = File("./src/test/resources/exampleWav.wav")
        Assert.assertTrue(file.isFile)
        val wavFile = wr.readWaveFile(file.inputStream())
        Assert.assertNotNull(wavFile)
        val floatData = wavFile.getFloatData(48000,WaveFileChannel.LEFT)
        Assert.assertEquals(floatData.size,wavFile.rawData.size/wavFile.header.nChannels/(wavFile.header.bitDepth/8))
        Assert.assertNull( floatData.find  { el -> el > 1.0f || el < -1.0f})
    }

    @Test
    fun resampleTest()
    {
        val wr = WavReader()
        val  file = File("./src/test/resources/exampleWav44100.wav")
        Assert.assertTrue(file.isFile)
        val wavFile = wr.readWaveFile(file.inputStream())
        Assert.assertNotNull(wavFile)
        val floatData = wavFile.getFloatData(48000,WaveFileChannel.BOTH)
        Assert.assertTrue(floatData.size > 220000)
    }
}