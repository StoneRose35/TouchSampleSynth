//
// Created by philipp on 10.12.24.
//

#ifndef TOUCHSAMPLESYNTH_SOUNDRECORDER_H
#define TOUCHSAMPLESYNTH_SOUNDRECORDER_H


class SoundRecorder {
    public:
        virtual void processNextSample(float){}
        virtual void startRecording(){}
        virtual void stopRecording(){}
        virtual void resetSample(){}
        virtual bool hasRecordedContent(){}
};


#endif //TOUCHSAMPLESYNTH_SOUNDRECORDER_H
