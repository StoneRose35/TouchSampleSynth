//
// Created by philipp on 23.12.23.
//

#ifndef TOUCHSAMPLESYNTH_SAMPLER_H
#define TOUCHSAMPLESYNTH_SAMPLER_H

#include "MusicalSoundGenerator.h"
#include <stdint.h>
#define DEFAULT_SAMPLE_SIZE 2880000
#define SAMPLER_MODE_LOOP 0
#define SAMPLE_MODE_TRIGGERED 1

class Sampler: public MusicalSoundGenerator  {

private:
    float * sampleData;
    uint32_t dataSize;
    uint32_t loopStartIndex;
    uint32_t loopEndIndex;
    uint32_t sampleStartIndex;
    uint32_t sampleEndIndex;
    uint32_t currentIndex;
    uint8_t loopMode;


public:
    void setLoopStartIndex(uint32_t);
    uint32_t getLoopStartIndex() const;
    void setLoopEndIndex(uint32_t);
    uint32_t getLoopEndIndex() const;
    void setMode(uint8_t);
    uint8_t getMode() const;
    void setSampleStartIndex(uint32_t);
    uint32_t getSampleStartIndex() const;
    void setSampleEndIndex(uint32_t);
    uint32_t getSampleEndIndex() const;
    void loadSample(const float*,uint32_t);
    uint32_t getSample(float**);
    float getNextSample() override;
    void setNote(float note) override;
    void switchOn(uint8_t) override;
    void switchOff(uint8_t) override;
    void trigger(uint8_t) override;
    int getType() override;
    bool isSounding() const;
    Sampler(float sr);
    ~Sampler();
};


#endif //TOUCHSAMPLESYNTH_SAMPLER_H
