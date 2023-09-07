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

void SineOscillator::setNote(float n) {
    float freq = powf(2,n/12.0f)*440.0f;
    phaseIncrement = freq/samplingRate*2.0f*M_PI;
}

SineOscillator::SineOscillator(float sr)
{
    samplingRate = sr;
    currentPhase = 0.0f;
    phaseIncrement=0.0f;
}

SineOscillator::SineOscillator() {
    samplingRate = 48000.0f;
    currentPhase = 0;
    setNote(0.0f);
}






