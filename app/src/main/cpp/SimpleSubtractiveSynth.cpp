//
// Created by philipp on 15.09.23.
//

#include "SimpleSubtractiveSynth.h"
#include "AudioEngine.h"
#include "components/SquareOscillator.h"
#define FILTER_SCALE_FACTOR 0.67f
float SimpleSubtractiveSynth::getNextSample() {
    float im;
    if (volumeEnv->isSounding()) {
        float nsample = osc1->getNextSample()/2.0f + osc2->getNextSample()*osc2Volume;
        nsample = filter->processSample(nsample*FILTER_SCALE_FACTOR)/FILTER_SCALE_FACTOR;
        nsample = nsample * (volumeEnvelopeVals[0] +
                             (volumeEnvelopeVals[1] - volumeEnvelopeVals[0]) / (float) envelopeUpdateInterval *
                             (float) currentSample);
        if (currentFilterUpdateSamples < modulatorsUpdateInSamples)
        {
            im = (newFilterCutoff - currentFilterCutoff)*((float)currentFilterUpdateSamples / (float)(modulatorsUpdateInSamples)) + currentFilterCutoff;
            float filterEnvelopeVal = (filterEnvelopeVals[0] + (filterEnvelopeVals[1] - filterEnvelopeVals[0])/(float)envelopeUpdateInterval);
            filter->SetCutoff(im + filterEnvelopeVal*filterEnvelopeLevel);
            filter->SetResonance(currentResonance);
            currentFilterUpdateSamples++;
            if (currentFilterUpdateSamples == modulatorsUpdateInSamples)
            {
                currentFilterCutoff = newFilterCutoff;
                currentFilterUpdateSamples=0;
            }
        }
        if (currentPitchUpdateInSamples < modulatorsUpdateInSamples)
        {
            im = (newPitchBend - currentPitchBend)*((float)currentPitchUpdateInSamples / (float)(modulatorsUpdateInSamples)) + currentPitchBend;
            osc1->setNote(note + im);
            osc2->setNote(note + (float)((int8_t)osc2Octave)*12.0f + osc2Detune + im);
            currentPitchUpdateInSamples++;
            if (currentPitchUpdateInSamples == modulatorsUpdateInSamples)
            {
                currentPitchBend = newPitchBend;
            }
        }
        currentSample++;
        if (currentSample == envelopeUpdateInterval) {
            volumeEnvelopeVals[0] = volumeEnvelopeVals[1];
            volumeEnvelopeVals[1] = volumeEnv->getValue((float) envelopeUpdateInterval / sampleRate);
            filterEnvelopeVals[0] = filterEnvelopeVals[1];
            filterEnvelopeVals[1] = filterEnv->getValue((float) envelopeUpdateInterval / sampleRate);
            currentSample = 0;
        }
        return  getNextSampleVolume()*nsample;
    }
    else
    {
        if ((availableForMidi & MIDI_AVAILABLE_MSK) != 0 )
        {
            availableForMidi &= ~(MIDI_TAKEN_MSK);
        }
    }
    return 0.0f;
}

void SimpleSubtractiveSynth::setNote(float n) {
    osc1->setNote(n);
    osc2->setNote(n + (float)((int8_t)osc2Octave)*12.0f + osc2Detune);
    currentPitchBend=0;
    currentPitchUpdateInSamples=modulatorsUpdateInSamples;
    MusicalSoundGenerator::setNote(n);
}

void SimpleSubtractiveSynth::setVolumeAttack(float a) {
    volumeEnv->setAttack(a);
}

float SimpleSubtractiveSynth::getVolumeAttack() {
    return volumeEnv->getAttack();
}

void SimpleSubtractiveSynth::setVolumeDecay(float d) {
    volumeEnv->setDecay(d);
}

float SimpleSubtractiveSynth::getVolumeDecay() {
    return volumeEnv->getDecay();
}

