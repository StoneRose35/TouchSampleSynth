package ch.sr35.touchsamplesynth


import android.content.pm.ActivityInfo
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.GrantPermissionRule
import androidx.test.rule.GrantPermissionRule.grant
import ch.sr35.touchsamplesynth.fragments.InstrumentsPageFragment
import ch.sr35.touchsamplesynth.fragments.SceneFragment
import ch.sr35.touchsamplesynth.fragments.SettingsFragment
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File
import java.io.FileOutputStream


@RunWith(AndroidJUnit4::class)
class TouchSampleSynthMainTest {

    private var activityScenario: ActivityScenario<TouchSampleSynthMain>?=null

    @JvmField
    @Rule
    val permissionRule: GrantPermissionRule = grant(
        android.Manifest.permission.READ_MEDIA_AUDIO,
        android.Manifest.permission.READ_MEDIA_IMAGES,
        android.Manifest.permission.READ_MEDIA_VIDEO,
        android.Manifest.permission.INTERNET
    )

    init {
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



    @After
    fun teardownTest()
    {
        activityScenario?.close()
    }


    @Test
    fun checkInstrumentsPage()
    {
        onView(withId(R.id.toolbar_instrumentspage)).perform(ViewActions.click())
        onView(withId(R.id.instrument_page_layout)).check(matches(isDisplayed()))
    }

    @Test
    fun checkScenesPage()
    {
        onView(withId(R.id.toolbar_scenespage)).perform(ViewActions.click())
        onView(withId(R.id.sceneList)).check(matches(isDisplayed()))
    }

    @Test
    fun checkSettingsPage()
    {
        onView(withId(R.id.toolbar_settingspage)).perform(ViewActions.click())
        onView(withId(R.id.settingParametersTable)).check(matches(isDisplayed()))
    }

    @Test
    fun rotatePlayPage()
    {
        onView(withId(R.id.toolbar_scenespage)).perform(ViewActions.click())
        onView(withId(R.id.sceneImport)).perform(ViewActions.click())
        onView(withId(R.id.toolbar_playpage)).perform(ViewActions.click())
        InstrumentationRegistry.getInstrumentation().waitForIdleSync()
        activityScenario?.onActivity {
            it.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        }
        InstrumentationRegistry.getInstrumentation().waitForIdleSync()
        activityScenario?.onActivity {
            it.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }
        InstrumentationRegistry.getInstrumentation().waitForIdleSync()
        activityScenario?.onActivity {
            it.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE
        }
        InstrumentationRegistry.getInstrumentation().waitForIdleSync()
        activityScenario?.onActivity {
            it.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT
        }
        InstrumentationRegistry.getInstrumentation().waitForIdleSync()
    }

    @Test
    fun rotateInstrumentPage()
    {
        onView(withId(R.id.toolbar_scenespage)).perform(ViewActions.click())
        onView(withId(R.id.sceneImport)).perform(ViewActions.click())
        onView(withId(R.id.toolbar_instrumentspage)).perform(ViewActions.click())
        InstrumentationRegistry.getInstrumentation().waitForIdleSync()
        activityScenario?.onActivity {
            it.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        }
        InstrumentationRegistry.getInstrumentation().waitForIdleSync()
        activityScenario?.onActivity {
            it.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }
        InstrumentationRegistry.getInstrumentation().waitForIdleSync()
        activityScenario?.onActivity {
            it.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE
        }
        InstrumentationRegistry.getInstrumentation().waitForIdleSync()
        activityScenario?.onActivity {
            it.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT
        }
        InstrumentationRegistry.getInstrumentation().waitForIdleSync()
    }

    @Test
    fun rotateScenesPage()
    {
        onView(withId(R.id.toolbar_scenespage)).perform(ViewActions.click())
        onView(withId(R.id.sceneImport)).perform(ViewActions.click())
        onView(withId(R.id.toolbar_scenespage)).perform(ViewActions.click())
        InstrumentationRegistry.getInstrumentation().waitForIdleSync()
        activityScenario?.onActivity {
            it.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        }
        InstrumentationRegistry.getInstrumentation().waitForIdleSync()
        activityScenario?.onActivity {
            it.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }
        InstrumentationRegistry.getInstrumentation().waitForIdleSync()
        activityScenario?.onActivity {
            it.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE
        }
        InstrumentationRegistry.getInstrumentation().waitForIdleSync()
        activityScenario?.onActivity {
            it.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT
        }
        InstrumentationRegistry.getInstrumentation().waitForIdleSync()

    }

    @Test
    fun rotateSettingsPage()
    {
        onView(withId(R.id.toolbar_scenespage)).perform(ViewActions.click())
        onView(withId(R.id.sceneImport)).perform(ViewActions.click())
        onView(withId(R.id.toolbar_settingspage)).perform(ViewActions.click())
        InstrumentationRegistry.getInstrumentation().waitForIdleSync()
        activityScenario?.onActivity {
            it.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        }
        InstrumentationRegistry.getInstrumentation().waitForIdleSync()
        activityScenario?.onActivity {
            it.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }
        InstrumentationRegistry.getInstrumentation().waitForIdleSync()
        activityScenario?.onActivity {
            it.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE
        }
        InstrumentationRegistry.getInstrumentation().waitForIdleSync()
        activityScenario?.onActivity {
            it.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT
        }
        InstrumentationRegistry.getInstrumentation().waitForIdleSync()
    }
}