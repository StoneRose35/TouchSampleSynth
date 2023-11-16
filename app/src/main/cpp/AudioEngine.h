//
// Created by philipp on 01.09.23.
//

#ifndef TOUCHSAMPLESYNTH_AUDIOENGINE_H
#define TOUCHSAMPLESYNTH_AUDIOENGINE_H

#include "aaudio/AAudio.h"
#include "amidi/AMidi.h"
#include "SoundGenerator.h"
#include "MusicalSoundGenerator.h"

#define N_SOUND_GENERATORS 24
constexpr int32_t kBufferSizeInBursts = 2;
constexpr int32_t framesPerDataCallback = 64;
constexpr int32_t bufferCapacityInFrames = 1024;

#define AVERAGE_LOWPASS_ALPHA 0.99f

#define MIDI_NOTE_OFF 0x80
#define MIDI_NOTE_ON 0x90

#define MIDI_AVAILABLE_MSK 0x200
#define MIDI_NOTE_CHANGE_MSK 0x100
#define MIDI_TAKEN_MSK 0x80

class AudioEngine {

public:
    bool start();
    void stop();
    void restart();
    int32_t getSamplingRate() const;

    int8_t getNSoundGenerators();
    MusicalSoundGenerator * getSoundGenerator(int8_t);
    int8_t addSoundGenerator(SoundGeneratorType);
    void removeSoundGenerator(int idx);
    AudioEngine();
    ~AudioEngine();
    float averageVolume;
    float cpuLoad;
    AMidiOutputPort * midiOutputPort;
    AMidiDevice * midiDevice;
private:
    AAudioStream *stream_;
    int32_t samplingRate;
    MusicalSoundGenerator ** soundGenerators;

};

AudioEngine * getAudioEngine();

#endif //TOUCHSAMPLESYNTH_AUDIOENGINE_H
