//
// Created by philipp on 15.09.23.
//

#include "SawOscillator.h"
#include "cmath"
#define FOURTIMES_OVERSAMPLING
#ifdef NAIVE_OVERSAMPLING
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
#endif
#ifdef FOURTIMES_OVERSAMPLING
float SawOscillator::getNextSample() {
    float val1;
    for (uint8_t c=0;c<4;c++) {
        currentPhase += phaseIncrement / 4.0f;
        if (currentPhase > 2 * M_PI) {
            currentPhase -= 2 * M_PI;
        }
        val1 = (currentPhase - M_PI) / M_PI;
        val1 = decimatingFilter->processSample(val1);
    }
    return val1;
}
#endif
void SawOscillator::setNote(float n) {
    float freq = powf(2,n/12.0f)*440.0f;
    phaseIncrement = freq/samplingRate*2.0f*M_PI;
}

SawOscillator::SawOscillator(float sr) {
    samplingRate = sr;
#ifdef FOURTIMES_OVERSAMPLING
    decimatingFilter=new SecondOrderIirFilter();

    decimatingFilter->coeffA[0] = -1.10922879f;
    decimatingFilter->coeffA[1] = 0.39815229f;

    decimatingFilter->coeffB[0] = 0.07223088f;
    decimatingFilter->coeffB[1] = 0.14446175f;
    decimatingFilter->coeffB[2] =  0.07223088f;

#endif
}

SawOscillator::SawOscillator() {
    samplingRate=48000.0f;
#ifdef FOURTIMES_OVERSAMPLING
    decimatingFilter=new SecondOrderIirFilter();

    decimatingFilter->coeffA[0] = -1.10922879f;
    decimatingFilter->coeffA[1] = 0.39815229f;

    decimatingFilter->coeffB[0] = 0.07223088f;
    decimatingFilter->coeffB[1] = 0.14446175f;
    decimatingFilter->coeffB[2] =  0.07223088f;

#endif
}




