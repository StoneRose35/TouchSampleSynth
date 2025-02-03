//
// Created by philipp on 29.01.25.
//

#ifndef TOUCHSAMPLESYNTH_SAWTRIANGLEOSCILLATOR_H
#define TOUCHSAMPLESYNTH_SAWTRIANGLEOSCILLATOR_H

#include <cstdint>
#include "cmath"
#include "../SoundGenerator.h"
#include "SecondOrderIirFilter.h"
#include "PolyBLEP.h"


class SawTriangleOscillator: public SoundGenerator {


public:
    float getNextSample() override ;
    void setNote(float) override;
    explicit SawTriangleOscillator(float);
    float getPulseWidth() const;
    uint8_t setPulseWidth(float);
    explicit SawTriangleOscillator();
private:
    float phaseIncrement{};
    float samplingRate{};
    float currentPhase{};
    float pulseWidth{}; // from -1.0 to 1.0, 0.0 is symmetrical
    float currentVal{};
    SecondOrderIirFilter * decimatingFilter{};
    SecondOrderIirFilter * decimatingFilter2{};
    void calculateFilterCoefficients(SecondOrderIirFilter*) const;
    PolyBLEP * polyBlep;
};


#endif //TOUCHSAMPLESYNTH_SAWTRIANGLEOSCILLATOR_H
