package ch.sr35.touchsamplesynth.audio.instruments

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.appcompat.content.res.AppCompatResources
import ch.sr35.touchsamplesynth.R
import ch.sr35.touchsamplesynth.audio.Instrument
import ch.sr35.touchsamplesynth.audio.MusicalSoundGenerator
import ch.sr35.touchsamplesynth.audio.voices.SimpleSubtractiveSynthK

class SimpleSubtractiveSynthI(private val context: Context,
                              override val voices: ArrayList<MusicalSoundGenerator>?,
                              name: String
) : Instrument(name) {
    val icon= AppCompatResources.getDrawable(context, R.drawable.simplesubtractivesynth)

    override fun getType(): String {
        return "SimpleSubtractiveSynth"
    }

    override fun getInstrumentIcon(): Drawable? {
        return icon
    }

    override fun generateVoices(cnt: Int) {
        if (voices != null) {
            val doCopy = voices.isNotEmpty()
            for (i in 0 until cnt) {
                voices.add(SimpleSubtractiveSynthK(context).generateAttachedInstance(context))
                if (doCopy) {
                    (voices[voices.size - 1] as SimpleSubtractiveSynthK).setAttack((voices[0] as SimpleSubtractiveSynthK).getAttack())
                    (voices[voices.size - 1] as SimpleSubtractiveSynthK).setDecay((voices[0] as SimpleSubtractiveSynthK).getDecay())
                    (voices[voices.size - 1] as SimpleSubtractiveSynthK).setSustain((voices[0] as SimpleSubtractiveSynthK).getSustain())
                    (voices[voices.size - 1] as SimpleSubtractiveSynthK).setRelease((voices[0] as SimpleSubtractiveSynthK).getRelease())
                    (voices[voices.size - 1] as SimpleSubtractiveSynthK).initialCutoff = (voices[0] as SimpleSubtractiveSynthK).initialCutoff
                    (voices[voices.size - 1] as SimpleSubtractiveSynthK).setResonance((voices[0] as SimpleSubtractiveSynthK).getResonance())
                    (voices[voices.size - 1] as SimpleSubtractiveSynthK).actionAmount = (voices[0] as SimpleSubtractiveSynthK).actionAmount
                }
            }
        }
    }

    fun getAttack(): Float
    {
        if (voices?.isNotEmpty() == true)
        {
            return (voices[0] as SimpleSubtractiveSynthK).getAttack()
        }
        return 0.0f
    }

    fun setAttack(v: Float)
    {
        for (voice in voices!!)
        {
            (voice as SimpleSubtractiveSynthK).setAttack(v)
        }
    }

    fun getDecay(): Float
    {
        if (voices?.isNotEmpty() == true)
        {
            return (voices[0] as SimpleSubtractiveSynthK).getDecay()
        }
        return 0.0f
    }

    fun setDecay(v: Float)
    {
        for (voice in voices!!)
        {
            (voice as SimpleSubtractiveSynthK).setDecay(v)
        }
    }

    fun getSustain(): Float
    {
        if (voices?.isNotEmpty() == true)
        {
            return (voices[0] as SimpleSubtractiveSynthK).getSustain()
        }
        return 0.0f
    }

    fun setSustain(v: Float)
    {
        for (voice in voices!!)
        {
            (voice as SimpleSubtractiveSynthK).setSustain(v)
        }
    }

    fun getRelease(): Float
    {
        if (voices?.isNotEmpty() == true)
        {
            return (voices[0] as SimpleSubtractiveSynthK).getSustain()
        }
        return 0.0f
    }

    fun setRelease(v: Float)
    {
        for (voice in voices!!)
        {
            (voice as SimpleSubtractiveSynthK).setRelease(v)
        }
    }


    fun setActionAmount(v: Float)
    {
        for (voice in voices!!)
        {
            (voice as SimpleSubtractiveSynthK).actionAmount = v
        }
    }

    fun getActionAmount(): Float
    {
        if (voices?.isNotEmpty() == true)
        {
            return (voices[0] as SimpleSubtractiveSynthK).actionAmount
        }
        return 0.0f
    }

    fun setResonance(v: Float)
    {
        for (voice in voices!!)
        {
            (voice as SimpleSubtractiveSynthK).setResonance(v)
        }
    }

    fun getResonance(): Float
    {
        if (voices?.isNotEmpty() == true)
        {
            return (voices[0] as SimpleSubtractiveSynthK).getResonance()
        }
        return 0.0f
    }

    fun getInitialCutoff(): Float
    {
        if (voices?.isNotEmpty() == true) {
            return (voices[0] as SimpleSubtractiveSynthK).initialCutoff
        }
        return 0.0f
    }

    fun setInitialCutoff(v: Float)
    {
        for (voice in voices!!)
        {
            (voice as SimpleSubtractiveSynthK).initialCutoff = v
        }
    }

    override fun generateInstance(nVoices: Int, n: String): Instrument {
        return SimpleSubtractiveSynthI(context,ArrayList(),"")
    }

    companion object
    {
        fun generateInstance(context: Context, name: String): Instrument {
            val vcs = ArrayList<MusicalSoundGenerator>()
            return SimpleSubtractiveSynthI(context, vcs, name)
        }
    }

}