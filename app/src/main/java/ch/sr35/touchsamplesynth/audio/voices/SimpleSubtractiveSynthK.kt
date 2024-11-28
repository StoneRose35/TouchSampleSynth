package ch.sr35.touchsamplesynth.audio.voices

import android.content.Context
import androidx.appcompat.content.res.AppCompatResources
import ch.sr35.touchsamplesynth.R
import ch.sr35.touchsamplesynth.audio.AudioEngineK
import ch.sr35.touchsamplesynth.audio.AudioUtils
import ch.sr35.touchsamplesynth.audio.MusicalSoundGenerator
import kotlin.math.log10
import kotlin.math.pow

class SimpleSubtractiveSynthK(context: Context): MusicalSoundGenerator() {

    override fun bindToAudioEngine()
    {
        val audioEngine= AudioEngineK()
        if (instance == (-1).toByte()) {
            instance = audioEngine.addSoundGenerator(MAGIC_NR)
        }
    }

    override fun hashCode(): Int {
        return MAGIC_NR + instance
    }

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
    var actionAmountToFilter: Float=0.0f

    override fun copyParamsTo(other: MusicalSoundGenerator) {
        super.copyParamsTo(other)
        (other as SimpleSubtractiveSynthK).setDecay(getDecay())
        other.setAttack(getAttack())
        other.setSustain(getSustain())
        other.setRelease(getRelease())
        other.setInitialCutoff(getInitialCutoff())
        other.setResonance(getResonance())
        other.actionAmountToFilter = actionAmountToFilter
    }


    override fun applyTouchAction(a: Float) {
        if (AudioUtils.NoteToFreq (AudioUtils.FreqToNote(getInitialCutoff()) + a*actionAmountToFilter) > 20.0f &&
            AudioUtils.NoteToFreq(AudioUtils.FreqToNote(getInitialCutoff()) + a*actionAmountToFilter) < 20000.0f)
        {
            setCutoff(AudioUtils.NoteToFreq(AudioUtils.FreqToNote(getInitialCutoff())+a*actionAmountToFilter))
        }
        if (a > 0.0f) {
            setVolume(10.0f.pow(log10(a) * actionAmountToVolume))
        }
    }

    override fun equals(other: Any?): Boolean {
        if(other is SimpleSubtractiveSynthK)
        {
            return this.instance == other.instance
        }
        return false
    }

    companion object {
        const val MAGIC_NR = 1
    }
}