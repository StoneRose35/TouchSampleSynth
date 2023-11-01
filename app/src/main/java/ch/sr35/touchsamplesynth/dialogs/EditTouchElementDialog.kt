package ch.sr35.touchsamplesynth.dialogs


import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.NumberPicker
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import ch.sr35.touchsamplesynth.MusicalPitch
import ch.sr35.touchsamplesynth.R
import ch.sr35.touchsamplesynth.audio.Instrument
import ch.sr35.touchsamplesynth.audio.MusicalSoundGenerator
import ch.sr35.touchsamplesynth.views.TouchElement
import java.util.stream.IntStream


class EditTouchElementFragmentDialog(private var touchElement: TouchElement,
                                     private var soundGenerators: ArrayList<Instrument>,
                                     context: Context): Dialog(context) {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.edit_touchelement)

        val instrumentList = findViewById<RecyclerView>(R.id.edit_te_soundgenerator_list)
        val instrumentListAdapter = SoundGeneratorListAdapter(soundGenerators,touchElement)
        instrumentList.adapter = instrumentListAdapter

        val numberPickerNotes = findViewById<NumberPicker>(R.id.numberPickerNote)
        numberPickerNotes?.minValue = 0
        numberPickerNotes?.maxValue = 87
        numberPickerNotes?.displayedValues = MusicalPitch.generateAllNotes().map { p -> p.name }.toTypedArray()
        numberPickerNotes?.value = touchElement.note?.index ?: -1
        val buttonOk=findViewById<Button>(R.id.edit_te_button_ok)
        buttonOk?.setOnClickListener {
            if (instrumentListAdapter.checkedPosition > -1) {
                touchElement.soundGenerator = soundGenerators[instrumentListAdapter.checkedPosition]
            }

            touchElement.note = MusicalPitch.generateAllNotes()[numberPickerNotes?.value!!] //spinnerNotesAdapter.note

            this.dismiss()
        }
        val buttonCancel = findViewById<Button>(R.id.edit_te_button_cancel)
        buttonCancel?.setOnClickListener {
            this.dismiss()
        }
    }


}

class SoundGeneratorListAdapter(private val instruments: ArrayList<Instrument>,
                                private val touchElement: TouchElement?): RecyclerView.Adapter<SoundGeneratorListAdapter.SoundGeneratorListViewHolder>(){

    var checkedPosition: Int=-1


    init {
        if (touchElement!=null)
        {
            checkedPosition = IntStream.range(0,instruments.size)
                              .filter { i -> instruments[i].name == touchElement.soundGenerator!!.name && instruments[i].getType() == touchElement.soundGenerator!!.getType() }
                              .findFirst()
                              .orElse(-1)
        }
    }


    class SoundGeneratorListViewHolder(itemView: View) : ViewHolder(itemView) {

        val iconView: ImageView
        val instrumentNameView: TextView
        val voices: NumberPicker
        val checkedView: CheckBox

        init {
            iconView=itemView.findViewById(R.id.instrument_entry_icon)
            instrumentNameView=itemView.findViewById(R.id.instrument_entry_text)
            checkedView=itemView.findViewById(R.id.instrument_entry_checkbox)
            voices=itemView.findViewById(R.id.instrument_entry_number_voices)
            voices.minValue = 1
            voices.maxValue = 16
        }
    }

    override fun getItemCount(): Int {
        return instruments.size
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SoundGeneratorListViewHolder {
        val view=LayoutInflater.from(parent.context).inflate(R.layout.instrument_entry,parent,false)
        return SoundGeneratorListViewHolder(view)
    }

    override fun onBindViewHolder(holder: SoundGeneratorListViewHolder, position: Int) {
        holder.iconView.setImageDrawable(instruments[position].getInstrumentIcon())
        holder.instrumentNameView.text = "%s".format(instruments[position].getType())
        holder.checkedView.isChecked= checkedPosition==position
        holder.voices.value = instruments[position].voicesCount()
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
