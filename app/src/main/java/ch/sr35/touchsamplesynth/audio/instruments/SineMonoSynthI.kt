package ch.sr35.touchsamplesynth.audio.instruments

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.appcompat.content.res.AppCompatResources
import ch.sr35.touchsamplesynth.R
import ch.sr35.touchsamplesynth.audio.InstrumentI
import ch.sr35.touchsamplesynth.audio.MusicalSoundGenerator
import ch.sr35.touchsamplesynth.audio.voices.SineMonoSynthK

class SineMonoSynthI(private val context: Context,
                     name: String
) : InstrumentI(name)  {
    val icon= AppCompatResources.getDrawable(context, R.drawable.sinemonosynth)

    init {
        voices = ArrayList()
    }
    override fun getType(): String {
        return "SineMonoSynth"
    }

    override fun getInstrumentIcon(): Drawable? {
        return icon
    }

    override fun generateVoices(cnt: Int) {
        val doCopy = voices.isNotEmpty()
        for (i in 0 until cnt) {
            voices.add(MusicalSoundGenerator.generateAttachedInstance<SineMonoSynthK>(context))
            if (doCopy)
            {
                voices[0].copyParamsTo(voices[voices.size-1])
            }
        }
    }

    fun getAttack(): Float
    {
        if (voices.isNotEmpty() )
        {
            return (voices[0] as SineMonoSynthK).getAttack()
        }
        return 0.0f
    }

    fun setAttack(v: Float)
    {
        for (voice in voices)
        {
            (voice as SineMonoSynthK).setAttack(v)
        }
    }

    fun getDecay(): Float
    {
        if (voices.isNotEmpty())
        {
            return (voices[0] as SineMonoSynthK).getDecay()
        }
        return 0.0f
    }

    fun setDecay(v: Float)
    {
        for (voice in voices)
        {
            (voice as SineMonoSynthK).setDecay(v)
        }
    }

    fun getSustain(): Float
    {
        if (voices.isNotEmpty())
        {
            return (voices[0] as SineMonoSynthK).getSustain()
        }
        return 0.0f
    }

    fun setSustain(v: Float)
    {
        for (voice in voices)
        {
            (voice as SineMonoSynthK).setSustain(v)
        }
    }

    fun getRelease(): Float
    {
        if (voices.isNotEmpty())
        {
            return (voices[0] as SineMonoSynthK).getRelease()
        }
        return 0.0f
    }

    fun setRelease(v: Float)
    {
        for (voice in voices)
        {
            (voice as SineMonoSynthK).setRelease(v)
        }
    }

}