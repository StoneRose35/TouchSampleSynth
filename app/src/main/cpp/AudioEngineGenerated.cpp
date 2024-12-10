#include "AudioEngine.h"
#include "SineMonoSynth.h"
#include "Sampler.h"
#include "SimpleSubtractiveSynth.h"


int8_t AudioEngine::addSoundGenerator(SoundGeneratorType sgt) {
    MusicalSoundGenerator *  sg;
    int8_t idx=nSoundGenerators;
    // get next free slot
    for (int8_t c=0;c<nSoundGenerators;c++)
    {
        if(*(soundGenerators+c)==nullptr)
        {
            idx=c;
            break;
        }
    }
    switch(sgt)
    {
        
        case SINE_MONO_SYNTH:
            sg=new SineMonoSynth((float) samplingRate);
            sg->midiInputPort = midiInputPort;
            if (idx < nSoundGenerators) {
                soundGenerators[idx] = sg;
            }
            break;
        
        case SAMPLER:
            sg=new Sampler((float) samplingRate);
            sg->midiInputPort = midiInputPort;
            if (idx < nSoundGenerators) {
                soundGenerators[idx] = sg;
            }
            break;
        
        case SIMPLE_SUBTRACTIVE_SYNTH:
            sg=new SimpleSubtractiveSynth((float) samplingRate);
            sg->midiInputPort = midiInputPort;
            if (idx < nSoundGenerators) {
                soundGenerators[idx] = sg;
            }
            break;
        
    }

    return idx; 
}
        