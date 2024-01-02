package ch.sr35.touchsamplesynth.audio.instruments

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.net.Uri
import androidx.appcompat.content.res.AppCompatResources
import ch.sr35.touchsamplesynth.R
import ch.sr35.touchsamplesynth.audio.AudioEngineK
import ch.sr35.touchsamplesynth.audio.Instrument
import ch.sr35.touchsamplesynth.audio.WavReader
import ch.sr35.touchsamplesynth.audio.WaveFileChannel
import ch.sr35.touchsamplesynth.audio.voices.SamplerK

const val WAVE_BMP_WIDTH=768
const val WAVE_BMP_HEIGHT=512
class SamplerI(private val context: Context,
               name: String): Instrument(name) {
    val icon = AppCompatResources.getDrawable(context, R.drawable.sampler)
    val sample=ArrayList<Float>()
    var sampleUri: Uri?=null
    var waveformImg: Bitmap?=null
    private val backgroundColor: Paint = Paint()
    private val waveColor:Paint = Paint()
    init {
        voices = ArrayList()
        backgroundColor.color = context.getColor(R.color.white)
        backgroundColor.style = Paint.Style.FILL
        backgroundColor.isAntiAlias= false

        waveColor.color = context.getColor(R.color.purple_700)
        waveColor.style = Paint.Style.STROKE
        waveColor.isAntiAlias = false

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
        val inputStream = context.contentResolver.openInputStream(fileName)
        val wavFile = wr.readWaveFile(inputStream!!)
        val ae = AudioEngineK()
        val floatSamples = wavFile.getFloatData(ae.getSamplingRate(),WaveFileChannel.LEFT)
        sample.clear()
        sample.addAll(floatSamples)
        createBufferBitmap()
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
        loadSample(floatSamples.toFloatArray())
        sampleUri = fileName
        inputStream.close()



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


    private fun createBufferBitmap()
    {
        waveformImg = Bitmap.createBitmap(WAVE_BMP_WIDTH, WAVE_BMP_HEIGHT, Bitmap.Config.ARGB_8888)
        val waveViewCanvas = Canvas(waveformImg!!)
        waveViewCanvas.drawRect(0.0f,0.0f,WAVE_BMP_WIDTH.toFloat(),WAVE_BMP_HEIGHT.toFloat(),backgroundColor)
        if (sample.size > 0)
        {
            val widthInShrinkedSamples = sample.size.toFloat()/WAVE_BMP_WIDTH.toFloat()
            for (c in 0 until WAVE_BMP_WIDTH-1) {
                val p1 = (c * widthInShrinkedSamples).toInt()
                val p2 = ((c + 1) * widthInShrinkedSamples).toInt()
                if (p2 != p1) {
                    val sublist = sample.subList(p1, p2)
                    val minAlongSamples = sublist.min()
                    val maxAlongSamples = sublist.max()
                    if (maxAlongSamples >= 0.0f && minAlongSamples <= 0.0f) {
                        waveViewCanvas.drawLine(
                            c.toFloat(),
                            WAVE_BMP_HEIGHT.toFloat() / 2.0f * (1.0f - maxAlongSamples),
                            c.toFloat(),
                            WAVE_BMP_HEIGHT.toFloat() / 2.0f * (1 - minAlongSamples),
                            waveColor
                        )
                    } else if (maxAlongSamples >= 0.0f && minAlongSamples >= 0.0f) {
                        waveViewCanvas.drawLine(
                            c.toFloat(),
                            WAVE_BMP_HEIGHT.toFloat() / 2.0f * (1.0f - maxAlongSamples),
                            c.toFloat(),
                            WAVE_BMP_HEIGHT.toFloat() / 2.0f * (1.0f),
                            waveColor
                        )
                    } else if (maxAlongSamples <= 0.0f && minAlongSamples <= 0.0f) {
                        waveViewCanvas.drawLine(
                            c.toFloat(),
                            WAVE_BMP_HEIGHT.toFloat() / 2.0f * (1.0f),
                            c.toFloat(),
                            WAVE_BMP_HEIGHT.toFloat() / 2.0f * (1.0f - minAlongSamples),
                            waveColor
                        )
                    }
                } else {
                    if (sample[p1] > 0.0f) {
                        waveViewCanvas.drawLine(
                            c.toFloat(),
                            WAVE_BMP_HEIGHT.toFloat() / 2.0f * (1.0f - sample[p1]),
                            c.toFloat(),
                            WAVE_BMP_HEIGHT.toFloat() / 2.0f * (1.0f),
                            waveColor
                        )
                    } else {
                        waveViewCanvas.drawLine(
                            c.toFloat(),
                            WAVE_BMP_HEIGHT.toFloat() / 2.0f * (1.0f),
                            c.toFloat(),
                            WAVE_BMP_HEIGHT.toFloat() / 2.0f * (1.0f - sample[p1]),
                            waveColor
                        )
                    }
                }
            }
        }
    }


}