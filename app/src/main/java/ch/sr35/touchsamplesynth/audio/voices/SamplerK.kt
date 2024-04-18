package ch.sr35.touchsamplesynth.audio.voices

import android.content.Context
import androidx.appcompat.content.res.AppCompatResources
import ch.sr35.touchsamplesynth.R
import ch.sr35.touchsamplesynth.audio.AudioEngineK
import ch.sr35.touchsamplesynth.audio.MusicalSoundGenerator
import kotlin.math.log10
import kotlin.math.pow

class SamplerK(context: Context): MusicalSoundGenerator() {
    private var instance: Byte=-1
    private var engaged: Boolean=false
    val icon= AppCompatResources.getDrawable(context, R.drawable.sampler)
    external fun getLoopStartIndex(): Int
    external fun setLoopStartIndex(ls: Int): Boolean
    external fun getLoopEndIndex(): Int
    external fun setLoopEndIndex(le: Int): Boolean
    external fun getSampleStartIndex(): Int
    external fun setSampleStartIndex(ss: Int): Boolean
    external fun getSampleEndIndex(): Int
    external fun setSampleEndIndex(se: Int): Boolean
    external fun setMode(mode: Byte): Boolean
    external fun getMode(): Byte
    external fun getVolume(): Float
    external fun setVolume(v: Float): Boolean
    external fun loadSample(sampleData: FloatArray): Boolean
    external fun switchOnExt(vel: Float): Boolean
    external fun switchOffExt(vel: Float):Boolean
    override fun setNote(note: Float): Boolean {
        return true
    }

    override fun switchOn(vel: Float): Boolean {
        engaged=true
        super.switchOn(vel)
        return switchOnExt(vel)
    }

    override fun switchOff(vel: Float): Boolean {
        engaged=false
        return switchOffExt(vel)
    }

    override fun isEngaged(): Boolean {
        return engaged
    }

    override fun getInstance(): Byte {
        return instance
    }

    override fun generateAttachedInstance(context: Context): MusicalSoundGenerator {
        val instance = SamplerK(context)
        instance.bindToAudioEngine()
        return instance
    }

    override fun bindToAudioEngine() {
        val audioEngine= AudioEngineK()
        if (instance == (-1).toByte()) {
            instance = audioEngine.addSoundGenerator(3)
        }
    }

    override fun detachFromAudioEngine() {
        val audioEngine = AudioEngineK()
        if (instance > -1)
        {
            audioEngine.removeSoundGenerator(instance)
            instance=(-1).toByte()
        }
    }

    override fun applyTouchAction(a: Float) {
        if (a > 0.0f) {
            setVolume(10.0f.pow(log10(a) * actionAmountToVolume))
        }
    }
    override fun hashCode(): Int {
        return 3000 + instance
    }

    override fun equals(other: Any?): Boolean {
        if (other is SamplerK)
        {
            return other.hashCode() == this.hashCode()
        }
        return false
    }
    external override fun isSounding(): Boolean

    override fun copyParamsTo(other: MusicalSoundGenerator) {
        val samples = this.copySample()
        (other as SamplerK).loadSample(samples)
        other.setMidiMode(this.getMidiMode())
        other.setMode(this.getMode())
        other.setSampleStartIndex(this.getSampleStartIndex())
        other.setSampleEndIndex(this.getSampleEndIndex())
        other.setLoopStartIndex(this.getLoopStartIndex())
        other.setLoopEndIndex(this.getLoopEndIndex())


    }

    external fun copySample(): FloatArray

    external override fun setMidiMode(midiMode: Int)

    external override fun getMidiMode(): Int
}