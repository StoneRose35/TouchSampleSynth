//
// Created by philipp on 03.09.23.
//

#include "MusicalSoundGenerator.h"
#include "AudioEngine.h"
void MusicalSoundGenerator::setNote(float note) {
    midiNote = (uint8_t)((int8_t)note + 69);
}

void MusicalSoundGenerator::switchOn(float velocity) {
    if (midiInputPort != nullptr)
    {
        uint8_t midiCommand[3];
        midiCommand[0]=MIDI_NOTE_ON | midiChannel;
        midiCommand[1]=midiNote;
        midiCommand[2]=0x7F;
        AMidiInputPort_send(midiInputPort,midiCommand,3);
    }
}

void MusicalSoundGenerator::switchOff(float velocity) {
    if (midiInputPort != nullptr)
    {
        uint8_t midiCommand[3];
        midiCommand[0]=MIDI_NOTE_OFF | midiChannel;
        midiCommand[1]=midiNote;
        midiCommand[2]=0x7F;
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




