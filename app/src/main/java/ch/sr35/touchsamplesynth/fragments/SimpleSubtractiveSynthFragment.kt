package ch.sr35.touchsamplesynth.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioGroup
import android.widget.SeekBar
import androidx.appcompat.widget.SwitchCompat
import ch.sr35.touchsamplesynth.R
import ch.sr35.touchsamplesynth.audio.AudioUtils
import ch.sr35.touchsamplesynth.audio.instruments.SimpleSubtractiveSynthI
import ch.sr35.touchsamplesynth.views.Knob


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
            var attack = view.findViewById<SeekBar>(R.id.seekBarVolAttack)
            attack.setOnSeekBarChangeListener(this)
            attack.progress = (it.getVolumeAttack() / 2.0f * 1000.0f).toInt()

            var decay = view.findViewById<SeekBar>(R.id.seekBarVolDecay)
            decay.setOnSeekBarChangeListener(this)
            decay.progress = (it.getVolumeDecay() / 2.0f * 1000.0f).toInt()

            var sustain = view.findViewById<SeekBar>(R.id.seekBarVolSustain)
            sustain.setOnSeekBarChangeListener(this)
            sustain.progress = (it.getVolumeSustain() * 1000.0f).toInt()

            var release = view.findViewById<SeekBar>(R.id.seekBarVolRelease)
            release.setOnSeekBarChangeListener(this)
            release.progress = (it.getVolumeRelease() / 2.0f * 1000.0f).toInt()

            attack = view.findViewById<SeekBar>(R.id.seekBarFilterAttack)
            attack.setOnSeekBarChangeListener(this)
            attack.progress = (it.getFilterAttack() / 2.0f * 1000.0f).toInt()

            decay = view.findViewById<SeekBar>(R.id.seekBarFilterDecay)
            decay.setOnSeekBarChangeListener(this)
            decay.progress = (it.getFilterDecay() / 2.0f * 1000.0f).toInt()

            sustain = view.findViewById<SeekBar>(R.id.seekBarFilterSustain)
            sustain.setOnSeekBarChangeListener(this)
            sustain.progress = (it.getFilterSustain() * 1000.0f).toInt()

            release = view.findViewById<SeekBar>(R.id.seekBarFilterRelease)
            release.setOnSeekBarChangeListener(this)
            release.progress = (it.getFilterRelease() / 2.0f * 1000.0f).toInt()

            view.findViewById<SeekBar>(R.id.seekBarEnvelopeAmount).let {
                sb ->
                sb.progress = (it.getFilterEnvelopeLevel()).toInt()
                sb.setOnSeekBarChangeListener(this)
            }

            val cutoff = view.findViewById<SeekBar>(R.id.seekBarCutoff)
            cutoff.setOnSeekBarChangeListener(this)
            cutoff.progress =
                ((AudioUtils.FreqToNote(it.getInitialCutoff()) + 39.0f) * 1000.0 / 105.0f).toInt()


            val resonance = view.findViewById<SeekBar>(R.id.seekBarResonance)
            resonance.setOnSeekBarChangeListener(this)
            resonance.progress = (it.getResonance() * 1000.0f).toInt()

            view.findViewById<SwitchCompat>(R.id.switchOsc1Waveform).let { sw ->
                sw.isChecked =  it.getOsc1Type().toInt()==1
                sw.setOnCheckedChangeListener { _, isChecked ->
                   if (isChecked)
                   {
                       it.setOsc1Type(1)
                   }
                    else
                   {
                       it.setOsc1Type(0)
                   }
                }
            }

            view.findViewById<SwitchCompat>(R.id.switchOsc2Waveform).let { sw ->
                sw.isChecked =  it.getOsc2Type().toInt()==1
                sw.setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked)
                    {
                        it.setOsc2Type(1)
                    }
                    else
                    {
                        it.setOsc2Type(0)
                    }
                }
            }

            val actionAmount = view.findViewById<SeekBar>(R.id.seekBarCutoffModulation)
            actionAmount.setOnSeekBarChangeListener(this)
            actionAmount.progress = (it.getActionAmountToFilter() / 50.0f * 1000.0f).toInt()


            val actionAmountToVolume = view.findViewById<SeekBar>(R.id.seekBarTouchToVolume)
            actionAmountToVolume.setOnSeekBarChangeListener(this)
            actionAmountToVolume.progress = (it.getVolumeModulation() * 1000.0f).toInt()


            view.findViewById<SeekBar>(R.id.seekBarTouchToPitchBend).let {  sb ->
                sb.progress = (it.getPitchBendAmount() * 100.0f).toInt()
                sb.setOnSeekBarChangeListener(this)
            }

            view.findViewById<Knob>(R.id.osc1PWSymmetry).let { sb ->
                sb.setOnSeekBarChangeListener(this)
                sb.progress = (it.getOsc1PulseWidth() * 1000.0f).toInt()
            }

            view.findViewById<Knob>(R.id.osc2PWSymmetry).let { sb ->
                sb.setOnSeekBarChangeListener(this)
                sb.progress = (it.getOsc1PulseWidth() * 1000.0f).toInt()
            }


            view.findViewById<Knob>(R.id.osc2Octave).let { sb ->
                sb.setOnSeekBarChangeListener(this)
                sb.progress = it.getOsc2Octave().toInt()
            }

            view.findViewById<Knob>(R.id.osc2Volume).let { sb ->
                sb.setOnSeekBarChangeListener(this)
                sb.progress = (it.getOsc2Volume() * 1000.0f).toInt()
            }

            view.findViewById<Knob>(R.id.osc2Detune).let {
                sb ->
                sb.setOnSeekBarChangeListener(this)
                sb.progress = (it.getOsc2Detune() * 100.0f).toInt()
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
            R.id.seekBarVolAttack -> {
                synth?.setVolumeAttack(p0.progress.toFloat() / 1000.0f * 2.0f)
            }
            R.id.seekBarVolDecay -> {
                synth?.setVolumeDecay(p0.progress.toFloat() / 1000.0f * 2.0f)
            }
            R.id.seekBarVolSustain -> {
                synth?.setVolumeSustain(p0.progress.toFloat() / 1000.0f)
            }
            R.id.seekBarVolRelease -> {
                synth?.setVolumeRelease(p0.progress.toFloat() / 1000.0f * 2.0f)
            }
            R.id.seekBarFilterAttack -> {
                synth?.setFilterAttack(p0.progress.toFloat() / 1000.0f * 2.0f)
            }
            R.id.seekBarFilterDecay -> {
                synth?.setFilterDecay(p0.progress.toFloat() / 1000.0f * 2.0f)
            }
            R.id.seekBarFilterSustain -> {
                synth?.setFilterSustain(p0.progress.toFloat() / 1000.0f)
            }
            R.id.seekBarFilterRelease -> {
                synth?.setFilterRelease(p0.progress.toFloat() / 1000.0f * 2.0f)
            }
            R.id.seekBarEnvelopeAmount -> {
                synth?.setFilterEnvelopeLevel(p0.progress.toFloat())
            }
            R.id.osc2Octave -> {
                synth?.setOsc2Octave(p0.progress.toByte())
            }
            R.id.osc2Volume ->
            {
                synth?.setOsc2Volume(p0.progress.toFloat()/1000.0f)
            }
            R.id.osc2Detune ->
            {
                synth?.setOsc2Detune(p0.progress.toFloat()/100.0f)
            }
            R.id.osc2PWSymmetry ->
            {
                synth?.setOsc2PulseWidth(p0.progress.toFloat()/1000.0f)
            }
            R.id.osc1PWSymmetry ->
            {
                synth?.setOsc1PulseWidth(p0.progress.toFloat()/1000.0f)
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