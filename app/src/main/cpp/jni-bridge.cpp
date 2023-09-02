//
// Created by philipp on 01.09.23.
//

#include <jni.h>
#include <android/input.h>
#include "AudioEngine.h"

static AudioEngine *audioEngine = new AudioEngine();

extern "C" {

JNIEXPORT void JNICALL
Java_ch_sr35_touchsamplesynth_TouchSampleSynthMain_touchEvent(JNIEnv *env, jobject obj, jint action) {
    switch (action) {
        case AMOTION_EVENT_ACTION_DOWN:
            //audioEngine->setToneOn(true);
            break;
        case AMOTION_EVENT_ACTION_UP:
            //audioEngine->setToneOn(false);
            break;
        default:
            break;
    }
}

JNIEXPORT void JNICALL
Java_ch_sr35_touchsamplesynth_TouchSampleSynthMain_startEngine(JNIEnv *env, jobject /* this */) {
    audioEngine->start();
}

JNIEXPORT void JNICALL
Java_ch_sr35_touchsamplesynth_TouchSampleSynthMain_stopEngine(JNIEnv *env, jobject /* this */) {
    audioEngine->stop();
}

JNIEXPORT int JNICALL
Java_ch_sr35_touchsamplesynth_TouchSampleSynthMain_getSamplingRate(JNIEnv *env, jobject)
{
    return audioEngine->getSamplingRate();
}

}