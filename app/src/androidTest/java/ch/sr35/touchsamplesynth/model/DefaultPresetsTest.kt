package ch.sr35.touchsamplesynth.model

import androidx.test.platform.app.InstrumentationRegistry
import com.google.gson.Gson
import org.junit.Assert
import org.junit.Test

class DefaultPresetsTest {

    @Test
    fun checkIfDefaultPresetsAreCorrect()
    {
        val presetsJson = InstrumentationRegistry.getInstrumentation().targetContext.assets.open("defaultPresets.json").reader().readText()
        val newScenes = Gson().fromJson(presetsJson,SceneListP::class.java)
        Assert.assertTrue(newScenes.screenResolutionX >= 0)
        Assert.assertTrue(newScenes.screenResolutionY >= 0)
        Assert.assertTrue(newScenes.scenes.size==9)
    }
}