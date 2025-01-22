
package ch.sr35.touchsamplesynth.audio.voices

import android.content.Context
import androidx.appcompat.content.res.AppCompatResources
import ch.sr35.touchsamplesynth.R
import ch.sr35.touchsamplesynth.audio.AudioEngineK
import ch.sr35.touchsamplesynth.audio.MusicalSoundGenerator

class SineMonoSynthK(context: Context): MusicalSoundGenerator() {

    override fun bindToAudioEngine() {
        val audioEngine= AudioEngineK()
        if (instance == (-1).toByte()) {
            instance = audioEngine.addSoundGenerator(MAGIC_NR)
        }
    }

    override fun hashCode(): Int {
        return (MAGIC_NR*1000) + instance
    }

    val icon= AppCompatResources.getDrawable(context, R.drawable.sinemonosynth)
    external fun setAttack(v: Float): Boolean
    external fun getAttack(): Float
    external fun setDecay(v: Float): Boolean
    external fun getDecay(): Float
    external fun setSustain(v: Float): Boolean
    external fun getSustain(): Float
    external fun setRelease(v: Float): Boolean
    external fun getRelease(): Float


    override fun copyParamsTo(other: MusicalSoundGenerator) {
        super.copyParamsTo(other)
        (other as SineMonoSynthK).setAttack(this.getAttack())
        other.setDecay(this.getDecay())
        other.setSustain(this.getSustain())
        other.setRelease(this.getRelease())
    }
 
    override fun equals(other: Any?): Boolean {
        if (other is SineMonoSynthK)
        {
            return other.hashCode() == this.hashCode()
        }
        return false
    }

    companion object
    {
        const val MAGIC_NR = 0
    }

}
    