//
// Created by philipp on 18.01.25.
//

#ifndef TOUCHSAMPLESYNTH_SQUAREOSCILLATOR_H
#define TOUCHSAMPLESYNTH_SQUAREOSCILLATOR_H


#include <cstdint>
#include "../SoundGenerator.h"
#include "SecondOrderIirFilter.h"
#include "PolyBLEP.h"

class SquareOscillator : public SoundGenerator {
public:
    float getNextSample() override ;
    void setNote(float) override;
    explicit SquareOscillator(float);
    float getPulseWidth() const;
    uint8_t setPulseWidth(float);

    SquareOscillator();
private:
    float phaseIncrement;
    float samplingRate;
    float currentPhase;
    float pulseWidth; // from -1.0 to 1.0, 0.0 is symmetrical
    SecondOrderIirFilter * decimatingFilter;
    SecondOrderIirFilter * decimatingFilter2;
    void calculateFilterCoefficients(SecondOrderIirFilter*) const;
    PolyBLEP * polyBlep;
};


#endif //TOUCHSAMPLESYNTH_SQUAREOSCILLATOR_H
