package ch.sr35.touchsamplesynth.model

import androidx.test.platform.app.InstrumentationRegistry
import ch.sr35.touchsamplesynth.BuildConfig
import com.google.gson.Gson
import org.junit.Assert
import org.junit.Test

class DataMigrationTest {

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

    @Test
    fun upgradeToCurrentTest()
    {
        val currentJson = InstrumentationRegistry.getInstrumentation().context.assets.open("defaultPresets1_8_6.json").reader().readText()
        val newJson = DataUpdater.upgradeToCurrent(currentJson)
        val newScenes = Gson().fromJson(newJson,SceneListP::class.java)
        Assert.assertTrue(newScenes.screenResolutionX >= 0)
        Assert.assertTrue(newScenes.screenResolutionY >= 0)
        Assert.assertTrue(newScenes.scenes.size==9)
        val currentVersionString = BuildConfig.VERSION_NAME
        Assert.assertTrue(newScenes.touchSampleSynthVersion == currentVersionString)
    }

    @Test
    fun upgradeTo194Test()
    {
        val currentJson = InstrumentationRegistry.getInstrumentation().context.assets.open("defaultPresets1_9_3.json").reader().readText()
        val newJson = DataUpdater.upgradeToCurrent(currentJson)
        val newScenes = Gson().fromJson(newJson,SceneListP::class.java)
        Assert.assertTrue(newScenes.screenResolutionX >= 0)
        Assert.assertTrue(newScenes.screenResolutionY >= 0)
        Assert.assertTrue(newScenes.scenes.size==9)
    }



}