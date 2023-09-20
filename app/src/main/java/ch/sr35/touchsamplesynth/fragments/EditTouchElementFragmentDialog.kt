package ch.sr35.touchsamplesynth.fragments


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.NumberPicker
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import ch.sr35.touchsamplesynth.MusicalPitch
import ch.sr35.touchsamplesynth.R
import ch.sr35.touchsamplesynth.audio.MusicalSoundGenerator
import ch.sr35.touchsamplesynth.views.TouchElement
import java.util.stream.IntStream


class EditTouchElementFragmentDialog(private var touchElement: TouchElement,private var soundGenerators: ArrayList<MusicalSoundGenerator>): DialogFragment() {
    //private var touchElement: TouchElement?=null
    //private var soundGenerators = ArrayList<MusicalSoundGenerator>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        /*
        val spinnerSoundGenerator=view.findViewById<Spinner>(R.id.spinnerSoundgenerator)
        val spinnerSoundGeneratorAdapter =
            this.context?.let { SoundGeneratorListAdapter(soundGenerators, it) }
        spinnerSoundGenerator.adapter = spinnerSoundGeneratorAdapter
        spinnerSoundGenerator.setSelection(soundGenerators.indexOf(touchElement!!.soundGenerator))
        spinnerSoundGenerator.onItemSelectedListener = spinnerSoundGeneratorAdapter
*/
        val instrumentList = view.findViewById<RecyclerView>(R.id.edit_te_soundgenerator_list)
        val instrumentListAdapter = SoundGeneratorListAdapter(soundGenerators,touchElement)
        instrumentList.adapter = instrumentListAdapter

        val numberPickerNotes = view.findViewById<NumberPicker>(R.id.numberPickerNote)
        numberPickerNotes?.minValue = 0
        numberPickerNotes?.maxValue = 88
        numberPickerNotes?.displayedValues = MusicalPitch.generateAllNotes().map { p -> p.name }.toTypedArray()
        numberPickerNotes?.value = touchElement.note?.index ?: -1
        val buttonOk=view.findViewById<Button>(R.id.edit_te_button_ok)
        buttonOk?.setOnClickListener {
            if (instrumentListAdapter.checkedPosition > -1) {
                touchElement.soundGenerator = soundGenerators[instrumentListAdapter.checkedPosition]
            }

            touchElement.note = MusicalPitch.generateAllNotes()[numberPickerNotes?.value!!] //spinnerNotesAdapter.note

            this.dismiss()
        }
        val buttonCancel = view.findViewById<Button>(R.id.edit_te_button_cancel)
        buttonCancel?.setOnClickListener {
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

class SoundGeneratorListAdapter(private val soundGenerators: ArrayList<MusicalSoundGenerator>,private val touchElement: TouchElement?): RecyclerView.Adapter<SoundGeneratorListAdapter.SoundGeneratorListViewHolder>(){

    var checkedPosition: Int=-1

    init {
        if (touchElement!=null)
        {
            checkedPosition = IntStream.range(0,soundGenerators.size)
                              .filter { i -> soundGenerators[i]==touchElement.soundGenerator }
                              .findFirst()
                              .orElse(-1)
        }
    }


    class SoundGeneratorListViewHolder(itemView: View) : ViewHolder(itemView) {

        val iconView: ImageView
        val instrumentNameView: TextView
        val checkedView: CheckBox

        init {
            iconView=itemView.findViewById(R.id.instrument_entry_icon)
            instrumentNameView=itemView.findViewById(R.id.instrument_entry_text)
            checkedView=itemView.findViewById(R.id.instrument_entry_checkbox)
        }
    }

    override fun getItemCount(): Int {
        return soundGenerators.size
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SoundGeneratorListViewHolder {
        val view=LayoutInflater.from(parent.context).inflate(R.layout.instrument_entry,parent,false)
        return SoundGeneratorListViewHolder(view)
    }

    override fun onBindViewHolder(holder: SoundGeneratorListViewHolder, position: Int) {
        holder.iconView.setImageDrawable(soundGenerators[position].getInstrumentIcon())
        holder.instrumentNameView.text = "%s, %d".format(soundGenerators[position].getType(), soundGenerators[position].getInstance())
        holder.checkedView.isChecked= checkedPosition==position
        //if (holder.checkedView.isChecked)
        //{
        //    checkedPosition=position
        //}
        holder.itemView.setOnClickListener {
            if (holder.adapterPosition!=checkedPosition)
            {
                notifyItemChanged(checkedPosition)
                checkedPosition = holder.adapterPosition
                holder.checkedView.isChecked= position==checkedPosition

            }
        }
    }



}
