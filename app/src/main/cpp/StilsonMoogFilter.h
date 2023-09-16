//
// Created by philipp on 15.09.23.
//

#ifndef TOUCHSAMPLESYNTH_STILSONMOOGFILTER_H
#define TOUCHSAMPLESYNTH_STILSONMOOGFILTER_H

#include <cmath>
#include "SoundProcessor.h"

static inline float saturate( float input ) { //clamp without branching
#define _limit 0.95f
    float x1 = fabsf( input + _limit );
    float x2 = fabsf( input - _limit );
    return 0.5 * (x1 - x2);
}

static inline float crossfade( float amount, float a, float b ) {
    return (1-amount)*a + amount*b;
}

class StilsonMoogFilter: SoundProcessor {
private:
    float p, Q;
    float samplingRate;
    float output, state[4];
public:
    float processSample(float);
    void SetCutoff(float);
    void SetResonance(float);
    StilsonMoogFilter(float);
    StilsonMoogFilter();
    float reso, cutoff;
};


#endif //TOUCHSAMPLESYNTH_STILSONMOOGFILTER_H
