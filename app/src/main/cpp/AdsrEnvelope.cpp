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
    switch(phase)
    {
        case 0:
            currentVal = 0.0f;
            break;
        case 1:
            currentVal += deltaT/attack;
            if (currentVal > 1.0f)
            {
                currentVal = 1.0f;
                phase++;
            }
            break;
        case 2:
            currentVal -= deltaT/decay;
            if (currentVal < sustain)
            {
                currentVal = sustain;
                phase++;
            }
            break;
        case 3:
            currentVal = sustain;
            break;
        case 4:
            currentVal -= deltaT/release;
            if (currentVal < 0.0f)
            {
                currentVal = 0.0f;
                phase = 0;
            }
            break;
        default:
            currentVal = 0.0f;
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

bool AdsrEnvelope::isSounding() {
    return phase != 0;
}

AdsrEnvelope::AdsrEnvelope() {
    attack=0.01f;
    decay=0.01f;
    sustain=1.0f;
    release=0.01f;
    phase=0;
    currentVal = 0.0f;
}


