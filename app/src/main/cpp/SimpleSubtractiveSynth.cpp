//
// Created by philipp on 15.09.23.
//

#include "SimpleSubtractiveSynth.h"
#include "AudioEngine.h"

float SimpleSubtractiveSynth::getNextSample() {
    float im;
    if (env->isSounding()) {
        float nsample = osc->getNextSample();
        nsample = filter->processSample(nsample);
        nsample = nsample * (envelopeVals[0] +
                             (envelopeVals[1] - envelopeVals[0]) / (float) envelopeUpdateInterval *
                             (float) currentSample);
        if (currentFilterUpdateSamples < modulatorsUpdateInSamples)
        {
            im = (newFilterCutoff - currentFilterCutoff)*((float)currentFilterUpdateSamples / (float)(modulatorsUpdateInSamples)) + currentFilterCutoff;
            filter->SetCutoff(im);
            filter->SetResonance(currentResonance);
            currentFilterUpdateSamples++;
            if (currentFilterUpdateSamples == modulatorsUpdateInSamples)
            {
                currentFilterCutoff = newFilterCutoff;
            }
        }
        if (currentPitchUpdateInSamples < modulatorsUpdateInSamples)
        {
            im = (newPitchBend - currentPitchBend)*((float)currentPitchUpdateInSamples / (float)(modulatorsUpdateInSamples)) + currentPitchBend;
            osc->setNote(note + im);
            currentPitchUpdateInSamples++;
            if (currentPitchUpdateInSamples == modulatorsUpdateInSamples)
            {
                currentPitchBend = newPitchBend;
            }
        }
        currentSample++;
        if (currentSample == envelopeUpdateInterval) {
            envelopeVals[0] = envelopeVals[1];
            envelopeVals[1] = env->getValue((float) envelopeUpdateInterval / sampleRate);
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
    osc->setNote(n);
    currentPitchBend=0;
    currentPitchUpdateInSamples=modulatorsUpdateInSamples;
    MusicalSoundGenerator::setNote(n);
}

void SimpleSubtractiveSynth::setAttack(float a) {
    env->setAttack(a);
}

float SimpleSubtractiveSynth::getAttack() {
    return env->getAttack();
}

void SimpleSubtractiveSynth::setDecay(float d) {
    env->setDecay(d);
}

float SimpleSubtractiveSynth::getDecay() {
    return env->getDecay();
}

void SimpleSubtractiveSynth::setSustain(float s) {
    env->setSustain(s);
}

float SimpleSubtractiveSynth::getSustain() {
    return env->getSustain();
}

void SimpleSubtractiveSynth::setRelease(float r) {
    env->setRelease(r);
}

float SimpleSubtractiveSynth::getRelease() {
    return env->getRelease();
}

void SimpleSubtractiveSynth::setCutoff(float co) {
    if (env->isSounding()) {
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
    if (env->isSounding()) {
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
    setCutoff(initialCutoff);
    env->trigger();
}

void SimpleSubtractiveSynth::switchOn(uint8_t vel) {
    setCutoff(initialCutoff);
    env->switchOn();
    MusicalSoundGenerator::switchOn(vel);
}

void SimpleSubtractiveSynth::switchOff(uint8_t vel) {
    env->switchOff();
    MusicalSoundGenerator::switchOff(vel);
}

bool SimpleSubtractiveSynth::isSounding() {
    return env->isSounding();
}

SimpleSubtractiveSynth::SimpleSubtractiveSynth(float sr) : MusicalSoundGenerator(sr) {
    sampleRate=sr;
    currentSample=0;
    envelopeUpdateInterval=32;
    envelopeVals[0]=0.0f;
    envelopeVals[1]=0.0f;
    initialCutoff=20000.0f;
    currentFilterCutoff=0.0f;
    currentResonance=0.0f;
    newFilterCutoff=0.0f;
    modulatorsUpdateInSamples = floor(sr / 100);
    currentFilterUpdateSamples=modulatorsUpdateInSamples;
    currentPitchUpdateInSamples=modulatorsUpdateInSamples;
    currentPitchBend = 0.0f;
    newPitchBend = 0.0f;
    env=new AdsrEnvelope();
    filter=new StilsonMoogFilter();
    osc=new SawOscillator(sr);

}

int SimpleSubtractiveSynth::getType() {
    return SIMPLE_SUBTRACTIVE_SYNTH;
}



