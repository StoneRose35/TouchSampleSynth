//
// Created by philipp on 03.09.23.
//

#include "AdrsEnvelope.h"

void AdrsEnvelope::setAttack(float a) {
    if (a > 0.0f) {
        attack = a;
    }
}

float AdrsEnvelope::getAttack() {
    return attack;
}

void AdrsEnvelope::setDecay(float d) {
    if (d > 0.0f)
    {
        decay = d;
    }
}

float AdrsEnvelope::getDecay() {
    return decay;
}

void AdrsEnvelope::setSustain(float s) {
    if (s >= 0.0f && s <= 1.0f)
    {
        sustain = s;
    }
}

float AdrsEnvelope::getSustain() {
    return sustain;
}

void AdrsEnvelope::setRelease(float r) {
    if (r > 0.0f)
    {
        release = r;
    }
}

float AdrsEnvelope::getRelease() {
    return release;
}

float AdrsEnvelope::getValue(float deltaT) {
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

float AdrsEnvelope::switchOn() {
    phase = 1;
    return getValue(0.0f);
}

float AdrsEnvelope::switchOff() {
    phase = 4;
    return getValue(0.0f);
}

bool AdrsEnvelope::isSounding() {
    return phase != 0;
}
