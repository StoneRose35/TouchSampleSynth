//
// Created by philipp on 01.09.23.
//

#include <jni.h>
#include <android/input.h>
#include "AudioEngine.h"

static AudioEngine *audioEngine = new AudioEngine();

extern "C" {

JNIEXPORT void JNICALL
Java_ch_sr35_touchsamplesynth_TouchSampleSynthMain_touchEvent(JNIEnv *env, jobject obj, jint action, jint soundGenerator) {
    switch (action) {
        case AMOTION_EVENT_ACTION_DOWN:
            audioEngine->getSoundGenerator(soundGenerator)->switchOn(1.0f);
            break;
        case AMOTION_EVENT_ACTION_UP:
            audioEngine->getSoundGenerator(soundGenerator)->switchOff(1.0f);
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