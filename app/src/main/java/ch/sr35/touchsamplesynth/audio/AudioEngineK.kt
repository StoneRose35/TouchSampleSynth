package ch.sr35.touchsamplesynth.audio

import android.media.midi.MidiDevice

class AudioEngineK {

    external fun startEngine(): Boolean
    external fun stopEngine()
    external fun getSamplingRate(): Int
    external fun addSoundGenerator(type: Int): Byte
    external fun removeSoundGenerator(idx: Byte): Byte
    external fun getAverageVolume(): Float
    external fun getCpuLoad(): Float
    // only for testing purposes
    external fun playFrames(nFrames: Int)
    external fun openMidiDeviceIn(midiDevice: MidiDevice,portNr: Int)
    external fun closeMidiDeviceIn()
    external fun openMidiDeviceOut(midiDevice: MidiDevice,portNr: Int)
    external fun closeMidiDeviceOut()
    external fun setFramesPerDataCallback(fpdc: Int): Int
    external fun getFramesPerDataCallback(): Int
    external fun setBufferCapacityInFrames(bcif: Int): Int
    external fun getBufferCapacityInFrames(): Int
    external fun emptySoundGenerators(): Byte
}