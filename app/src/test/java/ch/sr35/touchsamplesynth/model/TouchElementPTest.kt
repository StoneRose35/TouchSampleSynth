package ch.sr35.touchsamplesynth.model

import ch.sr35.touchsamplesynth.views.TouchElement
import com.google.gson.Gson
import org.junit.Assert
import org.junit.Test

class TouchElementPTest {

    @Test
    fun toJsonTest()
    {
        val synth1=SineMonoSynthP(0.684f,0.438f,0.1111f,0.93234f,0.765f,4,"HuntersMoon")
        val touchElement = TouchElementP(234,546,154,673,TouchElement.ActionDir.HORIZONTAL_LEFT_RIGHT,45,0,null,0,11,synth1)
        val gson=Gson()
        val json = gson.toJson(touchElement)
        Assert.assertTrue(json.contains("0.1111"))
    }
}