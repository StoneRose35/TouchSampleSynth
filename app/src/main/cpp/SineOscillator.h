//
// Created by philipp on 03.09.23.
//

#ifndef TOUCHSAMPLESYNTH_SINEOSCILLATOR_H
#define TOUCHSAMPLESYNTH_SINEOSCILLATOR_H


#include "SoundGenerator.h"


class SineOscillator: SoundGenerator {
private:
    float phaseIncrement;
    float currentPhase;
    float samplingRate;
public:
    float getNextSample();
    void setNote(float);
    SineOscillator(float);

    SineOscillator();
};


#endif //TOUCHSAMPLESYNTH_SINEOSCILLATOR_H
