package ch.sr35.touchsamplesynth.model

import com.google.gson.GsonBuilder
import org.junit.Assert
import org.junit.Test
import kotlin.math.sin

class LooperPTest {

    @Test
    fun toJsonAndBackTest()
    {
        val sample = FloatArray(512)
        for (i in 0..511)
        {
            sample[i] = sin(i.toFloat()/48000.0f*432.0f*2.0f*Math.PI.toFloat())
        }
        val synth=LooperP(3,2,512,1.0f,sample)
        val gson = GsonBuilder().apply {
            setExclusionStrategies(AnnotationExclusionStrategy())
        }.create()
        val json = gson.toJson(synth)
        val recreatedSynth = gson.fromJson(json,LooperP::class.java)
        Assert.assertNull(recreatedSynth.sample )

    }
}