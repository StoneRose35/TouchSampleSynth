package ch.sr35.touchsamplesynth.fragments


import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.SeekBar
import androidx.appcompat.widget.SwitchCompat
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import ch.sr35.touchsamplesynth.R
import ch.sr35.touchsamplesynth.TouchSampleSynthMain
import ch.sr35.touchsamplesynth.audio.instruments.SamplerI
import ch.sr35.touchsamplesynth.graphics.Converter
import ch.sr35.touchsamplesynth.views.WaitAnimation
import ch.sr35.touchsamplesynth.views.WaveDisplay
import ch.sr35.touchsamplesynth.views.WaveDisplayChangeListener
import com.developer.filepicker.model.DialogConfigs
import com.developer.filepicker.model.DialogProperties
import com.developer.filepicker.view.FilePickerDialog
import java.io.File
import java.util.concurrent.Executors
import kotlin.experimental.and
import kotlin.experimental.or


class SamplerFragment(s: SamplerI) : Fragment(), WaveDisplayChangeListener,SeekBar.OnSeekBarChangeListener {
    private val synth=s
    private var modeLoopedSwitch: SwitchCompat?=null
    private var modeTriggeredSwitch: SwitchCompat?=null
    private var waveViewer: WaveDisplay?=null
    private var buttonLoadSample: Button?=null
    private var sliderVolModulation: SeekBar?=null
    //private lateinit var fileChooserResultLauncher: ActivityResultLauncher<Intent>
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
        return inflater.inflate(R.layout.fragment_sampler, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        modeLoopedSwitch=view.findViewById(R.id.sampler_switch_mode_looped)
        modeTriggeredSwitch=view.findViewById(R.id.sampler_switch_mode_triggered)
        waveViewer=view.findViewById(R.id.sampler_wave_viewer)
        buttonLoadSample=view.findViewById(R.id.sampler_button_load_sample)
        sliderVolModulation=view.findViewById(R.id.seekBarTouchToVolume)
        waveViewer?.onChangeListener=this
        waveViewer?.waveViewBuffer = synth.waveformImg
        waveViewer?.startMarkerPosition = (synth.getSampleStartIndex().toFloat()/synth.sample.size.toFloat())
        waveViewer?.endMarkerPosition = (synth.getSampleEndIndex().toFloat()/synth.sample.size.toFloat())
        waveViewer?.loopStartMarkerPosition = (synth.getLoopStartIndex().toFloat()/synth.sample.size.toFloat())
        waveViewer?.loopEndMarkerPosition = (synth.getLoopEndIndex().toFloat()/synth.sample.size.toFloat())
        waveViewer?.invalidate()
        modeLoopedSwitch?.isChecked = (synth.getMode().toInt() and 0x1) != 0
        modeLoopedSwitch?.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked)
            {
                var mode = synth.getMode()
                mode = mode.or(1)
                synth.setMode(mode)
            }
            else
            {
                var mode = synth.getMode()
                mode = mode.and(-2)
                synth.setMode(mode)
            }
        }
        modeTriggeredSwitch?.isChecked = (synth.getMode().toInt() and 0x2) != 0
        modeTriggeredSwitch?.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked)
            {
                var mode = synth.getMode()
                mode = mode.or(2)
                synth.setMode(mode)
            }
            else
            {
                var mode = synth.getMode()
                mode = mode.and(-3)
                synth.setMode(mode)
            }
        }
        sliderVolModulation?.setOnSeekBarChangeListener(this)
        buttonLoadSample?.setOnClickListener {
            val dialogProperties = DialogProperties()
            dialogProperties.selection_mode = DialogConfigs.SINGLE_MODE
            dialogProperties.selection_type = DialogConfigs.FILE_SELECT
            dialogProperties.root = context?.filesDir
            dialogProperties.error_dir = File(DialogConfigs.DEFAULT_DIR)
            dialogProperties.offset = File(DialogConfigs.DEFAULT_DIR)
            dialogProperties.extensions= arrayOf("wav")
            dialogProperties.show_hidden_files=false
            val filePickerDialog= FilePickerDialog(context,dialogProperties)
            filePickerDialog.setTitle(R.string.sampler_select_sample)
            filePickerDialog.setDialogSelectionListener { it1 ->
                val waitAnimation= context?.let { it2 -> WaitAnimation(it2, null) }
                val constraintLayout = ConstraintLayout.LayoutParams(Converter.toPx(64), Converter.toPx(64))
                constraintLayout.topToTop= ConstraintLayout.LayoutParams.PARENT_ID
                constraintLayout.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
                constraintLayout.startToStart  = ConstraintLayout.LayoutParams.PARENT_ID
                constraintLayout.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
                waitAnimation?.layoutParams = constraintLayout
                val mainLayout = (context as TouchSampleSynthMain).findViewById<ConstraintLayout>(R.id.mainLayout)
                (mainLayout as ViewGroup).addView(waitAnimation)
                waitAnimation?.startAnimation()
                val sampleUri = Uri.fromFile(File(it1[0]))
                val executor=Executors.newSingleThreadExecutor()
                val handler= Handler(Looper.getMainLooper())
                executor.execute {
                    synth.setSampleFile(sampleUri)
                    Log.i("TouchSampleSynth","loading sample for %s".format(synth.name))
                    handler.post {
                        waveViewer?.waveViewBuffer = synth.waveformImg
                        waveViewer?.startMarkerPosition =
                            (synth.getSampleStartIndex().toFloat() / synth.sample.size.toFloat())
                        waveViewer?.endMarkerPosition =
                            (synth.getSampleEndIndex().toFloat() / synth.sample.size.toFloat())
                        waveViewer?.loopStartMarkerPosition =
                            (synth.getLoopStartIndex().toFloat() / synth.sample.size.toFloat())
                        waveViewer?.loopEndMarkerPosition =
                            (synth.getLoopEndIndex().toFloat() / synth.sample.size.toFloat())
                        waveViewer?.invalidate()
                        waitAnimation?.stopAnimation()
                        (mainLayout as ViewGroup).removeView(waitAnimation)
                    }
                }

            }
            filePickerDialog.show()
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

    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
        when(seekBar?.id)
        {
            R.id.seekBarTouchToVolume ->
            {
                synth.setVolumeModulation(seekBar.progress.toFloat() / 1000.0f)
            }
        }
    }

    override fun onStartTrackingTouch(seekBar: SeekBar?) {
    }

    override fun onStopTrackingTouch(seekBar: SeekBar?) {
    }
}