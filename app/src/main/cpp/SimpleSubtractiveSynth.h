//
// Created by philipp on 15.09.23.
//

#ifndef TOUCHSAMPLESYNTH_SIMPLESUBTRACTIVESYNTH_H
#define TOUCHSAMPLESYNTH_SIMPLESUBTRACTIVESYNTH_H


#include "MusicalSoundGenerator.h"
#include "components/AdsrEnvelope.h"
#include "components/SawOscillator.h"
#include "components/StilsonMoogFilter.h"

#define OSCILLATOR_TYPE_SAW 0
#define OSCILLATOR_TYPE_SQUARE 1

class SimpleSubtractiveSynth: public MusicalSoundGenerator {

private:
    AdsrEnvelope * volumeEnv;
    AdsrEnvelope * filterEnv;
    StilsonMoogFilter * filter;
    SoundGenerator * osc1;
    SoundGenerator * osc2;
    float volumeEnvelopeVals[2]{};
    float filterEnvelopeVals[2]{};
    float sampleRate;
    int8_t envelopeUpdateInterval;
    int8_t currentSample;
    uint8_t osc1Type;
    uint8_t osc2Type;
    uint8_t osc2Octave;
    float osc2Detune;
    float osc2Volume;
    float currentFilterCutoff, currentResonance, newFilterCutoff;
    float currentPitchBend,newPitchBend;
    float filterEnvelopeLevel;
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

    void setVolumeAttack(float);
    float getVolumeAttack();
    void setVolumeDecay(float);
    float getVolumeDecay();
    void setVolumeSustain(float);
    float getVolumeSustain();
    void setVolumeRelease(float);
    float getVolumeRelease();

    void setFilterAttack(float);
    float getFilterAttack();
    void setFilterDecay(float);
    float getFilterDecay();
    void setFilterSustain(float);
    float getFilterSustain();
    void setFilterRelease(float);
    float getFilterRelease();
    float getFilterEnvelopeLevel();
    void setFilterEnvelopeLevel(float);

    void setOsc1Type(uint8_t);
    uint8_t getOsc1Type();
    void setOsc2Type(uint8_t);
    uint8_t getOsc2Type();

    uint8_t getOsc2Octave();
    void setOsc2Octave(uint8_t);
    float getOsc2Detune();
    void setOsc2Detune(float);
    float getOsc2Volume();
    void setOsc2Volume(float);




    void setCutoff(float);
    float getCutoff();
    void setResonance(float);
    float getResonance();
    void setPitchBend(float) override;

};


#endif //TOUCHSAMPLESYNTH_SIMPLESUBTRACTIVESYNTH_H
