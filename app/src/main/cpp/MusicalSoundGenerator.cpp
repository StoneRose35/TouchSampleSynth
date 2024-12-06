//
// Created by philipp on 03.09.23.
//

#include <cmath>
#include "MusicalSoundGenerator.h"
#include "AudioEngine.h"

MusicalSoundGenerator::MusicalSoundGenerator(float sr) {
    alphaVolumeChange =  15.0f / (sr/2.0f);
    currentVolume = 1.0f;
    newVolume = 1.0f;
    midiVelocityScaling = 0.0f;
}

void MusicalSoundGenerator::setNote(float n) {
    note = n;
    midiNote = (uint8_t)((int8_t)note + 69);
}

void MusicalSoundGenerator::switchOn(uint8_t vel) {
    if (midiInputPort != nullptr)
    {
        uint8_t midiCommand[3];
        midiCommand[0]=MIDI_NOTE_ON | midiChannel;
        midiCommand[1]=midiNote;
        midiCommand[2]=vel;
        AMidiInputPort_send(midiInputPort,midiCommand,3);
    }
}

void MusicalSoundGenerator::trigger(uint8_t vel) {
    uint8_t midiCommand[3];
    midiCommand[0]=MIDI_NOTE_ON | midiChannel;
    midiCommand[1]=midiNote;
    midiCommand[2]=vel;
    AMidiInputPort_send(midiInputPort,midiCommand,3);
    midiCommand[0]=MIDI_NOTE_OFF | midiChannel;
    midiCommand[1]=midiNote;
    midiCommand[2]=vel;
    AMidiInputPort_send(midiInputPort,midiCommand,3);
}

void MusicalSoundGenerator::switchOff(uint8_t vel) {
    if (midiInputPort != nullptr)
    {
        uint8_t midiCommand[3];
        midiCommand[0]=MIDI_NOTE_OFF | midiChannel;
        midiCommand[1]=midiNote;
        midiCommand[2]=vel;
        AMidiInputPort_send(midiInputPort,midiCommand,3);
    }
}

void MusicalSoundGenerator::sendMidiCC(uint8_t ccNumber,uint8_t ccValue) {
    if (midiInputPort != nullptr)
    {
        uint8_t midiCommand[3];
        midiCommand[0]=MIDI_CC | midiChannel;
        midiCommand[1]=ccNumber;
        midiCommand[2]=ccValue;
        AMidiInputPort_send(midiInputPort,midiCommand,3);
    }
}

void MusicalSoundGenerator::setVolume(float v) {
    if (v >=0.0f && v<=1.0f)
    {
        newVolume = v;
    }
}

void MusicalSoundGenerator::setVolumeImmediate(float v) {
    if (v >=0.0f && v<=1.0f)
    {
        newVolume = v;
        currentVolume = v;
    }
}

float MusicalSoundGenerator::getNextSampleVolume() {

    currentVolume = currentVolume  + alphaVolumeChange*(newVolume - currentVolume);
    return currentVolume;
}

float MusicalSoundGenerator::getVolume() const {
    return currentVolume;
}

bool MusicalSoundGenerator::isSounding() {
    return false;
}

void MusicalSoundGenerator::setPitchBend(float) {

}




