package ch.sr35.touchsamplesynth.model

import androidx.test.platform.app.InstrumentationRegistry
import ch.sr35.touchsamplesynth.AudioTest
import ch.sr35.touchsamplesynth.audio.instruments.SimpleSubtractiveSynthI
import ch.sr35.touchsamplesynth.audio.instruments.SineMonoSynthI
import org.junit.Assert
import org.junit.Test

class PersistableInstrumentITest : AudioTest(){


    @Test
    fun equalityTest()
    {
        val o1 = PersistableInstrument()
        o1.isMonophonic = false
        o1.name="poly"
        val o2 = PersistableInstrument()
        o2.isMonophonic = false
        o2.name="poly"
        Assert.assertEquals(o1,o2)
    }

    @Test
    fun unequalityTest()
    {
        val o1 = PersistableInstrument()
        o1.isMonophonic = false
        o1.name="poly"
        val o2 = PersistableInstrument()
        o2.isMonophonic = false
        o2.name="poly 2"
        Assert.assertNotEquals(o1,o2)
    }

    @Test
    fun unequalityTest2()
    {
        val o1 = PersistableInstrument()
        o1.isMonophonic = true
        o1.name="poly"
        val o2 = PersistableInstrument()
        o2.isMonophonic = false
        o2.name="polyphia"
        Assert.assertNotEquals(o1,o2)
    }

    @Test
    fun persistableInstrumentFactoryTest1()
    {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val i1 = SimpleSubtractiveSynthI(context,"Breiss")
        i1.generateVoices(1)
        i1.setAttack(0.1f)
        i1.setDecay(0.2f)
        i1.setSustain(0.3f)
        i1.setRelease(0.4f)
        i1.setInitialCutoff(0.5f)
        i1.setActionAmountToFilter(0.6f)
        i1.setResonance(0.7f)
        val pi1 = PersistableInstrumentFactory.fromInstrument(i1)
        Assert.assertTrue(pi1 is SimpleSubtractiveSynthP)
        if (pi1 != null)
        {
            val ii1 = PersistableInstrumentFactory.toInstrument(pi1, context)
            Assert.assertTrue(inRange((ii1 as SimpleSubtractiveSynthI).getAttack(),i1.getAttack()))
            Assert.assertTrue(inRange(ii1.getAttack(),i1.getAttack()))
            Assert.assertTrue(inRange(ii1.getDecay(),i1.getDecay()))
            Assert.assertTrue(inRange(ii1.getSustain(),i1.getSustain()))
            Assert.assertTrue(inRange(ii1.getRelease(),i1.getRelease()))
            Assert.assertTrue(inRange(ii1.getInitialCutoff(),i1.getInitialCutoff()))
            Assert.assertTrue(inRange(ii1.getActionAmountToFilter(),i1.getActionAmountToFilter()))
            Assert.assertTrue(inRange(ii1.getResonance(), i1.getResonance()))
        }
        else
        {
            Assert.fail()
        }
    }

    @Test
    fun persistableInstrumentFactoryTest2()
    {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val i1 = SineMonoSynthI(context, "SeinOderNichtSein")
        i1.generateVoices(1)
        i1.setAttack(0.1f)
        i1.setDecay(0.2f)
        i1.setSustain(0.3f)
        i1.setRelease(0.4f)
        val pi1 = PersistableInstrumentFactory.fromInstrument(i1)
        Assert.assertTrue(pi1 is SineMonoSynthP)
        if (pi1 != null) {
            val ii1 = PersistableInstrumentFactory.toInstrument(pi1, context)
            Assert.assertTrue(inRange((ii1 as SineMonoSynthI).getAttack(),i1.getAttack()))
            Assert.assertTrue(inRange(ii1.getAttack(),i1.getAttack()))
            Assert.assertTrue(inRange(ii1.getDecay(),i1.getDecay()))
            Assert.assertTrue(inRange(ii1.getSustain(),i1.getSustain()))
            Assert.assertTrue(inRange(ii1.getRelease(),i1.getRelease()))
        }
        else
        {
            Assert.fail()
        }
    }


    private fun inRange(a: Float, b: Float): Boolean
    {
        val EPS= 0.00000001
        return (a - b > -EPS && a - b < EPS)
    }
}