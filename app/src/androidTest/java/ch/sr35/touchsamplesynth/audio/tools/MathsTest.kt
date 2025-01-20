package ch.sr35.touchsamplesynth.audio.tools

import android.util.Log
import androidx.test.ext.junit.runners.AndroidJUnit4
import ch.sr35.touchsamplesynth.TAG
import org.junit.Ignore
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.math.log10
import kotlin.system.measureTimeMillis

@RunWith(AndroidJUnit4::class)
class MathsTest {


        @Test
        @Ignore("Perfomance test")
        fun logTest(){
            val mathobj = Maths()
            var x: Float
            val incr=0.001f
            var res=0.0f
            val timeToDbFast = measureTimeMillis {
                for (i in 0..10000) {
                    x=0.0000001f
                    while(x < 1.0f)
                    {
                        res += mathobj.toDb(x)
                        x += incr
                    }
                }
            }
            Log.d(TAG,"timeToDbFast: $timeToDbFast")
            res=0.0f
            val timeToDbSlow = measureTimeMillis {
                for (i in 0..10000) {
                    x=0.0000001f
                    while(x < 1.0f)
                    {
                        res+=20.0f* log10(x)
                        x += incr
                    }
                }
            }

            Log.d(TAG,"timeToDbSlow: $timeToDbSlow")

        }


        companion object {
            // Used to load the 'touchsamplesynth' library on application startup.
            init {
                System.loadLibrary("touchsamplesynth")
            }
        }
    }
