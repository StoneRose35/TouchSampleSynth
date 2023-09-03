//
// Created by philipp on 01.09.23.
//

#ifndef TOUCHSAMPLESYNTH_AUDIOENGINE_H
#define TOUCHSAMPLESYNTH_AUDIOENGINE_H

#include "aaudio/AAudio.h"
#include "SoundGenerator.h"
class AudioEngine {

public:
    bool start();
    void stop();
    void restart();
    int32_t getSamplingRate() const;

    int8_t getNSoundGenerators();
    SoundGenerator * getSoundGenerator(uint8_t);
    AudioEngine();
    ~AudioEngine();

private:
    AAudioStream *stream_;
    int32_t samplingRate;
    SoundGenerator ** soundGenerators;
    int8_t nSoundGenerators;
};



#endif //TOUCHSAMPLESYNTH_AUDIOENGINE_H
