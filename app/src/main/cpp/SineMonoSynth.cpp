//
// Created by philipp on 03.09.23.
//

#include "SineMonoSynth.h"
#include "AudioEngine.h"
float SineMonoSynth::getNextSample() {
    if (env->isSounding()) {
        float nsample = osc->getNextSample();
        nsample = nsample * (envelopeVals[0] +
                             (envelopeVals[1] - envelopeVals[0]) / (float) envelopeUpdateInterval *
                             (float) currentSample);
        currentSample++;
        if (currentSample == envelopeUpdateInterval) {
            envelopeVals[0] = envelopeVals[1];
            envelopeVals[1] = env->getValue((float) envelopeUpdateInterval / sampleRate);
            currentSample = 0;
        }
        return  nsample;
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

SineMonoSynth::SineMonoSynth() {
    osc = new SineOscillator(48000.0f);
    env = new AdsrEnvelope();
    sampleRate = 48000.0f;
    envelopeVals[0]=0.0f;
    envelopeVals[1]=0.0f;
    envelopeUpdateInterval=32;
    currentSample = 0;
}

void SineMonoSynth::setNote(float note) {
    osc->setNote(note);
}

void SineMonoSynth::switchOn(float velocity) {
    env->switchOn();
}

void SineMonoSynth::switchOff(float velocity) {
    env->switchOff();
}

int SineMonoSynth::getType() {
    return SINE_MONO_SYNTH;
}


void SineMonoSynth::setAttack(float a) {
    env->setAttack(a);
}

float SineMonoSynth::getAttack() {
    return env->getAttack();
}

void SineMonoSynth::setDecay(float d) {
    env->setDecay(d);
}

float SineMonoSynth::getDecay() {
    return env->getDecay();
}

void SineMonoSynth::setSustain(float s) {
    env->setSustain(s);
}

float SineMonoSynth::getSustain() {
    return env->getSustain();
}

void SineMonoSynth::setRelease(float r) {
    env->setRelease(r);
}

float SineMonoSynth::getRelease() {
    return env->getRelease();
}

bool SineMonoSynth::isSounding() {
    return env->isSounding();
}