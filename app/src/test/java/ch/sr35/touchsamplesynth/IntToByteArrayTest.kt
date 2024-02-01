package ch.sr35.touchsamplesynth

import org.junit.Assert
import org.junit.Test
import kotlin.random.Random
import kotlin.random.nextUInt

class IntToByteArrayTest {

    @Test
    fun intToByteArrayAndBack() {
        val number1 = Random(4356).nextUInt()
        val array = ByteArray(4)
        array[0] = (number1 shr 24).toByte()
        array[1] = (number1 shr 16).toByte()
        array[2] = (number1 shr 8).toByte()
        array[3] = (number1 shr 0).toByte()
        var number2=0u
        number2 = number2 or (array[3].toUInt() and 0xFFu shl 0)
        number2 = number2 or (array[2].toUInt() and 0xFFu shl 8)
        number2 = number2 or (array[1].toUInt() and 0xFFu shl 16)
        number2 = number2 or (array[0].toUInt() and 0xFFu shl 24)
        Assert.assertEquals(number2,number1)
    }
}