package ch.sr35.touchsamplesynth.audio.voices

import android.content.Context
import androidx.appcompat.content.res.AppCompatResources
import ch.sr35.touchsamplesynth.R
import ch.sr35.touchsamplesynth.audio.AudioEngineK
import ch.sr35.touchsamplesynth.audio.MusicalSoundGenerator

class SineMonoSynthK(context: Context): MusicalSoundGenerator {

    private var instance: Byte=-1
    val icon=AppCompatResources.getDrawable(context,R.drawable.sinemonosynth)

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
    external override fun isSounding(): Boolean
    external override fun setMidiMode(midiMode: Int)
    external override fun getMidiMode(): Int
    override fun copyParamsTo(other: MusicalSoundGenerator) {
        (other as SineMonoSynthK).setAttack(getAttack())
        other.setMidiMode(this.getMidiMode())
        other.setDecay(getDecay())
        other.setSustain(getSustain())
        other.setRelease(getRelease())
    }

    companion object {
        // Used to load the 'touchsamplesynth' library on application startup.
        init {
            System.loadLibrary("touchsamplesynth")
        }
    }

    override fun bindToAudioEngine()
    {
        val audioEngine= AudioEngineK()
        if (instance == (-1).toByte()) {
            instance = audioEngine.addSoundGenerator(0)
        }
    }

    override fun detachFromAudioEngine()
    {
        val audioEngine = AudioEngineK()
        if (instance > -1)
        {
            audioEngine.removeSoundGenerator(instance)
            instance=(-1).toByte()
        }
    }

    override fun applyTouchAction(a: Float) {

    }




    override fun getInstance(): Byte
    {
        return instance
    }

    override fun generateAttachedInstance(context: Context): MusicalSoundGenerator {
        val instance = SineMonoSynthK(context)
        instance.bindToAudioEngine()
        return instance
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