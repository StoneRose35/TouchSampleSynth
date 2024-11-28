package ch.sr35.touchsamplesynth.audio.voices

import android.content.Context
import androidx.appcompat.content.res.AppCompatResources
import ch.sr35.touchsamplesynth.R
import ch.sr35.touchsamplesynth.audio.AudioEngineK
import ch.sr35.touchsamplesynth.audio.MusicalSoundGenerator


class SamplerK(context: Context): MusicalSoundGenerator() {

    override fun bindToAudioEngine() {
        val audioEngine= AudioEngineK()
        if (instance == (-1).toByte()) {
            instance = audioEngine.addSoundGenerator(MAGIC_NR)
        }
    }

    override fun hashCode(): Int {
        return (MAGIC_NR*1000) + instance
    }

    val icon= AppCompatResources.getDrawable(context, R.drawable.sampler)
    external fun getLoopStartIndex(): Int
    external fun setLoopStartIndex(v: Int): Boolean
    external fun getLoopEndIndex(): Int
    external fun setLoopEndIndex(le: Int): Boolean
    external fun getSampleStartIndex(): Int
    external fun setSampleStartIndex(ss: Int): Boolean
    external fun getSampleEndIndex(): Int
    external fun setSampleEndIndex(se: Int): Boolean
    external fun setMode(mode: Byte): Boolean
    external fun getMode(): Byte
    external fun setSample(sampleData: FloatArray): Boolean
    external fun getSample(): FloatArray

    override fun copyParamsTo(other: MusicalSoundGenerator) {
        super.copyParamsTo(other)
        (other as SamplerK).setSample(this.getSample())
        other.setMidiMode(this.getMidiMode())
        other.setMode(this.getMode())
        other.setSampleStartIndex(this.getSampleStartIndex())
        other.setSampleEndIndex(this.getSampleEndIndex())
        other.setLoopStartIndex(this.getLoopStartIndex())
        other.setLoopEndIndex(this.getLoopEndIndex())
    }

    override fun equals(other: Any?): Boolean {
        if (other is SamplerK)
        {
            return other.hashCode() == this.hashCode()
        }
        return false
    }

    companion object
    {
        const val MAGIC_NR = 3
    }

}