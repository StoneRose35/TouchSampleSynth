package ch.sr35.touchsamplesynth.audio.instruments

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import ch.sr35.touchsamplesynth.AudioTest
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import ch.sr35.touchsamplesynth.audio.voices.SimpleSubtractiveSynthK

@RunWith(AndroidJUnit4::class)
class SimpleSubtractiveSynthTest : AudioTest() {



        private val EPS=0.000001f
        @Test
        fun generationTest() {
            val context = InstrumentationRegistry.getInstrumentation().targetContext
            val instr = SimpleSubtractiveSynthI(context,"MastaBass")
            Assert.assertNotNull(instr)
        }

        @Test
        fun voiceGenerationTest()
        {
            val context = InstrumentationRegistry.getInstrumentation().targetContext
            val instr = SimpleSubtractiveSynthI(context,"MastaBass")
            instr.generateVoices(1)
            Assert.assertEquals(1,instr.voicesCount())

        }

        @Test
        fun parametersAssignTest1()
        {
            val context = InstrumentationRegistry.getInstrumentation().targetContext
            val instr = SimpleSubtractiveSynthI(context,"MastaBass")
            instr.generateVoices(1)
            instr.setVolumeAttack(0.6f)
            instr.setVolumeDecay(0.5f)
            instr.setVolumeSustain(0.78f)
            instr.setVolumeRelease(0.001f)
            instr.setInitialCutoff(0.35f)
            instr.setActionAmountToFilter(0.989f)
            instr.setVolumeModulation(0.125f)
            instr.setResonance(0.5887f)
            Assert.assertTrue(instr.getVolumeAttack() - 0.6f > -EPS && instr.getVolumeAttack() - 0.6f < EPS)
            Assert.assertTrue(instr.getVolumeDecay() - 0.5f > -EPS && instr.getVolumeDecay() - 0.5f < EPS)
            Assert.assertTrue(instr.getVolumeRelease() - 0.001f > -EPS && instr.getVolumeRelease() - 0.001f < EPS)
            Assert.assertTrue(instr.getVolumeSustain() - 0.78f > -EPS && instr.getVolumeSustain() - 0.78f < EPS)
            Assert.assertTrue(instr.getInitialCutoff() - 0.35f > -EPS && instr.getInitialCutoff() - 0.35f < EPS)
            Assert.assertTrue(instr.getActionAmountToFilter() - 0.989f > -EPS && instr.getActionAmountToFilter() - 0.989f < EPS)
            Assert.assertTrue(instr.getVolumeModulation() - 0.125f > -EPS && instr.getVolumeModulation() - 0.125f < EPS)
            Assert.assertTrue(instr.getResonance() - 0.5887f > -EPS && instr.getResonance() - 0.5887f < EPS)

        }

        @Test
        fun parametersAssignTest2()
        {
            val context = InstrumentationRegistry.getInstrumentation().targetContext
            val instr = SimpleSubtractiveSynthI(context,"MastaBass")
            instr.generateVoices(1)
            instr.setVolumeAttack(0.6f)
            instr.setVolumeDecay(0.5f)
            instr.setVolumeSustain(0.78f)
            instr.setVolumeRelease(0.001f)
            instr.setInitialCutoff(0.35f)
            instr.setActionAmountToFilter(0.989f)
            instr.setVolumeModulation(0.125f)
            instr.setResonance(0.5887f)

            instr.generateVoices(2)
            Assert.assertTrue(instr.getVolumeAttack() - 0.6f > -EPS && instr.getVolumeAttack() - 0.6f < EPS)
            Assert.assertTrue(instr.getVolumeDecay() - 0.5f > -EPS && instr.getVolumeDecay() - 0.5f < EPS)
            Assert.assertTrue(instr.getVolumeRelease() - 0.001f > -EPS && instr.getVolumeRelease() - 0.001f < EPS)
            Assert.assertTrue(instr.getVolumeSustain() - 0.78f > -EPS && instr.getVolumeSustain() - 0.78f < EPS)
            Assert.assertTrue(instr.getInitialCutoff() - 0.35f > -EPS && instr.getInitialCutoff() - 0.35f < EPS)
            Assert.assertTrue(instr.getActionAmountToFilter() - 0.989f > -EPS && instr.getActionAmountToFilter() - 0.989f < EPS)
            Assert.assertTrue(instr.getResonance() - 0.5887f > -EPS && instr.getResonance() - 0.5887f < EPS)
            Assert.assertTrue(instr.getVolumeModulation() - 0.125f > -EPS && instr.getVolumeModulation() - 0.125f < EPS)
            instr.voices.forEach {
                Assert.assertTrue((it as SimpleSubtractiveSynthK).getVolumeAttack() - 0.6f > -EPS && it.getVolumeAttack() - 0.6f < EPS)
                Assert.assertTrue(it.getVolumeDecay() - 0.5f > -EPS && it.getVolumeDecay() - 0.5f < EPS)
                Assert.assertTrue(it.getVolumeRelease() - 0.001f > -EPS && it.getVolumeRelease() - 0.001f < EPS)
                Assert.assertTrue(it.getVolumeSustain() - 0.78f > -EPS && it.getVolumeSustain() - 0.78f < EPS)
                Assert.assertTrue(it.getResonance() - 0.5887f > -EPS && it.getResonance() - 0.5887f < EPS)
                Assert.assertTrue(it.actionAmountToVolume - 0.125f > -EPS && it.actionAmountToVolume - 0.125f < EPS)
            }
        }


    companion object {
        init {
            System.loadLibrary("touchsamplesynth")
        }
    }

}