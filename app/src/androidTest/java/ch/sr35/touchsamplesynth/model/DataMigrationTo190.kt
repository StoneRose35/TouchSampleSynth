package ch.sr35.touchsamplesynth.model

import androidx.test.platform.app.InstrumentationRegistry
import com.google.gson.Gson
import org.junit.Assert
import org.junit.Test

class DataMigrationTo190 {

    @Test
    fun migrate()
    {
        val currentJson = InstrumentationRegistry.getInstrumentation().context.assets.open("defaultPresets1_8_6.json").reader().readText()
        val newJson = DataUpdater.updatersList.find { it -> it.versionTo == Version(1,9,0) && it.versionFrom == Version(1,8,6) }
            ?.processData(currentJson)
        val newScenes = Gson().fromJson(newJson,SceneListP::class.java)
        Assert.assertTrue(newScenes.screenResolutionX >= 0)
        Assert.assertTrue(newScenes.screenResolutionY >= 0)
        Assert.assertTrue(newScenes.scenes.size==9)
    }
}