//
// Created by philipp on 03.09.23.
//

#ifndef TOUCHSAMPLESYNTH_SINEMONOSYNTH_H
#define TOUCHSAMPLESYNTH_SINEMONOSYNTH_H
#include "MusicalSoundGenerator.h"
#include "AdsrEnvelope.h"
#include "SineOscillator.h"
#include <stdlib.h>


class SineMonoSynth: public MusicalSoundGenerator {
private:
    SineOscillator * osc;
    AdsrEnvelope * env;
    float sampleRate;
    int8_t envelopeUpdateInterval;
    int8_t currentSample;
    float envelopeVals[2];
public:
    float getNextSample();

    SineMonoSynth();

    void setNote(float note);
    void switchOn(float velocity);
    void switchOff(float velocity);
    int getType();
    bool isSounding();

    // specific interface
    void setAttack(float);
    float getAttack();
    void setDecay(float);
    float getDecay();
    void setSustain(float);
    float getSustain();
    void setRelease(float);
    float getRelease();
};


#endif //TOUCHSAMPLESYNTH_SINEMONOSYNTH_H
