//
// Created by philipp on 29.01.25.
//

// taken from https://www.martin-finke.de/articles/audio-plugins-018-polyblep-oscillator/

#include "PolyBLEP.h"

float PolyBLEP::pb_step(float t) {
    if (t < dt )
    {
        t /= dt;
        return t+t - t*t - 1.0f;
    }
    else if (t > 1.0 - dt)
    {
        t = (t - 1.0f) / dt;
        return t*t + t+t + 1.0f;
    }
    return 0.0f;
}

PolyBLEP::PolyBLEP() {
    samplingRate = 48000.0f;
    dt=0.5;
}

PolyBLEP::PolyBLEP(float sr) {
    samplingRate = sr;
    dt=0.5;
}

void PolyBLEP::setFrequency(float f) {
    dt = f/samplingRate;
}

