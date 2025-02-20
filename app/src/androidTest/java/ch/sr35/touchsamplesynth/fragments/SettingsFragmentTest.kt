package ch.sr35.touchsamplesynth.fragments

import android.os.Build
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onData
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isChecked
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withSpinnerText
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.GrantPermissionRule
import androidx.test.rule.GrantPermissionRule.grant
import ch.sr35.touchsamplesynth.BaseTSSTest
import ch.sr35.touchsamplesynth.R
import ch.sr35.touchsamplesynth.SCENES_FILE_NAME
import ch.sr35.touchsamplesynth.TouchSampleSynthMain
import org.hamcrest.CoreMatchers.anything
import org.hamcrest.CoreMatchers.containsString
import org.hamcrest.CoreMatchers.not
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File
import java.io.FileOutputStream

@RunWith(AndroidJUnit4::class)
class SettingsFragmentTest: BaseTSSTest() {


     @Test
     fun testSettingsPagePersistenceFramesPerDataCallback()
     {
         onView(withId(R.id.toolbar_settingspage)).perform(ViewActions.click())
         onView(withId(R.id.spinnerFramesPerDataCallback)).perform(ViewActions.click())
         onView(withText("256")).perform(ViewActions.click())
         onView(withId(R.id.toolbar_playpage)).perform(ViewActions.click())
         onView(withId(R.id.playpage_instrument_chips)).check(matches(not(isDisplayed()))) // dummy check to wait properly for play page to load
         onView(withId(R.id.toolbar_settingspage)).perform(ViewActions.click())
         onView(withId(R.id.spinnerFramesPerDataCallback)).check(matches(withSpinnerText(containsString("256"))))
     }


    @Test
    fun testSettingsPagePersistenceBufferCapacityInFrames()
    {
        onView(withId(R.id.toolbar_settingspage)).perform(ViewActions.click())
        onView(withId(R.id.spinnerBufferCapacityInFrames)).perform(ViewActions.click())
        onView(withText("512")).perform(ViewActions.click())
        onView(withId(R.id.toolbar_playpage)).perform(ViewActions.click())
        onView(withId(R.id.playpage_instrument_chips)).check(matches(not(isDisplayed()))) // dummy check to wait properly for play page to load
        onView(withId(R.id.toolbar_settingspage)).perform(ViewActions.click())
        onView(withId(R.id.spinnerBufferCapacityInFrames)).check(matches(withSpinnerText(containsString("512"))))
    }

    @Test
    fun testSettingsPagePersistenceBufferRtpMidiNoteRepeat()
    {
        onView(withId(R.id.toolbar_settingspage)).perform(ViewActions.click())
        onView(withId(R.id.spinnerRtpMidiNoteRepeat)).perform(ViewActions.click())
        onData(anything()).atPosition(2).perform(ViewActions.click())
        onView(withId(R.id.toolbar_playpage)).perform(ViewActions.click())
        onView(withId(R.id.playpage_instrument_chips)).check(matches(not(isDisplayed()))) // dummy check to wait properly for play page to load
        onView(withId(R.id.toolbar_settingspage)).perform(ViewActions.click())
        onView(withId(R.id.spinnerRtpMidiNoteRepeat)).check(matches(withSpinnerText(containsString("3"))))
    }

    @Test
    fun testSettingsPagePersistenceTEDisplayStyle()
    {
        onView(withId(R.id.toolbar_settingspage)).perform(ViewActions.click())
        onView(withId(R.id.spinnerTouchElementsDisplay)).perform(ViewActions.click())
        onData(anything()).atPosition(1).perform(ViewActions.click())
        onView(withId(R.id.toolbar_playpage)).perform(ViewActions.click())
        onView(withId(R.id.playpage_instrument_chips)).check(matches(not(isDisplayed()))) // dummy check to wait properly for play page to load
        onView(withId(R.id.toolbar_settingspage)).perform(ViewActions.click())
        onView(withId(R.id.spinnerTouchElementsDisplay)).check(matches(withSpinnerText(containsString("Descriptive"))))
    }

    @Test
    fun testSettingsPagePersistenceShowConnectors()
    {
        onView(withId(R.id.toolbar_settingspage)).perform(ViewActions.click())
        onView(withId(R.id.toggleButtonShowConnectors)).perform(ViewActions.click())
        onView(withId(R.id.toolbar_playpage)).perform(ViewActions.click())
        onView(withId(R.id.playpage_instrument_chips)).check(matches(not(isDisplayed()))) // dummy check to wait properly for play page to load
        onView(withId(R.id.toolbar_settingspage)).perform(ViewActions.click())
        onView(withId(R.id.toggleButtonShowConnectors)).check(matches(isChecked()))
    }
}