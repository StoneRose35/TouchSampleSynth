package ch.sr35.touchsamplesynth.model

import androidx.test.ext.junit.runners.AndroidJUnit4
import ch.sr35.touchsamplesynth.audio.instruments.PolyphonyDefinition
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SineMonoSynthPTest {
    @Test
    fun equalityTest()
    {
        val o1 = SineMonoSynthP(0.01f,0.02f,0.03f,0.04f,0.23f, PolyphonyDefinition.POLY_SATURATE,4,"Moog")
        val o2 = SineMonoSynthP(0.01f,0.02f,0.03f,0.04f,0.23f, PolyphonyDefinition.POLY_SATURATE,4,"Moog")
        Assert.assertEquals(o1,o2)
    }

    @Test
    fun unequalityTest()
    {
        val o1 = SineMonoSynthP(0.01f,0.02f,0.03f,0.14f,0.23f, PolyphonyDefinition.POLY_SATURATE,4,"Moog")
        val o2 = SineMonoSynthP(0.01f,0.02f,0.03f,0.04f,0.23f, PolyphonyDefinition.POLY_SATURATE,4,"Moog")
        Assert.assertNotEquals(o1,o2)
    }
}
