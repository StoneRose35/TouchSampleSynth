package ch.sr35.touchsamplesynth.audio.voices

import android.content.Context
import androidx.appcompat.content.res.AppCompatResources
import ch.sr35.touchsamplesynth.R
import ch.sr35.touchsamplesynth.audio.AudioEngineK
import ch.sr35.touchsamplesynth.audio.AudioUtils
import ch.sr35.touchsamplesynth.audio.MusicalSoundGenerator

class SimpleSubtractiveSynthK(context: Context): MusicalSoundGenerator {

    private var instance: Byte=-1
    var actionAmount: Float=0.0f
    val icon=AppCompatResources.getDrawable(context,R.drawable.simplesubtractivesynth)
    external fun setAttack(a: Float): Boolean
    external fun getAttack(): Float
    external fun setDecay(a: Float): Boolean
    external fun getDecay(): Float
    external fun setSustain(a: Float): Boolean
    external fun getSustain(): Float
    external fun setRelease(a: Float): Boolean
    external fun getRelease(): Float
    external fun setCutoff(c: Float): Boolean
    external fun getCutoff():Float
    external fun setResonance(c: Float): Boolean
    external fun getResonance():Float
    external fun setInitialCutoff(ic: Float): Boolean
    external fun getInitialCutoff(): Float
    external fun switchOnExt(vel: Float): Boolean
    external fun switchOffExt(vel: Float):Boolean
    external override fun isSounding(): Boolean
    external override fun setMidiMode(midiMode: Int)
    external override fun getMidiMode(): Int
    override fun copyParamsTo(other: MusicalSoundGenerator) {
        (other as SimpleSubtractiveSynthK).setDecay(getDecay())
        other.setMidiMode(this.getMidiMode())
        other.setAttack(getAttack())
        other.setSustain(getSustain())
        other.setRelease(getRelease())
        other.setInitialCutoff(getInitialCutoff())
        other.setResonance(getResonance())
        other.actionAmount = actionAmount
    }



    external override fun setNote(note: Float): Boolean



    override fun getInstance(): Byte {
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
        if (instance == (-1).toByte()) {
            instance = audioEngine.addSoundGenerator(1)
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

    override fun switchOn(vel: Float): Boolean {
        return switchOnExt(vel)
    }

    override fun switchOff(vel: Float): Boolean{
        return switchOffExt(vel)
    }


    override fun applyTouchAction(a: Float) {
        if (AudioUtils.NoteToFreq (AudioUtils.FreqToNote(getInitialCutoff()) + a*actionAmount) > 20.0f &&
            AudioUtils.NoteToFreq(AudioUtils.FreqToNote(getInitialCutoff()) + a*actionAmount) < 20000.0f)
        {
            setCutoff(AudioUtils.NoteToFreq(AudioUtils.FreqToNote(getInitialCutoff())+a*actionAmount))
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