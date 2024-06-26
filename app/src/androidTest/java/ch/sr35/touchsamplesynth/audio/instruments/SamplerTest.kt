package ch.sr35.touchsamplesynth.audio.instruments

import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert
import org.junit.Test
import org.junit.Ignore
import java.io.RandomAccessFile
import kotlin.math.sqrt
import kotlin.system.measureTimeMillis


class SamplerTest {

    @Test
    fun samplerInstatiationTest()
    {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val samplerI=SamplerI(context,"AkaiMPC")
        Assert.assertNotNull(samplerI)
    }



    @Test
    @Ignore("demonstrative performance test, not useable for regression")
    fun setSamplePerformanceTest()
    {
        System.loadLibrary("touchsamplesynth")
        val targetContext = InstrumentationRegistry.getInstrumentation().targetContext
        val samplerI=SamplerI(targetContext,"AkaiMPC")
        val context = InstrumentationRegistry.getInstrumentation().context
        val timesmeasured = ArrayList<Long>()
        for (c in 0 until 512) {
            val exampleWavStream = RandomAccessFile(context.getFileStreamPath("Funny-06.wav"),"r") // .open("Funny-06.wav")
            val timeInMsUsed = measureTimeMillis {
                samplerI.setSampleFile(exampleWavStream)
            }
            timesmeasured.add(timeInMsUsed)
            exampleWavStream.close()
        }
        val avg = timesmeasured.sum()/timesmeasured.size
        val variance = sqrt(timesmeasured.stream().map {
            (it-avg)*(it-avg)
        }.reduce { a,b -> a+b }.get().toDouble()/(timesmeasured.size-1))

        Assert.assertTrue(variance < avg)
    }
}