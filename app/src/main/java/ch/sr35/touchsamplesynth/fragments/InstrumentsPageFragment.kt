package ch.sr35.touchsamplesynth.fragments

import android.app.AlertDialog
import android.content.Context
import android.database.DataSetObserver
import android.media.midi.MidiDeviceInfo
import android.media.midi.MidiManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.ListAdapter
import android.widget.ListView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import ch.sr35.touchsamplesynth.R
import ch.sr35.touchsamplesynth.TouchSampleSynthMain
import ch.sr35.touchsamplesynth.audio.AudioEngineK
import ch.sr35.touchsamplesynth.audio.instruments.SamplerI
import ch.sr35.touchsamplesynth.audio.instruments.SimpleSubtractiveSynthI
import ch.sr35.touchsamplesynth.audio.instruments.SineMonoSynthI
import java.lang.NumberFormatException


/**
 * A simple [Fragment] subclass.

 */
class InstrumentsPageFragment : Fragment(), ListAdapter,
    AdapterView.OnItemClickListener {

    var selectedInstrument: Int=-1


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val instrumentsList = view.findViewById<ListView>(R.id.instruments_page_instruments_list)
        instrumentsList.adapter = this
        instrumentsList.onItemClickListener = this

        val midiManager = context?.getSystemService(Context.MIDI_SERVICE) as MidiManager
        val midiDevices = midiManager.devices
        var midiDev: MidiDeviceInfo?
        for (md in midiDevices)
        {
            if (md.outputPortCount > 0)
            {
                midiDev = md
                midiManager.openDevice(midiDev,{
                    val audioEngine=AudioEngineK()
                    audioEngine.openMidiDevice(it,0)
                },null)
            }
        }
        //val midiDev = midiDevices.stream().filter { d -> d.outputPortCount > 0 }.findFirst().orElse(null)

        view.findViewById<EditText>(R.id.instruments_page_nr_voices).text.clear()
        view.findViewById<EditText>(R.id.instruments_page_instr_name).text.clear()
        if (selectedInstrument == -1) {
            if (instrumentsList.adapter.count > 0) {
                onItemClick(instrumentsList, null, 0, 0)
            }
        }
        else
        {
            val oldSelection=selectedInstrument
            selectedInstrument=-1
            if (instrumentsList.adapter.count > 0 && oldSelection < instrumentsList.adapter.count)  {
                onItemClick(instrumentsList, null, oldSelection, 0)
            }
        }

        val addButtton = view.findViewById<Button>(R.id.instruments_page_add)
        addButtton.setOnClickListener {
            val addInstrumentDlg = AddInstrumentFragmentDialog(instrumentsList)

            (context as TouchSampleSynthMain).supportFragmentManager
                .beginTransaction()
                .add(addInstrumentDlg,null)
                .commit()
            addInstrumentDlg.dialog?.window?.setLayout(300, 600)
        }

        val deleteButton = view.findViewById<Button>(R.id.instruments_page_delete)
        deleteButton.setOnClickListener {

            if (selectedInstrument >=0 && !(context as TouchSampleSynthMain).touchElements.stream().anyMatch {
                    it.soundGenerator == (context as TouchSampleSynthMain).soundGenerators[selectedInstrument]
                })
            {
                val alertDlgBuilder = AlertDialog.Builder(context as TouchSampleSynthMain)
                    .setMessage((context as TouchSampleSynthMain).getString(R.string.alert_dialog_really_delete))
                    .setPositiveButton((context as TouchSampleSynthMain).getString(R.string.yes)) { _, _ ->
                        (context as TouchSampleSynthMain).soundGenerators[selectedInstrument].voices.forEach { v -> v.detachFromAudioEngine() }
                        (context as TouchSampleSynthMain).soundGenerators.removeAt(selectedInstrument)
                        selectedInstrument = -1
                        instrumentsList.invalidateViews()
                    }
                    .setNegativeButton((context as TouchSampleSynthMain).getString(R.string.no)) { _, _ -> }
                val alertDlg = alertDlgBuilder.create()
                alertDlg.show()
            }
        }

        view.findViewById<EditText>(R.id.instruments_page_instr_name).setOnEditorActionListener { textView, i, _ ->
            if (i == EditorInfo.IME_ACTION_DONE) {
                if (selectedInstrument >= 0 && selectedInstrument < (context as TouchSampleSynthMain).soundGenerators.size) {
                    (context as TouchSampleSynthMain).soundGenerators[selectedInstrument].name =
                        textView.text.toString()
                    ((context as Context).getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(
                        textView.windowToken,
                        0
                    )
                }
                return@setOnEditorActionListener true
            }
            return@setOnEditorActionListener false
        }

        view.findViewById<EditText>(R.id.instruments_page_nr_voices).setOnEditorActionListener { textView, i, _ ->
            if (i == EditorInfo.IME_ACTION_DONE)
            {
                if (selectedInstrument >= 0 && selectedInstrument < (context as TouchSampleSynthMain).soundGenerators.size) {
                    var voicesInt: Int
                    try {
                        voicesInt = Integer.parseInt(textView.text.toString())
                    } catch (e: NumberFormatException) {
                        voicesInt = -1
                    }
                    if (voicesInt in 1..16) {
                        // check that less voices are assigned than requested
                        val nAssignedSoundGenerators =
                            (context as TouchSampleSynthMain).touchElements.stream()
                                .map { te -> te.soundGenerator }
                                .filter { sg ->
                                    sg == (context as TouchSampleSynthMain).soundGenerators[selectedInstrument]
                                }
                                .count()
                        if (nAssignedSoundGenerators <= voicesInt) {
                            if (voicesInt < (context as TouchSampleSynthMain).soundGenerators[selectedInstrument].voicesCount()) {
                                // remove voices
                                val nVoicesToRemove =
                                    (context as TouchSampleSynthMain).soundGenerators[selectedInstrument].voicesCount() - voicesInt

                                for (c in 0 until nVoicesToRemove) {
                                    (context as TouchSampleSynthMain).soundGenerators[selectedInstrument].voices[0]
                                        .detachFromAudioEngine()
                                    (context as TouchSampleSynthMain).soundGenerators[selectedInstrument].voices.removeAt(
                                        0
                                    )
                                }
                                var currentVoiceIndex = 0
                                (context as TouchSampleSynthMain).touchElements
                                    .stream()
                                    .filter { te -> te.soundGenerator == (context as TouchSampleSynthMain).soundGenerators[selectedInstrument] }
                                    .forEach { t -> t.voiceNr = currentVoiceIndex++ }
                            } else if (voicesInt > (context as TouchSampleSynthMain).soundGenerators[selectedInstrument].voicesCount()) {
                                val voicesToAdd =
                                    voicesInt - (context as TouchSampleSynthMain).soundGenerators[selectedInstrument].voicesCount()
                                (context as TouchSampleSynthMain).soundGenerators[selectedInstrument].generateVoices(
                                    voicesToAdd
                                )
                                // add voices
                            }
                        }
                    }
                    ((context as Context).getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(
                        textView.windowToken,
                        0
                    )
                }
                return@setOnEditorActionListener true
            }
            return@setOnEditorActionListener false
        }

    }


    override fun onPause() {
        super.onPause()
        val audioEngine = AudioEngineK()
        audioEngine.closeMidiDevice()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_instruments_page, container, false)
    }

    companion object {

    }

    override fun registerDataSetObserver(p0: DataSetObserver?) {

    }

    override fun unregisterDataSetObserver(p0: DataSetObserver?) {

    }

    override fun getCount(): Int {
        return (context as TouchSampleSynthMain).soundGenerators.size
    }

    override fun getItem(p0: Int): Any {
        return (context as TouchSampleSynthMain).soundGenerators[p0]
    }

    override fun getItemId(p0: Int): Long {
        return 0
    }

    override fun hasStableIds(): Boolean {
        return true
    }

    override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {
        val itemView: ConstraintLayout = if (p1 is ConstraintLayout) {
            p1
        } else {
            View.inflate(context,R.layout.instrument_entry,null) as ConstraintLayout
        }
        itemView.findViewById<TextView>(R.id.instrument_entry_text).text =
            String.format("%s",
                (context as TouchSampleSynthMain).soundGenerators[p0].name)
        itemView.findViewById<ImageView>(R.id.instrument_entry_icon).setImageDrawable((context as TouchSampleSynthMain).soundGenerators[p0].getInstrumentIcon())
        itemView.findViewById<TextView>(R.id.instrument_entry_n_voices).text = (context as TouchSampleSynthMain).soundGenerators[p0].getPolyphonyDescription()
        itemView.findViewById<CheckBox>(R.id.instrument_entry_checkbox).isChecked = (selectedInstrument >= 0 && p0==selectedInstrument)
        return itemView

    }

    private fun putFragment(frag: Fragment,tag: String?)
    {
        childFragmentManager.beginTransaction().let {
            if (childFragmentManager.findFragmentById(R.id.instruments_page_content) != null)
            {
                it.replace(R.id.instruments_page_content,frag,tag)
            }
            else
            {
                it.add(R.id.instruments_page_content,frag,tag)
            }
            it.commit()
        }
    }
    override fun getItemViewType(p0: Int): Int {
        return 0
    }

    override fun getViewTypeCount(): Int {
        return 1
    }

    override fun isEmpty(): Boolean {
        return (context as TouchSampleSynthMain).soundGenerators.isEmpty()
    }

    override fun areAllItemsEnabled(): Boolean {
        return true
    }

    override fun isEnabled(p0: Int): Boolean {
        return true
    }

    fun selectInstrument(pos:Int,forceSelection: Boolean)
    {
        if(forceSelection)
        {
            selectedInstrument=-1
        }
        val instrumentsList = view?.findViewById<ListView>(R.id.instruments_page_instruments_list)
        if(instrumentsList != null && pos >= 0 && pos < (context as TouchSampleSynthMain).soundGenerators.size) {
            onItemClick(instrumentsList, null, pos, 0)
        }
        else if (instrumentsList != null && (context as TouchSampleSynthMain).soundGenerators.size==0)
        {
            childFragmentManager.beginTransaction().let {
                if (childFragmentManager.findFragmentById(R.id.instruments_page_content) != null)
                {
                    it.remove(childFragmentManager.findFragmentById(R.id.instruments_page_content)!!)
                }
                it.commit()
            }
            view?.findViewById<EditText>(R.id.instruments_page_nr_voices)?.text?.clear()
            view?.findViewById<EditText>(R.id.instruments_page_instr_name)?.text?.clear()
        }
    }

    override fun onItemClick(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        if (p2 != selectedInstrument) {
            val contentView = view?.findViewById<FrameLayout>(R.id.instruments_page_content)
            (context as TouchSampleSynthMain).soundGenerators.flatMap { sg -> sg.voices }.forEach { v -> v.setMidiMode(0) }
            (context as TouchSampleSynthMain).soundGenerators[p2].voices.forEach { v -> v.setMidiMode(4) }
            selectedInstrument = p2
            if (contentView != null) {
                if ((context as TouchSampleSynthMain).soundGenerators[p2] is SineMonoSynthI) {
                    val frag =
                        SineMonoSynthFragment((context as TouchSampleSynthMain).soundGenerators[p2] as SineMonoSynthI)
                    if (p1 != null) {
                        putFragment(
                            frag,
                            (p1 as ConstraintLayout).findViewById<TextView>(R.id.instrument_entry_text).text.toString()
                        )
                    } else {
                        putFragment(frag, "thefirstitem")
                    }
                }
                else if ((context as TouchSampleSynthMain).soundGenerators[p2] is SimpleSubtractiveSynthI)
                {
                    val frag =
                        SimpleSubtractiveSynthFragment((context as TouchSampleSynthMain).soundGenerators[p2] as SimpleSubtractiveSynthI)
                    if (p1 != null) {
                        putFragment(
                            frag,
                            (p1 as ConstraintLayout).findViewById<TextView>(R.id.instrument_entry_text).text.toString()
                        )
                    } else {
                        putFragment(frag, "thefirstitem")
                    }
                }

                else if  ((context as TouchSampleSynthMain).soundGenerators[p2] is SamplerI)
                {
                    val frag =
                        SamplerFragment((context as TouchSampleSynthMain).soundGenerators[p2] as SamplerI)
                    if (p1 != null) {
                        putFragment(
                            frag,
                            (p1 as ConstraintLayout).findViewById<TextView>(R.id.instrument_entry_text).text.toString()
                        )
                    } else {
                        putFragment(frag, "thefirstitem")
                    }
                }
            }
            if (view?.findViewById<TextView>(R.id.instruments_page_instr_name) != null) {
                view?.findViewById<TextView>(R.id.instruments_page_instr_name)?.text=
                    (context as TouchSampleSynthMain).soundGenerators[p2].name
            }
            if (view?.findViewById<TextView>(R.id.instruments_page_nr_voices)!= null) {
                view?.findViewById<TextView>(R.id.instruments_page_nr_voices)?.text =
                    (context as TouchSampleSynthMain).soundGenerators[p2].voicesCount().toString()
            }
            (p0 as ListView).invalidateViews()
        }
    }
}