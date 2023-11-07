//
// Created by philipp on 03.09.23.
//

#ifndef TOUCHSAMPLESYNTH_MUSICALSOUNDGENERATOR_H
#define TOUCHSAMPLESYNTH_MUSICALSOUNDGENERATOR_H

#include <cstdint>
#include "SoundGenerator.h"

class MusicalSoundGenerator: public SoundGenerator {
public:
    virtual void setNote(float note);

    virtual void switchOff(float velocity);

    virtual void switchOn(float velocity);

    // bit 9: set when generally available for midi
    // bit 8: set if note change is allowed (on monophonic instruments)
    // bit 7: set if the voice is taken
    uint16_t availableForMidi;
};


#endif //TOUCHSAMPLESYNTH_MUSICALSOUNDGENERATOR_H
