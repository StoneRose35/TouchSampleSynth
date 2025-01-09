//
// Created by philipp on 17.12.24.
//

#ifndef TOUCHSAMPLESYNTH_LOOPER_H
#define TOUCHSAMPLESYNTH_LOOPER_H

#define MIN_SAMPLE_SIZE 1440000
#define SAMPLE_SIZE_INCREMENT 131072
#define LOOPER_STATE_STOPPED 0
#define LOOPER_STATE_PLAYING 1
#define LOOPER_STATE_RECORDING 2

#include "MusicalSoundGenerator.h"
#include "SoundRecorder.h"



class Looper: public MusicalSoundGenerator,public SoundRecorder {
private:
    uint32_t loopEnd;
    uint32_t readPointer;
    uint32_t writePointer;
    uint8_t state;
    float * sample;
    uint32_t currentBufferSize;

    void clearSample();
public:
    explicit Looper(float sr);

    uint32_t getReadPointer();
    void setReadPointer(uint32_t);
    uint32_t getWritePointer();
    void setWritePointer(uint32_t);
    uint32_t getLoopEnd();
    void setLoopEnd(uint32_t);


    float getNextSample() override;

    void processNextSample(float) override;

    bool isSounding() override;

    void switchOff(uint8_t) override;

    void switchOn(uint8_t) override;

    int getType() override;

    void startRecording() override;

    void stopRecording() override;

    void resetSample() override;

    ~Looper();

};


#endif //TOUCHSAMPLESYNTH_LOOPER_H
