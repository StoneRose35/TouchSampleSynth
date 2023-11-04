package ch.sr35.touchsamplesynth.audio.instruments

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.appcompat.content.res.AppCompatResources
import ch.sr35.touchsamplesynth.R
import ch.sr35.touchsamplesynth.audio.Instrument
import ch.sr35.touchsamplesynth.audio.MusicalSoundGenerator
import ch.sr35.touchsamplesynth.audio.voices.SineMonoSynthK

class SineMonoSynthI(private val context: Context, override val voices: ArrayList<MusicalSoundGenerator>?,
                     name: String
) : Instrument(name)  {
    val icon= AppCompatResources.getDrawable(context, R.drawable.sinemonosynth)

    override fun getType(): String {
        return "SineMonoSynth"
    }

    override fun getInstrumentIcon(): Drawable? {
        return icon
    }

    override fun generateVoices(cnt: Int) {
        if (voices != null) {
            val doCopy = voices.isNotEmpty()
            for (i in 0 until cnt) {
                voices.add(SineMonoSynthK(context).generateAttachedInstance(context))
                if (doCopy)
                {
                    voices[0].copyParamsTo(voices[voices.size-1])
                }
            }
        }
    }

    fun getAttack(): Float
    {
        if (voices?.isNotEmpty() == true)
        {
            return (voices[0] as SineMonoSynthK).getAttack()
        }
        return 0.0f
    }

    fun setAttack(v: Float)
    {
        for (voice in voices!!)
        {
            (voice as SineMonoSynthK).setAttack(v)
        }
    }

    fun getDecay(): Float
    {
        if (voices?.isNotEmpty() == true)
        {
            return (voices[0] as SineMonoSynthK).getDecay()
        }
        return 0.0f
    }

    fun setDecay(v: Float)
    {
        for (voice in voices!!)
        {
            (voice as SineMonoSynthK).setDecay(v)
        }
    }

    fun getSustain(): Float
    {
        if (voices?.isNotEmpty() == true)
        {
            return (voices[0] as SineMonoSynthK).getSustain()
        }
        return 0.0f
    }

    fun setSustain(v: Float)
    {
        for (voice in voices!!)
        {
            (voice as SineMonoSynthK).setSustain(v)
        }
    }

    fun getRelease(): Float
    {
        if (voices?.isNotEmpty() == true)
        {
            return (voices[0] as SineMonoSynthK).getRelease()
        }
        return 0.0f
    }

    fun setRelease(v: Float)
    {
        for (voice in voices!!)
        {
            (voice as SineMonoSynthK).setRelease(v)
        }
    }

    override fun generateInstance(nVoices: Int, n: String): Instrument {
        return SineMonoSynthI(context,ArrayList(),"")
    }

    companion object
    {
        fun generateInstance(context: Context, name: String): Instrument {
            val vcs = ArrayList<MusicalSoundGenerator>()
            return SineMonoSynthI(context, vcs, name)
        }
    }
}