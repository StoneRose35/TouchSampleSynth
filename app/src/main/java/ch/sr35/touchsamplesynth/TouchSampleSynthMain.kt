package ch.sr35.touchsamplesynth

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import ch.sr35.touchsamplesynth.databinding.ActivityMainBinding

class TouchSampleSynthMain : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    external fun stringFromJNI(): String
    external fun touchEvent(action: Int,soundGenerator: Int)
    external fun startEngine()
    external fun stopEngine()
    external fun getSamplingRate(): Int

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        supportActionBar?.hide()
        startEngine()
    }

    override fun onDestroy() {
        super.onDestroy()
        stopEngine()
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