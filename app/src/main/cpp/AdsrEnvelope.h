//
// Created by philipp on 03.09.23.
//

#ifndef TOUCHSAMPLESYNTH_ADSRENVELOPE_H
#define TOUCHSAMPLESYNTH_ADSRENVELOPE_H


class AdsrEnvelope {
private:
    float attack;
    float decay;
    float sustain;
    float release;
    int phase;
    float currentVal;
public:
    void setAttack(float);
    float getAttack() const;
    void setDecay(float);
    float getDecay() const;
    void setSustain(float);
    float getSustain() const;
    void setRelease(float);
    float getRelease() const;
    AdsrEnvelope();

    float getValue(float);
    float switchOn();
    float switchOff();
    bool isSounding();
};



#endif //TOUCHSAMPLESYNTH_ADSRENVELOPE_H
