package ch.sr35.touchsamplesynth

import androidx.test.ext.junit.runners.AndroidJUnit4
import ch.sr35.touchsamplesynth.audio.AudioEngineK
import org.junit.AfterClass
import org.junit.BeforeClass
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
abstract class AudioTest {



    companion object
    {
        @JvmStatic
        @BeforeClass
        fun setupAudioTest(): Unit {

        }

        @JvmStatic
        @AfterClass
        fun teardownAudioTest(): Unit {
            val audioEngineK = AudioEngineK()
            audioEngineK.emptySoundGenerators()
        }
    }
}