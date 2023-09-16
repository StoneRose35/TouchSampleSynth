//
// Created by philipp on 15.09.23.
//

#include "SimpleSubtractiveSynth.h"

float SimpleSubtractiveSynth::getNextSample() {

    if (env->isSounding()) {
        float nsample = osc->getNextSample();
        nsample = filter->processSample(nsample);
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
    filter->SetCutoff(co);
}

float SimpleSubtractiveSynth::getCutoff() {
    return filter->cutoff;
}

void SimpleSubtractiveSynth::setResonance(float rs) {
    filter->SetResonance(rs*4.0f);
}

float SimpleSubtractiveSynth::getResonance() {
    return filter->reso/4.0f;
}

void SimpleSubtractiveSynth::switchOn(float vel) {
    env->switchOn();
}

void SimpleSubtractiveSynth::switchOff(float vel) {
    env->switchOff();
}

SimpleSubtractiveSynth::SimpleSubtractiveSynth() {
    sampleRate=48000;
    currentSample=0;
    envelopeUpdateInterval=32;
    envelopeVals[0]=0.0f;
    envelopeVals[1]=0.0f;
    env=new AdsrEnvelope();
    filter=new StilsonMoogFilter();
    osc=new SawOscillator(48000.0);

}



