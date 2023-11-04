//
// Created by philipp on 15.09.23.
//

#ifndef TOUCHSAMPLESYNTH_SAWOSCILLATOR_H
#define TOUCHSAMPLESYNTH_SAWOSCILLATOR_H


#include "SoundGenerator.h"
#include "SecondOrderIirFilter.h"

class SawOscillator: SoundGenerator {
public:
    float getNextSample();
    void setNote(float);
    SawOscillator(float);

    SawOscillator();
private:
    float phaseIncrement;
    float samplingRate;
    float currentPhase;
    SecondOrderIirFilter * decimatingFilter;
    SecondOrderIirFilter * decimatingFilter2;
    void calculateFilterCoefficients(SecondOrderIirFilter*) const;
};


#endif //TOUCHSAMPLESYNTH_SAWOSCILLATOR_H
