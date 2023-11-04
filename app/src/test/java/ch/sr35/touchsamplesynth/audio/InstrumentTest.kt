package ch.sr35.touchsamplesynth.audio

import org.junit.Test

class InstrumentTest {

    @Test
    fun equalityTest1()
    {
        val a: Instrument=Instrument("Harmonica")
        val b: Instrument=Instrument("Harmonica")
        assert(a == b)
    }

    @Test
    fun equalityTest2()
    {
        val a: Instrument=Instrument("Harmonica")
        val b: Instrument=Instrument("Guitar")
        assert(a != b)
    }

    @Test
    fun equalityTest3()
    {
        val a: Instrument=Instrument("Harmonica")
        val b = ArrayList<Int>()
        assert(a != b)
    }
}