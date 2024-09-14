package ch.sr35.touchsamplesynth

import org.junit.Assert
import org.junit.Test


class dumbTests {


    @Test
    fun arrayListTest()
    {
        val arrList = ArrayList<Int>()
        arrList.add(1)
        arrList.add(2)
        arrList.add(3)
        var res = 0
        for (idx in arrList)
        {
            res += idx
        }
        Assert.assertTrue(res==6)
    }

    @Test
    fun KeepOnlyFirstTest()
    {
        val arrList = ArrayList<Int>()
        arrList.addAll(listOf(1,2,3,45,67,23,5,6))
        arrList.apply {
            val f = first()
            clear()
            add(f)
        }


        Assert.assertTrue(arrList.size == 1)
    }


}