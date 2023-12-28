package ch.sr35.touchsamplesynth.audio.instruments

import android.content.Context
import android.graphics.drawable.Drawable
import android.net.Uri
import androidx.appcompat.content.res.AppCompatResources
import ch.sr35.touchsamplesynth.R
import ch.sr35.touchsamplesynth.audio.AudioEngineK
import ch.sr35.touchsamplesynth.audio.Instrument
import ch.sr35.touchsamplesynth.audio.WavReader
import ch.sr35.touchsamplesynth.audio.WaveFileChannel
import ch.sr35.touchsamplesynth.audio.voices.SamplerK

class SamplerI(private val context: Context,
               name: String): Instrument(name) {
    val icon = AppCompatResources.getDrawable(context, R.drawable.sampler)
    val sample=ArrayList<Float>()
    var sampleUri: Uri?=null
    init {
        voices = ArrayList()
    }

    override fun getType(): String {
        return "Sampler"
    }

    override fun getInstrumentIcon(): Drawable? {
        return icon
    }

    fun setSampleFile(fileName: Uri)
    {

        val wr = WavReader()
        context.contentResolver.openInputStream(fileName)?.let {
            val wavFile = wr.readWaveFile(it)
            val ae = AudioEngineK()
            val floatSamples = wavFile.getFloatData(ae.getSamplingRate(),WaveFileChannel.LEFT)
            sample.clear()
            sample.addAll(floatSamples)
            loadSample(floatSamples.toFloatArray())
            for (vc in voices)
            {
                if (vc.getInstance()==(-1).toByte())
                {
                    vc.bindToAudioEngine()
                }
                (vc as SamplerK).setLoopStartIndex(0)
                (vc).setSampleStartIndex(0)
                (vc).setLoopEndIndex(sample.size-1)
                (vc).setSampleEndIndex(sample.size-1)
            }
            sampleUri = fileName
        }


    }

    override fun generateVoices(cnt: Int) {
        val doCopy = voices.isNotEmpty()
        for (i in 0 until cnt) {
            voices.add(SamplerK(context).generateAttachedInstance(context))
            if (doCopy) {
                voices[0].copyParamsTo(voices[voices.size - 1])
            }
        }
    }

    fun getLoopStartIndex(): Int
    {
        if(voices.isNotEmpty())
        {
            return (voices[0] as SamplerK).getLoopStartIndex()
        }
        return -1
    }
    fun setLoopStartIndex(ls: Int)
    {
        for (vc in voices)
        {
            (vc as SamplerK).setLoopStartIndex(ls)
        }
    }
    fun getLoopEndIndex(): Int
    {
        if(voices.isNotEmpty())
        {
            return (voices[0] as SamplerK).getLoopEndIndex()
        }
        return -1
    }
    fun setLoopEndIndex(le: Int)
    {
        for (vc in voices)
        {
            (vc as SamplerK).setLoopEndIndex(le)
        }
    }
    fun getSampleStartIndex(): Int
    {
        if(voices.isNotEmpty())
        {
            return (voices[0] as SamplerK).getSampleStartIndex()
        }
        return -1
    }
    fun setSampleStartIndex(ss: Int)
    {
        for (vc in voices)
        {
            (vc as SamplerK).setSampleStartIndex(ss)
        }
    }
    fun getSampleEndIndex(): Int
    {
        if(voices.isNotEmpty())
        {
            return (voices[0] as SamplerK).getSampleEndIndex()
        }
        return -1
    }
    fun setSampleEndIndex(se: Int)
    {
        for (vc in voices)
        {
            (vc as SamplerK).setSampleEndIndex(se)
        }
    }
    fun setMode(mode: Byte)
    {
        for (vc in voices)
        {
            (vc as SamplerK).setMode(mode)
        }
    }
    fun getMode(): Byte
    {
        if(voices.isNotEmpty())
        {
            return (voices[0] as SamplerK).getMode()
        }
        return -1
    }

    private fun loadSample(sample: FloatArray)
    {
        for (vc in voices)
        {
            (vc as SamplerK).loadSample(sample)
        }
    }


}