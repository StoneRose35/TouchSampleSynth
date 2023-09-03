//
// Created by philipp on 03.09.23.
//

#include "SineOscillator.h"
#include <cmath>

float SineOscillator::getNextSample() {
    currentPhase += phaseIncrement;
    if (currentPhase > 2.0f*M_PI)
    {
        currentPhase -= 2.0f*M_PI;
    }
    return sinf(currentPhase);
}

void SineOscillator::setNote(float note) {
    float freq = powf(2,(note)/12.0f)*440.0f;
    phaseIncrement = samplingRate/freq*2.0f*M_PI;
}

SineOscillator::SineOscillator(float sr)
{
    samplingRate = sr;
    currentPhase = 0.0f;
}

SineOscillator::SineOscillator() {
    samplingRate = 44100.0f;
    currentPhase = 0;
}
