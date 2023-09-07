//
// Created by philipp on 03.09.23.
//

#include "AdsrEnvelope.h"

void AdsrEnvelope::setAttack(float a) {
    if (a > 0.0f) {
        attack = a;
    }
}

float AdsrEnvelope::getAttack() const {
    return attack;
}

void AdsrEnvelope::setDecay(float d) {
    if (d > 0.0f)
    {
        decay = d;
    }
}

float AdsrEnvelope::getDecay() const {
    return decay;
}

void AdsrEnvelope::setSustain(float s) {
    if (s >= 0.0f && s <= 1.0f)
    {
        sustain = s;
    }
}

float AdsrEnvelope::getSustain() const {
    return sustain;
}

void AdsrEnvelope::setRelease(float r) {
    if (r > 0.0f)
    {
        release = r;
    }
}

float AdsrEnvelope::getRelease() const {
    return release;
}

float AdsrEnvelope::getValue(float deltaT) {
    float currentVal;
    switch(phase)
    {
        case 0:
            currentVal = 0.0f;
            break;
        case 1:
            currentVal = time/attack;
            break;
        case 2:
            currentVal = 1.0f - time/decay*(1.0f-sustain);
            break;
        case 3:
            currentVal = sustain;
            break;
        case 4:
            currentVal = sustain - time/decay*sustain;
            break;
        default:
            currentVal = 0.0f;
            break;
    }
    time += deltaT;
    switch(phase)
    {
        case 1:
            if (time > attack)
            {
                time -= attack;
                phase++;
            }
            break;
        case 2:
            if (time > decay)
            {
                time -= decay;
                phase++;
            }
            break;
        case 3:
            time = 0;
            break;
        case 4:
            if(time > release)
            {
                time = 0;
            }
            phase = 0;
            break;
        default:
            time = 0;
            break;
    }
    return currentVal;
}

float AdsrEnvelope::switchOn() {
    phase = 1;
    return getValue(0.0f);
}

float AdsrEnvelope::switchOff() {
    phase = 4;
    return getValue(0.0f);
}

bool AdsrEnvelope::isSounding() const {
    return phase != 0;
}

AdsrEnvelope::AdsrEnvelope() {
    attack=0.0f;
    decay=0.0f;
    sustain=1.0f;
    release=0.0f;
    phase=0;
    time=0.0f;
}