void SimpleSubtractiveSynth::setVolumeSustain(float s) {
    volumeEnv->setSustain(s);
}

float SimpleSubtractiveSynth::getVolumeSustain() {
    return volumeEnv->getSustain();
}

void SimpleSubtractiveSynth::setVolumeRelease(float r) {
    volumeEnv->setRelease(r);
}

float SimpleSubtractiveSynth::getVolumeRelease() {
    return volumeEnv->getRelease();
}

void SimpleSubtractiveSynth::setCutoff(float co) {
    if (volumeEnv->isSounding()) {
        newFilterCutoff = co;
        currentFilterUpdateSamples = 0;
    }
    else
    {
        filter->SetCutoff(co);
        filter->SetResonance(currentResonance);
    }
}

void SimpleSubtractiveSynth::setPitchBend(float pb) {
    if (volumeEnv->isSounding() && fabs(pb) > 0.0001f) {
        newPitchBend = pb;
        currentPitchUpdateInSamples = 0;
    }
}

float SimpleSubtractiveSynth::getCutoff() {
    return filter->cutoff;
}

void SimpleSubtractiveSynth::setResonance(float rs) {
    currentResonance = rs;
    filter->SetResonance(rs);
}

float SimpleSubtractiveSynth::getResonance() {
    return filter->reso;
}

void SimpleSubtractiveSynth::trigger(uint8_t vel) {
    MusicalSoundGenerator::trigger(vel);
    volumeEnv->trigger();
    filterEnv->trigger();
}

void SimpleSubtractiveSynth::switchOn(uint8_t vel) {
    MusicalSoundGenerator::switchOn(vel);
    filter->SetCutoff(initialCutoff);
    filter->SetResonance(currentResonance);
    volumeEnv->switchOn();
    filterEnv->switchOn();
}

void SimpleSubtractiveSynth::switchOff(uint8_t vel) {
    volumeEnv->switchOff();
    filterEnv->switchOff();
    MusicalSoundGenerator::switchOff(vel);
}

bool SimpleSubtractiveSynth::isSounding() {
    return volumeEnv->isSounding();
}

SimpleSubtractiveSynth::SimpleSubtractiveSynth(float sr) : MusicalSoundGenerator(sr) {
    sampleRate=sr;
    currentSample=0;
    envelopeUpdateInterval=32;
    volumeEnvelopeVals[0]=0.0f;
    volumeEnvelopeVals[1]=0.0f;
    initialCutoff=20000.0f;
    currentFilterCutoff=0.0f;
    currentResonance=0.0f;
    newFilterCutoff=0.0f;
    modulatorsUpdateInSamples = floor(sr / 100);
    currentFilterUpdateSamples=modulatorsUpdateInSamples;
    currentPitchUpdateInSamples=modulatorsUpdateInSamples;
    currentPitchBend = 0.0f;
    newPitchBend = 0.0f;
    volumeEnv=new AdsrEnvelope();
    filterEnv=new AdsrEnvelope();
    filter=new StilsonMoogFilter();
    osc1=new SawOscillator(sr);
    osc1Type = OSCILLATOR_TYPE_SAW;
    osc2=new SawOscillator(sr);
    osc2Type = OSCILLATOR_TYPE_SAW;

}

int SimpleSubtractiveSynth::getType() {
    return SIMPLE_SUBTRACTIVE_SYNTH;
}

void SimpleSubtractiveSynth::setFilterAttack(float a) {
    filterEnv->setAttack(a);
}

float SimpleSubtractiveSynth::getFilterAttack() {
    return filterEnv->getAttack();
}

void SimpleSubtractiveSynth::setFilterDecay(float d) {
    filterEnv->setDecay(d);
}

float SimpleSubtractiveSynth::getFilterDecay() {
    return filterEnv->getDecay();
}

void SimpleSubtractiveSynth::setFilterSustain(float s) {
    filterEnv->setSustain(s);
}

