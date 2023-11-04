//
// Created by philipp on 15.09.23.
//

#include "SawOscillator.h"
#include "cmath"
#include "FloatMath.h"
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
        if (currentPhase > 2 * M_PI_F) {
            currentPhase -= 2 * M_PI_F;
        }
        val1 = (currentPhase - M_PI_F) / M_PI_F;
        val1 = decimatingFilter->processSample(val1);
        val1 = decimatingFilter2->processSample(val1);
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
#ifdef FOURTIMES_OVERSAMPLING
    /*
     * coefficient calculation is based on https://stackoverflow.com/questions/20924868/calculate-coefficients-of-2nd-order-butterworth-low-pass-filter#:~:text=Let%20C%20%3D%20tan(wd*,%3D%202%2FT*C%20.&text=The%20best%20way%20would%20be,the%20code%20to%20your%20microcon.
     * */
    decimatingFilter=new SecondOrderIirFilter();
    calculateFilterCoefficients(decimatingFilter);
    decimatingFilter2=new SecondOrderIirFilter();
    calculateFilterCoefficients(decimatingFilter2);
/*
    decimatingFilter->coeffA[0] = -1.10922879f;
    decimatingFilter->coeffA[1] = 0.39815229f;

    decimatingFilter->coeffB[0] = 0.07223088f;
    decimatingFilter->coeffB[1] = 0.14446175f;
    decimatingFilter->coeffB[2] =  0.07223088f;
*/
#endif
}

SawOscillator::SawOscillator() {
    samplingRate=48000.0f;
    currentPhase=0.0f;
    phaseIncrement = 432.0f/samplingRate*M_PI_F*2.0f;
#ifdef FOURTIMES_OVERSAMPLING
    decimatingFilter=new SecondOrderIirFilter();
    calculateFilterCoefficients(decimatingFilter);
    decimatingFilter2=new SecondOrderIirFilter();
    calculateFilterCoefficients(decimatingFilter2);
/*
    decimatingFilter->coeffA[0] = -1.10922879f;
    decimatingFilter->coeffA[1] = 0.39815229f;

    decimatingFilter->coeffB[0] = 0.07223088f;
    decimatingFilter->coeffB[1] = 0.14446175f;
    decimatingFilter->coeffB[2] =  0.07223088f;
*/
#endif
}

void SawOscillator::calculateFilterCoefficients(SecondOrderIirFilter* filter) const {
    const float ita =1.0f/ tanf(M_PI_F*5000.0f/(samplingRate*4.0f));
    const float q=sqrtf(2.0f);
    filter->coeffB[0] = 1.0f / (1.0f + q*ita + ita*ita);
    filter->coeffB[1] = 2*filter->coeffB[0];
    filter->coeffB[2] = filter->coeffB[0];
    filter->coeffA[0] = -2.0f * (ita*ita - 1.0f) * filter->coeffB[0];
    filter->coeffA[1] = (1.0f - q*ita + ita*ita) * filter->coeffB[0];
}




