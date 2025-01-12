
package ch.sr35.touchsamplesynth.audio.instruments

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.appcompat.content.res.AppCompatResources
import ch.sr35.touchsamplesynth.R
import ch.sr35.touchsamplesynth.audio.MusicalSoundGenerator
import ch.sr35.touchsamplesynth.audio.voices.LooperK

class LooperI(private val context: Context,
                     name: String
) : InstrumentI(name)  {
    val icon= AppCompatResources.getDrawable(context, R.drawable.looper)

    init {
        voices = ArrayList()
    }
    override fun getType(): String {
        return "Looper"
    }

    override fun getInstrumentIcon(): Drawable? {
        return icon
    }

    override fun generateVoices(cnt: Int) {
        val doCopy = voices.isNotEmpty()
        for (i in 0 until cnt) {
            voices.add(MusicalSoundGenerator.generateAttachedInstance<LooperK>(context))
            if (doCopy)
            {
                voices[0].copyParamsTo(voices[voices.size-1])
            }
        }
    }

        
    fun getReadPointer(): Int
    {
        if (voices.isNotEmpty() )
        {
            return (voices[0] as LooperK).getReadPointer()
        }
        return 0
    }
    
    fun setReadPointer(v: Int)
    {
        for (voice in voices)
        {
            (voice as LooperK).setReadPointer(v)
        }
    }
        
    fun getWritePointer(): Int
    {
        if (voices.isNotEmpty() )
        {
            return (voices[0] as LooperK).getWritePointer()
        }
        return 0
    }
    
    fun setWritePointer(v: Int)
    {
        for (voice in voices)
        {
            (voice as LooperK).setWritePointer(v)
        }
    }
        
    fun getLoopEnd(): Int
    {
        if (voices.isNotEmpty() )
        {
            return (voices[0] as LooperK).getLoopEnd()
        }
        return 0
    }
    
    fun setLoopEnd(v: Int)
    {
        for (voice in voices)
        {
            (voice as LooperK).setLoopEnd(v)
        }
    }

    fun setSample(sampleData: FloatArray)
    {
        for (voice in voices)
        {
            (voice as LooperK).setSample(sampleData)
        }
    }

    fun hasRecordedContent(): Boolean
    {
        if (voices.isNotEmpty() )
        {
            return (voices[0] as LooperK).hasRecordedContent()
        }
        return false
    }

    fun getSample(): FloatArray
    {
        if (voices.isNotEmpty() )
        {
            return (voices[0] as LooperK).getSample()
        }
        return FloatArray(0)
    }

}
