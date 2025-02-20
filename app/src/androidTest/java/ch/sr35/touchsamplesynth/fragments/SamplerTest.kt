package ch.sr35.touchsamplesynth.fragments

import androidx.appcompat.widget.SwitchCompat
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import ch.sr35.touchsamplesynth.BaseTSSTest
import ch.sr35.touchsamplesynth.R
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SamplerTest: BaseTSSTest() {


    @Test
    fun noLoopAndTriggeredTest()
    {
        onView(withId(R.id.toolbar_instrumentspage)).perform(click())
        onView(withText("Add")).perform(click())
        onView(withText("Sampler")).perform(click())
        onView(withText("OK")).perform(click())
        onView(withText("basic")).perform(click())
        onView(withId(R.id.sampler_switch_mode_looped)).perform(click())
        onView(withId(R.id.sampler_switch_mode_triggered)).perform(click())
        onView(withId(R.id.sampler_switch_mode_looped)).check { view, _ -> !((view as SwitchCompat).isChecked) }
        onView(withId(R.id.sampler_switch_mode_looped)).perform(click())
        onView(withId(R.id.sampler_switch_mode_triggered)).check { view, _ -> !((view as SwitchCompat).isChecked) }
    }
}