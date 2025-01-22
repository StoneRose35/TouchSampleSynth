
package ch.sr35.touchsamplesynth.model
import ch.sr35.touchsamplesynth.audio.instruments.InstrumentI
import ch.sr35.touchsamplesynth.audio.instruments.PolyphonyDefinition
import ch.sr35.touchsamplesynth.audio.instruments.SimpleSubtractiveSynthI
import java.io.Serializable

class SimpleSubtractiveSynthP(
    private var volumeAttack: Float=0.0f,
    private var volumeDecay: Float=0.0f,
    private var volumeSustain: Float=0.0f,
    private var volumeRelease: Float=0.0f,
    private var filterAttack: Float=0.0f,
    private var filterDecay: Float=0.0f,
    private var filterSustain: Float=0.0f,
    private var filterRelease: Float=0.0f,
    private var filterEnvelopeLevel: Float=0.0f,
    private var osc1Type: Byte=0,
    private var osc2Type: Byte=0,
    private var osc2Octave: Byte=0,
    private var osc2Detune: Float=0.0f,
    private var osc2Volume: Float=0.0f,
    private var osc1PulseWidth: Float=0.0f,
    private var osc2PulseWidth: Float=0.0f,
    private var cutoff: Float=0.0f,
    private var resonance: Float=0.0f,
    private var actionAmountToFilter: Float=0.0f,
    private var initialCutoff: Float=0.0f,
    actionAmountToVolume: Float=0.0f,
    actionAmountToPitchBend: Float=0.0f,    
    polyphonyDefinition: PolyphonyDefinition=PolyphonyDefinition.MONOPHONIC,
    horizontalToActionB: Boolean=false,
    nVoices: Int=0,
    name: String=""
): InstrumentP(actionAmountToVolume,actionAmountToPitchBend,polyphonyDefinition,horizontalToActionB,nVoices,name),Serializable, Cloneable {
    private val className: String=this.javaClass.name
    override fun fromInstrument(i: InstrumentI) {
        super.fromInstrument(i)
        if (i is SimpleSubtractiveSynthI)
        {
            volumeAttack = i.getVolumeAttack()
            volumeDecay = i.getVolumeDecay()
            volumeSustain = i.getVolumeSustain()
            volumeRelease = i.getVolumeRelease()
            filterAttack = i.getFilterAttack()
            filterDecay = i.getFilterDecay()
            filterSustain = i.getFilterSustain()
            filterRelease = i.getFilterRelease()
            filterEnvelopeLevel = i.getFilterEnvelopeLevel()
            osc1Type = i.getOsc1Type()
            osc2Type = i.getOsc2Type()
            osc2Octave = i.getOsc2Octave()
            osc2Detune = i.getOsc2Detune()
            osc2Volume = i.getOsc2Volume()
            osc1PulseWidth = i.getOsc1PulseWidth()
            osc2PulseWidth = i.getOsc2PulseWidth()
            cutoff = i.getCutoff()
            resonance = i.getResonance()
            actionAmountToFilter = i.getActionAmountToFilter()
            initialCutoff = i.getInitialCutoff()

        }
    }

    override fun toInstrument(i: InstrumentI) {
        if (i is SimpleSubtractiveSynthI)
        {
            super.toInstrument(i)
            i.setVolumeAttack(this.volumeAttack)
            i.setVolumeDecay(this.volumeDecay)
            i.setVolumeSustain(this.volumeSustain)
            i.setVolumeRelease(this.volumeRelease)
            i.setFilterAttack(this.filterAttack)
            i.setFilterDecay(this.filterDecay)
            i.setFilterSustain(this.filterSustain)
            i.setFilterRelease(this.filterRelease)
            i.setFilterEnvelopeLevel(this.filterEnvelopeLevel)
            i.setOsc1Type(this.osc1Type)
            i.setOsc2Type(this.osc2Type)
            i.setOsc2Octave(this.osc2Octave)
            i.setOsc2Detune(this.osc2Detune)
            i.setOsc2Volume(this.osc2Volume)
            i.setOsc1PulseWidth(this.osc1PulseWidth)
            i.setOsc2PulseWidth(this.osc2PulseWidth)
            i.setCutoff(this.cutoff)
            i.setResonance(this.resonance)
            i.setActionAmountToFilter(this.actionAmountToFilter)
            i.setInitialCutoff(this.initialCutoff)
        }
    }

    override fun equals(other: Any?): Boolean {
        if (other is SimpleSubtractiveSynthP)
        {
            return this.hashCode() == other.hashCode()
        }
        return false
    }

    override fun hashCode(): Int {
        return super.hashCode() + 
        this.volumeAttack.toRawBits() +
        this.volumeDecay.toRawBits() +
        this.volumeSustain.toRawBits() +
        this.volumeRelease.toRawBits() +
        this.filterAttack.toRawBits() +
        this.filterDecay.toRawBits() +
        this.filterSustain.toRawBits() +
        this.filterRelease.toRawBits() +
        this.filterEnvelopeLevel.toRawBits() +
        this.osc1Type +
        this.osc2Type +
        this.osc2Octave +
        this.osc2Detune.toRawBits() +
        this.osc2Volume.toRawBits() +
        this.osc1PulseWidth.toRawBits() +
        this.osc2PulseWidth.toRawBits() +
        this.cutoff.toRawBits() +
        this.resonance.toRawBits()  
    }

    override fun toString(): String
    {
        return "SimpleSubtractiveSynth: %s, voices: %b".format(this.name, this.polyphonyDefinition)
    }

    override fun clone(): Any {
        return SimpleSubtractiveSynthP(
            this.volumeAttack,
            this.volumeDecay,
            this.volumeSustain,
            this.volumeRelease,
            this.filterAttack,
            this.filterDecay,
            this.filterSustain,
            this.filterRelease,
            this.filterEnvelopeLevel,
            this.osc1Type,
            this.osc2Type,
            this.osc2Octave,
            this.osc2Detune,
            this.osc2Volume,
            this.osc1PulseWidth,
            this.osc2PulseWidth,
            this.cutoff,
            this.resonance, 
            this.actionAmountToVolume,
            this.actionAmountToPitchBend,
            this.actionAmountToFilter,
            this.initialCutoff,
            this.polyphonyDefinition,
            this.horizontalToActionB,
            this.nVoices,
            this.name)
    }
}
    