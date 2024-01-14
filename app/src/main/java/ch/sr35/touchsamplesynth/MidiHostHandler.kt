package ch.sr35.touchsamplesynth

import android.content.Context
import android.media.midi.MidiDeviceInfo
import android.media.midi.MidiDeviceStatus
import android.media.midi.MidiManager
import android.os.Build
import ch.sr35.touchsamplesynth.audio.AudioEngineK
import java.util.concurrent.Executor
import java.util.stream.Collectors

class MidiHostHandler(val ctx: Context) : MidiManager.DeviceCallback() {
    val midiDevices= ArrayList<MidiDeviceInfo>()
    var midiDeviceChangedHandler: MidiDevicesChanged?=null
    private var connectedDevice: MidiDeviceInfo?=null
    private val midiManager: MidiManager = ctx.getSystemService(Context.MIDI_SERVICE) as MidiManager

    fun startMidiDeviceListener()
    {
        // add devices already there
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            midiDevices.addAll(midiManager.getDevicesForTransport(MidiManager.TRANSPORT_MIDI_BYTE_STREAM).stream().filter { el -> el.outputPortCount > 0 }.collect(Collectors.toList()))
        }
        else
        {
            midiDevices.addAll(
                midiManager.devices.filter { el -> el.outputPortCount > 0 })
        }

        // register listener
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val midiDeviceDiscoveryExecutor= Executor { command ->
                command.run()
            }
            midiManager.registerDeviceCallback(
                MidiManager.TRANSPORT_MIDI_BYTE_STREAM,
                midiDeviceDiscoveryExecutor,
                this
            )
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU)
        {
            midiManager.registerDeviceCallback(this,null)
        }
    }

    fun connectMidiDevice(dev: MidiDeviceInfo)
    {
        if (connectedDevice != null)
        {
            AudioEngineK().closeMidiDevice()
        }
        connectedDevice=null
        midiManager.openDevice(dev,{
            val audioEngine=AudioEngineK()
            audioEngine.openMidiDevice(it,0)
        },null)
        connectedDevice = dev
    }


    fun stopMidiDeviceListener()
    {
        midiManager.unregisterDeviceCallback(this)
        midiDevices.clear()
    }

    override fun onDeviceAdded(device: MidiDeviceInfo?) {
        super.onDeviceAdded(device)
        if (device != null)
        {
            if (device.outputPortCount > 0)
            {
                midiDevices.add(device)
                midiDeviceChangedHandler?.onMidiDevicesChanged()
            }
        }
    }

    override fun onDeviceStatusChanged(status: MidiDeviceStatus?) {
        super.onDeviceStatusChanged(status)
    }

    override fun onDeviceRemoved(device: MidiDeviceInfo?) {
        super.onDeviceRemoved(device)
        if (device != null && device==connectedDevice)
        {
            // kill all note just to be sure
            (ctx as TouchSampleSynthMain).soundGenerators.stream().flatMap { i -> i.voices.stream() }.forEach {
                it.switchOff(.0f)
            }
        }
        midiDeviceChangedHandler?.onMidiDevicesChanged()
        midiDevices.remove(device)
    }
}

interface MidiDevicesChanged {
    fun onMidiDevicesChanged()
}