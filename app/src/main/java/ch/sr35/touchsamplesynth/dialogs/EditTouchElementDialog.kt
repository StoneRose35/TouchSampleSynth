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
import ch.sr35.touchsamplesynth.TouchSampleSynthMain
import ch.sr35.touchsamplesynth.audio.Instrument
import ch.sr35.touchsamplesynth.views.TouchElement
import java.util.stream.Collectors
import java.util.stream.IntStream


class EditTouchElementFragmentDialog(private var touchElement: TouchElement,
                                     private var context: Context): Dialog(context) {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.edit_touchelement)
        val soundGenerators = (context as TouchSampleSynthMain).soundGenerators
        val touchElements = (context as TouchSampleSynthMain).touchElements
        val instrumentList = findViewById<RecyclerView>(R.id.edit_te_soundgenerator_list)

        // filter soundgenerators to contain only monophonic instruments and
        // polyphonic whose voices haven't been taken yet
        val availableSoundGenerators = soundGenerators.stream().filter { i ->
            (touchElements.stream().map { te -> te.soundGenerator }
                .filter { el -> (el?.getType() ?: "") == i.getType() && (el?.name ?: "srz")== i.name}.count() < i.voicesCount())
                    || i.voicesCount() == 1
        }.collect(Collectors.toList())

        val instrumentListAdapter = SoundGeneratorListAdapter(availableSoundGenerators,touchElement)
        instrumentList.adapter = instrumentListAdapter

        val numberPickerNotes = findViewById<NumberPicker>(R.id.numberPickerNote)
        numberPickerNotes?.minValue = 0
        numberPickerNotes?.maxValue = 87
        numberPickerNotes?.displayedValues = MusicalPitch.generateAllNotes().map { p -> p.name }.toTypedArray()
        numberPickerNotes?.value = touchElement.note?.index ?: -1
        val buttonOk=findViewById<Button>(R.id.edit_te_button_ok)
        buttonOk?.setOnClickListener {
            if (instrumentListAdapter.checkedPosition > -1) {
                touchElement.soundGenerator = availableSoundGenerators[instrumentListAdapter.checkedPosition]
                if (touchElement.soundGenerator!!.voicesCount() > 1) {
                    val voiceNrs = (context as TouchSampleSynthMain).touchElements.stream()
                        .filter { te -> te.soundGenerator?.name == availableSoundGenerators[instrumentListAdapter.checkedPosition].name && te.soundGenerator!!.getType() == availableSoundGenerators[instrumentListAdapter.checkedPosition].getType() }
                        .map { te -> te.voiceNr }.collect(Collectors.toList())
                    var currentVoiceIdx = 0
                    while (currentVoiceIdx < soundGenerators[instrumentListAdapter.checkedPosition].voicesCount()) {
                        if (voiceNrs.stream().noneMatch { vn -> vn == currentVoiceIdx }) {
                            touchElement.voiceNr = currentVoiceIdx
                            currentVoiceIdx =
                                soundGenerators[instrumentListAdapter.checkedPosition].voicesCount()
                        } else {
                            currentVoiceIdx++
                        }
                    }
                }
                else // monophonic case
                {
                    touchElement.voiceNr = 0
                }
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

class SoundGeneratorListAdapter(private val instruments: List<Instrument>,
                                private val touchElement: TouchElement?): RecyclerView.Adapter<SoundGeneratorListAdapter.SoundGeneratorListViewHolder>(){

    var checkedPosition: Int=-1

    init {

            if (touchElement?.soundGenerator != null) {
                checkedPosition = IntStream.range(0, instruments.size)
                    .filter { i -> (instruments[i].name == touchElement.soundGenerator!!.name) && (instruments[i].getType() == touchElement.soundGenerator!!.getType()) }
                    .findFirst()
                    .orElse(-1)
            }
        else{
            checkedPosition = -1
        }

    }


    class SoundGeneratorListViewHolder(itemView: View) : ViewHolder(itemView) {

        val iconView: ImageView
        val instrumentNameView: TextView
        val checkedView: CheckBox
        val polyphonyView: TextView

        init {
            iconView=itemView.findViewById(R.id.instrument_entry_icon)
            instrumentNameView=itemView.findViewById(R.id.instrument_entry_text)
            checkedView=itemView.findViewById(R.id.instrument_entry_checkbox)
            polyphonyView=itemView.findViewById(R.id.instrument_entry_n_voices)
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
        if (instruments[position].name.isNotEmpty()) {
            holder.instrumentNameView.text =
                "%s".format(instruments[position].name)
        }
        else
        {
            holder.instrumentNameView.text = instruments[position].getType()
        }
        holder.polyphonyView.text = " "
        holder.checkedView.isChecked= checkedPosition==position
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
