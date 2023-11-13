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
        if (currentFilterUpdateSample < filterUpdateInSamples)
        {
            im = (newFilterCutoff - currentFilterCutoff)*((float)currentFilterUpdateSample/(float)(filterUpdateInSamples)) + currentFilterCutoff;
            filter->SetCutoff(im);
            filter->SetResonance(currentResonance);
            currentFilterUpdateSample++;
            if (currentFilterUpdateSample == filterUpdateInSamples)
            {
                currentFilterCutoff = newFilterCutoff;
            }
        }
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

void SimpleSubtractiveSynth::setNote(float note) {
    osc->setNote(note);
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
        currentFilterUpdateSample = 0;
    }
    else
    {
        filter->SetCutoff(co);
        filter->SetResonance(currentResonance);
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

void SimpleSubtractiveSynth::switchOn(float vel) {
    setCutoff(initialCutoff);
    env->switchOn();
}

void SimpleSubtractiveSynth::switchOff(float vel) {
    env->switchOff();
}

bool SimpleSubtractiveSynth::isSounding() {
    return env->isSounding();
}

SimpleSubtractiveSynth::SimpleSubtractiveSynth(float sr) {
    sampleRate=sr;
    currentSample=0;
    envelopeUpdateInterval=32;
    envelopeVals[0]=0.0f;
    envelopeVals[1]=0.0f;
    initialCutoff=20000.0f;

    currentFilterCutoff=0.0f;
    currentResonance=0.0f;
    newFilterCutoff=0.0f;
    currentFilterUpdateSample=0;
    filterUpdateInSamples = floor(sr/100);
    env=new AdsrEnvelope();
    filter=new StilsonMoogFilter();
    osc=new SawOscillator(sr);

}

int SimpleSubtractiveSynth::getType() {
    return SIMPLE_SUBTRACTIVE_SYNTH;
}



