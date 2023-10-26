package ch.sr35.touchsamplesynth.fragments

import android.app.AlertDialog
import android.database.DataSetObserver
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Button
import android.widget.CheckBox
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ListAdapter
import android.widget.ListView
import android.widget.TextView
import ch.sr35.touchsamplesynth.R
import ch.sr35.touchsamplesynth.TouchSampleSynthMain
import ch.sr35.touchsamplesynth.audio.instruments.SimpleSubtractiveSynthI
import ch.sr35.touchsamplesynth.audio.voices.SimpleSubtractiveSynthK
import ch.sr35.touchsamplesynth.audio.voices.SineMonoSynthK
import ch.sr35.touchsamplesynth.audio.instruments.SineMonoSynthI


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

        if (selectedInstrument == -1) {
            onItemClick(instrumentsList, null, 0, 0)
        }
        else
        {
            val oldSelection=selectedInstrument
            selectedInstrument=-1
            onItemClick(instrumentsList,null,oldSelection,0)
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
                        (context as TouchSampleSynthMain).soundGenerators[selectedInstrument].voices!!.forEach { v -> v.detachFromAudioEngine() }
                        (context as TouchSampleSynthMain).soundGenerators.removeAt(selectedInstrument)
                        instrumentsList.invalidateViews()
                    }
                    .setNegativeButton((context as TouchSampleSynthMain).getString(R.string.no)) { _, _ -> }
                val alertDlg = alertDlgBuilder.create()
                alertDlg.show()
            }
        }
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
        return if (p1 is LinearLayout) {
            p1.findViewById<TextView>(R.id.instrument_entry_text).text =
                String.format("%s, %s",(context as TouchSampleSynthMain).soundGenerators[p0].getType(),
                                        (context as TouchSampleSynthMain).soundGenerators[p0].name)
            p1.findViewById<ImageView>(R.id.instrument_entry_icon)
                .setImageDrawable((context as TouchSampleSynthMain).soundGenerators[p0].getInstrumentIcon())
            p1.findViewById<CheckBox>(R.id.instrument_entry_checkbox).isChecked = (selectedInstrument >= 0 && p0==selectedInstrument)
            p1
        } else {
            val tv = View.inflate(context,R.layout.instrument_entry,null) as LinearLayout
            tv.findViewById<TextView>(R.id.instrument_entry_text).text =
                String.format("%s, %s",(context as TouchSampleSynthMain).soundGenerators[p0].getType(),
                                        (context as TouchSampleSynthMain).soundGenerators[p0].name)
            tv.findViewById<ImageView>(R.id.instrument_entry_icon)
                .setImageDrawable((context as TouchSampleSynthMain).soundGenerators[p0].getInstrumentIcon())
            tv.findViewById<CheckBox>(R.id.instrument_entry_checkbox).isChecked = (selectedInstrument >= 0 && p0==selectedInstrument)
            tv
        }
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


    override fun onItemClick(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        if (p2 != selectedInstrument) {
            val contentView = view?.findViewById<FrameLayout>(R.id.instruments_page_content)
            selectedInstrument = p2
            if (contentView != null) {
                if ((context as TouchSampleSynthMain).soundGenerators[p2] is SineMonoSynthI) {
                    val frag =
                        SineMonoSynthFragment((context as TouchSampleSynthMain).soundGenerators[p2] as SineMonoSynthK)
                    if (p1 != null) {
                        putFragment(
                            frag,
                            (p1 as LinearLayout).findViewById<TextView>(R.id.instrument_entry_text).text.toString()
                        )
                    } else {
                        putFragment(frag, "thefirstitem")
                    }
                }
                else if ((context as TouchSampleSynthMain).soundGenerators[p2] is SimpleSubtractiveSynthI)
                {
                    val frag =
                        SimpleSubtractiveSynthFragment((context as TouchSampleSynthMain).soundGenerators[p2] as SimpleSubtractiveSynthK)
                    if (p1 != null) {
                        putFragment(
                            frag,
                            (p1 as LinearLayout).findViewById<TextView>(R.id.instrument_entry_text).text.toString()
                        )
                    } else {
                        putFragment(frag, "thefirstitem")
                    }
                }
            }
            (p0 as ListView).invalidateViews()
        }
    }
}