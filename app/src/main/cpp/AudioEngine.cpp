//
// Created by philipp on 01.09.23.
//
#include <android/log.h>
#include <jni.h>
#include <string>
#include <thread>
#include <mutex>
#include "AudioEngine.h"
#include "SoundGenerator.h"
#include "MusicalSoundGenerator.h"
#include "SineMonoSynth.h"

#define N_SOUND_GENERATORS 64
constexpr int32_t kBufferSizeInBursts = 2;
static AudioEngine *audioEngine = new AudioEngine();

aaudio_data_callback_result_t dataCallback(
        AAudioStream *stream,
        void *userData,
        void *audioData,
        int32_t numFrames) {

    auto * audioDataFloat = static_cast<float *>(audioData);
    float audioSum = 0.0f;
    //auto * audioEngineInstance = static_cast<class AudioEngine *>(userData);
    for(uint32_t i=0;i<numFrames;i++)
    {
        for (int8_t c=0;c<N_SOUND_GENERATORS;c++)
        {
            if (audioEngine->getSoundGenerator(c) != nullptr) {
                audioSum = audioEngine->getSoundGenerator(c)->getNextSample();
            }
        }
        audioSum /= (float)audioEngine->getNSoundGenerators();
        *(audioDataFloat + i) = audioSum;
    }
    return AAUDIO_CALLBACK_RESULT_CONTINUE;
}

void errorCallback(AAudioStream *stream,
                   void *userData,
                   aaudio_result_t error){
    if (error == AAUDIO_ERROR_DISCONNECTED){
        std::function<void(void)> restartFunction = [ObjectPtr = static_cast<AudioEngine *>(userData)] { ObjectPtr->restart(); };
        new std::thread(restartFunction);
    }
}

bool AudioEngine::start() {
    if (stream_ != nullptr) {
        aaudio_stream_state_t streamState = AAudioStream_getState(stream_);
        if (streamState!=AAUDIO_STREAM_STATE_CLOSED && streamState >= 0) {
            return false;
        }
    }
    AAudioStreamBuilder *streamBuilder;
    AAudio_createStreamBuilder(&streamBuilder);
    AAudioStreamBuilder_setFormat(streamBuilder, AAUDIO_FORMAT_PCM_FLOAT);
    AAudioStreamBuilder_setChannelCount(streamBuilder, 1);
    AAudioStreamBuilder_setPerformanceMode(streamBuilder, AAUDIO_PERFORMANCE_MODE_LOW_LATENCY);
    AAudioStreamBuilder_setDataCallback(streamBuilder, ::dataCallback, nullptr);
    AAudioStreamBuilder_setErrorCallback(streamBuilder, ::errorCallback, this);

    // Opens the stream.
    aaudio_result_t result = AAudioStreamBuilder_openStream(streamBuilder, &stream_);
    if (result != AAUDIO_OK) {
        __android_log_print(ANDROID_LOG_ERROR, "AudioEngine", "Error opening stream %s",
                            AAudio_convertResultToText(result));
        return false;
    }

    // Retrieves the sample rate of the stream for our oscillator.
    samplingRate = AAudioStream_getSampleRate(stream_);

    // Sets the buffer size.
    AAudioStream_setBufferSizeInFrames(
            stream_, AAudioStream_getFramesPerBurst(stream_) * kBufferSizeInBursts);

    // Starts the stream.
    result = AAudioStream_requestStart(stream_);
    if (result != AAUDIO_OK) {
        __android_log_print(ANDROID_LOG_ERROR, "AudioEngine", "Error starting stream %s",
                            AAudio_convertResultToText(result));
        return false;
    }

    AAudioStreamBuilder_delete(streamBuilder);
    return true;

}

void AudioEngine::stop() {
    if (stream_ != nullptr) {
        AAudioStream_requestStop(stream_);
        AAudioStream_close(stream_);
    }
}

void AudioEngine::restart() {
    static std::mutex restartingLock;
    if (restartingLock.try_lock()){
        stop();
        start();
        restartingLock.unlock();
    }
}

int32_t AudioEngine::getSamplingRate() const {
    return samplingRate;
}

int8_t AudioEngine::getNSoundGenerators() {
    int8_t cntr = 0;
    for (uint8_t c=0;c<N_SOUND_GENERATORS;c++)
    {
        if (*(soundGenerators+c) != nullptr)
        {
            cntr++;
        }
    }
    return cntr;
}

AudioEngine::AudioEngine() {
    soundGenerators=(MusicalSoundGenerator**)malloc(N_SOUND_GENERATORS*sizeof(MusicalSoundGenerator*));
    stream_ = nullptr;
    samplingRate = 48000.0f;
    for (uint16_t c=0;c<N_SOUND_GENERATORS;c++)
    {
        *(soundGenerators + c) = nullptr;
    }

}

AudioEngine::~AudioEngine() {
    free(soundGenerators);

}

MusicalSoundGenerator *AudioEngine::getSoundGenerator(int8_t idx) {
    if (idx < N_SOUND_GENERATORS && idx >= 0)
    {
        return *(soundGenerators + idx);
    }
    return nullptr;
}

int32_t AudioEngine::addSoundGenerator(SoundGeneratorType sgt) {
    SineMonoSynth *  sg;
    uint16_t idx=N_SOUND_GENERATORS;
    // get next free slot
    for (uint16_t c=0;c<N_SOUND_GENERATORS;c++)
    {
        if(*(soundGenerators+c)==nullptr)
        {
            idx=c;
            break;
        }
    }
    switch(sgt)
    {
        case SINE_MONO_SYNTH:
            sg=new SineMonoSynth();
            if (idx < N_SOUND_GENERATORS) {
                soundGenerators[idx] = sg;
            }
            break;
        case ANALOGUE_SYNTH:
            break;
        case FM_SYNTH:
            break;
        case SAMPLER:
            break;
    }
    return idx;
}

void AudioEngine::removeSoundGenerator(int idx) {
    if (*(soundGenerators + idx)!= nullptr)
    {
        delete (*(soundGenerators + idx));
        *(soundGenerators + idx) = nullptr;
    }
}

AudioEngine * getAudioEngine()
{
    return audioEngine;
}


