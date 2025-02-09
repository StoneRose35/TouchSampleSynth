#include <iostream>
#include <cstdint>
#include <cmath>
#include "../components/SawTriangleOscillator.h"
#include "../components/SquareOscillator.h"
#include "../AudioEngine.h"
#include "../SoundGenerator.h"

void visualizeSawTriangleOscillator()
{
    SawTriangleOscillator * osc;
    osc = new SawTriangleOscillator(48000.0f);
    osc->setNote(24.0f);
    osc->setPulseWidth(2.0f);
    FILE * fid;
    fid = fopen("osc_datatable.txt","wt");
    int nsamples = 8*(48000.0/1760.0);
    for (int c=0;c<nsamples;c++)
    {
        fprintf(fid,"%f\n",osc->getNextSample());
    }
    fclose(fid);
}

void visualizeSquareOscillator()
{
    SquareOscillator * osc;
    osc = new SquareOscillator(48000.0f);
    osc->setNote(24.0f);
    osc->setPulseWidth(0.0f);
    FILE * fid;
    fid = fopen("osc_datatable.txt","wt");
    int nsamples = 8*(48000.0/1760.0);
    for (int c=0;c<nsamples;c++)
    {
        fprintf(fid,"%f\n",osc->getNextSample());
    }
    fclose(fid);
}

void midiModeTest1()
{
    uint8_t mididata[2];
    AudioEngine * engine;
    engine = new AudioEngine();
    engine->addSoundGenerator(SINE_MONO_SYNTH);
    engine->addSoundGenerator(SINE_MONO_SYNTH);

    engine->getSoundGenerator(0)->availableForMidi = 1 << 9;
    engine->getSoundGenerator(1)->availableForMidi = 1 << 9;
    engine->midiMode = MIDI_MODE_NOTE_STEAL;

    // first virtual key down
    mididata[0]= 64;
    mididata[1]=100;
    engine->startNextVoice(mididata);

    // second virtual key down
    mididata[0] = 66;
    engine->startNextVoice(mididata);

    // third virtual key down
    mididata[0]=72;
    engine->startNextVoice(mididata);

    // first virtual key up
    mididata[0]=64;
    engine->stopVoice(mididata);
    
    if (!((engine->notesPlaying)->note != 64 && (engine->notesPlaying + 1)->note != 64))
    {
        std::cout << "Error: first not still on stack" << std::endl;
    } 

    // second virtual key up
    mididata[0]=72;
    engine->stopVoice(mididata);
    
    if ((engine->notesPlaying)->note !=66)
    {
        std::cout << "Error: first note playing should be 66" << std::endl;  
    }

    //third virtual key up
    mididata[0]=66;
    engine->stopVoice(mididata);

    for (uint8_t c=0;c< MAX_SOUND_GENERATORS;c++)
    {
        if ((engine->notesPlaying + c)->note !=-1)
        {
            std::cout << "Error: note at position " << c << ", stack should be empty" << std::endl;  
        }
    }

}

uint8_t countDepressedFingers(int8_t * fp)
{
    uint8_t cnt=0;
    for (uint8_t c=0;c<10;c++)
    {
        if(*(fp + c) != -1)
        {
            cnt++;
        }
    }
    return cnt;
}

int8_t getRandomUnpressedNote(int8_t * fp)
{
    int8_t candidate;
    bool taken=true;
    while(taken)
    {
        candidate  = rand()&0x7F;
        taken = false;
        for(uint8_t c=0;c<10;c++)
        {
            if(*(fp + c)== candidate)
            {
                taken=true;
            }
        }
    }
    return candidate;
}

int8_t getRandomPressedIndex(int8_t * fp)
{
    int8_t pressedIndexes[10];
    uint8_t cntPressed=0;
    for (uint8_t c=0;c<10;c++)
    {
        if(*(fp + c)!=-1)
        {
            pressedIndexes[cntPressed++]=c;
        }
    }
    uint8_t idx = rand() % cntPressed;
    return *(pressedIndexes + idx);
}

