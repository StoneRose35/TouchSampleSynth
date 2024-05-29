package ch.sr35.touchsamplesynth.model

import ch.sr35.touchsamplesynth.views.TouchElement
import com.google.gson.Gson
import org.junit.Assert
import org.junit.Test
import java.util.UUID

class TouchElementPTest {

    @Test
    fun toJsonTest()
    {
        val synth1=SineMonoSynthP(0.684f,0.438f,0.1111f,0.93234f,0.765f,false,"HuntersMoon")
        synth1.id = UUID.randomUUID().toString()
        val touchElement = TouchElementP(234,546,154,673,TouchElement.ActionDir.HORIZONTAL_LEFT_RIGHT,45,null,0,11,synth1.id)
        val gson=Gson()
        val json = gson.toJson(touchElement)
        Assert.assertTrue(json.contains("546"))
    }
}