//
// Created by philipp on 03.09.23.
//

#include "SineMonoSynth.h"

float SineMonoSynth::getNextSample() {
    if (env.isSounding()) {
        float nsample = osc.getNextSample();
        nsample = nsample * (envelopeVals[0] +
                             (envelopeVals[1] - envelopeVals[0]) / (float) envelopeUpdateInterval *
                             (float) currentSample);
        currentSample++;
        if (currentSample == envelopeUpdateInterval) {
            envelopeVals[0] = envelopeVals[1];
            envelopeVals[1] = env.getValue((float) envelopeUpdateInterval / sampleRate);
            currentSample = 0;
        }
        return  nsample;
    }
    return 0.0f;
}

SineMonoSynth::SineMonoSynth() {
    osc = SineOscillator(48000.0f);
    env = AdrsEnvelope();
    sampleRate = 48000.0f;
    envelopeVals[0]=0.0f;
    envelopeVals[1]=0.0f;
    envelopeUpdateInterval=32;
    currentSample = 0;
}

void SineMonoSynth::setNote(float note) {

}

void SineMonoSynth::switchOn(float velocity) {}

void SineMonoSynth::switchOff(float velocity) {}

SineMonoSynth::~SineMonoSynth() {

}
