//
// Created by philipp on 17.12.24.
//

#include <malloc.h>
#include "Looper.h"
#include <android/log.h>

#define APP_NAME "TouchSampleSynth"

int Looper::getType() {
    return LOOPER;
}

Looper::Looper(float sr) : MusicalSoundGenerator(sr) {
        loopEnd=0;
        readPointer=0;
        writePointer=0;
        state=LOOPER_STATE_STOPPED;
        sample = ((float*)malloc(sizeof(float)*MIN_SAMPLE_SIZE));
        currentBufferSize = MIN_SAMPLE_SIZE;
        clearSample();
}

float Looper::getNextSample() {
    if (loopEnd > 0 && state != LOOPER_STATE_STOPPED)
    {
        float sampleVal = (*(sample + readPointer++));
        if (readPointer >= loopEnd)
        {
            readPointer = 0;
        }
        return sampleVal;
    }
    return  0.0f;
}

void Looper::processNextSample(float s) {
    if (state == LOOPER_STATE_RECORDING) {
        if (loopEnd > 0)
        {
            *(sample + writePointer++) = (s + *(sample + writePointer));
        }
        else
        {
            *(sample + writePointer++) = s;
        }

        if (loopEnd > 0 && writePointer >= loopEnd)
        {
            writePointer = 0;
        }
        if (writePointer > currentBufferSize - SAMPLE_SIZE_INCREMENT)
        {
            float * newBufferLocation;
            newBufferLocation = (float*)realloc(sample,sizeof(float)*(currentBufferSize + SAMPLE_SIZE_INCREMENT));
            if (newBufferLocation != nullptr) {
                sample = newBufferLocation;
                currentBufferSize += SAMPLE_SIZE_INCREMENT;
                //__android_log_print(ANDROID_LOG_VERBOSE,APP_NAME,"Reallocated sample content, new size: %d, new location %08llx",currentBufferSize,(uint64_t)sample);
            }
            else // failed to alloc more memory, stop recording
            {
                loopEnd = writePointer;
                state = LOOPER_STATE_STOPPED;
            }
        }
    }
}

bool Looper::isSounding() {
    return (state == LOOPER_STATE_PLAYING);
}

void Looper::switchOff(uint8_t vel) {
    MusicalSoundGenerator::switchOff(vel);
    if (state == LOOPER_STATE_PLAYING)
    {
        state = LOOPER_STATE_STOPPED;
        readPointer = 0;
        writePointer = 0;
    }
}

void Looper::switchOn(uint8_t vel) {
    MusicalSoundGenerator::switchOn(vel);
    if (loopEnd > 0 && state != LOOPER_STATE_RECORDING)
    {
        state = LOOPER_STATE_PLAYING;
    }
}


Looper::~Looper()
{
    free(sample);
}

void Looper::startRecording() {
    if (state != LOOPER_STATE_PLAYING)
    {
        loopEnd = 0;
        writePointer=0;
    }
    else
    {
        writePointer = readPointer;
    }
    state = LOOPER_STATE_RECORDING;
}

void Looper::stopRecording() {
    if (loopEnd == 0) {
        loopEnd = writePointer;
    }
    state = LOOPER_STATE_PLAYING;
}

void Looper::clearSample() {
    for(uint32_t c=0;c<currentBufferSize;c++)
    {
        *(sample+c) = 0.0f;
    }

}

uint32_t Looper::getReadPointer() {
    return readPointer;
}

void Looper::setReadPointer(uint32_t) {

}

uint32_t Looper::getWritePointer() {
    return writePointer;
}

void Looper::setWritePointer(uint32_t) {

}

uint32_t Looper::getLoopEnd() {
    return loopEnd;
}

void Looper::setLoopEnd(uint32_t) {

}

void Looper::resetSample() {
    loopEnd=0;
}
