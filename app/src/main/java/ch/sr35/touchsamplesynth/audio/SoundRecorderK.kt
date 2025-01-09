package ch.sr35.touchsamplesynth.audio

interface SoundRecorder {

    fun startRecording(): Boolean
    fun stopRecording(): Boolean
    fun resetSample(): Boolean
}