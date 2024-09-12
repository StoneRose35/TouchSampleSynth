package ch.sr35.touchsamplesynth

import org.junit.Assert
import org.junit.Test
import kotlin.math.ceil
import kotlin.math.floor

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


}