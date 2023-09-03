//
// Created by philipp on 03.09.23.
//

#ifndef TOUCHSAMPLESYNTH_SINEMONOSYNTH_H
#define TOUCHSAMPLESYNTH_SINEMONOSYNTH_H
#include "MusicalSoundGenerator.h"
#include "AdrsEnvelope.h"
#include "SineOscillator.h"
#include <stdlib.h>

class SineMonoSynth: MusicalSoundGenerator {
private:
    SineOscillator osc;
    AdrsEnvelope env;
    float sampleRate;
    int8_t envelopeUpdateInterval;
    int8_t currentSample;
    float envelopeVals[2];
public:
    float getNextSample();

    SineMonoSynth();
    ~SineMonoSynth();

    void setNote(float note);

    void switchOn(float velocity);

    void switchOff(float velocity);
};


#endif //TOUCHSAMPLESYNTH_SINEMONOSYNTH_H
