package ch.sr35.touchsamplesynth.fragments

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.SwitchCompat
import androidx.fragment.app.Fragment
import ch.sr35.touchsamplesynth.R
import ch.sr35.touchsamplesynth.TouchSampleSynthMain
import ch.sr35.touchsamplesynth.audio.instruments.SamplerI
import ch.sr35.touchsamplesynth.model.SceneP
import ch.sr35.touchsamplesynth.views.WaveDisplay
import ch.sr35.touchsamplesynth.views.WaveDisplayChangeListener
import com.developer.filepicker.model.DialogConfigs
import com.developer.filepicker.model.DialogProperties
import com.developer.filepicker.view.FilePickerDialog
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.google.gson.JsonParseException
import java.io.File


class SamplerFragment(s: SamplerI) : Fragment(), WaveDisplayChangeListener {
    private val synth=s
    private var modeSwitch: SwitchCompat?=null
    private var waveViewer: WaveDisplay?=null
    private var buttonLoadSample: Button?=null
    //private lateinit var fileChooserResultLauncher: ActivityResultLauncher<Intent>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
        /*
        fileChooserResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { r ->
            if (r != null && r.resultCode == Activity.RESULT_OK) {

                    synth.setSampleFile(r.data!!.data!!)
                    Log.i("TouchSampleSynth","loading sample for %s".format(synth.name))
                    requireActivity().contentResolver.takePersistableUriPermission(
                        r.data!!.data!!,
                        Intent.FLAG_GRANT_READ_URI_PERMISSION
                    )
                    waveViewer?.waveViewBuffer=synth.waveformImg
                    waveViewer?.startMarkerPosition = (synth.getSampleStartIndex().toFloat()/synth.sample.size.toFloat())
                    waveViewer?.endMarkerPosition = (synth.getSampleEndIndex().toFloat()/synth.sample.size.toFloat())
                    waveViewer?.loopStartMarkerPosition = (synth.getLoopStartIndex().toFloat()/synth.sample.size.toFloat())
                    waveViewer?.loopEndMarkerPosition = (synth.getLoopEndIndex().toFloat()/synth.sample.size.toFloat())
            }
        }*/
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
        waveViewer?.waveViewBuffer = synth.waveformImg
        waveViewer?.startMarkerPosition = (synth.getSampleStartIndex().toFloat()/synth.sample.size.toFloat())
        waveViewer?.endMarkerPosition = (synth.getSampleEndIndex().toFloat()/synth.sample.size.toFloat())
        waveViewer?.loopStartMarkerPosition = (synth.getLoopStartIndex().toFloat()/synth.sample.size.toFloat())
        waveViewer?.loopEndMarkerPosition = (synth.getLoopEndIndex().toFloat()/synth.sample.size.toFloat())
        waveViewer?.invalidate()
        modeSwitch?.isChecked = synth.getMode().toInt() == 1
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
            val dialogProperties = DialogProperties()
            dialogProperties.selection_mode = DialogConfigs.SINGLE_MODE
            dialogProperties.selection_type = DialogConfigs.FILE_SELECT
            dialogProperties.root = File(DialogConfigs.DEFAULT_DIR)
            dialogProperties.error_dir = File(DialogConfigs.DEFAULT_DIR)
            dialogProperties.offset = File(DialogConfigs.DEFAULT_DIR)
            dialogProperties.extensions= arrayOf("wav")
            dialogProperties.show_hidden_files=false
            val filePickerDialog= FilePickerDialog(context,dialogProperties)
            filePickerDialog.setTitle(R.string.sampler_select_sample)
            filePickerDialog.setDialogSelectionListener { it1 ->
                val sampleUri = Uri.fromFile(File(it1[0]))
                synth.setSampleFile(sampleUri)
                Log.i("TouchSampleSynth","loading sample for %s".format(synth.name))
                //requireActivity().contentResolver.takePersistableUriPermission(
                //    sampleUri,
                //    Intent.FLAG_GRANT_READ_URI_PERMISSION
                //)
                waveViewer?.waveViewBuffer=synth.waveformImg
                waveViewer?.startMarkerPosition = (synth.getSampleStartIndex().toFloat()/synth.sample.size.toFloat())
                waveViewer?.endMarkerPosition = (synth.getSampleEndIndex().toFloat()/synth.sample.size.toFloat())
                waveViewer?.loopStartMarkerPosition = (synth.getLoopStartIndex().toFloat()/synth.sample.size.toFloat())
                waveViewer?.loopEndMarkerPosition = (synth.getLoopEndIndex().toFloat()/synth.sample.size.toFloat())
                waveViewer?.invalidate()
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
}