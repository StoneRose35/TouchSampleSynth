package ch.sr35.touchsamplesynth.model

import com.google.gson.Gson
import org.junit.Assert
import org.junit.Test

class SineMonoSynthTest {

    @Test
    fun toJsonTest()
    {
        val synth=SineMonoSynthP(0.684f,0.438f,0.1111f,0.93234f,0.456f,false,"HuntersMoon")
        val gson = Gson()
        val json = gson.toJson(synth)
        Assert.assertTrue(json.contains("HuntersMoon"))
    }
}