//
// Created by philipp on 15.09.23.
//

#ifndef TOUCHSAMPLESYNTH_SAWOSCILLATOR_H
#define TOUCHSAMPLESYNTH_SAWOSCILLATOR_H


#include "SoundGenerator.h"

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
};


#endif //TOUCHSAMPLESYNTH_SAWOSCILLATOR_H
