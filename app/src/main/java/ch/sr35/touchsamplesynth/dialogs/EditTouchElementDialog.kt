package ch.sr35.touchsamplesynth.dialogs


import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.PaintDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import android.widget.NumberPicker
import android.widget.TextView
import androidx.core.graphics.ColorUtils
import androidx.core.graphics.blue
import androidx.core.graphics.green
import androidx.core.graphics.red
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import ch.sr35.touchsamplesynth.MusicalPitch
import ch.sr35.touchsamplesynth.R
import ch.sr35.touchsamplesynth.TouchSampleSynthMain
import ch.sr35.touchsamplesynth.audio.InstrumentI
import ch.sr35.touchsamplesynth.views.TouchElement
import codes.side.andcolorpicker.converter.toColorInt
import codes.side.andcolorpicker.group.PickerGroup
import codes.side.andcolorpicker.group.registerPickers
import codes.side.andcolorpicker.hsl.HSLColorPickerSeekBar
import codes.side.andcolorpicker.model.IntegerHSLColor
import codes.side.andcolorpicker.view.picker.ColorSeekBar
import java.lang.NumberFormatException
import java.util.stream.IntStream


class EditTouchElementFragmentDialog(private var touchElement: TouchElement,
                                     private var context: Context): Dialog(context) {
     var pickedColor: IntegerHSLColor?=null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.edit_touchelement)
        val soundGenerators = (context as TouchSampleSynthMain).soundGenerators
        //val touchElements = (context as TouchSampleSynthMain).touchElements
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
                pickedColor?.let {
                    touchElement.fillColor.color = it.toColorInt()
                }
            }
            touchElement.invalidate()
            touchElement.note = MusicalPitch.generateAllNotes()[numberPickerNotes?.value!!] //spinnerNotesAdapter.note

            this.dismiss()
        }
        val buttonCancel = findViewById<Button>(R.id.edit_te_button_cancel)
        buttonCancel?.setOnClickListener {
            this.dismiss()
        }

        val pickerGroup = PickerGroup<IntegerHSLColor>().also {
            val huePicker = findViewById<HSLColorPickerSeekBar>(R.id.touchElementColorHueSeekBar)
            val satPicker = findViewById<HSLColorPickerSeekBar>(R.id.touchElementColorSaturationSeekBar)
            val lvlPicker = findViewById<HSLColorPickerSeekBar>(R.id.touchElementColorLightnessSeekBar)
            it.registerPickers(huePicker,satPicker,lvlPicker)
        }
        pickerGroup.setColor(IntegerHSLColor().also {
            val hsl= floatArrayOf(0.0f,0.0f,0.0f)
            ColorUtils.RGBToHSL(touchElement.fillColor.color.red,touchElement.fillColor.color.green,touchElement.fillColor.color.blue,hsl)
            it.intA=0xff
            it.floatH=hsl[0]
            it.floatS=hsl[1]
            it.floatL=hsl[2]

        })
        findViewById<ImageView>(R.id.touchElementColor).let {
            it.background=PaintDrawable(touchElement.fillColor.color)
            it.invalidate()
        }
        pickerGroup.addListener(object : ColorSeekBar.OnColorPickListener<ColorSeekBar<IntegerHSLColor>,IntegerHSLColor> {
            override fun onColorChanged(
                picker: ColorSeekBar<IntegerHSLColor>,
                color: IntegerHSLColor,
                value: Int
            ) {
                val touchElementColorDisplay = findViewById<ImageView>(R.id.touchElementColor)
                touchElementColorDisplay.background=PaintDrawable(color.toColorInt())

            }

            override fun onColorPicked(
                picker: ColorSeekBar<IntegerHSLColor>,
                color: IntegerHSLColor,
                value: Int,
                fromUser: Boolean
            ) {
                pickedColor=color
            }

            override fun onColorPicking(
                picker: ColorSeekBar<IntegerHSLColor>,
                color: IntegerHSLColor,
                value: Int,
                fromUser: Boolean
            ) {
            }

        })

        findViewById<EditText>(R.id.midiChannel).also {
            (it as TextView).text = (this.touchElement.midiChannel+1).toString()
            it.setOnEditorActionListener { v, actionId, event ->
                    if (actionId == EditorInfo.IME_ACTION_DONE) {
                        try {
                            val midiChannelInt =  v.text.toString().toInt() - 1
                            v.background=null
                            if (midiChannelInt < 0 || midiChannelInt > 15)
                            {
                                v.background= ColorDrawable(Color.RED)
                            }
                            else
                            {
                                v.background=null
                                this.touchElement.midiChannel=midiChannelInt

                            }
                        }
                        catch (e: NumberFormatException)
                        {
                            v.background= ColorDrawable(Color.RED)
                        }
                        return@setOnEditorActionListener true
                    }
                return@setOnEditorActionListener false
            }
        }

        findViewById<EditText>(R.id.midiControlChange).also {
            (it as TextView).text = this.touchElement.midiCC.toString()
            it.setOnEditorActionListener { v, actionId, event ->
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    try {
                        val midiCCInt =  v.text.toString().toInt()
                        v.background=null
                        if (midiCCInt < 0 || midiCCInt > 127)
                        {
                            v.background= ColorDrawable(Color.RED)
                        }
                        else
                        {
                            v.background=null
                            this.touchElement.midiCC=midiCCInt
                        }
                    }
                    catch (e: NumberFormatException)
                    {
                        v.background= ColorDrawable(Color.RED)
                    }
                    return@setOnEditorActionListener true
                }
                return@setOnEditorActionListener false
            }
        }

    }
}

class SoundGeneratorListAdapter(private val instrumentIS: List<InstrumentI>,
                                private val touchElement: TouchElement?): RecyclerView.Adapter<SoundGeneratorListAdapter.SoundGeneratorListViewHolder>(){

    var checkedPosition: Int=-1

    init {

        checkedPosition = if (touchElement?.soundGenerator != null) {
            IntStream.range(0, instrumentIS.size)
                .filter { i -> (instrumentIS[i].name == touchElement.soundGenerator!!.name) && (instrumentIS[i].getType() == touchElement.soundGenerator!!.getType()) }
                .findFirst()
                .orElse(-1)
        } else if (instrumentIS.isNotEmpty()) {
            0
        } else {
            -1
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
        return instrumentIS.size
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SoundGeneratorListViewHolder {
        val view=LayoutInflater.from(parent.context).inflate(R.layout.instrument_entry,parent,false)
        return SoundGeneratorListViewHolder(view)
    }

    override fun onBindViewHolder(holder: SoundGeneratorListViewHolder, position: Int) {
        holder.iconView.setImageDrawable(instrumentIS[position].getInstrumentIcon())
        if (instrumentIS[position].name.isNotEmpty()) {
            holder.instrumentNameView.text =
                "%s".format(instrumentIS[position].name)
        }
        else
        {
            holder.instrumentNameView.text = instrumentIS[position].getType()
        }
        holder.polyphonyView.text = " "
        holder.checkedView.isChecked= checkedPosition==position
        holder.itemView.setOnClickListener {
            if (holder.bindingAdapterPosition!=checkedPosition)
            {
                notifyItemChanged(checkedPosition)
                checkedPosition = holder.bindingAdapterPosition
                holder.checkedView.isChecked= position==checkedPosition
            }
        }
    }



}
