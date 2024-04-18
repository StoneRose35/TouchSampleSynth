package ch.sr35.touchsamplesynth.model
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class SimpleSubtractiveSynthPTest {

    @Test
    fun equalityTest()
    {
        val o1 = SimpleSubtractiveSynthP(0.01f,0.02f,0.03f,0.04f,0.05f,0.06f,0.07f,0.23f,5,"Moog")
        val o2 = SimpleSubtractiveSynthP(0.01f,0.02f,0.03f,0.04f,0.05f,0.06f,0.07f,0.23f,5,"Moog")
        Assert.assertEquals(o1,o2)
    }

    @Test
    fun unequalityTest()
    {
        val o1 = SimpleSubtractiveSynthP(0.01f,0.02f,0.03f,0.14f,0.05f,0.06f,0.07f,0.23f,5,"Moog")
        val o2 = SimpleSubtractiveSynthP(0.01f,0.02f,0.03f,0.04f,0.05f,0.06f,0.07f,0.23f,5,"Moog")
        Assert.assertNotEquals(o1,o2)
    }

}