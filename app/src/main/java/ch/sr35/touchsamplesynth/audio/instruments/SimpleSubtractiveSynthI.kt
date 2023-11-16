package ch.sr35.touchsamplesynth.audio.instruments

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.appcompat.content.res.AppCompatResources
import ch.sr35.touchsamplesynth.R
import ch.sr35.touchsamplesynth.audio.Instrument
import ch.sr35.touchsamplesynth.audio.voices.SimpleSubtractiveSynthK

class SimpleSubtractiveSynthI(private val context: Context,
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

        val doCopy = voices.isNotEmpty()
        for (i in 0 until cnt) {
            voices.add(SimpleSubtractiveSynthK(context).generateAttachedInstance(context))
            if (doCopy) {
                voices[0].copyParamsTo(voices[voices.size-1])
            }
        }

    }

    fun getAttack(): Float
    {
        if (voices.isNotEmpty())
        {
            return (voices[0] as SimpleSubtractiveSynthK).getAttack()
        }
        return 0.0f
    }

    fun setAttack(v: Float)
    {
        for (voice in voices)
        {
            (voice as SimpleSubtractiveSynthK).setAttack(v)
        }
    }

    fun getDecay(): Float
    {
        if (voices.isNotEmpty())
        {
            return (voices[0] as SimpleSubtractiveSynthK).getDecay()
        }
        return 0.0f
    }

    fun setDecay(v: Float)
    {
        for (voice in voices)
        {
            (voice as SimpleSubtractiveSynthK).setDecay(v)
        }
    }

    fun getSustain(): Float
    {
        if (voices.isNotEmpty())
        {
            return (voices[0] as SimpleSubtractiveSynthK).getSustain()
        }
        return 0.0f
    }

    fun setSustain(v: Float)
    {
        for (voice in voices)
        {
            (voice as SimpleSubtractiveSynthK).setSustain(v)
        }
    }

    fun getRelease(): Float
    {
        if (voices.isNotEmpty())
        {
            return (voices[0] as SimpleSubtractiveSynthK).getRelease()
        }
        return 0.0f
    }

    fun setRelease(v: Float)
    {
        for (voice in voices)
        {
            (voice as SimpleSubtractiveSynthK).setRelease(v)
        }
    }


    fun setActionAmount(v: Float)
    {
        for (voice in voices)
        {
            (voice as SimpleSubtractiveSynthK).actionAmount = v
        }
    }

    fun getActionAmount(): Float
    {
        if (voices.isNotEmpty())
        {
            return (voices[0] as SimpleSubtractiveSynthK).actionAmount
        }
        return 0.0f
    }

    fun setResonance(v: Float)
    {
        for (voice in voices)
        {
            (voice as SimpleSubtractiveSynthK).setResonance(v)
        }
    }

    fun getResonance(): Float
    {
        if (voices.isNotEmpty())
        {
            return (voices[0] as SimpleSubtractiveSynthK).getResonance()
        }
        return 0.0f
    }

    fun getInitialCutoff(): Float
    {
        if (voices.isNotEmpty()) {
            return (voices[0] as SimpleSubtractiveSynthK).getInitialCutoff()
        }
        return 0.0f
    }

    fun setInitialCutoff(v: Float)
    {
        for (voice in voices)
        {
            (voice as SimpleSubtractiveSynthK).setInitialCutoff(v)
        }
    }

    fun getCutoff(): Float
    {
        if (voices.isNotEmpty()) {
            return (voices[0] as SimpleSubtractiveSynthK).getCutoff()
        }
        return 0.0f
    }

    fun setCutoff(v: Float)
    {
        for (voice in voices)
        {
            (voice as SimpleSubtractiveSynthK).setCutoff(v)
        }
    }

}