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
    if (idx > loopStartIndex && idx <= dataSize)
    {
        loopEndIndex = idx;
    }
}

uint32_t Sampler::getLoopEndIndex() const {
    return loopEndIndex;
}

void Sampler::setMode(uint8_t loopmode) {
    if (loopmode < 2)
    {
        loopMode = loopmode;
    }
}

uint8_t Sampler::getMode() const {
    return loopMode;
}

void Sampler::loadSample(const float * inputData, uint32_t size) {
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
        if (loopMode==SAMPLER_MODE_LOOP)
        {
            if (currentIndex == loopEndIndex)
            {
                currentIndex = loopStartIndex;
            }
        }
        if (currentIndex==sampleEndIndex)
        {
            currentIndex=0xFFFFFFFF;
        }
        currentIndex++;
    }
    if (currentIndex != 0xFFFFFFFF)
    {
        return sampleData[currentIndex];
    }
    else
    {
        return 0.f;
    }
}

void Sampler::setNote(float note) {
}

void Sampler::switchOn(float vel) {
    currentIndex = sampleStartIndex;
}

void Sampler::switchOff(float vel) {
    currentIndex = 0xFFFFFFFF;
}

bool Sampler::isSounding() const {
    return currentIndex != 0xFFFFFFFF;
}

int Sampler::getType() {
    return SAMPLER;
}

uint32_t Sampler::getSample(float ** samplePtr) {
    *(samplePtr) = sampleData;
    return dataSize;
}

Sampler::Sampler() {
    loopStartIndex=0;
    loopEndIndex=DEFAULT_SAMPLE_SIZE;
    sampleStartIndex=0;
    sampleEndIndex=DEFAULT_SAMPLE_SIZE;
    currentIndex=0xFFFFFFFF;
    loopMode=SAMPLER_MODE_ONE_SHOT;
    dataSize = DEFAULT_SAMPLE_SIZE;
    sampleData = (float*)malloc(DEFAULT_SAMPLE_SIZE*sizeof(float));
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
