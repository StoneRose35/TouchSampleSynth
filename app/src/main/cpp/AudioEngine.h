//
// Created by philipp on 01.09.23.
//

#ifndef TOUCHSAMPLESYNTH_AUDIOENGINE_H
#define TOUCHSAMPLESYNTH_AUDIOENGINE_H

#include "aaudio/AAudio.h"
class AudioEngine {

public:
    bool start();
    void stop();
    void restart();
    int32_t getSamplingRate() const;


private:
    AAudioStream *stream_;
    int32_t samplingRate;
};



#endif //TOUCHSAMPLESYNTH_AUDIOENGINE_H
