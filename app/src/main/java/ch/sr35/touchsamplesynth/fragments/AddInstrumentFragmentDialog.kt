package ch.sr35.touchsamplesynth.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ListView
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.RecyclerView
import ch.sr35.touchsamplesynth.R
import ch.sr35.touchsamplesynth.TouchSampleSynthMain
import ch.sr35.touchsamplesynth.audio.InstrumentI
import ch.sr35.touchsamplesynth.audio.instruments.SamplerI
import ch.sr35.touchsamplesynth.audio.instruments.SimpleSubtractiveSynthI
import ch.sr35.touchsamplesynth.audio.instruments.SineMonoSynthI
import ch.sr35.touchsamplesynth.dialogs.SoundGeneratorListAdapter


/**
 * A simple [Fragment] subclass.
 * create an instance of this fragment.
 */
class AddInstrumentFragmentDialog(private val generatorsList: ListView) : DialogFragment() {

    private var soundGenerators = ArrayList<InstrumentI>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_instrument_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (soundGenerators.isEmpty()) {
            this.context?.let {
                soundGenerators.add(SimpleSubtractiveSynthI(it,""))
                soundGenerators.add(SineMonoSynthI(it, ""))
                soundGenerators.add(SamplerI(it, ""))
            }
        }

        val instrumentsList = view.findViewById<RecyclerView>(R.id.add_instr_instr_list)
        val instrumentListAdapter = SoundGeneratorListAdapter(soundGenerators,null)
        instrumentsList.adapter = instrumentListAdapter
        val buttonOk=view.findViewById<Button>(R.id.add_instr_button_ok)
        buttonOk.setOnClickListener {
            if ((instrumentsList?.adapter as SoundGeneratorListAdapter).checkedPosition > -1) {
                context?.let { it1 ->
                    var newInstrumentI: InstrumentI?=null
                    if (soundGenerators[(instrumentsList?.adapter as SoundGeneratorListAdapter).checkedPosition] is SineMonoSynthI)
                    {
                        newInstrumentI = SineMonoSynthI(requireContext(),"basic")
                    }
                    else if (soundGenerators[(instrumentsList?.adapter as SoundGeneratorListAdapter).checkedPosition] is SimpleSubtractiveSynthI)
                    {
                        newInstrumentI = SimpleSubtractiveSynthI(requireContext(),"basic")
                    }
                    else if (soundGenerators[(instrumentsList?.adapter as SoundGeneratorListAdapter).checkedPosition] is SamplerI)
                    {
                        newInstrumentI = SamplerI(requireContext(),"basic")
                    }
                    newInstrumentI?.let {
                        newInstrumentI.generateVoices(1)
                        (context as TouchSampleSynthMain).soundGenerators.add(it)
                    }
                    generatorsList.invalidateViews()
                }
            }

            this.dismiss()
        }
        val buttonCancel = view.findViewById<Button>(R.id.add_instr_button_cancel)
        buttonCancel.setOnClickListener {
            this.dismiss()
        }
    }

    companion object {

    }
}