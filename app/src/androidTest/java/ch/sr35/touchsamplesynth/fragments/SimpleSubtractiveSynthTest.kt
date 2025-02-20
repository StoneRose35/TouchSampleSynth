package ch.sr35.touchsamplesynth.fragments

import android.widget.TextView
import androidx.test.espresso.Espresso.onData
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import ch.sr35.touchsamplesynth.BaseTSSTest
import ch.sr35.touchsamplesynth.R
import org.hamcrest.CoreMatchers.anything
import org.junit.Test
import org.junit.runner.RunWith
import java.util.regex.Pattern.matches

@RunWith(AndroidJUnit4::class)
class SimpleSubtractiveSynthTest: BaseTSSTest() {

    @Test
    fun checkExistenceOfInvisibleLabels()
    {
        onView(withId(R.id.toolbar_scenespage)).perform(click())
        onView(withId(R.id.sceneImport)).perform(click())
        onView(withId(R.id.toolbar_playpage)).perform(click())
        onView(withId(R.id.toolbar_scenes)).perform(click())
        onView(withText("Chords D")).perform(click())
        onView(withId(R.id.toolbar_instrumentspage)).perform(click())
        onView(withId(R.id.tvTouchToVolume)).check { view, noViewFoundException ->
            val screenlocation = intArrayOf(1,2)
            view.getLocationOnScreen(screenlocation)
            (view as TextView).text.contains("Vol Mod.")

         }

    }
}