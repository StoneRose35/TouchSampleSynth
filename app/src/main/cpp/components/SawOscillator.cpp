//
// Created by philipp on 15.09.23.
//

#include "SawOscillator.h"
#include "cmath"
#include "../tools/FloatMath.h"
#define OVERSAMPLING
#ifdef OVERSAMPLING
#define FOURTIMES_OVERSAMPLING
#else
float SawOscillator::getNextSample() {
    float val1, val2;
    currentPhase += phaseIncrement/2.0f;
    while (currentPhase > 2*M_PI)
    {
        currentPhase -= 2*M_PI;
    }
    val1 = (currentPhase-M_PI)/M_PI;
    currentPhase += phaseIncrement/2.0f;
    while (currentPhase > 2*M_PI)
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
        while (currentPhase > 2 * M_PI_F) {
            currentPhase -= 2 * M_PI_F;
        }
        val1 = (currentPhase - M_PI_F) / M_PI_F;
        val1 = decimatingFilter->processSample(val1);
        val1 = decimatingFilter2->processSample(val1);
    }
    return val1;
}
#endif

#ifdef EIGHTTIMES_OVERSAMPLING
float SawOscillator::getNextSample() {
    float val1;
    for (uint8_t c=0;c<8;c++) {
        currentPhase += phaseIncrement / 8.0f;
        while (currentPhase > 2 * M_PI_F) {
            currentPhase -= 2 * M_PI_F;
        }
        val1 = (currentPhase - M_PI_F) / M_PI_F;
        val1 = decimatingFilter->processSample(val1);
        //val1 = decimatingFilter2->processSample(val1);
    }
    return val1;
}

#endif
void SawOscillator::setNote(float n) {
    float freq = powf(2.0f,n/12.0f)*440.0f;
    phaseIncrement = freq/samplingRate*2.0f*M_PI_F;
}

SawOscillator::SawOscillator(float sr) {
    samplingRate = sr;
    currentPhase=0.0f;
    phaseIncrement = 432.0f/samplingRate*M_PI_F*2.0f;
#ifdef OVERSAMPLING
    /*
     * coefficient calculation is based on https://stackoverflow.com/questions/20924868/calculate-coefficients-of-2nd-order-butterworth-low-pass-filter#:~:text=Let%20C%20%3D%20tan(wd*,%3D%202%2FT*C%20.&text=The%20best%20way%20would%20be,the%20code%20to%20your%20microcon.
     * */
    decimatingFilter=new SecondOrderIirFilter();
    calculateFilterCoefficients(decimatingFilter);
    decimatingFilter2=new SecondOrderIirFilter();
    calculateFilterCoefficients(decimatingFilter2);
#endif
}

SawOscillator::SawOscillator() {
    samplingRate=48000.0f;
    currentPhase=0.0f;
    phaseIncrement = 432.0f/samplingRate*M_PI_F*2.0f;
#ifdef OVERSAMPLING
    decimatingFilter=new SecondOrderIirFilter();
    calculateFilterCoefficients(decimatingFilter);
    decimatingFilter2=new SecondOrderIirFilter();
    calculateFilterCoefficients(decimatingFilter2);
#endif
}

void SawOscillator::calculateFilterCoefficients(SecondOrderIirFilter* filter) const {
#ifdef FOURTIMES_OVERSAMPLING
    const float ita =1.0f/ tanf(M_PI_F*10000.0f/(samplingRate*4.0f));
#endif
#ifdef EIGHTTIMES_OVERSAMPLING
    const float ita =1.0f/ tanf(M_PI_F*20000.0f/(samplingRate*8.0f));
#endif
    const float q=sqrtf(2.0f);
    filter->coeffB[0] = 1.0f / (1.0f + q*ita + ita*ita);
    filter->coeffB[1] = 2.0f*filter->coeffB[0];
    filter->coeffB[2] = filter->coeffB[0];
    filter->coeffA[0] = -2.0f * (ita*ita - 1.0f) * filter->coeffB[0];
    filter->coeffA[1] = (1.0f - q*ita + ita*ita) * filter->coeffB[0];
}