float SimpleSubtractiveSynth::getFilterSustain() {
    return filterEnv->getSustain();
}

void SimpleSubtractiveSynth::setFilterRelease(float r) {
    filterEnv->setRelease(r);
}

float SimpleSubtractiveSynth::getFilterRelease() {
    return filterEnv->getRelease();
}

void SimpleSubtractiveSynth::setOsc1Type(uint8_t t) {
    switch (t) {
        case OSCILLATOR_TYPE_SAW:
            osc1 = new SawOscillator();
            osc1Type = OSCILLATOR_TYPE_SAW;
            break;
        case OSCILLATOR_TYPE_SQUARE:
            osc1 = new SquareOscillator();
            osc1Type = OSCILLATOR_TYPE_SQUARE;
        default:
            break;
    }
}

uint8_t SimpleSubtractiveSynth::getOsc1Type() {
    return osc1Type;
}

void SimpleSubtractiveSynth::setOsc2Type(uint8_t t) {
    switch (t) {
        case OSCILLATOR_TYPE_SAW:
            osc2 = new SawOscillator();
            osc2Type = OSCILLATOR_TYPE_SAW;
            break;
        case OSCILLATOR_TYPE_SQUARE:
            osc2 = new SquareOscillator();
            osc2Type = OSCILLATOR_TYPE_SQUARE;
        default:
            break;
    }
}

uint8_t SimpleSubtractiveSynth::getOsc2Type() {
    return osc2Type;
}

uint8_t SimpleSubtractiveSynth::getOsc2Octave() {
    return osc2Octave;
}

void SimpleSubtractiveSynth::setOsc2Octave(uint8_t oct) {
    int8_t osc2OctCurrent = (int8_t)osc2Octave;
    if ((int8_t)oct >-5 && (int8_t)oct < 4 && osc2OctCurrent != (int8_t)oct)
    {
        osc2Octave = oct;
        if (currentPitchUpdateInSamples == modulatorsUpdateInSamples)
        {
            currentPitchUpdateInSamples = 0;
        }
    }
}

float SimpleSubtractiveSynth::getOsc2Detune() {
    return osc2Detune;
}

void SimpleSubtractiveSynth::setOsc2Detune(float det) {
    if (det > -12.0f && det< 12.0f && det != osc2Detune)
    {
        osc2Detune = det;
        if (currentPitchUpdateInSamples == modulatorsUpdateInSamples)
        {
            currentPitchUpdateInSamples = 0;
        }
    }
}

float SimpleSubtractiveSynth::getOsc2Volume() {
    return osc2Volume;
}

void SimpleSubtractiveSynth::setOsc2Volume(float v) {
    if(v < 0.5f && v >= 0.0f )
    {
        osc2Volume = v;
    }
}

float SimpleSubtractiveSynth::getFilterEnvelopeLevel() {
    return filterEnvelopeLevel;
}

void SimpleSubtractiveSynth::setFilterEnvelopeLevel(float el) {
    filterEnvelopeLevel = el;
}

float SimpleSubtractiveSynth::getOsc1PulseWidth() {
    return osc1Pulsewidth;
}

void SimpleSubtractiveSynth::setOsc1PulseWidth(float pw) {
    if (pw > -1.0f && pw < 1.0f)
    {
        osc1Pulsewidth = pw;
        if (osc1Type == OSCILLATOR_TYPE_SQUARE)
        {
            ((SquareOscillator*)osc1)->setPulseWidth(pw);
        }
    }
}

float SimpleSubtractiveSynth::getOsc2PulseWidth() {
    return osc2Pulsewidth;
}

void SimpleSubtractiveSynth::setOsc2PulseWidth(float pw) {
    if (pw > -1.0f && pw < 1.0f )
    {
        osc2Pulsewidth=pw;
        if (osc2Type == OSCILLATOR_TYPE_SQUARE)
        {
            ((SquareOscillator*)osc2)->setPulseWidth(pw);
        }
    }
}








