package ch.sr35.touchsamplesynth.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import ch.sr35.touchsamplesynth.R
import ch.sr35.touchsamplesynth.audio.AudioEngineK



class SettingsFragment : Fragment(), AdapterView.OnItemSelectedListener {


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


    }

    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        val audioEngine = AudioEngineK()

        if (p1 != null && p1.id == R.id.spinnerFramesPerDataCallback)
        {
            val framesPerDataCallback = resources.getStringArray(R.array.framesPerDataCallbackValues)
            if (audioEngine.setFramesPerDataCallback(framesPerDataCallback[p2].toInt())==0)
            {
                // show a "is valid" sign somewhere
            }
            else
            {

            }
        }
        else if (p1 != null && p1.id == R.id.spinnerFramesPerDataCallback)
        {
            val bufferCapacityInFrames = resources.getStringArray(R.array.bufferCapacityInFramesValues)
            if (audioEngine.setBufferCapacityInFrames(bufferCapacityInFrames[p2].toInt())==0)
            {

            }
            else
            {

            }
        }
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {

    }
}