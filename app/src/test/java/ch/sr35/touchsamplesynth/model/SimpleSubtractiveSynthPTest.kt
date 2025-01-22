package ch.sr35.touchsamplesynth.model

import ch.sr35.touchsamplesynth.audio.instruments.PolyphonyDefinition
import com.google.gson.Gson
import org.junit.Assert
import org.junit.Test

class SimpleSubtractiveSynthPTest {
    @Test
    fun toJsonTest()
    {
        val synth=SimpleSubtractiveSynthP(0.1f,0.23f,0.98f,0.564f,0.2323f,0.78f,0.6754f,0.2323f,0.6237f,
            0,1,1,0.1f,0.2f,10.0f,0.74f,0.23f,0.45f,0.12f,0.26f,0.987f,
            PolyphonyDefinition.POLY_SATURATE,false,4,"TestIt")
        val gson = Gson()
        val json = gson.toJson(synth)
        Assert.assertTrue(json.contains("TestIt"))
    }


    @Test
    fun fromJsonTest()
    {
        val jsonString = "{\"attack\":0.1,\"decay\":0.23,\"sustain\":0.98,\"release\":0.564,\"initialCutoff\":0.2323,\"actionAmountToFilter\":0.78,\"resonance\":0.2323,\"actionAmountToVolume\":0.6754,\"actionAmountToPitchBend\":0.6237,\"polyphonyDefinition\":\"POLY_SATURATE\",\"name\":\"TestIt\",\"id\":\"\"}"
        val gson = Gson()
        val instrumentP = gson.fromJson(jsonString,SimpleSubtractiveSynthP::class.java)
        Assert.assertTrue(instrumentP.polyphonyDefinition == PolyphonyDefinition.POLY_SATURATE)
    }
}