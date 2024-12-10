//
// Created by philipp on 10.12.24.
//

#ifndef TOUCHSAMPLESYNTH_SOUNDRECORDER_H
#define TOUCHSAMPLESYNTH_SOUNDRECORDER_H


class SoundRecorder {
    public:
        virtual void processNextSample(float)=0;
};


#endif //TOUCHSAMPLESYNTH_SOUNDRECORDER_H
