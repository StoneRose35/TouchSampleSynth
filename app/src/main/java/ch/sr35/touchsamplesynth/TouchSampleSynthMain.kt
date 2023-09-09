package ch.sr35.touchsamplesynth

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ToggleButton
import ch.sr35.touchsamplesynth.audio.AudioEngineK
import ch.sr35.touchsamplesynth.audio.MusicalSoundGenerator
import ch.sr35.touchsamplesynth.audio.SineMonoSynthK
import ch.sr35.touchsamplesynth.databinding.ActivityMainBinding
import ch.sr35.touchsamplesynth.views.TouchElement
import ch.sr35.touchsamplesynth.views.VuMeter

class TouchSampleSynthMain : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val audioEngine: AudioEngineK=AudioEngineK()
    private val exampleSynth: SineMonoSynthK= SineMonoSynthK()
    val soundGenerators=ArrayList<MusicalSoundGenerator>()
    val touchElements=ArrayList<TouchElement>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val startButton = findViewById<ImageButton>(R.id.buttonStart)
        val stopButton = findViewById<ImageButton>(R.id.buttonStop)
        val newButton = findViewById<Button>(R.id.buttonNew)
        val touchElement = findViewById<TouchElement>(R.id.touchElement)

        exampleSynth.bindToAudioEngine()
        exampleSynth.setAttack(0.1f)
        exampleSynth.setDecay(0.1f)
        exampleSynth.setSustain(1.0f)
        exampleSynth.setRelease(0.1f)
        exampleSynth.setNote(12.0f)
        touchElement.setSoundGenerator(exampleSynth)
        touchElements.add(touchElement)

        val toggleButton = findViewById<ToggleButton>(R.id.toggleEdit)
        toggleButton.setOnCheckedChangeListener { _, toggleval ->

            if (toggleval)
            {
                for (touchel: TouchElement in touchElements)
                {
                    touchel.setEditmode(true)
                }
                startButton.visibility = View.INVISIBLE
                stopButton.visibility = View.INVISIBLE
                newButton.visibility = View.VISIBLE
            }
            else
            {
                for (touchel: TouchElement in touchElements)
                {
                    touchel.setEditmode(false)
                }
                startButton.visibility = View.VISIBLE
                stopButton.visibility = View.VISIBLE
                newButton.visibility = View.INVISIBLE
            }
        }

        val vuMeter = findViewById<VuMeter>(R.id.vuMeter)

        startButton.setOnClickListener {
            audioEngine.startEngine()
            vuMeter.setActive(true)
        }

        stopButton.setOnClickListener{
            audioEngine.stopEngine()
            vuMeter.setActive(false)
        }

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