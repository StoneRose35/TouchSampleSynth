//
// Created by philipp on 03.09.23.
//

#ifndef TOUCHSAMPLESYNTH_SOUNDGENERATOR_H
#define TOUCHSAMPLESYNTH_SOUNDGENERATOR_H


class SoundGenerator {
public:
    virtual float getNextSample();
    virtual int getType();

    virtual void setNote(float d);
};

enum SoundGeneratorType{
    SINE_MONO_SYNTH = 0,
    SIMPLE_SUBTRACTIVE_SYNTH = 1,
    LOOPER = 2,
    SAMPLER = 3
};


#endif //TOUCHSAMPLESYNTH_SOUNDGENERATOR_H
