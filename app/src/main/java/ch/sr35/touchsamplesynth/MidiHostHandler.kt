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
    val midiDevicesIn= ArrayList<MidiDeviceInfo>()
    val midiDevicesOut= ArrayList<MidiDeviceInfo>()
    var midiDeviceChangedHandler: MidiDevicesChanged?=null
    var connectedDeviceIn: MidiDeviceInfo?=null
    var connectedDeviceOut: MidiDeviceInfo?=null
    val midiManager: MidiManager = ctx.getSystemService(Context.MIDI_SERVICE) as MidiManager

    fun startMidiDeviceListener()
    {
        // add devices already there
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            midiDevicesIn.addAll(midiManager.getDevicesForTransport(MidiManager.TRANSPORT_MIDI_BYTE_STREAM).stream().filter { el -> el.outputPortCount > 0 }.collect(Collectors.toList()))
            midiDevicesOut.addAll(midiManager.getDevicesForTransport(MidiManager.TRANSPORT_MIDI_BYTE_STREAM).stream().filter { el -> el.inputPortCount > 0 }.collect(Collectors.toList()))
        }
        else
        {
            midiDevicesIn.addAll(
                midiManager.devices.filter { el -> el.outputPortCount > 0 })
            midiDevicesOut.addAll(
                midiManager.devices.filter { el -> el.inputPortCount > 0 })

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

    fun connectMidiDeviceIn(dev: MidiDeviceInfo)
    {
        if (connectedDeviceIn != null)
        {
            AudioEngineK().closeMidiDeviceIn()
            connectedDeviceIn=null
        }

        midiManager.openDevice(dev, {
            val audioEngine = AudioEngineK()
            audioEngine.openMidiDeviceIn(it, 0)
        }, null)

        connectedDeviceIn = dev
    }

    fun connectMidiDeviceOut(dev: MidiDeviceInfo)
    {
        if (connectedDeviceOut != null)
        {
            AudioEngineK().closeMidiDeviceOut()
            connectedDeviceOut=null
        }

        midiManager.openDevice(dev, {
            val audioEngine = AudioEngineK()
            audioEngine.openMidiDeviceOut(it, 0)
        }, null)

        connectedDeviceOut = dev
    }


    fun stopMidiDeviceListener()
    {
        midiManager.unregisterDeviceCallback(this)
        midiDevicesIn.clear()
        midiDevicesOut.clear()
    }

    override fun onDeviceAdded(device: MidiDeviceInfo?) {
        super.onDeviceAdded(device)
        if (device != null)
        {
            if (device.outputPortCount > 0)
            {
                midiDevicesIn.add(device)
                midiDeviceChangedHandler?.onMidiDevicesChanged()
            }
            if (device.inputPortCount > 0)
            {
                midiDevicesOut.add(device)
                midiDeviceChangedHandler?.onMidiDevicesChanged()
            }
        }
    }

    override fun onDeviceStatusChanged(status: MidiDeviceStatus?) {
        super.onDeviceStatusChanged(status)
    }

    override fun onDeviceRemoved(device: MidiDeviceInfo?) {
        super.onDeviceRemoved(device)
        if (device != null && device==connectedDeviceIn)
        {
            // kill all note just to be sure
            (ctx as TouchSampleSynthMain).soundGenerators.stream().flatMap { i -> i.voices.stream() }.forEach {
                it.switchOff(.0f)
            }
        }

        midiDeviceChangedHandler?.onMidiDevicesChanged()
        midiDevicesIn.remove(device)
        midiDevicesOut.remove(device)
    }
}

interface MidiDevicesChanged {
    fun onMidiDevicesChanged()
}