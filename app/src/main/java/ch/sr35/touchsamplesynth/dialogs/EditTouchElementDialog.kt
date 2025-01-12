package ch.sr35.touchsamplesynth.dialogs


import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
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
import android.widget.TextView
import androidx.core.graphics.ColorUtils
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import ch.sr35.touchsamplesynth.R
import ch.sr35.touchsamplesynth.TouchSampleSynthMain
import ch.sr35.touchsamplesynth.audio.instruments.InstrumentI
import ch.sr35.touchsamplesynth.graphics.RgbColor
import ch.sr35.touchsamplesynth.views.PianoRoll
import ch.sr35.touchsamplesynth.views.TouchElement
import ch.sr35.touchsamplesynth.views.TouchElement.ActionDir
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
        val instrumentList = findViewById<RecyclerView>(R.id.edit_te_soundgenerator_list)



        val instrumentListAdapter = SoundGeneratorListAdapter(soundGenerators,touchElement)
        instrumentList.adapter = instrumentListAdapter

        val pianoRollSelector = findViewById<PianoRoll>(R.id.edit_te_pianoroll)
        pianoRollSelector.selectionMode = PianoRoll.SelectionMode.MULTIPLE
        pianoRollSelector.selectedKeys.clear()
        pianoRollSelector.selectedKeys.addAll(touchElement.notes)
        pianoRollSelector.octave = touchElement.notes.minByOrNull { it.index }!!.getOctave()

        val rotationChangeTouchElement = findViewById<TouchElement>(R.id.edit_te_touchElement)
        rotationChangeTouchElement.actionDir = touchElement.actionDir
        rotationChangeTouchElement.setOnClickListener { it ->
            when(rotationChangeTouchElement.actionDir) {
                ActionDir.HORIZONTAL_LR_VERTICAL_DU -> {
                    rotationChangeTouchElement.actionDir= ActionDir.HORIZONTAL_RL_VERTICAL_DU
                }
                ActionDir.HORIZONTAL_RL_VERTICAL_DU -> {
                    rotationChangeTouchElement.actionDir= ActionDir.HORIZONTAL_RL_VERTICAL_UD
                }
                ActionDir.HORIZONTAL_RL_VERTICAL_UD -> {
                    rotationChangeTouchElement.actionDir= ActionDir.HORIZONTAL_LR_VERTICAL_UD
                }
                ActionDir.HORIZONTAL_LR_VERTICAL_UD -> {
                    rotationChangeTouchElement.actionDir = ActionDir.HORIZONTAL_LR_VERTICAL_DU
                }
            }
        }

        val buttonOk=findViewById<Button>(R.id.edit_te_button_ok)
        buttonOk?.setOnClickListener {

            if (instrumentListAdapter.checkedPosition > -1) {
                touchElement.setSoundGenerator (soundGenerators[instrumentListAdapter.checkedPosition])
                pickedColor?.let {
                    touchElement.setColor(RgbColor.fromColorInt( it.toColorInt()))
                }
                touchElement.actionDir = rotationChangeTouchElement.actionDir
            }


            touchElement.invalidate()
            touchElement.notes.clear()
            touchElement.notes.addAll( pianoRollSelector.selectedKeys)

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
            val touchElementMainColor = touchElement.getMainColor()
            ColorUtils.RGBToHSL(touchElementMainColor.r,touchElementMainColor.g,touchElementMainColor.b,hsl)
            it.intA=0xff
            it.floatH=hsl[0]
            it.floatS=hsl[1]
            it.floatL=hsl[2]

        })
        findViewById<TouchElement>(R.id.edit_te_touchElement).let {
            it.setColor(touchElement.getMainColor())
            it.invalidate()
        }
        pickerGroup.addListener(object : ColorSeekBar.OnColorPickListener<ColorSeekBar<IntegerHSLColor>,IntegerHSLColor> {
            override fun onColorChanged(
                picker: ColorSeekBar<IntegerHSLColor>,
                color: IntegerHSLColor,
                value: Int
            ) {
                val exampleTouchElement = findViewById<TouchElement>(R.id.edit_te_touchElement)
                exampleTouchElement.setColor(RgbColor.fromColorInt( color.toColorInt()))
                exampleTouchElement.invalidate()
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

        findViewById<EditText>(R.id.midiControlChangeA).also {
            (it as TextView).text = this.touchElement.midiCCA.toString()
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
                            this.touchElement.midiCCB=midiCCInt
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

        findViewById<EditText>(R.id.midiControlChangeB).also {
            (it as TextView).text = this.touchElement.midiCCB.toString()
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
                            this.touchElement.midiCCA=midiCCInt
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

        findViewById<CheckBox>(R.id.checkboxToggled).also {
            it.isChecked = this.touchElement.touchMode == TouchElement.TouchMode.TOGGLED
            it.setOnClickListener { _ ->
                this.touchElement.touchMode = if (it.isChecked)
                {
                    TouchElement.TouchMode.TOGGLED
                }
                else
                {
                    TouchElement.TouchMode.MOMENTARY
                }
                this.touchElement.invalidate()
            }
        }

    }
}

class SoundGeneratorListAdapter(private val instrumentIS: List<InstrumentI>,
                                private val touchElement: TouchElement?): RecyclerView.Adapter<SoundGeneratorListAdapter.SoundGeneratorListViewHolder>(){

    var checkedPosition: Int=-1

    init {

        checkedPosition = if (touchElement?.getSoundGenerator() != null) {
            IntStream.range(0, instrumentIS.size)
                .filter { i -> (instrumentIS[i].name == touchElement.getSoundGenerator()!!.name) && (instrumentIS[i].getType() == touchElement.getSoundGenerator()!!.getType()) }
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
