//
// Created by philipp on 23.12.23.
//

#include "Sampler.h"
#include "AudioEngine.h"
#include <cstdlib>


void Sampler::setLoopStartIndex(uint32_t idx) {
    if (idx < loopEndIndex) {
        loopStartIndex = idx;
    }
}

uint32_t Sampler::getLoopStartIndex() const {
    return loopStartIndex;
}

void Sampler::setLoopEndIndex(uint32_t idx) {
    if (idx > loopStartIndex && idx <= sampleEndIndex)
    {
        loopEndIndex = idx;
    }
}

uint32_t Sampler::getLoopEndIndex() const {
    return loopEndIndex;
}

void Sampler::setMode(uint8_t loopmode) {
    loopMode = loopmode;
}

uint8_t Sampler::getMode() const {
    return loopMode;
}

void Sampler::setSample(const float * inputData, uint32_t size) {
    if (size < DEFAULT_SAMPLE_SIZE)
    {
        for(uint32_t c=0;c<size;c++)
        {
            *(sampleData + c)= *(inputData+ c);
        }
        dataSize = size;
    }
}

float Sampler::getNextSample() {
    if (currentIndex != 0xFFFFFFFF)
    {
        if ((loopMode & (1 << SAMPLER_MODE_LOOP))== (1 << SAMPLER_MODE_LOOP))
        {
            if (currentIndex >= loopEndIndex)
            {
                currentIndex = loopStartIndex + (currentIndex - loopEndIndex);
            }
        }
        if (currentIndex>=sampleEndIndex)
        {
            currentIndex=0xFFFFFFFF;
        }
        else
        {
            currentIndex = (uint32_t)((float)currentIndex + phaseIncrement);
        }
    }
    if (currentIndex != 0xFFFFFFFF)
    {
        return getNextSampleVolume()*sampleData[currentIndex];
    }
    else
    {
        return 0.f;
    }
}

void Sampler::setNote(float note) {
    MusicalSoundGenerator::setNote(note);
}

void Sampler::trigger(uint8_t vel) {
    MusicalSoundGenerator::trigger(vel);
    currentIndex = sampleStartIndex;
}

void Sampler::switchOn(uint8_t vel) {
    MusicalSoundGenerator::switchOn(vel);
    currentIndex = sampleStartIndex;
}

void Sampler::switchOff(uint8_t vel) {
    MusicalSoundGenerator::switchOff(vel);
    if ((loopMode & (1 << SAMPLE_MODE_TRIGGERED))==0) {
        currentIndex = 0xFFFFFFFF;
    }
}

bool Sampler::isSounding() {
    return currentIndex != 0xFFFFFFFF;
}

int Sampler::getType() {
    return SAMPLER;
}

uint32_t Sampler::getSample(float ** samplePtr) {
    *(samplePtr) = sampleData;
    return dataSize;
}

Sampler::Sampler(float sr): MusicalSoundGenerator(sr) {
    loopStartIndex=0;
    loopEndIndex=DEFAULT_SAMPLE_SIZE;
    sampleStartIndex=0;
    sampleEndIndex=DEFAULT_SAMPLE_SIZE;
    currentIndex=0xFFFFFFFF;
    loopMode=0;
    dataSize = DEFAULT_SAMPLE_SIZE;
    sampleData = (float*)malloc(DEFAULT_SAMPLE_SIZE*sizeof(float));
    phaseIncrement = 1.0f;
    for(uint32_t c=0;c<DEFAULT_SAMPLE_SIZE;c++)
    {
        *(sampleData + c) = 0.f;
    }
}

Sampler::~Sampler() {
    free((void*)sampleData);
}

void Sampler::setSampleStartIndex(uint32_t sampleStartIdx) {
    if (sampleStartIdx < sampleEndIndex && sampleStartIdx <= loopStartIndex) {
        sampleStartIndex = sampleStartIdx;
    }
}

uint32_t Sampler::getSampleStartIndex() const {
    return sampleStartIndex;
}

void Sampler::setSampleEndIndex(uint32_t sampleEndIdx) {
    if (sampleEndIdx > sampleStartIndex && sampleEndIdx >= loopEndIndex)
    {
        sampleEndIndex = sampleEndIdx;
    }
}

uint32_t Sampler::getSampleEndIndex() const {
    return sampleEndIndex;
}

void Sampler::setPitchBend(float pb) {
    if (pb < 0.0f)
    {
        phaseIncrement = 1.0f + pb/4.0f;
    }
    else
    {
        phaseIncrement = 1.0f + pb*4;
    }
}
