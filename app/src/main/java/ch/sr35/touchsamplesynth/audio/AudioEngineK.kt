package ch.sr35.touchsamplesynth.audio

class AudioEngineK {

    external fun startEngine()
    external fun stopEngine()
    external fun getSamplingRate(): Int
    external fun addSoundGenerator(type: Int): Int
    external fun removeSoundGenerator(idx: Int): Int
    external fun getAverageVolume(): Float
    external fun getCpuLoad(): Float

    // only for testing purposes
    external fun playFrames(nFrames: Int)

}