package ch.sr35.touchsamplesynth.fragments

import android.database.DataSetObserver
import android.media.midi.MidiDeviceInfo
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.BaseAdapter
import android.widget.ListView
import android.widget.Spinner
import android.widget.TextView
import android.widget.ToggleButton
import ch.sr35.touchsamplesynth.R
import ch.sr35.touchsamplesynth.audio.AudioEngineK
import ch.sr35.touchsamplesynth.BuildConfig
import ch.sr35.touchsamplesynth.MidiDevicesChanged
import ch.sr35.touchsamplesynth.MidiHostHandler
import ch.sr35.touchsamplesynth.TouchSampleSynthMain
import ch.sr35.touchsamplesynth.views.TouchElement
import ch.sr35.touchsamplesynth.views.WaitAnimation
import com.google.android.material.snackbar.Snackbar
import java.net.NetworkInterface
import java.net.SocketException


class SettingsFragment : Fragment(), AdapterView.OnItemSelectedListener, MidiDevicesChanged {

    private var framesPerDataCallbackIdx = -1
    private var bufferSizeInFramesIdx = -1
    lateinit var listViewMidiDevices: ListView



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_settings, container, false)


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val audioEngine = AudioEngineK()
        super.onViewCreated(view, savedInstanceState)
        val framesPerDataCallback = view.findViewById<Spinner>(R.id.spinnerFramesPerDataCallback)
        ArrayAdapter.createFromResource(
            view.context,
            R.array.framesPerDataCallbackValues,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears.
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner.
            framesPerDataCallback.adapter = adapter
        }
        val fpdcVals = resources.getStringArray(R.array.framesPerDataCallbackValues)
        val currentFpdc = audioEngine.getFramesPerDataCallback()
        var idx = 0
        for (fpdc in fpdcVals)
        {
            if (fpdcVals[idx].toInt() == currentFpdc)
            {
                break
            }
            idx++
        }
        if (idx < fpdcVals.size)
        {
            framesPerDataCallback.setSelection(idx)
            framesPerDataCallbackIdx = idx
        }
        framesPerDataCallback.onItemSelectedListener = this

        val bufferCapacityInFrames = view.findViewById<Spinner>(R.id.spinnerBufferCapacityInFrames)
        ArrayAdapter.createFromResource(
            view.context,
            R.array.bufferCapacityInFramesValues,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears.
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner.
            bufferCapacityInFrames.adapter = adapter
        }
        val bcifVals = resources.getStringArray(R.array.bufferCapacityInFramesValues)
        val currentBcif = audioEngine.getBufferCapacityInFrames()
        idx = 0
        for (bcif in bcifVals)
        {
            if (bcifVals[idx].toInt() == currentBcif)
            {
                break
            }
            idx++
        }
        if (idx < bcifVals.size)
        {
            bufferCapacityInFrames.setSelection(idx)
            bufferSizeInFramesIdx = idx
        }
        bufferCapacityInFrames.onItemSelectedListener = this

        val spinnerTouchElementStyle = view.findViewById<Spinner>(R.id.spinnerTouchElementsDisplay)
        ArrayAdapter.createFromResource(view.context,
            R.array.touchElementDisplayStyle,
            android.R.layout.simple_spinner_item
            ).also {
                arrayAdapter -> arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinnerTouchElementStyle.adapter = arrayAdapter
            spinnerTouchElementStyle.setSelection(0,false)
            spinnerTouchElementStyle.onItemSelectedListener = this
        }

        listViewMidiDevices=view.findViewById(R.id.settingListViewMidiDevices)
        listViewMidiDevices.adapter= (context as TouchSampleSynthMain).midiHostHandler?.let {
            MidiDevicesListAdapter(
                it
            )
        }
        listViewMidiDevices.onItemSelectedListener=this
        ((context as TouchSampleSynthMain).midiHostHandler)?.midiDeviceChangedHandler =this


        val textViewAbout = view.findViewById<TextView>(R.id.settingTextViewAbout)
        val aboutString="Touch Sample Synth Version %s".format(BuildConfig.VERSION_NAME)
        textViewAbout.text = aboutString

        view.findViewById<ToggleButton>(R.id.toggleButtonRtpMidi).also { it ->

            it.setOnCheckedChangeListener { toggleButtonView, isChecked ->
                if (isChecked) {
                    context?.let { it1 ->
                        (context as TouchSampleSynthMain).rtpMidiServer?.let { it3 ->
                            it3.startServer()
                            (context as TouchSampleSynthMain).nsdHandler?.registerService(it3.port)
                            view.findViewById<TextView>(R.id.rtpMidiPorts).also {
                                it.text = "%d / %d".format(it3.port, it3.port + 1)
                            }
                        }
                    }
                    toggleButtonView.text= (context as TouchSampleSynthMain).getString(R.string.disable)
                }
                else
                {
                    (context as TouchSampleSynthMain).nsdHandler?.tearDown()
                    (context as TouchSampleSynthMain).rtpMidiServer?.stopServer()
                    toggleButtonView.text= (context as TouchSampleSynthMain).getString(R.string.enable)
                    view.findViewById<TextView>(R.id.rtpMidiPorts).also {
                        it.text = ""
                    }
                }
            }
        }

        val ipAdressTextView = view.findViewById<TextView>(R.id.ipAddress)
        try {
            val ifaces = NetworkInterface.getNetworkInterfaces()

            for (iface in ifaces)
            {
                if (!iface.isLoopback())
                {
                    val addresses = iface.inetAddresses
                    for (addr in addresses)
                    {
                        if(!addr.isLoopbackAddress && addr.hostAddress !=null && addr.hostAddress?.contains(":")==false) {
                            ipAdressTextView.text = addr.hostAddress
                        }
                    }
                }
            }
        }
        catch (e: SocketException)
        {
            ipAdressTextView.text=getText(R.string.offline)
        }
        view.findViewById<WaitAnimation>(R.id.waitAnimationTest).apply {
            WaitAnimation.startAnimation(this)
        }
    }

    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        val audioEngine = AudioEngineK()

        if (p0 != null && p0.id == R.id.spinnerFramesPerDataCallback)
        {
            val framesPerDataCallback = resources.getStringArray(R.array.framesPerDataCallbackValues)
            if (audioEngine.setFramesPerDataCallback(framesPerDataCallback[p2].toInt())!=0)
            {
                this.view?.let { val sb = Snackbar.make(it,resources.getText(R.string.audioBuffersErrorMessage),5000)
                sb.show()}
                p0.setSelection(framesPerDataCallbackIdx)
            }
            else
            {
                framesPerDataCallbackIdx = p2
            }

        }
        else if (p0 != null && p0.id == R.id.spinnerBufferCapacityInFrames)
        {
            val bufferCapacityInFrames = resources.getStringArray(R.array.bufferCapacityInFramesValues)
            if (audioEngine.setBufferCapacityInFrames(bufferCapacityInFrames[p2].toInt())!=0)
            {
                this.view?.let { val sb = Snackbar.make(it,resources.getText(R.string.audioBuffersErrorMessage),5000)
                sb.show()}
                p0.setSelection(bufferSizeInFramesIdx)
            }
            else
            {
                bufferSizeInFramesIdx = p2
            }
        }
        else if (p0 != null && p0.id == R.id.spinnerTouchElementsDisplay)
        {
            (context as TouchSampleSynthMain).touchElements.forEach {
                if (p2 == 0)
                {
                    it.setDefaultMode(TouchElement.TouchElementState.PLAYING)
                }
                else
                {
                    it.setDefaultMode(TouchElement.TouchElementState.PLAYING_VERBOSE)
                }
            }
            if (p2 == 0)
            {
                (context as TouchSampleSynthMain).touchElementsDisplayMode=TouchElement.TouchElementState.PLAYING
            }
            else
            {
                (context as TouchSampleSynthMain).touchElementsDisplayMode=TouchElement.TouchElementState.PLAYING_VERBOSE
            }
        }
        else if (p0 != null && p0.id == R.id.settingListViewMidiDevices)
        {
            (context as TouchSampleSynthMain).midiHostHandler?.connectMidiDevice(p0.selectedItem as MidiDeviceInfo)
        }
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {

    }

    class MidiDevicesListAdapter(private val midiHostHandler: MidiHostHandler) : BaseAdapter() {
        override fun registerDataSetObserver(observer: DataSetObserver?) {
        }

        override fun unregisterDataSetObserver(observer: DataSetObserver?) {

        }

        override fun getCount(): Int {
            return midiHostHandler.midiDevices.size
        }

        override fun getItem(position: Int): Any {
            return midiHostHandler.midiDevices[position]
        }

        override fun getItemId(position: Int): Long {
            return midiHostHandler.midiDevices[position].id.toLong()
        }

        override fun hasStableIds(): Boolean {
            return false
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            if (convertView != null && convertView is TextView)
            {
                convertView.text=midiHostHandler.midiDevices[position].properties.getString(MidiDeviceInfo.PROPERTY_NAME)
                return convertView
            }
            return TextView(midiHostHandler.ctx).also {
                it.text = midiHostHandler.midiDevices[position].properties.getString(MidiDeviceInfo.PROPERTY_NAME)
            }
        }

        override fun getItemViewType(position: Int): Int {
            return 0
        }

        override fun getViewTypeCount(): Int {
            return  1
        }

        override fun isEmpty(): Boolean {
            return midiHostHandler.midiDevices.isEmpty()
        }

        override fun areAllItemsEnabled(): Boolean {
            return true
        }

        override fun isEnabled(position: Int): Boolean {
            return true
        }

    }


    override fun onMidiDevicesChanged() {
        (listViewMidiDevices.adapter as BaseAdapter).notifyDataSetChanged()
    }
}