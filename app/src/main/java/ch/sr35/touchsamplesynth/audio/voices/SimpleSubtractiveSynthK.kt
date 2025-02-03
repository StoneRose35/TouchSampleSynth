
package ch.sr35.touchsamplesynth.audio.voices

import android.content.Context
import androidx.appcompat.content.res.AppCompatResources
import ch.sr35.touchsamplesynth.R
import ch.sr35.touchsamplesynth.audio.AudioEngineK
import ch.sr35.touchsamplesynth.audio.AudioUtils
import ch.sr35.touchsamplesynth.audio.MusicalSoundGenerator

class SimpleSubtractiveSynthK(context: Context): MusicalSoundGenerator() {

    override fun bindToAudioEngine() {
        val audioEngine= AudioEngineK()
        if (instance == (-1).toByte()) {
            instance = audioEngine.addSoundGenerator(MAGIC_NR)
        }
    }

    override fun applyTouchActionA(a: Float) {
        if (AudioUtils.NoteToFreq (AudioUtils.FreqToNote(getInitialCutoff()) + a*actionAmountToFilter) > 20.0f &&
            AudioUtils.NoteToFreq(AudioUtils.FreqToNote(getInitialCutoff()) + a*actionAmountToFilter) < 20000.0f)
        {
            setCutoff(AudioUtils.NoteToFreq(AudioUtils.FreqToNote(getInitialCutoff())+a*actionAmountToFilter))
        }
        super.applyTouchActionA(a)
    }

    override fun hashCode(): Int {
        return (MAGIC_NR*1000) + instance
    }

    val icon= AppCompatResources.getDrawable(context, R.drawable.simplesubtractivesynth)
    external fun setVolumeAttack(v: Float): Boolean
    external fun getVolumeAttack(): Float
    external fun setVolumeDecay(v: Float): Boolean
    external fun getVolumeDecay(): Float
    external fun setVolumeSustain(v: Float): Boolean
    external fun getVolumeSustain(): Float
    external fun setVolumeRelease(v: Float): Boolean
    external fun getVolumeRelease(): Float
    external fun setFilterAttack(v: Float): Boolean
    external fun getFilterAttack(): Float
    external fun setFilterDecay(v: Float): Boolean
    external fun getFilterDecay(): Float
    external fun setFilterSustain(v: Float): Boolean
    external fun getFilterSustain(): Float
    external fun setFilterRelease(v: Float): Boolean
    external fun getFilterRelease(): Float
    external fun getFilterEnvelopeLevel(): Float
    external fun setFilterEnvelopeLevel(v: Float): Boolean
    external fun setOsc1Type(v: Byte): Boolean
    external fun getOsc1Type(): Byte
    external fun setOsc2Type(v: Byte): Boolean
    external fun getOsc2Type(): Byte
    external fun getOsc2Octave(): Byte
    external fun setOsc2Octave(v: Byte): Boolean
    external fun getOsc2Detune(): Float
    external fun setOsc2Detune(v: Float): Boolean
    external fun getOsc2Volume(): Float
    external fun setOsc2Volume(v: Float): Boolean
    external fun getOsc1PulseWidth(): Float
    external fun setOsc1PulseWidth(v: Float): Boolean
    external fun getOsc2PulseWidth(): Float
    external fun setOsc2PulseWidth(v: Float): Boolean
    external fun setCutoff(v: Float): Boolean
    external fun getCutoff(): Float
    external fun setResonance(v: Float): Boolean
    external fun getResonance(): Float
    external fun setInitialCutoff(ic: Float): Boolean
    external fun getInitialCutoff(): Float
    var actionAmountToFilter: Float=0.0f


    override fun copyParamsTo(other: MusicalSoundGenerator) {
        super.copyParamsTo(other)
        (other as SimpleSubtractiveSynthK).setVolumeAttack(this.getVolumeAttack())
        other.setVolumeDecay(this.getVolumeDecay())
        other.setVolumeSustain(this.getVolumeSustain())
        other.setVolumeRelease(this.getVolumeRelease())
        other.setFilterAttack(this.getFilterAttack())
        other.setFilterDecay(this.getFilterDecay())
        other.setFilterSustain(this.getFilterSustain())
        other.setFilterRelease(this.getFilterRelease())
        other.setFilterEnvelopeLevel(this.getFilterEnvelopeLevel())
        other.setOsc1Type(this.getOsc1Type())
        other.setOsc2Type(this.getOsc2Type())
        other.setOsc2Octave(this.getOsc2Octave())
        other.setOsc2Detune(this.getOsc2Detune())
        other.setOsc2Volume(this.getOsc2Volume())
        other.setOsc1PulseWidth(this.getOsc1PulseWidth())
        other.setOsc2PulseWidth(this.getOsc2PulseWidth())
        other.setCutoff(this.getCutoff())
        other.setResonance(this.getResonance())
        other.setInitialCutoff(this.getInitialCutoff())
    }
 
    override fun equals(other: Any?): Boolean {
        if (other is SimpleSubtractiveSynthK)
        {
            return other.hashCode() == this.hashCode()
        }
        return false
    }

    companion object
    {
        const val MAGIC_NR = 1
    }

}
    