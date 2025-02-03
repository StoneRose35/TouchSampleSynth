//
// Created by philipp on 31.01.25.
//

#ifndef TOUCHSAMPLESYNTH_UTILS_H
#define TOUCHSAMPLESYNTH_UTILS_H

#include <cmath>

static inline float saturate(float input ) { //clamp without branching
#define _limit 0.99f // was 0.95 originally
    float x1 = fabsf( input + _limit );
    float x2 = fabsf( input - _limit );
    return 0.5f * (x1 - x2);
}

static inline float crossfade( float amount, float a, float b ) {
    return (1-amount)*a + amount*b;
}
#endif //TOUCHSAMPLESYNTH_UTILS_H
