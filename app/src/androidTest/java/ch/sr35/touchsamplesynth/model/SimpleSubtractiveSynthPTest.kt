package ch.sr35.touchsamplesynth.model
import androidx.test.ext.junit.runners.AndroidJUnit4
import ch.sr35.touchsamplesynth.audio.instruments.PolyphonyDefinition
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class SimpleSubtractiveSynthPTest {

    @Test
    fun equalityTest()
    {
        val o1 = SimpleSubtractiveSynthP(0.01f,0.02f,0.03f,0.04f,
            0.05f,0.06f,0.07f,0.23f,0.47f,0,1,-1,0.2f,0.12f,1200.0f,0.7f,0.12f,0.53f,1.0f,0.22f,0.002f,
            0.856f, PolyphonyDefinition.POLY_SATURATE,false,4,"Moog")
        val o2 = SimpleSubtractiveSynthP(0.01f,0.02f,0.03f,0.04f,
            0.05f,0.06f,0.07f,0.23f,0.47f,0,1,-1,0.2f,0.12f,1200.0f,0.7f,0.12f,0.53f,1.0f,0.22f,0.002f,
            0.856f,PolyphonyDefinition.POLY_SATURATE,false,4,"Moog")
        Assert.assertEquals(o1,o2)
    }

    @Test
    fun unequalityTest()
    {
        val o1 = SimpleSubtractiveSynthP(0.01f,0.02f,0.03f,0.04f,
            0.05f,0.06f,0.07f,0.23f,0.47f,0,1,-1,0.2f,0.12f,1200.0f,0.7f,0.12f,0.53f,1.0f,0.22f,0.674f,
            0.857f,PolyphonyDefinition.POLY_SATURATE,false,4,"Moog")
        val o2 = SimpleSubtractiveSynthP(0.01f,0.02f,0.03f,0.04f,
            0.05f,0.06f,0.07f,0.23f,0.47f,0,0,-1,0.2f,0.12f,1200.0f,0.7f,0.12f,0.53f,1.0f,0.22f,0.674f,
            0.856f,PolyphonyDefinition.POLY_SATURATE,false,4,"Moog")
        Assert.assertNotEquals(o1,o2)
    }

}