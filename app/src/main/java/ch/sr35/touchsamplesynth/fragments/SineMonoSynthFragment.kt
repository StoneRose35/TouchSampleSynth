package ch.sr35.touchsamplesynth.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import ch.sr35.touchsamplesynth.R
import ch.sr35.touchsamplesynth.audio.SineMonoSynthK


/**
 * A simple [Fragment] subclass.
 */
class SineMonoSynthFragment(s: SineMonoSynthK) : Fragment(), SeekBar.OnSeekBarChangeListener {


    private val synth = s
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_sine_mono_synth, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<SeekBar>(R.id.seekBarAttack).setOnSeekBarChangeListener(this)
        view.findViewById<SeekBar>(R.id.seekBarDecay)
            .setOnSeekBarChangeListener(this)
        view.findViewById<SeekBar>(R.id.seekBarSustain)
            .setOnSeekBarChangeListener(this)
        view.findViewById<SeekBar>(R.id.seekBarRelease)
            .setOnSeekBarChangeListener(this)

    }

    companion object {

    }



    override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
        when (p0?.id) {
            R.id.seekBarAttack -> {
                synth.setAttack(p0.progress.toFloat() / 1000.0f * 8.0f)
            }

            R.id.seekBarDecay -> {
                synth.setDecay(p0.progress.toFloat() / 1000.0f * 8.0f)
            }

            R.id.seekBarSustain -> {
                synth.setSustain(p0.progress.toFloat() / 1000.0f)
            }

            R.id.seekBarRelease -> {
                synth.setRelease(p0.progress.toFloat() / 1000.0f * 8.0f)
            }
        }
    }

    override fun onStartTrackingTouch(p0: SeekBar?) {
    }

    override fun onStopTrackingTouch(p0: SeekBar?) {
    }
}



