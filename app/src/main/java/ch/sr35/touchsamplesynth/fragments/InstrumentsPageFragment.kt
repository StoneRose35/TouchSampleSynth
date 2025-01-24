package ch.sr35.touchsamplesynth.fragments

import android.app.AlertDialog
import android.content.Context
import android.database.DataSetObserver
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
import android.widget.RadioGroup
import android.widget.ScrollView
import android.widget.Spinner
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet.WRAP_CONTENT
import ch.sr35.touchsamplesynth.R
import ch.sr35.touchsamplesynth.TouchSampleSynthMain
import ch.sr35.touchsamplesynth.audio.MIDI_MODE_OFF
import ch.sr35.touchsamplesynth.audio.MIDI_MODE_ON_POLY
import ch.sr35.touchsamplesynth.audio.MusicalSoundGenerator
import ch.sr35.touchsamplesynth.audio.instruments.PolyphonyDefinition


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

        val addButton = view.findViewById<Button>(R.id.instruments_page_add)
        addButton.setOnClickListener {
            val addInstrumentDlg = AddInstrumentFragmentDialog(instrumentsList)

            (context as TouchSampleSynthMain).supportFragmentManager
                .beginTransaction()
                .add(addInstrumentDlg,null)
                .commit()
            addInstrumentDlg.dialog?.window?.setLayout(WRAP_CONTENT, WRAP_CONTENT)
        }

        val deleteButton = view.findViewById<Button>(R.id.instruments_page_delete)
        deleteButton.setOnClickListener {
            if (selectedInstrument >=0 && !(context as TouchSampleSynthMain).touchElements.stream().anyMatch {
                    it.getSoundGenerator() == (context as TouchSampleSynthMain).soundGenerators[selectedInstrument]
                })
            {
                val alertDlgBuilder = AlertDialog.Builder(context as TouchSampleSynthMain)
                    .setMessage((context as TouchSampleSynthMain).getString(R.string.alert_dialog_really_delete))
                    .setPositiveButton((context as TouchSampleSynthMain).getString(R.string.yes)) { _, _ ->
                        (context as TouchSampleSynthMain).soundGenerators[selectedInstrument].voices.forEach { v -> v.detachFromAudioEngine() }
                        (context as TouchSampleSynthMain).soundGenerators.removeAt(selectedInstrument)
                        selectedInstrument = -1
                        instrumentsList.invalidateViews()
                        removeCurrentFragment()
                        view.findViewById<EditText>(R.id.instruments_page_instr_name).text.clear()
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


        view.findViewById<RadioGroup>(R.id.instrument_page_rg_polyphony).setOnCheckedChangeListener { _, checkedId ->

            val voicesSpinner = view.findViewById<Spinner>(R.id.instrument_page_sp_voices)
            when(checkedId)
            {
                R.id.instrument_page_rg_poly_sat -> {
                    if (selectedInstrument > -1)
                    {
                        (context as TouchSampleSynthMain).soundGenerators[selectedInstrument].polyphonyDefinition = PolyphonyDefinition.POLY_SATURATE
                    }
                    voicesSpinner.isEnabled = true
                }
                R.id.instrument_page_rg_poly_ns -> {
                    if (selectedInstrument > -1)
                    {
                        (context as TouchSampleSynthMain).soundGenerators[selectedInstrument].polyphonyDefinition = PolyphonyDefinition.POLY_NOTE_STEAL
                    }
                    voicesSpinner.isEnabled = true
                }
                R.id.instrument_page_rg_mono -> {
                    if (selectedInstrument > -1)
                    {
                        (context as TouchSampleSynthMain).soundGenerators[selectedInstrument].polyphonyDefinition = PolyphonyDefinition.MONOPHONIC
                    }
                    voicesSpinner.apply {
                        setSelection(0)
                        isEnabled = false
                    }
                }
            }
            view.findViewById<ListView>(R.id.instruments_page_instruments_list).invalidateViews()
        }

        view.findViewById<Spinner>(R.id.instrument_page_sp_voices).onItemSelectedListener = VoicesChangedHandler(this)


    }


    override fun onPause() {
        super.onPause()
        (context as TouchSampleSynthMain).soundGenerators.flatMap { sg -> sg.voices }.forEach { v -> v.setMidiMode(
            MIDI_MODE_OFF) }
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

    private fun removeCurrentFragment()
    {
        if (childFragmentManager.fragments.isNotEmpty()) {
            childFragmentManager.beginTransaction().let {
                val fragment = childFragmentManager.fragments[0]
                it.remove(fragment)
                it.commit()
            }
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
            view?.findViewById<EditText>(R.id.instruments_page_instr_name)?.text?.clear()
            view?.findViewById<RadioGroup>(R.id.instrument_page_rg_polyphony)?.clearCheck()
            view?.findViewById<Spinner>(R.id.instrument_page_sp_voices)?.setSelection(0)
        }
    }

    override fun onItemClick(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        if (p2 != selectedInstrument) {
            val contentView = view?.findViewById<FrameLayout>(R.id.instruments_page_content)
            (context as TouchSampleSynthMain).soundGenerators.flatMap { sg -> sg.voices }.forEach { v -> v.setMidiMode(
                MIDI_MODE_OFF) }
            (context as TouchSampleSynthMain).soundGenerators[p2].voices.forEach { v -> v.setMidiMode(
                MIDI_MODE_ON_POLY) }
            selectedInstrument = p2
            val frag: Fragment
            if (contentView != null) {
                val framentName = (context as TouchSampleSynthMain).soundGenerators[p2]::class.java.name.split(".").last().let {
                    cn ->
                    cn.substring(0,cn.length-1) + "Fragment"
                }
                frag = Class.forName("ch.sr35.touchsamplesynth.fragments.$framentName").constructors
                    .first { cstr -> cstr.parameterCount == 1 }
                    .newInstance((context as TouchSampleSynthMain)
                    .soundGenerators[p2]) as Fragment
                if (p1 != null) {
                    putFragment(
                        frag,
                        (p1 as ConstraintLayout).findViewById<TextView>(R.id.instrument_entry_text).text.toString()
                    )
                } else {
                    putFragment(frag, "thefirstitem")
                }
            }
            view?.findViewById<TextView>(R.id.instruments_page_instr_name)?.text =
                    (context as TouchSampleSynthMain).soundGenerators[p2].name
            when ((context as TouchSampleSynthMain).soundGenerators[p2].polyphonyDefinition)
            {
                PolyphonyDefinition.POLY_SATURATE -> {
                    view?.findViewById<RadioGroup>(R.id.instrument_page_rg_polyphony)?.check(R.id.instrument_page_rg_poly_sat)
                }
                PolyphonyDefinition.POLY_NOTE_STEAL -> {
                    view?.findViewById<RadioGroup>(R.id.instrument_page_rg_polyphony)?.check(R.id.instrument_page_rg_poly_ns)
                }
                PolyphonyDefinition.MONOPHONIC -> {
                    view?.findViewById<RadioGroup>(R.id.instrument_page_rg_polyphony)?.check(R.id.instrument_page_rg_mono)
                }
            }
            val nVoices = (context as TouchSampleSynthMain).soundGenerators[p2].voices.size
            val voicesIdx = resources.getStringArray(R.array.numberOfVoices).map { it -> it.toInt()}.indexOf(nVoices)
            if (voicesIdx > -1) {
                view?.findViewById<Spinner>(R.id.instrument_page_sp_voices)?.setSelection(voicesIdx)
            }
            (p0 as ListView).invalidateViews()
        }
    }
}

class VoicesChangedHandler(val instrumentsPageFragment: InstrumentsPageFragment): AdapterView.OnItemSelectedListener {
    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

        if (instrumentsPageFragment.selectedInstrument > -1)
        {
            val nVoicesOld = (instrumentsPageFragment.context as TouchSampleSynthMain).soundGenerators[instrumentsPageFragment.selectedInstrument].voices.size
            val nVoicesNew = ((parent as Spinner).selectedItem as String).toInt()
            if (nVoicesNew > nVoicesOld)
            {
                (instrumentsPageFragment.context as TouchSampleSynthMain).soundGenerators[instrumentsPageFragment.selectedInstrument].generateVoices(nVoicesNew-nVoicesOld)
            }
            else if (nVoicesOld > nVoicesNew)
            {
                (instrumentsPageFragment.context as TouchSampleSynthMain).soundGenerators[instrumentsPageFragment.selectedInstrument].voices.filterIndexed { index, _ -> index >= nVoicesNew }.forEach { it -> it.detachFromAudioEngine() }
                (instrumentsPageFragment.context as TouchSampleSynthMain).soundGenerators[instrumentsPageFragment.selectedInstrument].voices=
                    (instrumentsPageFragment.context as TouchSampleSynthMain).soundGenerators[instrumentsPageFragment.selectedInstrument].voices.filterIndexed { index, _ -> index < nVoicesNew } as ArrayList<MusicalSoundGenerator>
            }
            (instrumentsPageFragment.context as TouchSampleSynthMain).findViewById<ListView>(R.id.instruments_page_instruments_list).invalidateViews()
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {

    }

}