package ch.sr35.touchsamplesynth.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioGroup
import android.widget.SeekBar
import ch.sr35.touchsamplesynth.R
import ch.sr35.touchsamplesynth.audio.AudioUtils
import ch.sr35.touchsamplesynth.audio.instruments.SimpleSubtractiveSynthI


/**
 * A simple [Fragment] subclass.
 * create an instance of this fragment.
 */
class SimpleSubtractiveSynthFragment() : Fragment(), SeekBar.OnSeekBarChangeListener {

    private var synth: SimpleSubtractiveSynthI? = null

    constructor(s: SimpleSubtractiveSynthI) : this() {
        synth = s
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_simple_subtractive_synth, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        synth?.let {
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

            val cutoff = view.findViewById<SeekBar>(R.id.seekBarCutoff)
            cutoff.progress =
                ((AudioUtils.FreqToNote(it.getInitialCutoff()) + 39.0f) * 1000.0 / 105.0f).toInt()
            cutoff.setOnSeekBarChangeListener(this)

            val resonance = view.findViewById<SeekBar>(R.id.seekBarResonance)
            resonance.progress = (it.getResonance() * 1000.0f).toInt()
            resonance.setOnSeekBarChangeListener(this)

            val actionAmount = view.findViewById<SeekBar>(R.id.seekBarCutoffModulation)
            actionAmount.progress = (it.getActionAmountToFilter() / 50.0f * 1000.0f).toInt()
            actionAmount.setOnSeekBarChangeListener(this)

            val actionAmountToVolume = view.findViewById<SeekBar>(R.id.seekBarTouchToVolume)
            actionAmountToVolume.progress = (it.getVolumeModulation() * 1000.0f).toInt()
            actionAmountToVolume.setOnSeekBarChangeListener(this)

            view.findViewById<SeekBar>(R.id.seekBarTouchToPitchBend).let {  sb ->
                sb.progress = (it.getPitchBendAmount() * 100.0f).toInt()
                sb.setOnSeekBarChangeListener(this)
            }

            view.findViewById<RadioGroup>(R.id.radioGroupPitchBendOrientation).let { rg ->
                if (it.horizontalToActionB) {
                    rg.check(R.id.radioButtonHorizontalToActionB)
                } else {
                    rg.check(R.id.radioButtonHorizontalToActionA)
                }
                rg.setOnCheckedChangeListener {
                        _, checkedId ->
                    when (checkedId) {
                        R.id.radioButtonHorizontalToActionB -> {
                            it.horizontalToActionB = true
                        }

                        R.id.radioButtonHorizontalToActionA -> {
                            it.horizontalToActionB = false
                        }
                    }
                }
            }
        }
    }

    companion object {

    }

    override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
        when(p0?.id){
            R.id.seekBarAttack -> {
                synth?.setAttack(p0.progress.toFloat() / 1000.0f * 2.0f)
            }
            R.id.seekBarDecay -> {
                synth?.setDecay(p0.progress.toFloat() / 1000.0f * 2.0f)
            }
            R.id.seekBarSustain -> {
                synth?.setSustain(p0.progress.toFloat() / 1000.0f)
            }
            R.id.seekBarRelease -> {
                synth?.setRelease(p0.progress.toFloat() / 1000.0f * 2.0f)
            }
            R.id.seekBarCutoff -> {
                synth?.setInitialCutoff( AudioUtils.NoteToFreq(p0.progress.toFloat()/1000.0f*105.0f-39.0f))
                synth?.setCutoff(AudioUtils.NoteToFreq(p0.progress.toFloat()/1000.0f*105.0f-39.0f))
            }
            R.id.seekBarResonance -> {
                synth?.setResonance(p0.progress.toFloat()/1000.0f)
            }
            R.id.seekBarCutoffModulation -> {
                synth?.setActionAmountToFilter(p0.progress.toFloat()/1000.0f*50.0f)
            }
            R.id.seekBarTouchToVolume -> {
                synth?.setVolumeModulation(p0.progress.toFloat() / 1000.0f)
            }
            R.id.seekBarTouchToPitchBend -> {
                synth?.setPitchBendAmount(p0.progress.toFloat() / 100.0f)
            }
        }
    }

    override fun onStartTrackingTouch(p0: SeekBar?) {
    }

    override fun onStopTrackingTouch(p0: SeekBar?) {
    }
}