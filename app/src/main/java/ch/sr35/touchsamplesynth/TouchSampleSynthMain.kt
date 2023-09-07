package ch.sr35.touchsamplesynth

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import ch.sr35.touchsamplesynth.audio.AudioEngineK
import ch.sr35.touchsamplesynth.audio.SineMonoSynthK
import ch.sr35.touchsamplesynth.databinding.ActivityMainBinding
import ch.sr35.touchsamplesynth.views.TouchElement

class TouchSampleSynthMain : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val audioEngine: AudioEngineK=AudioEngineK()
    external fun touchEvent(action: Int,soundGenerator: Int)
    private val exampleSynth: SineMonoSynthK= SineMonoSynthK()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        supportActionBar?.hide()
        audioEngine.startEngine()

        val touchElement = findViewById<TouchElement>(R.id.touchElement)
        exampleSynth.bindToAudioEngine()
        exampleSynth.setAttack(0.1f)
        exampleSynth.setDecay(0.1f)
        exampleSynth.setSustain(1.0f)
        exampleSynth.setRelease(0.1f)
        exampleSynth.setNote(12.0f)
        touchElement.setSoundGenerator(exampleSynth)
    }

    override fun onDestroy() {
        super.onDestroy()
        audioEngine.stopEngine()
    }
    /**
     * A native method that is implemented by the 'touchsamplesynth' native library,
     * which is packaged with this application.
     */


    companion object {
        // Used to load the 'touchsamplesynth' library on application startup.
        init {
            System.loadLibrary("touchsamplesynth")
        }
    }
}