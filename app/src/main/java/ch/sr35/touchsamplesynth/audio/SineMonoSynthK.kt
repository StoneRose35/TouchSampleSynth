package ch.sr35.touchsamplesynth.audio

class SineMonoSynthK: MusicalSoundGenerator {

    private var instance: Int=-1


    external fun setAttack(a: Float): Boolean
    external fun getAttack(): Float
    external fun setDecay(a: Float): Boolean
    external fun getDecay(): Float
    external fun setSustain(a: Float): Boolean
    external fun getSustain(): Float
    external fun setRelease(a: Float): Boolean
    external fun getRelease(): Float
    external override fun switchOn(vel: Float): Boolean
    external override fun switchOff(vel:Float): Boolean
    external override fun setNote(note: Float): Boolean
    companion object {
        // Used to load the 'touchsamplesynth' library on application startup.
        init {
            System.loadLibrary("touchsamplesynth")
        }
    }

    fun bindToAudioEngine()
    {
        val audioEngine= AudioEngineK()
        if (instance == -1) {
            instance = audioEngine.addSoundGenerator(0)
        }
    }

    fun detachFromAudioEngine()
    {
        val audioEngine = AudioEngineK()
        if (instance > -1)
        {
            audioEngine.removeSoundGenerator(instance)
        }
    }

    override fun getInstance(): Int
    {
        return instance
    }

    override fun getType(): String {
        return "SineMonoSynth"
    }

    override fun equals(other: Any?): Boolean {
        if(other is SineMonoSynthK)
        {
            return this.instance == other.instance
        }
        return false
    }

    override fun hashCode(): Int {
        return 1000 + instance
    }


}