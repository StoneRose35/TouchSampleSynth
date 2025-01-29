//
// Created by philipp on 15.09.23.
//

#ifndef TOUCHSAMPLESYNTH_SAWOSCILLATOR_H
#define TOUCHSAMPLESYNTH_SAWOSCILLATOR_H


#include "../SoundGenerator.h"
#include "SecondOrderIirFilter.h"
#include "PolyBLEP.h"

class SawOscillator: public SoundGenerator {
public:
    float getNextSample() override;
    void setNote(float) override;
    explicit SawOscillator(float);

    SawOscillator();
private:
    float phaseIncrement;
    float samplingRate;
    float currentPhase;
    SecondOrderIirFilter * decimatingFilter;
    SecondOrderIirFilter * decimatingFilter2;
    void calculateFilterCoefficients(SecondOrderIirFilter*) const;
    PolyBLEP*polyBlep;
};


#endif //TOUCHSAMPLESYNTH_SAWOSCILLATOR_H
