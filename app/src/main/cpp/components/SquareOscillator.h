//
// Created by philipp on 18.01.25.
//

#ifndef TOUCHSAMPLESYNTH_SQUAREOSCILLATOR_H
#define TOUCHSAMPLESYNTH_SQUAREOSCILLATOR_H


#include <stdint.h>
#include "../SoundGenerator.h"
#include "SecondOrderIirFilter.h"

class SquareOscillator : public SoundGenerator {
public:
    float getNextSample() override ;
    void setNote(float) override;
    SquareOscillator(float);
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
};


#endif //TOUCHSAMPLESYNTH_SQUAREOSCILLATOR_H
