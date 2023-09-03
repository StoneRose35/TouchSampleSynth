//
// Created by philipp on 03.09.23.
//

#ifndef TOUCHSAMPLESYNTH_ADRSENVELOPE_H
#define TOUCHSAMPLESYNTH_ADRSENVELOPE_H


class AdrsEnvelope {
private:
    float attack;
    float decay;
    float sustain;
    float release;
    int phase;
    float time;
public:
    void setAttack(float);
    float getAttack();
    void setDecay(float);
    float getDecay();
    void setSustain(float);
    float getSustain();
    void setRelease(float);
    float getRelease();

    float getValue(float);
    float switchOn();
    float switchOff();
    bool isSounding();
};


#endif //TOUCHSAMPLESYNTH_ADRSENVELOPE_H
