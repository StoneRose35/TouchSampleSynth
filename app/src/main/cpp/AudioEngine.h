//
// Created by philipp on 01.09.23.
//

#ifndef TOUCHSAMPLESYNTH_AUDIOENGINE_H
#define TOUCHSAMPLESYNTH_AUDIOENGINE_H

#include "aaudio/AAudio.h"
#include "SoundGenerator.h"
#include "MusicalSoundGenerator.h"



class AudioEngine {

public:
    bool start();
    void stop();
    void restart();
    int32_t getSamplingRate() const;

    int8_t getNSoundGenerators();
    MusicalSoundGenerator * getSoundGenerator(int8_t);
    int32_t addSoundGenerator(SoundGeneratorType);
    void removeSoundGenerator(int idx);
    AudioEngine();
    ~AudioEngine();

private:
    AAudioStream *stream_;
    int32_t samplingRate;
    MusicalSoundGenerator ** soundGenerators;
};

AudioEngine * getAudioEngine();

#endif //TOUCHSAMPLESYNTH_AUDIOENGINE_H
