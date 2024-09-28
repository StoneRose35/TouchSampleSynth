package ch.sr35.touchsamplesynth.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import ch.sr35.touchsamplesynth.R
import ch.sr35.touchsamplesynth.audio.instruments.SineMonoSynthI


/**
 * A simple [Fragment] subclass.
 */
class SineMonoSynthFragment() : Fragment(), SeekBar.OnSeekBarChangeListener {

    constructor(s: SineMonoSynthI): this()
    {
        synth = s
    }

    private var synth: SineMonoSynthI?=null
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
        synth?.let {
            super.onViewCreated(view, savedInstanceState)
            val attack = view.findViewById<SeekBar>(R.id.seekBarAttack)
            attack.progress = (it.getAttack() / 2.0f * 1000.0f).toInt()
            attack.setOnSeekBarChangeListener(this)

            val decay = view.findViewById<SeekBar>(R.id.seekBarDecay)
            decay.progress = (it.getDecay() / 2.0f * 1000.0f).toInt()
            decay.setOnSeekBarChangeListener(this)

            val sustain = view.findViewById<SeekBar>(R.id.seekBarSustain)
            sustain.progress = (it.getSustain() * 1000.0f).toInt()
            sustain.setOnSeekBarChangeListener(this)

            val release = view.findViewById<SeekBar>(R.id.seekBarRelease)
            release.progress = (it.getRelease() / 2.0f * 1000.0f).toInt()
            release.setOnSeekBarChangeListener(this)

            val volumeModulation = view.findViewById<SeekBar>(R.id.seekBarTouchToVolume)
            volumeModulation.progress = (it.getVolumeModulation() * 1000.0f).toInt()
            volumeModulation.setOnSeekBarChangeListener(this)
        }

    }

    companion object {

    }



    override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
        synth?.let {
            when (p0?.id) {
                R.id.seekBarAttack -> {
                    it.setAttack(p0.progress.toFloat() / 1000.0f * 2.0f)
                }

                R.id.seekBarDecay -> {
                    it.setDecay(p0.progress.toFloat() / 1000.0f * 2.0f)
                }

                R.id.seekBarSustain -> {
                    it.setSustain(p0.progress.toFloat() / 1000.0f)
                }

                R.id.seekBarRelease -> {
                    it.setRelease(p0.progress.toFloat() / 1000.0f * 2.0f)
                }

                R.id.seekBarTouchToVolume -> {
                    it.setVolumeModulation(p0.progress.toFloat() / 1000.0f)
                }
            }
        }
    }

    override fun onStartTrackingTouch(p0: SeekBar?) {
    }

    override fun onStopTrackingTouch(p0: SeekBar?) {
    }
}



