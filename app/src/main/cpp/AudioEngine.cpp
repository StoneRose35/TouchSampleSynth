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

constexpr int32_t kBufferSizeInBursts = 2;

aaudio_data_callback_result_t dataCallback(
        AAudioStream *stream,
        void *userData,
        void *audioData,
        int32_t numFrames) {

    auto * audioDataFloat = static_cast<float *>(audioData);
    float audioSum = 0.0f;
    auto * audioEngine = static_cast<class AudioEngine *>(userData);
    for(uint32_t i=0;i<numFrames;i++)
    {
        for (uint8_t c=0;c<audioEngine->getNSoundGenerators();c++)
        {
            audioSum = audioEngine->getSoundGenerator(c)->getNextSample();
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
        std::function<void(void)> restartFunction = std::bind(&AudioEngine::restart,
                                                              static_cast<AudioEngine *>(userData));
        new std::thread(restartFunction);
    }
}

bool AudioEngine::start() {
    AAudioStreamBuilder *streamBuilder;
    AAudio_createStreamBuilder(&streamBuilder);
    AAudioStreamBuilder_setFormat(streamBuilder, AAUDIO_FORMAT_PCM_FLOAT);
    AAudioStreamBuilder_setChannelCount(streamBuilder, 1);
    AAudioStreamBuilder_setPerformanceMode(streamBuilder, AAUDIO_PERFORMANCE_MODE_LOW_LATENCY);
    AAudioStreamBuilder_setDataCallback(streamBuilder, ::dataCallback, this);
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
    return nSoundGenerators;
}

AudioEngine::AudioEngine() {
    nSoundGenerators = 0;
    soundGenerators=(MusicalSoundGenerator**)(64*sizeof(MusicalSoundGenerator*));
}

AudioEngine::~AudioEngine() {
    free(soundGenerators);

}

MusicalSoundGenerator *AudioEngine::getSoundGenerator(uint8_t idx) {
    if (idx < nSoundGenerators)
    {
        return *(soundGenerators + idx);
    }
    return nullptr;
}

uint32_t AudioEngine::addSoundGenerator(SoundGeneratorType sgt) {
    SineMonoSynth *  sg;
    switch(sgt)
    {
        case SINE_MONO_SYNTH:
            sg=new SineMonoSynth();
            soundGenerators[nSoundGenerators++] = sg;
            break;
        case ANALOGUE_SYNTH:
            break;
        case FM_SYNTH:
            break;
        case SAMPLER:
            break;
    }
    return nSoundGenerators-1;
}


