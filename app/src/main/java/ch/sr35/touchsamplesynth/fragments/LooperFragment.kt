
package ch.sr35.touchsamplesynth.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import ch.sr35.touchsamplesynth.R
import ch.sr35.touchsamplesynth.audio.instruments.LooperI

class LooperFragment() : Fragment(),  SeekBar.OnSeekBarChangeListener {

    constructor(s: LooperI): this()
    {
        synth = s
    }

    private var synth: LooperI?=null
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
        return inflater.inflate(R.layout.fragment_looper, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        synth?.let {
            val recordGain = view.findViewById<SeekBar>(R.id.seekBarRecordGain)
            recordGain.progress = ((it.getRecordGain()-1.0f)/15.0f*1000.0f).toInt()
            recordGain.setOnSeekBarChangeListener(this)
        }
    }

    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
        synth?.setRecordGain(progress.toFloat()/1000.0f*15.0f+1.0f)
    }

    override fun onStartTrackingTouch(seekBar: SeekBar?) {

    }

    override fun onStopTrackingTouch(seekBar: SeekBar?) {

    }
}
