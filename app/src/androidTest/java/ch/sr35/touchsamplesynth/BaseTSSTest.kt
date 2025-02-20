package ch.sr35.touchsamplesynth

import android.os.Build
import androidx.test.core.app.ActivityScenario
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.GrantPermissionRule
import androidx.test.rule.GrantPermissionRule.grant
import org.junit.Rule
import java.io.File
import java.io.FileOutputStream

open class BaseTSSTest {
    var activityScenario: ActivityScenario<TouchSampleSynthMain>?=null

    @JvmField
    @Rule
    val permissionRule: GrantPermissionRule

    init {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.TIRAMISU)
            permissionRule = grant(
                android.Manifest.permission.READ_MEDIA_AUDIO,
                android.Manifest.permission.RECORD_AUDIO,
                android.Manifest.permission.READ_MEDIA_IMAGES,
                android.Manifest.permission.READ_MEDIA_VIDEO,
                android.Manifest.permission.INTERNET
            )
        else {
            permissionRule = grant(
                android.Manifest.permission.READ_MEDIA_AUDIO,
                android.Manifest.permission.RECORD_AUDIO,
                android.Manifest.permission.READ_MEDIA_IMAGES,
                android.Manifest.permission.READ_MEDIA_VIDEO,
                android.Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED,
                android.Manifest.permission.INTERNET
            )
        }

        val fname =
            InstrumentationRegistry.getInstrumentation().targetContext.filesDir.absolutePath + File.separator + SCENES_FILE_NAME
        val testpresets =
            InstrumentationRegistry.getInstrumentation().context.assets.open("defaultPresets_noSamples.json")
        val f = File(fname)
        val fos = FileOutputStream(f)
        fos.write(testpresets.readAllBytes())
        fos.close()
        activityScenario = ActivityScenario.launch(TouchSampleSynthMain::class.java)
    }

}