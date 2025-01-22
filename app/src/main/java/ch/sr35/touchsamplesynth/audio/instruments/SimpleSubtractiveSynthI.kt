
package ch.sr35.touchsamplesynth.audio.instruments

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.appcompat.content.res.AppCompatResources
import ch.sr35.touchsamplesynth.R
import ch.sr35.touchsamplesynth.audio.instruments.InstrumentI
import ch.sr35.touchsamplesynth.audio.MusicalSoundGenerator
import ch.sr35.touchsamplesynth.audio.voices.SimpleSubtractiveSynthK

class SimpleSubtractiveSynthI(private val context: Context,
                     name: String
) : InstrumentI(name)  {
    val icon= AppCompatResources.getDrawable(context, R.drawable.simplesubtractivesynth)

    init {
        voices = ArrayList()
    }
    override fun getType(): String {
        return "SimpleSubtractiveSynth"
    }

    override fun getInstrumentIcon(): Drawable? {
        return icon
    }

    override fun generateVoices(cnt: Int) {
        val doCopy = voices.isNotEmpty()
        for (i in 0 until cnt) {
            voices.add(MusicalSoundGenerator.generateAttachedInstance<SimpleSubtractiveSynthK>(context))
            if (doCopy)
            {
                voices[0].copyParamsTo(voices[voices.size-1])
            }
        }
    }

        
    fun setVolumeAttack(v: Float)
    {
        for (voice in voices)
        {
            (voice as SimpleSubtractiveSynthK).setVolumeAttack(v)
        }
    }
        
    fun getVolumeAttack(): Float
    {
        if (voices.isNotEmpty() )
        {
            return (voices[0] as SimpleSubtractiveSynthK).getVolumeAttack()
        }
        return 0.0f
    }
    
    fun setVolumeDecay(v: Float)
    {
        for (voice in voices)
        {
            (voice as SimpleSubtractiveSynthK).setVolumeDecay(v)
        }
    }
        
    fun getVolumeDecay(): Float
    {
        if (voices.isNotEmpty() )
        {
            return (voices[0] as SimpleSubtractiveSynthK).getVolumeDecay()
        }
        return 0.0f
    }
    
    fun setVolumeSustain(v: Float)
    {
        for (voice in voices)
        {
            (voice as SimpleSubtractiveSynthK).setVolumeSustain(v)
        }
    }
        
    fun getVolumeSustain(): Float
    {
        if (voices.isNotEmpty() )
        {
            return (voices[0] as SimpleSubtractiveSynthK).getVolumeSustain()
        }
        return 0.0f
    }
    
    fun setVolumeRelease(v: Float)
    {
        for (voice in voices)
        {
            (voice as SimpleSubtractiveSynthK).setVolumeRelease(v)
        }
    }
        
    fun getVolumeRelease(): Float
    {
        if (voices.isNotEmpty() )
        {
            return (voices[0] as SimpleSubtractiveSynthK).getVolumeRelease()
        }
        return 0.0f
    }
    
    fun setFilterAttack(v: Float)
    {
        for (voice in voices)
        {
            (voice as SimpleSubtractiveSynthK).setFilterAttack(v)
        }
    }
        
    fun getFilterAttack(): Float
    {
        if (voices.isNotEmpty() )
        {
            return (voices[0] as SimpleSubtractiveSynthK).getFilterAttack()
        }
        return 0.0f
    }
    
    fun setFilterDecay(v: Float)
    {
        for (voice in voices)
        {
            (voice as SimpleSubtractiveSynthK).setFilterDecay(v)
        }
    }
        
    fun getFilterDecay(): Float
    {
        if (voices.isNotEmpty() )
        {
            return (voices[0] as SimpleSubtractiveSynthK).getFilterDecay()
        }
        return 0.0f
    }
    
    fun setFilterSustain(v: Float)
    {
        for (voice in voices)
        {
            (voice as SimpleSubtractiveSynthK).setFilterSustain(v)
        }
    }
        
    fun getFilterSustain(): Float
    {
        if (voices.isNotEmpty() )
        {
            return (voices[0] as SimpleSubtractiveSynthK).getFilterSustain()
        }
        return 0.0f
    }
    
    fun setFilterRelease(v: Float)
    {
        for (voice in voices)
        {
            (voice as SimpleSubtractiveSynthK).setFilterRelease(v)
        }
    }
        
    fun getFilterRelease(): Float
    {
        if (voices.isNotEmpty() )
        {
            return (voices[0] as SimpleSubtractiveSynthK).getFilterRelease()
        }
        return 0.0f
    }
    
    fun getFilterEnvelopeLevel(): Float
    {
        if (voices.isNotEmpty() )
        {
            return (voices[0] as SimpleSubtractiveSynthK).getFilterEnvelopeLevel()
        }
        return 0.0f
    }
    
    fun setFilterEnvelopeLevel(v: Float)
    {
        for (voice in voices)
        {
            (voice as SimpleSubtractiveSynthK).setFilterEnvelopeLevel(v)
        }
    }
        
    fun setOsc1Type(v: Byte)
    {
        for (voice in voices)
        {
            (voice as SimpleSubtractiveSynthK).setOsc1Type(v)
        }
    }
        
    fun getOsc1Type(): Byte
    {
        if (voices.isNotEmpty() )
        {
            return (voices[0] as SimpleSubtractiveSynthK).getOsc1Type()
        }
        return 0
    }
    
    fun setOsc2Type(v: Byte)
    {
        for (voice in voices)
        {
            (voice as SimpleSubtractiveSynthK).setOsc2Type(v)
        }
    }
        
    fun getOsc2Type(): Byte
    {
        if (voices.isNotEmpty() )
        {
            return (voices[0] as SimpleSubtractiveSynthK).getOsc2Type()
        }
        return 0
    }
    
    fun getOsc2Octave(): Byte
    {
        if (voices.isNotEmpty() )
        {
            return (voices[0] as SimpleSubtractiveSynthK).getOsc2Octave()
        }
        return 0
    }
    
    fun setOsc2Octave(v: Byte)
    {
        for (voice in voices)
        {
            (voice as SimpleSubtractiveSynthK).setOsc2Octave(v)
        }
    }
        
    fun getOsc2Detune(): Float
    {
        if (voices.isNotEmpty() )
        {
            return (voices[0] as SimpleSubtractiveSynthK).getOsc2Detune()
        }
        return 0.0f
    }
    
    fun setOsc2Detune(v: Float)
    {
        for (voice in voices)
        {
            (voice as SimpleSubtractiveSynthK).setOsc2Detune(v)
        }
    }
        
    fun getOsc2Volume(): Float
    {
        if (voices.isNotEmpty() )
        {
            return (voices[0] as SimpleSubtractiveSynthK).getOsc2Volume()
        }
        return 0.0f
    }
    
    fun setOsc2Volume(v: Float)
    {
        for (voice in voices)
        {
            (voice as SimpleSubtractiveSynthK).setOsc2Volume(v)
        }
    }
        
    fun getOsc1PulseWidth(): Float
    {
        if (voices.isNotEmpty() )
        {
            return (voices[0] as SimpleSubtractiveSynthK).getOsc1PulseWidth()
        }
        return 0.0f
    }
    
    fun setOsc1PulseWidth(v: Float)
    {
        for (voice in voices)
        {
            (voice as SimpleSubtractiveSynthK).setOsc1PulseWidth(v)
        }
    }
        
    fun getOsc2PulseWidth(): Float
    {
        if (voices.isNotEmpty() )
        {
            return (voices[0] as SimpleSubtractiveSynthK).getOsc2PulseWidth()
        }
        return 0.0f
    }
    
    fun setOsc2PulseWidth(v: Float)
    {
        for (voice in voices)
        {
            (voice as SimpleSubtractiveSynthK).setOsc2PulseWidth(v)
        }
    }
        
    fun setCutoff(v: Float)
    {
        for (voice in voices)
        {
            (voice as SimpleSubtractiveSynthK).setCutoff(v)
        }
    }
        
    fun getCutoff(): Float
    {
        if (voices.isNotEmpty() )
        {
            return (voices[0] as SimpleSubtractiveSynthK).getCutoff()
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
        if (voices.isNotEmpty() )
        {
            return (voices[0] as SimpleSubtractiveSynthK).getResonance()
        }
        return 0.0f
    }

    fun setInitialCutoff(ic: Float)
    {
        for (voice in voices)
        {
            (voice as SimpleSubtractiveSynthK).setInitialCutoff(ic)
        }
    }

    fun getInitialCutoff(): Float
    {
        if (voices.isNotEmpty() )
        {
            return (voices[0] as SimpleSubtractiveSynthK).getInitialCutoff()
        }
        return 0.0f
    }

    fun setActionAmountToFilter(v: Float)
    {
        for (voice in voices)
        {
            (voice as SimpleSubtractiveSynthK).actionAmountToFilter = v
        }
    }

    fun getActionAmountToFilter(): Float
    {
        if (voices.isNotEmpty() )
        {
            return (voices[0] as SimpleSubtractiveSynthK).actionAmountToFilter
        }
        return 0.0f
    }
}
