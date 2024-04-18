//
// Created by philipp on 03.09.23.
//

#ifndef TOUCHSAMPLESYNTH_MUSICALSOUNDGENERATOR_H
#define TOUCHSAMPLESYNTH_MUSICALSOUNDGENERATOR_H

#include <cstdint>
#include <amidi/AMidi.h>
#include "SoundGenerator.h"

class MusicalSoundGenerator: public SoundGenerator {
public:
    virtual void setNote(float note);

    virtual void switchOff(float velocity);

    virtual void switchOn(float velocity);

    void setVolume(float);
    float getVolume();

    virtual void sendMidiCC(uint8_t ccNumber,uint8_t ccValue);

    // bit 9: set when generally available for midi
    // bit 8: set if note change is allowed (on monophonic instruments)
    // bit 7: set if the voice is taken
    uint16_t availableForMidi=0;
    uint8_t midiNote=69;
    uint8_t midiChannel=0;
    float volume=1.0f;
    AMidiInputPort * midiInputPort= nullptr;
};


#endif //TOUCHSAMPLESYNTH_MUSICALSOUNDGENERATOR_H
