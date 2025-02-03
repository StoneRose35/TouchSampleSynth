package ch.sr35.touchsamplesynth.audio

import android.content.Context
import ch.sr35.touchsamplesynth.views.TouchElement
import java.time.LocalDateTime
import java.time.ZoneOffset
import kotlin.math.log10
import kotlin.math.pow
import kotlin.reflect.KFunction

const val MIDI_MODE_OFF=0
const val MIDI_MODE_ON_POLY=4
const val MIDI_MODE_ON_MONO=6
open class MusicalSoundGenerator {
    var actionAmountToVolume: Float=0.0f
    var actionAmountToPitchBend: Float=0.0f
    var switchOnTime: Long=-1
    var relatedTouchElement: TouchElement?=null
    protected open var instance: Byte=-1
    private var engaged: Boolean=false

    open external fun setNote(note: Float): Boolean
    open external fun setMidiVelocityScaling(mv: Float): Boolean
    open fun bindToAudioEngine() {}
    external fun setMidiChannel(channel: Int)
    external fun sendMidiCC(ccNumber: Int,ccValue: Int)
    open external fun isSounding(): Boolean

    // 0: no midi, 2: midi available, 3: midi available, note change possible (monophonic)
    open external fun setMidiMode(midiMode: Int)
    open external fun getMidiMode(): Int
    external fun getVolume(): Float
    external fun setVolume(v: Float): Boolean
    external fun setPitchBend(bend: Float): Boolean
    private external fun switchOnExt(vel: Float): Boolean
    private external fun switchOffExt(vel: Float):Boolean
    private external fun triggerExt(vel: Float): Boolean
    open fun switchOn(vel: Float): Boolean {
        val now = LocalDateTime.now()
        switchOnTime = now.toEpochSecond(ZoneOffset.UTC)*1000 + now.nano/1000
        engaged=true
        return switchOnExt(vel)
    }

    open fun switchOff(vel: Float): Boolean {
        engaged=false
        return switchOffExt(vel)
    }

    open fun trigger(vel: Float): Boolean {
        return triggerExt(vel)
    }

    fun isEngaged(): Boolean {
        return engaged
    }

    open fun copyParamsTo(other: MusicalSoundGenerator) {
        other.setMidiMode(this.getMidiMode())
        other.actionAmountToVolume = actionAmountToVolume
        other.actionAmountToPitchBend = actionAmountToPitchBend
    }

    open fun applyTouchActionA(a: Float) {
        if (a > 0.0f && a <= 1.0f) {
            setVolume(10.0f.pow(log10(a) * actionAmountToVolume))
        }
    }

    open fun applyTouchActionB(b: Float) {
        if (b >= -1.0f && b <= 1.0f) {
            setPitchBend(b* actionAmountToPitchBend)
        }
    }

    override fun equals(other: Any?): Boolean {
        if (other is MusicalSoundGenerator)
        {
            return other.hashCode() == this.hashCode()
        }
        return false
    }

    fun detachFromAudioEngine() {
        val audioEngine = AudioEngineK()
        if (instance > -1)
        {
            audioEngine.removeSoundGenerator(instance)
            instance=(-1).toByte()
        }
    }

    fun instanceNr(): Byte
    {
        return instance
    }

    override fun hashCode(): Int {
        return instance.toInt()
    }

    companion object {
        inline fun <reified T> generateAttachedInstance(context: Context): MusicalSoundGenerator {
            val actualRuntimeClassConstructor : KFunction<T> = T::class.constructors.first()
            val instance : T = actualRuntimeClassConstructor.call(context)
            (instance as MusicalSoundGenerator).bindToAudioEngine()
            return instance
        }
    }

}