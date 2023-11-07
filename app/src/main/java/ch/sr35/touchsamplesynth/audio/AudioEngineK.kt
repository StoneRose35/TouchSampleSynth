package ch.sr35.touchsamplesynth.audio

import android.media.midi.MidiDevice

class AudioEngineK {

    external fun startEngine()
    external fun stopEngine()
    external fun getSamplingRate(): Int
    external fun addSoundGenerator(type: Int): Byte
    external fun removeSoundGenerator(idx: Byte): Byte
    external fun getAverageVolume(): Float
    external fun getCpuLoad(): Float

    // only for testing purposes
    external fun playFrames(nFrames: Int)

    external fun openMidiDevice(midiDevice: MidiDevice,portNr: Int)

    external fun closeMidiDevice()

}