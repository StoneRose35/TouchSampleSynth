//
// Created by philipp on 01.09.23.
//

#ifndef TOUCHSAMPLESYNTH_AUDIOENGINE_H
#define TOUCHSAMPLESYNTH_AUDIOENGINE_H

#include <typeinfo>
#include "aaudio/AAudio.h"
#include "amidi/AMidi.h"
#include "SoundGenerator.h"
#include "MusicalSoundGenerator.h"


#define AVERAGE_LOWPASS_ALPHA 0.99f

#define MAX_SOUND_GENERATORS 64

#define MIDI_NOTE_OFF 0x80
#define MIDI_NOTE_ON 0x90
#define MIDI_CC 0xB0

#define MIDI_AVAILABLE_MSK 0x200
#define MIDI_NOTE_CHANGE_MSK 0x100
#define MIDI_TAKEN_MSK 0x80

class AudioEngine {

public:

    bool start();
    void stop();
    void restart();
    int32_t getSamplingRate() const;

    int8_t getActiveSoundGenerators();
    int8_t getNSoundGenerators() const;
    MusicalSoundGenerator * getSoundGenerator(int8_t);
    int8_t addSoundGenerator(SoundGeneratorType);
    void removeSoundGenerator(int idx);
    void emptySoundGenerators();
    AudioEngine();
    ~AudioEngine();
    float averageVolume;
    float cpuLoad;
    AMidiOutputPort * midiOutputPort;
    AMidiInputPort * midiInputPort;
    AMidiDevice * midiDevice;
    int32_t getBufferCapacityInFrames() const;
    int8_t setBufferCapacityInFrames(int32_t);
    int32_t getFramesPerDataCallback() const;
    int8_t setFramesPerDataCallback(int32_t);

    // switches on the next voice by browsing all sound sound generators which are available for midi
    // and idle
    // midiData[0] is the voice nr and mididata[1] is the velocity
    // returns true, if a voice is assigned, false otherwise
    static bool startNextVoice(uint8_t *midiData);
    // switches off the voice which has the given midi note assigned and which is on
    // midiData[0] is the voice nr and mididata[1] is the velocity
    // returns true if a voice could be found and is switched off, false otherwise
    static bool stopVoice(uint8_t * midiData);

    template <typename T> T* getSoundGeneratorFromJni(JNIEnv * env,jobject me)
    {
        jclass synth=env->GetObjectClass(me);
        jmethodID getInstance=env->GetMethodID(synth,"getInstance","()B");
        int8_t instance = env->CallByteMethod(me,getInstance);
        MusicalSoundGenerator * msg = this->getSoundGenerator(instance);
        if  (msg == nullptr)
        {
            return nullptr;
        }
        return (T*)msg;
    }

private:
    AAudioStream *stream_= nullptr;
    AAudioStream *streamRecording_ = nullptr;
    int32_t samplingRate;
    MusicalSoundGenerator ** soundGenerators= nullptr;
    int32_t kBufferSizeInBursts = 2;
    int32_t framesPerDataCallback = 64;
    int32_t bufferCapacityInFrames = 1024;
    int8_t nSoundGenerators=24;


};

AudioEngine * getAudioEngine();


#endif //TOUCHSAMPLESYNTH_AUDIOENGINE_H
