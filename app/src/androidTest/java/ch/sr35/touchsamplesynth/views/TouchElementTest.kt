package ch.sr35.touchsamplesynth.views

import android.view.MotionEvent
import androidx.test.core.view.MotionEventBuilder
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import ch.sr35.touchsamplesynth.audio.AudioEngineK
import ch.sr35.touchsamplesynth.audio.instruments.SineMonoSynthI
import ch.sr35.touchsamplesynth.R
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TouchElementTest {
    @Test
    fun generateTouchElementTest()
    {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        context.setTheme(R.style.Theme_TouchSampleSynth)
        val te = TouchElement(context,null)
        Assert.assertNotNull(te)
    }

    @Test
    fun virtuallyPlayTouchElement()
    {
        val context = InstrumentationRegistry.getInstrumentation().targetContext

        context.setTheme(R.style.Theme_TouchSampleSynth)
        val te = TouchElement(context,null)
        val i1 = SineMonoSynthI(context,"Basic")
        i1.generateVoices(3)
        te.soundGenerator=i1
        te.voiceNr=1

        //touch
        var evnt = MotionEventBuilder.newBuilder()
            .setAction(MotionEvent.ACTION_DOWN)
            .setPointer(0.1f,0.1f)
            .build()
        te.setEditmode(false)
        // virtually click
        te.onTouchEvent(evnt)

        //check that instrument is on
        Assert.assertTrue(i1.voices[1].isSounding())

        // release
        evnt = MotionEventBuilder.newBuilder()
            .setAction(MotionEvent.ACTION_UP)
            .setPointer(0.1f,0.1f)
            .build()
        te.onTouchEvent(evnt)


        val ae = AudioEngineK()
        ae.playFrames(200)
        Assert.assertFalse(i1.voices[1].isSounding())
    }
}