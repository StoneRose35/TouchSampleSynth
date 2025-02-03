//
// Created by philipp on 15.09.23.
//

#ifndef TOUCHSAMPLESYNTH_STILSONMOOGFILTER_H
#define TOUCHSAMPLESYNTH_STILSONMOOGFILTER_H

#include <cmath>
#include "../SoundProcessor.h"


class StilsonMoogFilter: SoundProcessor {
private:
    float p, Q;
    float samplingRate;
    float output, state[4];
public:
    float processSample(float) override;
    void SetCutoff(float);
    void SetResonance(float);
    explicit StilsonMoogFilter(float);
    StilsonMoogFilter();
    float reso, cutoff;
};


#endif //TOUCHSAMPLESYNTH_STILSONMOOGFILTER_H
