//
// Created by philipp on 15.09.23.
//

#include "SawOscillator.h"
#include "cmath"

float SawOscillator::getNextSample() {
    float val1, val2;
    currentPhase += phaseIncrement/2.0f;
    if (currentPhase > 2*M_PI)
    {
        currentPhase -= 2*M_PI;
    }
    val1 = (currentPhase-M_PI)/M_PI;
    currentPhase += phaseIncrement/2.0f;
    if (currentPhase > 2*M_PI)
    {
        currentPhase -= 2*M_PI;
    }
    val2 = (currentPhase-M_PI)/M_PI;

    return (val1 + val2)*0.5f;
}

void SawOscillator::setNote(float n) {
    float freq = powf(2,n/12.0f)*440.0f;
    phaseIncrement = freq/samplingRate*2.0f*M_PI;
}

SawOscillator::SawOscillator(float sr) {
    samplingRate = sr;
}

SawOscillator::SawOscillator() {
    samplingRate=48000.0f;
}

