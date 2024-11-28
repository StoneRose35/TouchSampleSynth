package ch.sr35.touchsamplesynth.audio.voices

import android.content.Context
import androidx.appcompat.content.res.AppCompatResources
import ch.sr35.touchsamplesynth.R
import ch.sr35.touchsamplesynth.audio.AudioEngineK
import ch.sr35.touchsamplesynth.audio.MusicalSoundGenerator


class SineMonoSynthK(context: Context): MusicalSoundGenerator() {
    override fun bindToAudioEngine()
    {
        val audioEngine= AudioEngineK()
        if (instance == (-1).toByte()) {
            instance = audioEngine.addSoundGenerator(SimpleSubtractiveSynthK.MAGIC_NR)
        }
    }

    override fun hashCode(): Int {
        return MAGIC_NR + instance
    }

    val icon=AppCompatResources.getDrawable(context,R.drawable.sinemonosynth)
    external fun setAttack(a: Float): Boolean
    external fun getAttack(): Float
    external fun setDecay(a: Float): Boolean
    external fun getDecay(): Float
    external fun setSustain(a: Float): Boolean
    external fun getSustain(): Float
    external fun setRelease(a: Float): Boolean
    external fun getRelease(): Float


    override fun copyParamsTo(other: MusicalSoundGenerator) {
        super.copyParamsTo(other)
        (other as SineMonoSynthK).setAttack(getAttack())
        other.setMidiMode(this.getMidiMode())
        other.setDecay(getDecay())
        other.setSustain(getSustain())
        other.setRelease(getRelease())

    }

    override fun equals(other: Any?): Boolean {
        if(other is SineMonoSynthK)
        {
            return this.instance == other.instance
        }
        return false
    }

    companion object {
        const val MAGIC_NR = 0
    }


}