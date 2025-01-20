//
// Created by philipp on 18.01.25.
//

#include "SquareOscillator.h"
#include "cmath"
#include "../tools/FloatMath.h"
float SquareOscillator::getNextSample() {
    float val1;
    for (uint8_t c=0;c<4;c++) {
        currentPhase += phaseIncrement / 4.0f;
        while (currentPhase > 2 * M_PI_F) {
            currentPhase -= 2 * M_PI_F;
        }
        val1 = (currentPhase - M_PI_F) / M_PI_F;
        if (val1 < pulseWidth)
        {
            val1 = -1.0f;
        }
        else
        {
            val1 = 1.0f;
        }
        val1 = decimatingFilter->processSample(val1);
        val1 = decimatingFilter2->processSample(val1);
    }
    return val1;
}

void SquareOscillator::setNote(float n) {
    float freq = powf(2.0f,n/12.0f)*440.0f;
    phaseIncrement = freq/samplingRate*2.0f*M_PI_F;
}

SquareOscillator::SquareOscillator(float sr) {
    samplingRate=sr;
    currentPhase=0.0f;
    phaseIncrement = 432.0f/samplingRate*M_PI_F*2.0f;
    pulseWidth = 0.0;
    decimatingFilter=new SecondOrderIirFilter();
    calculateFilterCoefficients(decimatingFilter);
    decimatingFilter2=new SecondOrderIirFilter();
    calculateFilterCoefficients(decimatingFilter2);
}

SquareOscillator::SquareOscillator() {
    samplingRate=48000.0f;
    currentPhase=0.0f;
    phaseIncrement = 432.0f/samplingRate*M_PI_F*2.0f;
    pulseWidth = 0.0;
    decimatingFilter=new SecondOrderIirFilter();
    calculateFilterCoefficients(decimatingFilter);
    decimatingFilter2=new SecondOrderIirFilter();
    calculateFilterCoefficients(decimatingFilter2);

}


void SquareOscillator::calculateFilterCoefficients(SecondOrderIirFilter* filter) const {
    const float ita =1.0f/ tanf(M_PI_F*10000.0f/(samplingRate*4.0f));
    const float q=sqrtf(2.0f);
    filter->coeffB[0] = 1.0f / (1.0f + q*ita + ita*ita);
    filter->coeffB[1] = 2.0f*filter->coeffB[0];
    filter->coeffB[2] = filter->coeffB[0];
    filter->coeffA[0] = -2.0f * (ita*ita - 1.0f) * filter->coeffB[0];
    filter->coeffA[1] = (1.0f - q*ita + ita*ita) * filter->coeffB[0];
}

float SquareOscillator::getPulseWidth() const {
    return pulseWidth;
}

uint8_t SquareOscillator::setPulseWidth(float pw) {
    if (pw >= -1.0f && pw <= 1.0f)
    {
        pulseWidth = pw;
        return 0;
    }
    return 1;
}
