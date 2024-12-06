//
// Created by philipp on 03.09.23.
//

#ifndef TOUCHSAMPLESYNTH_SINEMONOSYNTH_H
#define TOUCHSAMPLESYNTH_SINEMONOSYNTH_H
#include "MusicalSoundGenerator.h"
#include "AdsrEnvelope.h"
#include "SineOscillator.h"
#include <cstdlib>
#include <cmath>

class SineMonoSynth: public MusicalSoundGenerator {
private:
    SineOscillator * osc;
    AdsrEnvelope * env;
    float sampleRate;
    int8_t envelopeUpdateInterval;
    int8_t currentSample;
    float currentPitchBend,newPitchBend;
    float envelopeVals[2];
    float note;
    int modulatorsUpdateInSamples, currentPitchUpdateInSamples;
public:
    float getNextSample() override;

    explicit SineMonoSynth(float sr);

    void setNote(float note) override;
    void switchOn(uint8_t) override;
    void switchOff(uint8_t) override;
    void trigger(uint8_t) override;
    int getType() override;
    bool isSounding() override;
    void setPitchBend(float) override;

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
