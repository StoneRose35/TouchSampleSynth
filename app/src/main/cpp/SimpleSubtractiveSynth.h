//
// Created by philipp on 15.09.23.
//

#ifndef TOUCHSAMPLESYNTH_SIMPLESUBTRACTIVESYNTH_H
#define TOUCHSAMPLESYNTH_SIMPLESUBTRACTIVESYNTH_H


#include "MusicalSoundGenerator.h"
#include "components/AdsrEnvelope.h"
#include "components/SawOscillator.h"
#include "components/StilsonMoogFilter.h"

class SimpleSubtractiveSynth: public MusicalSoundGenerator {

private:
    AdsrEnvelope * env;
    StilsonMoogFilter * filter;
    SawOscillator * osc;
    float envelopeVals[2]{};
    float sampleRate;
    int8_t envelopeUpdateInterval;
    int8_t currentSample;
    float currentFilterCutoff, currentResonance, newFilterCutoff;
    float currentPitchBend,newPitchBend;
    int modulatorsUpdateInSamples, currentFilterUpdateSamples, currentPitchUpdateInSamples;


public:
    float initialCutoff;
    explicit SimpleSubtractiveSynth(float);
    float getNextSample() override;
    void setNote(float note) override;
    void switchOn(uint8_t) override;
    void switchOff(uint8_t) override;
    void trigger(uint8_t) override;
    int getType() override;
    bool isSounding() override;

    void setAttack(float);
    float getAttack();
    void setDecay(float);
    float getDecay();
    void setSustain(float);
    float getSustain();
    void setRelease(float);
    float getRelease();

    void setCutoff(float);
    float getCutoff();
    void setResonance(float);
    float getResonance();
    void setPitchBend(float) override;

};


#endif //TOUCHSAMPLESYNTH_SIMPLESUBTRACTIVESYNTH_H
