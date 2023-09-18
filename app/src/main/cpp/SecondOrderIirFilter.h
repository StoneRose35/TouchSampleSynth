//
// Created by philipp on 18.09.23.
//

#ifndef TOUCHSAMPLESYNTH_SECONDORDERIIRFILTER_H
#define TOUCHSAMPLESYNTH_SECONDORDERIIRFILTER_H


#include "SoundProcessor.h"

class SecondOrderIirFilter: SoundProcessor {
private:
    float w[3];
public:
    float coeffA[2];
    float coeffB[3];
    float processSample(float) override;
    SecondOrderIirFilter();
};


#endif //TOUCHSAMPLESYNTH_SECONDORDERIIRFILTER_H
