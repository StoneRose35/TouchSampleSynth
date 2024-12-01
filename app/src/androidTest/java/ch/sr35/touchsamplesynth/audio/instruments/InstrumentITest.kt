package ch.sr35.touchsamplesynth.audio.instruments

import androidx.fragment.app.Fragment
import androidx.test.platform.app.InstrumentationRegistry
import ch.sr35.touchsamplesynth.TouchSampleSynthMain
import ch.sr35.touchsamplesynth.model.InstrumentP
import ch.sr35.touchsamplesynth.model.PersistableInstrumentFactory
import ch.sr35.touchsamplesynth.model.SineMonoSynthP
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.Assert

import org.junit.Test

class InstrumentITest {

    @Test
    fun equalityTest1()
    {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val a= SineMonoSynthI(context,"Harmonica") as InstrumentI
        val b = SineMonoSynthI(context,"Harmonica") as InstrumentI
        assert(a == b)
    }

    @Test
    fun equalityTest2()
    {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val a= SineMonoSynthI(context,"Harmonica") as InstrumentI
        val b= SineMonoSynthI(context,"Guitar") as InstrumentI
        assert(a != b)
    }

    @Test
    fun equalityTest3()
    {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val a= SineMonoSynthI(context,"Harmonica") as InstrumentI
        val b = ArrayList<Int>()
        assert(a != b)
    }

    @Test
    fun getAllInstrumentsTest()
    {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val subclasses = InstrumentI::class.sealedSubclasses
        assertThat(subclasses.size,greaterThan(2))
        assertThat(subclasses,hasItem(SineMonoSynthI::class))
    }

    @Test
    fun constructAllUsingReflection()
    {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val subclasses = InstrumentI::class.sealedSubclasses
        var i=0
        var instruments = ArrayList<InstrumentI>()
        subclasses.forEach {
            subClass ->
            instruments.add(subClass.constructors.first().call(context,"Instrument ${i++}"))
        }
        assertThat(instruments.size,greaterThan(2))
    }

    @Test
    fun cloneOneInstrumentUsingReflection()
    {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val subclasses = InstrumentI::class.sealedSubclasses
        val instrument = subclasses.first().constructors.first().call(context,"Instrument")
        val cloneInstrument = instrument.javaClass.constructors.first().newInstance(context, "Instrument 2")
        assertThat(cloneInstrument::class,sameInstance(instrument::class))
    }

    @Test
    fun getFragmentForInstrument()
    {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val subclasses = InstrumentI::class.sealedSubclasses
        val instrument = subclasses.first().constructors.first().call(context,"Instrument")
        val framentName = instrument::class.java.name.split(".").last().let {
                cn ->
            cn.substring(0,cn.length-1) + "Fragment"
        }
        val frag = Class.forName("ch.sr35.touchsamplesynth.fragments.$framentName").constructors.first { cstr -> cstr.parameterCount == 1 } .newInstance(instrument) as Fragment
        Assert.assertNotNull(frag)
    }

    @Test
    fun instrumentIToPTest()
    {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val subclasses = InstrumentI::class.sealedSubclasses
        val instrument = subclasses.first().constructors.first().call(context,"Instrument")
        val pp = PersistableInstrumentFactory.fromInstrument(instrument)
        Assert.assertNotNull(pp)
    }

    @Test
    fun instrumentPToITest()
    {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val instrumentP = SineMonoSynthP()
        val pi = PersistableInstrumentFactory.toInstrument(instrumentP,context)
        Assert.assertNotNull(pi)

    }
}