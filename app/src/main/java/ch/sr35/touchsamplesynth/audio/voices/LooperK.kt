
package ch.sr35.touchsamplesynth.audio.voices

import android.content.Context
import androidx.appcompat.content.res.AppCompatResources
import ch.sr35.touchsamplesynth.R
import ch.sr35.touchsamplesynth.audio.AudioEngineK
import ch.sr35.touchsamplesynth.audio.MusicalSoundGenerator
import ch.sr35.touchsamplesynth.audio.SoundRecorder

class LooperK(context: Context): MusicalSoundGenerator(), SoundRecorder {

    override fun bindToAudioEngine() {
        val audioEngine= AudioEngineK()
        if (instance == (-1).toByte()) {
            instance = audioEngine.addSoundGenerator(MAGIC_NR)
        }
    }

    override fun hashCode(): Int {
        return (MAGIC_NR*1000) + instance
    }

    val icon= AppCompatResources.getDrawable(context, R.drawable.looper)
    external fun getReadPointer(): Int
    external fun setReadPointer(v: Int): Boolean
    external fun getWritePointer(): Int
    external fun setWritePointer(v: Int): Boolean
    external fun getLoopEnd(): Int
    external fun setLoopEnd(v: Int): Boolean
    external fun getSample(): FloatArray
    external fun setSample(v: FloatArray): Boolean


    override fun copyParamsTo(other: MusicalSoundGenerator) {
        super.copyParamsTo(other)
        (other as LooperK).setReadPointer(this.getReadPointer())
        other.setWritePointer(this.getWritePointer())
        other.setLoopEnd(this.getLoopEnd())
    }

    external override fun startRecording(): Boolean

    external override fun stopRecording(): Boolean

    external override fun resetSample(): Boolean
    external override fun hasRecordedContent(): Boolean

    external fun getRecordGain(): Float
    external fun setRecordGain(v: Float): Boolean

    override fun equals(other: Any?): Boolean {
        if (other is LooperK)
        {
            return other.hashCode() == this.hashCode()
        }
        return false
    }

    companion object
    {
        const val MAGIC_NR = 2
    }

}
    