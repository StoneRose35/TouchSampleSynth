package ch.sr35.touchsamplesynth.model

import ch.sr35.touchsamplesynth.audio.instruments.PolyphonyDefinition
import ch.sr35.touchsamplesynth.views.TouchElement
import com.google.gson.Gson
import org.junit.Assert
import org.junit.Test
import java.util.UUID

class ScenePTest {
    @Test
    fun toJsonTest()
    {
        val synth1=SineMonoSynthP(0.684f,0.438f,0.1111f,0.93234f,0.1f,
            PolyphonyDefinition.POLY_SATURATE,4,"HuntersMoon")
        synth1.id = UUID.randomUUID().toString()
        val synth2 = SamplerP(12532,5959594,13000,5700000,0,"/sdcard/searchlight.wav",0.25f,
            PolyphonyDefinition.POLY_SATURATE,4,"WatcherInTheSky")
        synth2.id = UUID.randomUUID().toString()
        val touchElement1 = TouchElementP(234,546,154,673,
            TouchElement.ActionDir.HORIZONTAL_RIGHT_LEFT, ArrayList(45),null,0,3,synth1.id)
        val touchElement2 = TouchElementP(24,541,354,273,
            TouchElement.ActionDir.HORIZONTAL_RIGHT_LEFT,ArrayList(34),null,0,7,synth1.id)
        val touchElement3 = TouchElementP(342,343,44,55,
            TouchElement.ActionDir.HORIZONTAL_RIGHT_LEFT,ArrayList(45),null,0,11,synth1.id)
        val touchElement4 = TouchElementP(563,92,100,200,TouchElement.ActionDir.VERTICAL_DOWN_UP,ArrayList(62),null,0,3,synth2.id)
        val scene=SceneP()
        scene.instruments.add(synth1)
        scene.instruments.add(synth2)
        scene.name="Immaculate"
        scene.touchElements.addAll(arrayListOf(touchElement1,touchElement2,touchElement3,touchElement4))
        val gson= Gson()
        val json = gson.toJson(scene)
        Assert.assertNotNull(json.contains("Immaculate"))

    }
}