package ch.sr35.touchsamplesynth.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.SwitchCompat
import androidx.fragment.app.Fragment
import ch.sr35.touchsamplesynth.R
import ch.sr35.touchsamplesynth.audio.instruments.SamplerI
import ch.sr35.touchsamplesynth.views.WaveDisplay
import ch.sr35.touchsamplesynth.views.WaveDisplayChangeListener

class SamplerFragment(s: SamplerI) : Fragment(), WaveDisplayChangeListener {
    private val synth=s
    private var modeSwitch: SwitchCompat?=null
    private var waveViewer: WaveDisplay?=null
    private var buttonLoadSample: Button?=null
    private lateinit var fileChooserResultLauncher: ActivityResultLauncher<Intent>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
        fileChooserResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { r ->
            if (r != null) {
                run {
                    r.data?.data?.path?.let { synth.setSampleFile(it) }
                    waveViewer?.setSampleData(synth.sample)
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sampler, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        modeSwitch=view.findViewById(R.id.sampler_switch_mode)
        waveViewer=view.findViewById(R.id.sampler_wave_viewer)
        buttonLoadSample=view.findViewById(R.id.sampler_button_load_sample)
        waveViewer?.onChangeListener=this
        modeSwitch?.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked)
            {
                synth.setMode(1)
            }
            else
            {
                synth.setMode(0)
            }
        }
        buttonLoadSample?.setOnClickListener {
            val intent=Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION
                type = "audio/wav"
            }
         fileChooserResultLauncher.launch(intent)
        }
    }

    companion object {

    }

    override fun sampleStartMarkerChanged(v: Float) {
        synth.setSampleStartIndex((v*synth.sample.size).toInt())
    }

    override fun sampleEndMarkerChanged(v: Float) {
        synth.setSampleEndIndex((v*synth.sample.size).toInt())
    }

    override fun loopStartMarkerChanged(v: Float) {
        synth.setLoopStartIndex((v*synth.sample.size).toInt())
    }

    override fun loopEndMarkerChanged(v: Float) {
        synth.setLoopEndIndex((v*synth.sample.size).toInt())
    }
}