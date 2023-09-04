//
// Created by philipp on 03.09.23.
//

#ifndef TOUCHSAMPLESYNTH_MUSICALSOUNDGENERATOR_H
#define TOUCHSAMPLESYNTH_MUSICALSOUNDGENERATOR_H

#include "SoundGenerator.h"

class MusicalSoundGenerator: public SoundGenerator {
public:
    virtual void setNote(float note);
    virtual void switchOn(float velocity);
    virtual void switchOff(float velocity);
};


#endif //TOUCHSAMPLESYNTH_MUSICALSOUNDGENERATOR_H
