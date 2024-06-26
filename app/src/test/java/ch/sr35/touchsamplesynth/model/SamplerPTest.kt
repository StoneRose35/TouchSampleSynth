package ch.sr35.touchsamplesynth.model

import com.google.gson.Gson
import org.junit.Assert
import org.junit.Test

class SamplerPTest {
    @Test
    fun toJsonTest()
    {
        val synth=SamplerP(12532,5959594,13000,5700000,0,"/sdcard/searchlight.wav",0.0f,false,"WatcherInTheSky")
        val gson = Gson()
        val json = gson.toJson(synth)
        Assert.assertTrue(json.contains("WatcherInTheSky"))
    }
}