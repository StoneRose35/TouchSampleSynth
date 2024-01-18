package ch.sr35.touchsamplesynth.model

import com.google.gson.Gson
import org.junit.Assert
import org.junit.Test

class SimpleSubtractiveSynthPTest {
    @Test
    fun toJsonTest()
    {
        val synth=SimpleSubtractiveSynthP(0.1f,0.23f,0.98f,0.564f,0.2323f,0.78f,0.6754f,4,"TestIt")
        val gson = Gson()
        val json = gson.toJson(synth)
        Assert.assertTrue(json.contains("TestIt"))
    }
}