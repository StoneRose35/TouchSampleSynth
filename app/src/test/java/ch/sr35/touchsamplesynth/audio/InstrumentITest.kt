package ch.sr35.touchsamplesynth.audio

import org.junit.Test

class InstrumentITest {

    @Test
    fun equalityTest1()
    {
        val a= InstrumentI("Harmonica")
        val b = InstrumentI("Harmonica")
        assert(a == b)
    }

    @Test
    fun equalityTest2()
    {
        val a=InstrumentI("Harmonica")
        val b=InstrumentI("Guitar")
        assert(a != b)
    }

    @Test
    fun equalityTest3()
    {
        val a=InstrumentI("Harmonica")
        val b = ArrayList<Int>()
        assert(a != b)
    }
}