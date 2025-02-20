package ch.sr35.touchsamplesynth


import android.content.pm.ActivityInfo
import android.os.Build

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.GrantPermissionRule
import androidx.test.rule.GrantPermissionRule.grant
import org.junit.After
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File
import java.io.FileOutputStream


@RunWith(AndroidJUnit4::class)
class TouchSampleSynthMainTest: BaseTSSTest() {


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