package ch.sr35.touchsamplesynth.model

import android.view.ContextThemeWrapper

import androidx.constraintlayout.widget.ConstraintLayout
import androidx.test.platform.app.InstrumentationRegistry
import ch.sr35.touchsamplesynth.AudioTest
import ch.sr35.touchsamplesynth.MusicalPitch
import ch.sr35.touchsamplesynth.audio.instruments.InstrumentI
import ch.sr35.touchsamplesynth.audio.instruments.SineMonoSynthI
import ch.sr35.touchsamplesynth.views.TouchElement
import org.junit.Assert
import org.junit.Test
import ch.sr35.touchsamplesynth.audio.instruments.SimpleSubtractiveSynthI
import ch.sr35.touchsamplesynth.graphics.Converter

class ScenePTest: AudioTest() {

    @Test
    fun toSceneAndBackTest()
    {
        val instrumentIS = ArrayList<InstrumentI>()
        val touchelements = ArrayList<TouchElement>()
        val lp = ConstraintLayout.LayoutParams(Converter.toPx(134), Converter.toPx(166))
        lp.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
        lp.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
        lp.marginStart = Converter.toPx(10)
        lp.topMargin = Converter.toPx(10)
        val notes = MusicalPitch.generateAllNotes()
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val themedContext =ContextThemeWrapper(context, ch.sr35.touchsamplesynth.R.style.Theme_TouchSampleSynth)
        val i1 = SineMonoSynthI(themedContext, "SeinOderNichtSein")
        i1.generateVoices(4)
        i1.setAttack(0.1f)
        i1.setDecay(0.2f)
        i1.setSustain(0.3f)
        i1.setRelease(0.4f)
        val i2 = SimpleSubtractiveSynthI(themedContext, "PortoTipp")
        i2.generateVoices(1)
        i2.setAttack(0.01f)
        i2.setDecay(0.4f)
        i2.setSustain(0.0f)
        i2.setRelease(0.56f)
        instrumentIS.add(i1)
        instrumentIS.add(i2)

        val te1 = TouchElement(themedContext,null)
        te1.setSoundGenerator (i1)
        te1.notes.add(notes[5])
        te1.layoutParams = lp
        touchelements.add(te1)

        val te2 = TouchElement(themedContext,null)
        te2.setSoundGenerator (i1)
        te2.notes.add(notes[7])
        te2.layoutParams = lp
        touchelements.add(te2)

        val te3 = TouchElement(themedContext,null)
        te3.setSoundGenerator (i1)
        te3.notes.add(notes[9])
        te3.layoutParams = lp
        touchelements.add(te3)

        val te4 = TouchElement(themedContext,null)
        te4.setSoundGenerator (i1)
        te4.notes.add(notes[11])
        te4.layoutParams = lp
        touchelements.add(te4)

        val te5 = TouchElement(themedContext,null)
        te5.setSoundGenerator (i1)
        te5.notes.add(notes[13])
        te5.layoutParams = lp
        touchelements.add(te5)

        val te10 = TouchElement(themedContext, null)
        te10.setSoundGenerator (i1)
        te10.notes.add(notes[45])
        te10.layoutParams = lp
        touchelements.add(te10)

        val te11 = TouchElement(themedContext, null)
        te11.setSoundGenerator (i1)
        te11.notes.add(notes[57])
        te11.layoutParams = lp
        touchelements.add(te11)

        val scene = SceneP().also {
            it.persist(instrumentIS,touchelements)
        }
        instrumentIS.flatMap { instr -> instr.voices }.forEach { it.detachFromAudioEngine() }
        val regeneratedInstrumentIS = ArrayList<InstrumentI>()
        val regeneratedTouchElements = ArrayList<TouchElement>()
        scene.populate(regeneratedInstrumentIS,regeneratedTouchElements,themedContext)

        Assert.assertTrue(regeneratedInstrumentIS.size == 2)
        Assert.assertTrue(regeneratedTouchElements.size == 7)
        instrumentIS.forEach { i ->
            Assert.assertTrue(regeneratedInstrumentIS.filter { ri ->
                ri.name == i.name && ri.polyphonyDefinition == i.polyphonyDefinition && ri.javaClass == i.javaClass
            }.size == 1)
        }

        touchelements.forEach {
            t ->
            Assert.assertTrue(
            regeneratedTouchElements.filter {
                rt ->
                rt.notes.all { t.notes.contains(it) }  && rt.getSoundGenerator()!!.javaClass == t.getSoundGenerator()!!.javaClass
            }.size==1)
        }
    }
}