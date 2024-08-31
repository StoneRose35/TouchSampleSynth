package ch.sr35.touchsamplesynth.audio.instruments

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import ch.sr35.touchsamplesynth.AudioTest
import ch.sr35.touchsamplesynth.TouchSampleSynthMain
import ch.sr35.touchsamplesynth.audio.voices.SimpleSubtractiveSynthK
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith


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
            instr.setAttack(0.6f)
            instr.setDecay(0.5f)
            instr.setSustain(0.78f)
            instr.setRelease(0.001f)
            instr.setInitialCutoff(0.35f)
            instr.setActionAmountToFilter(0.989f)
            instr.setVolumeModulation(0.125f)
            instr.setResonance(0.5887f)
            Assert.assertTrue(instr.getAttack() - 0.6f > -EPS && instr.getAttack() - 0.6f < EPS)
            Assert.assertTrue(instr.getDecay() - 0.5f > -EPS && instr.getDecay() - 0.5f < EPS)
            Assert.assertTrue(instr.getRelease() - 0.001f > -EPS && instr.getRelease() - 0.001f < EPS)
            Assert.assertTrue(instr.getSustain() - 0.78f > -EPS && instr.getSustain() - 0.78f < EPS)
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
            instr.setAttack(0.6f)
            instr.setDecay(0.5f)
            instr.setSustain(0.78f)
            instr.setRelease(0.001f)
            instr.setInitialCutoff(0.35f)
            instr.setActionAmountToFilter(0.989f)
            instr.setVolumeModulation(0.125f)
            instr.setResonance(0.5887f)

            instr.generateVoices(2)
            Assert.assertTrue(instr.getAttack() - 0.6f > -EPS && instr.getAttack() - 0.6f < EPS)
            Assert.assertTrue(instr.getDecay() - 0.5f > -EPS && instr.getDecay() - 0.5f < EPS)
            Assert.assertTrue(instr.getRelease() - 0.001f > -EPS && instr.getRelease() - 0.001f < EPS)
            Assert.assertTrue(instr.getSustain() - 0.78f > -EPS && instr.getSustain() - 0.78f < EPS)
            Assert.assertTrue(instr.getInitialCutoff() - 0.35f > -EPS && instr.getInitialCutoff() - 0.35f < EPS)
            Assert.assertTrue(instr.getActionAmountToFilter() - 0.989f > -EPS && instr.getActionAmountToFilter() - 0.989f < EPS)
            Assert.assertTrue(instr.getResonance() - 0.5887f > -EPS && instr.getResonance() - 0.5887f < EPS)
            Assert.assertTrue(instr.getVolumeModulation() - 0.125f > -EPS && instr.getVolumeModulation() - 0.125f < EPS)
            instr.voices.forEach {
                Assert.assertTrue((it as SimpleSubtractiveSynthK).getAttack() - 0.6f > -EPS && it.getAttack() - 0.6f < EPS)
                Assert.assertTrue(it.getDecay() - 0.5f > -EPS && it.getDecay() - 0.5f < EPS)
                Assert.assertTrue(it.getRelease() - 0.001f > -EPS && it.getRelease() - 0.001f < EPS)
                Assert.assertTrue(it.getSustain() - 0.78f > -EPS && it.getSustain() - 0.78f < EPS)
                Assert.assertTrue(it.getResonance() - 0.5887f > -EPS && it.getResonance() - 0.5887f < EPS)
                Assert.assertTrue(it.actionAmountToVolume - 0.125f > -EPS && it.actionAmountToVolume - 0.125f < EPS)
            }
        }



}