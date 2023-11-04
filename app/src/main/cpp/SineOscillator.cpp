//
// Created by philipp on 03.09.23.
//

#include "SineOscillator.h"
#include <cmath>
#include "FloatMath.h"

float SineOscillator::getNextSample() {
    currentPhase += phaseIncrement;
    if (currentPhase > 2.0f*M_PI_F)
    {
        currentPhase -= 2.0f*M_PI_F;
    }
    return sinf(currentPhase);
}

void SineOscillator::setNote(float n) {
    float freq = powf(2.0f,n/12.0f)*440.0f;
    phaseIncrement = freq/samplingRate*2.0f*M_PI_F;
}

SineOscillator::SineOscillator(float sr)
{
    samplingRate = sr;
    currentPhase = 0.0f;
    phaseIncrement = 432.0f/samplingRate*M_PI_F*2.0f;
}

SineOscillator::SineOscillator() {
    samplingRate = 48000.0f;
    currentPhase = 0.0f;
    phaseIncrement = 432.0f/samplingRate*M_PI_F*2.0f;
}






