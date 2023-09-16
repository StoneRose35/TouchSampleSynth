package ch.sr35.touchsamplesynth.audio.instruments

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.appcompat.content.res.AppCompatResources
import ch.sr35.touchsamplesynth.R
import ch.sr35.touchsamplesynth.audio.AudioEngineK
import ch.sr35.touchsamplesynth.audio.MusicalSoundGenerator

class SimpleSubtractiveSynthK(context: Context): MusicalSoundGenerator {

    private var instance: Int=-1
    val icon=AppCompatResources.getDrawable(context,R.drawable.simplesubtractivesynth)
    external fun setAttack(a: Float): Boolean
    external fun getAttack(): Float
    external fun setDecay(a: Float): Boolean
    external fun getDecay(): Float
    external fun setSustain(a: Float): Boolean
    external fun getSustain(): Float
    external fun setRelease(a: Float): Boolean
    external fun getRelease(): Float
    external override fun setNote(note: Float): Boolean
    external override fun switchOn(vel: Float): Boolean
    external override fun switchOff(vel: Float): Boolean

    override fun getType(): String {
        return "SimpleSubtractiveSynth"
    }

    override fun getInstrumentIcon(): Drawable? {
        return icon
    }

    override fun getInstance(): Int {
        return instance
    }

    override fun generateAttachedInstance(context: Context): MusicalSoundGenerator {
        val instance = SimpleSubtractiveSynthK(context)
        instance.bindToAudioEngine()
        return instance
    }

    override fun bindToAudioEngine()
    {
        val audioEngine= AudioEngineK()
        if (instance == -1) {
            instance = audioEngine.addSoundGenerator(1)
        }
    }

    override fun detachFromAudioEngine()
    {
        val audioEngine = AudioEngineK()
        if (instance > -1)
        {
            audioEngine.removeSoundGenerator(instance)
        }
    }

    override fun equals(other: Any?): Boolean {
        if(other is SimpleSubtractiveSynthK)
        {
            return this.instance == other.instance
        }
        return false
    }

    override fun hashCode(): Int {
        return 2000 + instance
    }
    companion object {
        // Used to load the 'touchsamplesynth' library on application startup.
        init {
            System.loadLibrary("touchsamplesynth")
        }
    }
}