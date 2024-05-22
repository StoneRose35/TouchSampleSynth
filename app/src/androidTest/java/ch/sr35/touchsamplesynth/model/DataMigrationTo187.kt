package ch.sr35.touchsamplesynth.model

import androidx.test.platform.app.InstrumentationRegistry
import com.google.gson.Gson
import com.google.gson.JsonElement
import org.junit.Assert
import org.junit.Test

class DataMigrationTo187 {

    @Test
    fun migrate()
    {
        val currentJson = InstrumentationRegistry.getInstrumentation().targetContext.assets.open("defaultPresets.json").reader().readText()
        val newJson = DataUpdater.updatersList.find { it -> it.versionTo == Version(1,8,7) && it.versionFrom == Version(1,8,6) }
            ?.processData(currentJson)

        Assert.assertTrue(Gson().fromJson(newJson,JsonElement::class.java).isJsonObject)
    }
}