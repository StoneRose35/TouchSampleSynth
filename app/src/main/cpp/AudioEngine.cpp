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
#include "SoundRecorder.h"
#include <ctime>

#define APP_NAME "TouchSampleSynth"


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
    uint8_t lastMidiStatus=0x00;
    availableTime = (float)numFrames/((float) (AAudioStream_getSamplesPerFrame(stream)*
                                               AAudioStream_getSampleRate(stream)));
    float usedTime;
    start = clock();
    int32_t xrunCnt;
    xrunCnt = AAudioStream_getXRunCount(stream);
    if (xrunCnt > 0)
    {
        //__android_log_print(ANDROID_LOG_VERBOSE,APP_NAME,"XRuns occurred: #%d",xrunCnt);
    }
    for(uint32_t i=0;i<numFrames;i++)
    {

        audioSum=0.0f;
        for (int8_t c=0;c<audioEngine->getNSoundGenerators();c++)
        {
            if (audioEngine->getSoundGenerator(c) != nullptr) {
                audioSum += audioEngine->getSoundGenerator(c)->getNextSample();
            }
        }
        audioSum /= 8.0f;//;audioEngine->getNSoundGenerators();
        audioEngine->averageVolume = audioEngine->averageVolume*AVERAGE_LOWPASS_ALPHA  + fabsf(audioSum)*(1.0f - AVERAGE_LOWPASS_ALPHA);
        if (audioSum > 1.0f)
        {
            audioSum = 1.0f;
        }
        else if (audioSum < -1.0f)
        {
            audioSum = -1.0f;
        }
        *(audioDataFloat + i) = audioSum;
    }
    if (audioEngine->midiOutputPort != nullptr) {
        numMessages =
                AMidiOutputPort_receive(audioEngine->midiOutputPort, &opCode, midiDataBuffer,
                                        sizeof(midiDataBuffer), &midiBytesReceived, &timestamp);
        while (numMessages > 0) {
            if (opCode == AMIDI_OPCODE_DATA) {
                // ordinary note on
                if ((*(midiDataBuffer + 0) & 0xF0) == MIDI_NOTE_ON && midiBytesReceived==3 && midiDataBuffer[2] != 0x0)
                {
                    lastMidiStatus = MIDI_NOTE_ON;
                    //__android_log_print(ANDROID_LOG_VERBOSE,APP_NAME,"midi note on: %d",midiDataBuffer[1]);
                    AudioEngine::startNextVoice(midiDataBuffer+1);
                }
                // note on with velocity 0, this is effectively a note off
                else if ((*(midiDataBuffer + 0) & 0xF0) == MIDI_NOTE_ON && midiBytesReceived==3 && midiDataBuffer[2] == 0x0)
                {
                    lastMidiStatus = MIDI_NOTE_ON;
                    //__android_log_print(ANDROID_LOG_VERBOSE,APP_NAME,"midi note on with vel 0: %d",midiDataBuffer[1]);
                    AudioEngine::stopVoice(midiDataBuffer+1);
                }
                // ordinary note off
                else if ((*(midiDataBuffer + 0) & 0xF0) == MIDI_NOTE_OFF && midiBytesReceived==3)
                {
                    lastMidiStatus = MIDI_NOTE_OFF;
                    //__android_log_print(ANDROID_LOG_VERBOSE,APP_NAME,"midi note off: %d",midiDataBuffer[1]);
                    AudioEngine::stopVoice(midiDataBuffer+1);
                }
                // unhandled midi command
                else if ((*(midiDataBuffer + 0) & 0x80) != 0) // unhandled midi command
                {
                    //__android_log_print(ANDROID_LOG_VERBOSE,APP_NAME,"unknown midi command: %x",midiDataBuffer[0]);
                    lastMidiStatus = *(midiDataBuffer + 0) & 0xF0;
                }
                // running status
                else if ((*(midiDataBuffer + 0) & 0x80)==0x00 && midiBytesReceived==2)
                {
                    // running status note on
                    if (lastMidiStatus==MIDI_NOTE_ON && midiDataBuffer[1] != 0)
                    {
                        //__android_log_print(ANDROID_LOG_VERBOSE,APP_NAME,"running status, note on: %d",midiDataBuffer[0]);
                        AudioEngine::startNextVoice(midiDataBuffer);
                    }
                    // running status note on with velocity zero -> note off
                    else if ((lastMidiStatus==MIDI_NOTE_ON && midiDataBuffer[1] == 0) || lastMidiStatus==MIDI_NOTE_OFF)
                    {
                        //__android_log_print(ANDROID_LOG_VERBOSE,APP_NAME,"running status, note on with vel 0: %d",midiDataBuffer[0]);
                        AudioEngine::stopVoice(midiDataBuffer);
                    }
                }
            }
            numMessages = AMidiOutputPort_receive(audioEngine->midiOutputPort, &opCode, midiDataBuffer,
                                    sizeof(midiDataBuffer), &midiBytesReceived, &timestamp);
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

aaudio_data_callback_result_t dataCallbackRecorder(
        AAudioStream *stream,
        void *,
        void *audioData,
        int32_t numFrames)
{
    auto * audioDataFloat = static_cast<float *>(audioData);
    for(uint32_t i=0;i<numFrames;i++) {

        for (int8_t c = 0; c < audioEngine->getNSoundGenerators(); c++) {
            auto *  recorder = dynamic_cast<SoundRecorder*>(audioEngine->getSoundGenerator(c));
            if (recorder != nullptr) {
                recorder->processNextSample(*(audioDataFloat+i));
            }
        }
    }

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

    if (stream_ != nullptr || streamRecording_ != nullptr) {
        return false;
    }

    AAudioStreamBuilder *streamBuilder;
    AAudio_createStreamBuilder(&streamBuilder);
    AAudioStreamBuilder_setFormat(streamBuilder, AAUDIO_FORMAT_PCM_FLOAT);
    AAudioStreamBuilder_setChannelCount(streamBuilder, 1);
    AAudioStreamBuilder_setPerformanceMode(streamBuilder, AAUDIO_PERFORMANCE_MODE_LOW_LATENCY);
    AAudioStreamBuilder_setSharingMode(streamBuilder,AAUDIO_SHARING_MODE_EXCLUSIVE);
    AAudioStreamBuilder_setFramesPerDataCallback(streamBuilder,framesPerDataCallback);
    AAudioStreamBuilder_setBufferCapacityInFrames(streamBuilder,bufferCapacityInFrames);
    AAudioStreamBuilder_setDataCallback(streamBuilder, ::dataCallback, nullptr);
    AAudioStreamBuilder_setErrorCallback(streamBuilder, ::errorCallback, this);


    AAudioStreamBuilder *streamBuilderRecorder;
    AAudio_createStreamBuilder(&streamBuilderRecorder);
    AAudioStreamBuilder_setFormat(streamBuilderRecorder, AAUDIO_FORMAT_PCM_FLOAT);
    AAudioStreamBuilder_setChannelCount(streamBuilderRecorder, 1);
    AAudioStreamBuilder_setPerformanceMode(streamBuilderRecorder, AAUDIO_PERFORMANCE_MODE_LOW_LATENCY);
    AAudioStreamBuilder_setSharingMode(streamBuilder,AAUDIO_SHARING_MODE_EXCLUSIVE);
    AAudioStreamBuilder_setFramesPerDataCallback(streamBuilderRecorder,framesPerDataCallback);
    AAudioStreamBuilder_setBufferCapacityInFrames(streamBuilderRecorder,bufferCapacityInFrames);
    AAudioStreamBuilder_setDirection(streamBuilderRecorder, AAUDIO_DIRECTION_INPUT);
    AAudioStreamBuilder_setDataCallback(streamBuilderRecorder, ::dataCallbackRecorder, nullptr);
    AAudioStreamBuilder_setErrorCallback(streamBuilderRecorder, ::errorCallback, this);

    // Opens the stream.
    aaudio_result_t result = AAudioStreamBuilder_openStream(streamBuilder, &stream_);
    if (result != AAUDIO_OK) {
        __android_log_print(ANDROID_LOG_ERROR, "AudioEngine", "Error opening playback stream %s",
                            AAudio_convertResultToText(result));
        return false;
    }

    result = AAudioStreamBuilder_openStream(streamBuilderRecorder, &streamRecording_);
    if (result != AAUDIO_OK) {
        __android_log_print(ANDROID_LOG_ERROR, "AudioEngine", "Error opening recording stream %s",
                            AAudio_convertResultToText(result));
        streamRecording_ = nullptr;
    }

    // Retrieves the sample rate of the stream for our oscillator.
    samplingRate = AAudioStream_getSampleRate(stream_);

    // Sets the buffer size.
    AAudioStream_setBufferSizeInFrames(
            stream_, AAudioStream_getFramesPerBurst(stream_) * kBufferSizeInBursts);


    if (streamRecording_ != nullptr)
    {
        AAudioStream_setBufferSizeInFrames(
                streamRecording_, AAudioStream_getFramesPerBurst(streamRecording_) * kBufferSizeInBursts);
    }

    // Starts the stream.
    result = AAudioStream_requestStart(stream_);
    if (result != AAUDIO_OK) {
        __android_log_print(ANDROID_LOG_ERROR, "AudioEngine", "Error starting stream %s",
                            AAudio_convertResultToText(result));
        return false;
    }

    // Starts the stream for recording (if available).
    if (streamRecording_ != nullptr) {
        result = AAudioStream_requestStart(streamRecording_);
        if (result != AAUDIO_OK) {
            __android_log_print(ANDROID_LOG_ERROR, "AudioEngine", "Error starting stream %s",
                                AAudio_convertResultToText(result));
            return false;
        }
    }

    AAudioStreamBuilder_delete(streamBuilderRecorder);
    AAudioStreamBuilder_delete(streamBuilder);
    return true;

}

bool AudioEngine::startNextVoice(uint8_t *midiData) {
    uint8_t midiProcessed=0;
    for (int8_t c=0;c<audioEngine->getNSoundGenerators();c++)
    {
        if (audioEngine->getSoundGenerator(c) != nullptr
            && ((audioEngine->getSoundGenerator(c)->availableForMidi & MIDI_AVAILABLE_MSK) != 0)
            && (((audioEngine->getSoundGenerator(c)->availableForMidi & MIDI_TAKEN_MSK) == 0)
                || ((audioEngine->getSoundGenerator(c)->availableForMidi & MIDI_NOTE_CHANGE_MSK) != 0))
            && midiProcessed == 0) {
            __android_log_print(ANDROID_LOG_VERBOSE,APP_NAME,"switching on voice %d",c);
            audioEngine->getSoundGenerator(c)->setNote((float)midiData[0]-69.0f);
            audioEngine->getSoundGenerator(c)->setVolumeImmediate(1.0f - audioEngine->getSoundGenerator(c)->midiVelocityScaling + ((float)midiData[1])/127.0f*audioEngine->getSoundGenerator(c)->midiVelocityScaling);
            audioEngine->getSoundGenerator(c)->switchOn(midiData[1]);
            audioEngine->getSoundGenerator(c)->availableForMidi |= MIDI_TAKEN_MSK | midiData[0];
            midiProcessed = 1;
        }
    }
    return (bool)midiProcessed;
}

bool AudioEngine::stopVoice(uint8_t * midiData) {
    uint8_t midiProcessed = 0;
    for (int8_t c=0;c<audioEngine->getNSoundGenerators();c++)
    {
        if (audioEngine->getSoundGenerator(c) != nullptr
            && ((audioEngine->getSoundGenerator(c)->availableForMidi & MIDI_AVAILABLE_MSK) != 0 )
            && ((audioEngine->getSoundGenerator(c)->availableForMidi & MIDI_TAKEN_MSK) != 0)
            && ((audioEngine->getSoundGenerator(c)->availableForMidi & 0x7F) == midiData[0] )
            ) {
            __android_log_print(ANDROID_LOG_VERBOSE,APP_NAME,"switching off voice %d",c);
            audioEngine->getSoundGenerator(c)->switchOff(midiData[1]);
            audioEngine->getSoundGenerator(c)->availableForMidi &= ~(0xFF);
            midiProcessed = 1;
        }
    }
    return (bool)midiProcessed;
}

void AudioEngine::stop() {
    aaudio_result_t aaudioResult;
    aaudio_stream_state_t  aaudioStreamState;
    aaudio_stream_state_t nextState;
    if (stream_ != nullptr) {
        aaudioResult = AAudioStream_requestStop(stream_);
        if (aaudioResult == AAUDIO_OK)
        {
            // wait until the stream has stopped
            aaudioStreamState = AAudioStream_getState(stream_);
            AAudioStream_waitForStateChange(stream_,aaudioStreamState,&nextState,10000000);

            // close the stream, wait until the stream has closed
            AAudioStream_close(stream_);
            //AAudioStream_waitForStateChange(stream_,aaudioStreamState,&nextState,10000000);
        }
        stream_ = nullptr;
    }

    if (streamRecording_ != nullptr)
    {
        aaudioResult = AAudioStream_requestStop(streamRecording_);
        if (aaudioResult == AAUDIO_OK)
        {
            // wait until the stream has stopped
            aaudioStreamState = AAudioStream_getState(streamRecording_);
            AAudioStream_waitForStateChange(streamRecording_,aaudioStreamState,&nextState,10000000);

            // close the stream, wait until the stream has closed
            AAudioStream_close(streamRecording_);
            //AAudioStream_waitForStateChange(stream_,aaudioStreamState,&nextState,10000000);
        }
        streamRecording_ = nullptr;
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

int8_t AudioEngine::getActiveSoundGenerators() {
    int8_t cntr = 0;
    for (uint8_t c=0;c<nSoundGenerators;c++)
    {
        if (*(soundGenerators+c) != nullptr)
        {
            cntr++;
        }
    }
    return cntr;
}

int8_t AudioEngine::getNSoundGenerators() const{
    return nSoundGenerators;
}

AudioEngine::AudioEngine() {
    soundGenerators=(MusicalSoundGenerator**)malloc(MAX_SOUND_GENERATORS* sizeof(MusicalSoundGenerator*));
    stream_ = nullptr;
    midiOutputPort = nullptr;
    midiInputPort = nullptr;
    midiDevice = nullptr;
    samplingRate = 48000.0f;
    cpuLoad=0.0f;
    for (uint16_t c=0;c<MAX_SOUND_GENERATORS;c++)
    {
        *(soundGenerators + c) = nullptr;
    }
    averageVolume = 0.0f;

}

AudioEngine::~AudioEngine() {
    stop();
    free(soundGenerators);
    stream_=nullptr;

}

int32_t AudioEngine::getBufferCapacityInFrames() const
{
    return bufferCapacityInFrames;
}
int8_t AudioEngine::setBufferCapacityInFrames(int32_t bcif){
    if (bcif < 2*framesPerDataCallback)
    {
        return 1;
    }
    bufferCapacityInFrames = bcif;
    audioEngine->restart();
    return 0;
}
int32_t AudioEngine::getFramesPerDataCallback() const
{
    return framesPerDataCallback;
}
int8_t AudioEngine::setFramesPerDataCallback(int32_t fpdc)
{
    if (fpdc*2 > bufferCapacityInFrames)
    {
        return 1;
    }
    framesPerDataCallback = fpdc;
    audioEngine->restart();
    return 0;
}

MusicalSoundGenerator *AudioEngine::getSoundGenerator(int8_t idx) {
    if (idx < nSoundGenerators && idx >= 0)
    {
        return *(soundGenerators + idx);
    }
    return nullptr;
}



void AudioEngine::removeSoundGenerator(int idx) {
    if (*(soundGenerators + idx)!= nullptr)
    {
        delete (*(soundGenerators + idx));
        *(soundGenerators + idx) = nullptr;
    }
}

void AudioEngine::emptySoundGenerators()
{
    int c=0;
    for (c=0;c<MAX_SOUND_GENERATORS;c++)
    {
        if(*(soundGenerators + c) != nullptr)
        {
            delete (*(soundGenerators + c));
            *(soundGenerators + c) = nullptr;
        }
    }
}


AudioEngine * getAudioEngine()
{
    return audioEngine;
}