#define EPISODE_LENGTH 2048
void randomPlaying()
{

    uint8_t fingersDepressed = 0;
    int8_t fingerPositions[10]={-1,-1,-1,-1,-1,-1,-1,-1,-1,-1};
    uint8_t mididata[2]={0,100};
    AudioEngine * engine;
    engine = new AudioEngine();
    engine->addSoundGenerator(SINE_MONO_SYNTH);
    engine->addSoundGenerator(SINE_MONO_SYNTH);
    engine->addSoundGenerator(SINE_MONO_SYNTH);
    engine->addSoundGenerator(SINE_MONO_SYNTH);
    engine->addSoundGenerator(SINE_MONO_SYNTH);
    engine->addSoundGenerator(SINE_MONO_SYNTH);
    engine->addSoundGenerator(SINE_MONO_SYNTH);

    engine->getSoundGenerator(0)->availableForMidi = 1 << 9;
    engine->getSoundGenerator(1)->availableForMidi = 1 << 9;
    engine->getSoundGenerator(2)->availableForMidi = 1 << 9;
    engine->getSoundGenerator(3)->availableForMidi = 1 << 9;
    engine->getSoundGenerator(4)->availableForMidi = 1 << 9;
    engine->getSoundGenerator(5)->availableForMidi = 1 << 9;
    engine->getSoundGenerator(6)->availableForMidi = 1 << 9;
    engine->midiMode = MIDI_MODE_MONOPHONIC_LOWPRIO;

    for (uint16_t ec=0;ec<EPISODE_LENGTH;ec++)
    {
        if (countDepressedFingers(fingerPositions)==0)
        {
            mididata[0]= getRandomUnpressedNote(fingerPositions);
            engine->startNextVoice(mididata);
            uint8_t pos=0;
            while(fingerPositions[pos]!=-1)
            {
                pos++;
            }
            fingerPositions[pos]= mididata[0];
            std::cout << "Hammering at Position " << int(mididata[0]) << ", total notes " <<  int(countDepressedFingers(fingerPositions)) << std::endl;
        }
        else if (countDepressedFingers(fingerPositions)==10 || // were emulating a single player having 10 Fingers 
                 countDepressedFingers(fingerPositions) >= EPISODE_LENGTH - ec - 1) // release at the end of the episode
        {
            uint8_t liftPosition = getRandomPressedIndex(fingerPositions);
            mididata[0]= fingerPositions[liftPosition];
            int oldpos = *(fingerPositions + liftPosition);
            fingerPositions[liftPosition]=-1;
            engine->stopVoice(mididata);
            std::cout << "Lift at Position " << oldpos << ", total notes " << int(countDepressedFingers(fingerPositions)) << std::endl;
        
        }
        else
        {
            // decide whether releasing or hammering a virtual finger
            if(rand()&1)
            {
                // hammering
                mididata[0]= getRandomUnpressedNote(fingerPositions);
                engine->startNextVoice(mididata);
                uint8_t pos=0;
                while(fingerPositions[pos]!=-1)
                {
                    pos++;
                }
                fingerPositions[pos]= mididata[0];
                std::cout << "Hammering at Position " << int(mididata[0]) << ", total notes " << int(countDepressedFingers(fingerPositions)) << std::endl;
            }
            else
            {
                // releasing
                uint8_t liftPosition = getRandomPressedIndex(fingerPositions);
                mididata[0] = *(fingerPositions + liftPosition);
                engine->stopVoice(mididata);
                int oldpos = *(fingerPositions + liftPosition);
                *(fingerPositions + liftPosition)=-1;
                std::cout << "Lift at Position " << oldpos << ", total notes " << int(countDepressedFingers(fingerPositions)) << std::endl;
            }
        }
        // check if the maximum number of soundgenerators the the soundgenerator amount equal to the finges depressed is playing, 
        // whatever is smaller
        int nSimultaneouslySounding;
        if (countDepressedFingers(fingerPositions) <= 7)
        {
            nSimultaneouslySounding = countDepressedFingers(fingerPositions);
        }
        else
        {
            nSimultaneouslySounding = 7;
        }
        int cntSoundGeneratorsWorking=0;
        
        /*
        if (countDepressedFingers(fingerPositions) > 0)
        {
            int noteValueSounding;
            for (int c=0;c < 7;c++)
            {
                if (engine->getSoundGenerator(c)->availableForMidi & (1 << 7))
                {
                    noteValueSounding = engine->getSoundGenerator(c)->midiNote;
                    cntSoundGeneratorsWorking++;
                }
            }

            int highestFingerDepressed = 0;
            for(uint8_t c=0;c<10;c++)
            {
                if (*(fingerPositions + c )> highestFingerDepressed)
                {
                    highestFingerDepressed = *(fingerPositions + c );
                }
            }
            if (noteValueSounding != highestFingerDepressed)
            {
                std::cout << "Error: Sound note isn't highest" << std::endl;
            }
        }
        */
       if (countDepressedFingers(fingerPositions) > 0)
       {
           int noteValueSounding;
           for (int c=0;c < 7;c++)
           {
               if (engine->getSoundGenerator(c)->availableForMidi & (1 << 7))
               {
                   noteValueSounding = engine->getSoundGenerator(c)->midiNote;
                   cntSoundGeneratorsWorking++;
               }
           }

           int lowestFingerDepressed = 128;
           for(uint8_t c=0;c<10;c++)
           {
               if (*(fingerPositions + c )< lowestFingerDepressed && *(fingerPositions + c) >=0)
               {
                   lowestFingerDepressed = *(fingerPositions + c );
               }
           }
           if (noteValueSounding != lowestFingerDepressed)
           {
               std::cout << "Error: Sound note isn't lowest" << std::endl;
           }
       }

        if (cntSoundGeneratorsWorking > nSimultaneouslySounding)
        {
            std::cout << "Error: Note Leak, more soundgenerators on than fingers depressed" << std::endl;
        }
    }
    // assert that the "notesPlaying"-buffer is empty and none of the sound generators is playing
    for (int c=0;c< MAX_SOUND_GENERATORS;c++)
    {
        if ((engine->notesPlaying + c)->note !=-1)
        {
            std::cout << "Error: note at position " << c << ", stack should be empty" << std::endl;  
        }
    }
    for (int c =0;c<7;c++)
    {
        if (engine->getSoundGenerator(c)->availableForMidi & (1 << 7))
        {
            std::cout << "Error: sound generator " << c << "'s midi status is denoted as 'taken'" << std::endl;
        }
    }
}

int main() {
    randomPlaying();
    return 0;
}

