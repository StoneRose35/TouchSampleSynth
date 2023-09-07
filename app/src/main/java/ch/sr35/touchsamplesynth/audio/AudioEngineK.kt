package ch.sr35.touchsamplesynth.audio

class AudioEngineK {

    external fun startEngine()
    external fun stopEngine()
    external fun getSamplingRate(): Int

    external fun addSoundGenerator(type: Int): Int

    external fun removeSoundGenerator(idx: Int): Int
}