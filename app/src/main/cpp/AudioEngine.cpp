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
#include "SimpleSubtractiveSynth.h"
#include <ctime>


constexpr int32_t kBufferSizeInBursts = 2;
static AudioEngine *audioEngine = new AudioEngine();

aaudio_data_callback_result_t dataCallback(
        AAudioStream *stream,
        void *,
        void *audioData,
        int32_t numFrames) {
    clock_t start, end;
    auto * audioDataFloat = static_cast<float *>(audioData);
    float audioSum;
    float availableTime;
    ssize_t numMessages;
    size_t midiBytesReceived;
    int32_t opCode;
    int64_t timestamp;
    uint8_t midiDataBuffer[4];
    uint8_t midiProcessed;
    availableTime = (float)numFrames/((float) (AAudioStream_getSamplesPerFrame(stream)*
                                               AAudioStream_getSampleRate(stream)));
    float usedTime;
    start = clock();
    for(uint32_t i=0;i<numFrames;i++)
    {

        audioSum=0.0f;
        for (int8_t c=0;c<N_SOUND_GENERATORS;c++)
        {
            if (audioEngine->getSoundGenerator(c) != nullptr) {
                audioSum += audioEngine->getSoundGenerator(c)->getNextSample();
            }
        }
        audioSum /= (float)audioEngine->getNSoundGenerators();
        audioEngine->averageVolume = audioEngine->averageVolume*AVERAGE_LOWPASS_ALPHA  + fabsf(audioSum)*(1.0f - AVERAGE_LOWPASS_ALPHA);
        *(audioDataFloat + i) = audioSum;
    }
    if (audioEngine->midiOutputPort != nullptr) {
        numMessages =
                AMidiOutputPort_receive(audioEngine->midiOutputPort, &opCode, midiDataBuffer,
                                        sizeof(midiDataBuffer), &midiBytesReceived, &timestamp);
        if (numMessages >= 0) {
            if (opCode == AMIDI_OPCODE_DATA) {
                // Dispatch the MIDI data….

                if ((*(midiDataBuffer + 0) & 0xF0) == MIDI_NOTE_ON )
                {
                    midiProcessed = 0;
                    for (int8_t c=0;c<N_SOUND_GENERATORS;c++)
                    {
                        if (audioEngine->getSoundGenerator(c) != nullptr
                        && ((audioEngine->getSoundGenerator(c)->availableForMidi & MIDI_AVAILABLE_MSK) != 0)
                           && (((audioEngine->getSoundGenerator(c)->availableForMidi & MIDI_TAKEN_MSK) == 0)
                            || ((audioEngine->getSoundGenerator(c)->availableForMidi & MIDI_NOTE_CHANGE_MSK) != 0))
                        && midiProcessed == 0) {
                            audioEngine->getSoundGenerator(c)->setNote(midiDataBuffer[1]);
                            audioEngine->getSoundGenerator(c)->switchOn(0);
                            audioEngine->getSoundGenerator(c)->availableForMidi |= MIDI_TAKEN_MSK | midiDataBuffer[1];
                            midiProcessed = 1;
                        }
                    }
                }
                else if ((*(midiDataBuffer + 0) & 0xF0) == MIDI_NOTE_OFF )
                {
                    midiProcessed = 0;
                    for (int8_t c=0;c<N_SOUND_GENERATORS;c++)
                    {
                        if (audioEngine->getSoundGenerator(c) != nullptr
                            && ((audioEngine->getSoundGenerator(c)->availableForMidi & MIDI_AVAILABLE_MSK) != 0 )
                            && ((audioEngine->getSoundGenerator(c)->availableForMidi & 0x7F) == midiDataBuffer[1] )
                            && midiProcessed == 0) {
                            audioEngine->getSoundGenerator(c)->switchOff(0);
                            midiProcessed = 1;
                        }
                    }
                }
            }
        }// else {
        // some error occurred, the negative numMessages is the error code
        // int32_t errorCode = numMessages;
        // }
    }
    end = clock();
    usedTime = (float)(end-start)/CLOCKS_PER_SEC;
    audioEngine->cpuLoad = usedTime /availableTime;
    return AAUDIO_CALLBACK_RESULT_CONTINUE;
}

void errorCallback(AAudioStream *,
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
        if (streamState!=AAUDIO_STREAM_STATE_CLOSED && streamState > 0) {
            return false;
        }
    }
    AAudioStreamBuilder *streamBuilder;
    AAudio_createStreamBuilder(&streamBuilder);
    AAudioStreamBuilder_setFormat(streamBuilder, AAUDIO_FORMAT_PCM_FLOAT);
    AAudioStreamBuilder_setChannelCount(streamBuilder, 1);
    AAudioStreamBuilder_setPerformanceMode(streamBuilder, AAUDIO_PERFORMANCE_MODE_LOW_LATENCY);
    AAudioStreamBuilder_setFramesPerDataCallback(streamBuilder,256);
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
    midiOutputPort = nullptr;
    midiDevice = nullptr;
    samplingRate = 48000.0f;
    cpuLoad=0.0f;
    for (uint16_t c=0;c<N_SOUND_GENERATORS;c++)
    {
        *(soundGenerators + c) = nullptr;
    }
    averageVolume = 0.0f;

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

int8_t AudioEngine::addSoundGenerator(SoundGeneratorType sgt) {
    MusicalSoundGenerator *  sg;
    int8_t idx=N_SOUND_GENERATORS;
    // get next free slot
    for (int8_t c=0;c<N_SOUND_GENERATORS;c++)
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
        case SIMPLE_SUBTRACTIVE_SYNTH:
            sg=new SimpleSubtractiveSynth((float)samplingRate);
            if (idx < N_SOUND_GENERATORS) {
                soundGenerators[idx] = sg;
            }
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


