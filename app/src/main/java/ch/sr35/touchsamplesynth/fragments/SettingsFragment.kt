package ch.sr35.touchsamplesynth.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import ch.sr35.touchsamplesynth.R
import ch.sr35.touchsamplesynth.audio.AudioEngineK
import ch.sr35.touchsamplesynth.BuildConfig
import ch.sr35.touchsamplesynth.TouchSampleSynthMain
import ch.sr35.touchsamplesynth.views.TouchElement
import com.google.android.material.snackbar.Snackbar


class SettingsFragment : Fragment(), AdapterView.OnItemSelectedListener {

    private var framesPerDataCallbackIdx = -1
    private var bufferSizeInFramesIdx = -1
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
        return inflater.inflate(R.layout.fragment_settings, container, false)


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val audioEngine = AudioEngineK()
        super.onViewCreated(view, savedInstanceState)
        val framesPerDataCallback = view.findViewById<Spinner>(R.id.spinnerFramesPerDataCallback)
        ArrayAdapter.createFromResource(
            view.context,
            R.array.framesPerDataCallbackValues,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears.
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner.
            framesPerDataCallback.adapter = adapter
        }
        val fpdcVals = resources.getStringArray(R.array.framesPerDataCallbackValues)
        val currentFpdc = audioEngine.getFramesPerDataCallback()
        var idx = 0
        for (fpdc in fpdcVals)
        {
            if (fpdcVals[idx].toInt() == currentFpdc)
            {
                break
            }
            idx++
        }
        if (idx < fpdcVals.size)
        {
            framesPerDataCallback.setSelection(idx)
            framesPerDataCallbackIdx = idx
        }
        framesPerDataCallback.onItemSelectedListener = this

        val bufferCapacityInFrames = view.findViewById<Spinner>(R.id.spinnerBufferCapacityInFrames)
        ArrayAdapter.createFromResource(
            view.context,
            R.array.bufferCapacityInFramesValues,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears.
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner.
            bufferCapacityInFrames.adapter = adapter
        }
        val bcifVals = resources.getStringArray(R.array.bufferCapacityInFramesValues)
        val currentBcif = audioEngine.getBufferCapacityInFrames()
        idx = 0
        for (bcif in bcifVals)
        {
            if (bcifVals[idx].toInt() == currentBcif)
            {
                break
            }
            idx++
        }
        if (idx < bcifVals.size)
        {
            bufferCapacityInFrames.setSelection(idx)
            bufferSizeInFramesIdx = idx
        }
        bufferCapacityInFrames.onItemSelectedListener = this

        val spinnerTouchElementStyle = view.findViewById<Spinner>(R.id.spinnerTouchElementsDisplay)
        ArrayAdapter.createFromResource(view.context,
            R.array.touchElementDisplayStyle,
            android.R.layout.simple_spinner_item
            ).also {
                arrayAdapter -> arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinnerTouchElementStyle.adapter = arrayAdapter
            spinnerTouchElementStyle.setSelection(0,false)
            spinnerTouchElementStyle.onItemSelectedListener = this
        }


        val textViewAbout = view.findViewById<TextView>(R.id.settingTextViewAbout)
        val aboutString="Touch Sample Synth Version %s".format(BuildConfig.VERSION_NAME)
        textViewAbout.text = aboutString

    }

    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        val audioEngine = AudioEngineK()

        if (p0 != null && p0.id == R.id.spinnerFramesPerDataCallback)
        {
            val framesPerDataCallback = resources.getStringArray(R.array.framesPerDataCallbackValues)
            if (audioEngine.setFramesPerDataCallback(framesPerDataCallback[p2].toInt())!=0)
            {
                this.view?.let { val sb = Snackbar.make(it,resources.getText(R.string.audioBuffersErrorMessage),5000)
                sb.show()}
                p0.setSelection(framesPerDataCallbackIdx)
            }
            else
            {
                framesPerDataCallbackIdx = p2
            }

        }
        else if (p0 != null && p0.id == R.id.spinnerBufferCapacityInFrames)
        {
            val bufferCapacityInFrames = resources.getStringArray(R.array.bufferCapacityInFramesValues)
            if (audioEngine.setBufferCapacityInFrames(bufferCapacityInFrames[p2].toInt())!=0)
            {
                this.view?.let { val sb = Snackbar.make(it,resources.getText(R.string.audioBuffersErrorMessage),5000)
                sb.show()}
                p0.setSelection(bufferSizeInFramesIdx)
            }
            else
            {
                bufferSizeInFramesIdx = p2
            }
        }
        else if (p0 != null && p0.id == R.id.spinnerTouchElementsDisplay)
        {
            (context as TouchSampleSynthMain).touchElements.forEach {
                if (p2 == 0)
                {
                    it.setDefaultMode(TouchElement.TouchElementState.PLAYING)
                }
                else
                {
                    it.setDefaultMode(TouchElement.TouchElementState.PLAYING_VERBOSE)
                }
            }
            if (p2 == 0)
            {
                (context as TouchSampleSynthMain).touchElementsDisplayMode=TouchElement.TouchElementState.PLAYING
            }
            else
            {
                (context as TouchSampleSynthMain).touchElementsDisplayMode=TouchElement.TouchElementState.PLAYING_VERBOSE
            }
        }
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {

    }
}