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
    float osc1Pulsewidth;
    float osc2Pulsewidth;
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
    float getFilterEnvelopeLevel() const;
    void setFilterEnvelopeLevel(float);

    void setOsc1Type(uint8_t);
    uint8_t getOsc1Type() const;
    void setOsc2Type(uint8_t);
    uint8_t getOsc2Type() const;

    uint8_t getOsc2Octave() const;
    void setOsc2Octave(uint8_t);
    float getOsc2Detune() const;
    void setOsc2Detune(float);
    float getOsc2Volume() const;
    void setOsc2Volume(float);

    float getOsc1PulseWidth() const;
    void setOsc1PulseWidth(float);
    float getOsc2PulseWidth() const;
    void setOsc2PulseWidth(float);


    void setCutoff(float);
    float getCutoff();
    void setResonance(float);
    float getResonance();
    void setPitchBend(float) override;

};


#endif //TOUCHSAMPLESYNTH_SIMPLESUBTRACTIVESYNTH_H
