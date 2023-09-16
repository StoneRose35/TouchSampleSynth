package ch.sr35.touchsamplesynth.fragments


import android.content.Context
import android.database.DataSetObserver
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.NumberPicker
import android.widget.Spinner
import android.widget.SpinnerAdapter
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import ch.sr35.touchsamplesynth.MusicalPitch
import ch.sr35.touchsamplesynth.R
import ch.sr35.touchsamplesynth.audio.MusicalSoundGenerator
import ch.sr35.touchsamplesynth.views.TouchElement


class EditTouchElementFragmentDialog: DialogFragment() {
    private var touchElement: TouchElement?=null
    private var soundGenerators = ArrayList<MusicalSoundGenerator>()
    fun setData(te: TouchElement,sg: ArrayList<MusicalSoundGenerator>)
    {
        touchElement = te
        soundGenerators = sg
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val spinnerSoundGenerator=view.findViewById<Spinner>(R.id.spinnerSoundgenerator)
        val spinnerSoundGeneratorAdapter =
            this.context?.let { SoundGeneratorSpinnerAdapter(soundGenerators, it) }
        spinnerSoundGenerator.adapter = spinnerSoundGeneratorAdapter
        spinnerSoundGenerator.setSelection(soundGenerators.indexOf(touchElement!!.soundGenerator))
        spinnerSoundGenerator.onItemSelectedListener = spinnerSoundGeneratorAdapter

        val numberPickerNotes = view.findViewById<NumberPicker>(R.id.numberPickerNote)
        numberPickerNotes.minValue = 0
        numberPickerNotes.maxValue = 88
        numberPickerNotes.displayedValues = MusicalPitch.generateAllNotes().map { p -> p.name }.toTypedArray()
        numberPickerNotes.value = touchElement!!.note?.index ?: -1
        val buttonOk=view.findViewById<Button>(R.id.edit_te_button_ok)
        buttonOk.setOnClickListener {
            if (spinnerSoundGeneratorAdapter?.soundGenerator != null) {
                touchElement!!.soundGenerator = spinnerSoundGeneratorAdapter.soundGenerator
            }

            touchElement!!.note = MusicalPitch.generateAllNotes()[numberPickerNotes.value] //spinnerNotesAdapter.note

            this.dismiss()
        }
        val buttonCancel = view.findViewById<Button>(R.id.edit_te_button_cancel)
        buttonCancel.setOnClickListener {
            this.dismiss()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.edit_touchelement,container,false)
    }
}

class SoundGeneratorSpinnerAdapter(sg: ArrayList<MusicalSoundGenerator>, ctx: Context): SpinnerAdapter,
    AdapterView.OnItemSelectedListener {
    private var soundGenerators =  ArrayList<MusicalSoundGenerator>()
    private var context: Context?=null
    var soundGenerator: MusicalSoundGenerator?=null
    init {
        soundGenerators = sg
        context = ctx
    }
    override fun registerDataSetObserver(p0: DataSetObserver?) {

    }

    override fun unregisterDataSetObserver(p0: DataSetObserver?) {

    }

    override fun getCount(): Int {
        return soundGenerators.size
    }

    override fun getItem(p0: Int): Any {
        return soundGenerators[p0]
    }

    override fun getItemId(p0: Int): Long {
        return 0
    }

    override fun hasStableIds(): Boolean {
        return true
    }

    override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {
        return if (p1 is LinearLayout) {
            p1.findViewById<TextView>(R.id.instrument_entry_text).text = String.format("%s, %d",soundGenerators[p0].getType(),soundGenerators[p0].getInstance())
            p1.findViewById<ImageView>(R.id.instrument_entry_icon).setImageDrawable(soundGenerators[p0].getInstrumentIcon())
            p1.background=null
            p1
        } else {
            val tv = View.inflate(context,R.layout.instrument_entry,null) as LinearLayout //tv = TextView(context)
            tv.findViewById<TextView>(R.id.instrument_entry_text).text = String.format("%s, %d",soundGenerators[p0].getType(),soundGenerators[p0].getInstance())
            tv.findViewById<ImageView>(R.id.instrument_entry_icon).setImageDrawable(soundGenerators[p0].getInstrumentIcon())
            tv.background=null
            tv
        }
    }

    override fun getItemViewType(p0: Int): Int {
        return 0
    }

    override fun getViewTypeCount(): Int {
        return  1
    }

    override fun isEmpty(): Boolean {
        return soundGenerators.isEmpty()
    }

    override fun getDropDownView(p0: Int, p1: View?, p2: ViewGroup?): View {
        return if (p1 is LinearLayout) {
            p1.findViewById<TextView>(R.id.instrument_entry_text).text = String.format("%s, %d",soundGenerators[p0].getType(),soundGenerators[p0].getInstance())
            p1.findViewById<ImageView>(R.id.instrument_entry_icon).setImageDrawable(soundGenerators[p0].getInstrumentIcon())
            p1.background=null
            p1
        } else {
            val tv = View.inflate(context,R.layout.instrument_entry,null) as LinearLayout
            tv.findViewById<TextView>(R.id.instrument_entry_text).text = String.format("%s, %d",soundGenerators[p0].getType(),soundGenerators[p0].getInstance())
            tv.findViewById<ImageView>(R.id.instrument_entry_icon).setImageDrawable(soundGenerators[p0].getInstrumentIcon())
            tv.background=null
            tv
        }
    }

    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        soundGenerator = soundGenerators[p2]
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
        soundGenerator=null
    }

}
