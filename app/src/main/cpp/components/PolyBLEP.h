//
// Created by philipp on 29.01.25.
//

#ifndef TOUCHSAMPLESYNTH_POLYBLEP_H
#define TOUCHSAMPLESYNTH_POLYBLEP_H


class PolyBLEP {
private:
    float dt, samplingRate;
public:
    float pb_step(float);
    PolyBLEP();
    explicit PolyBLEP(float);
    void setFrequency(float);
};


#endif //TOUCHSAMPLESYNTH_POLYBLEP_H
