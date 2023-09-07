//
// Created by philipp on 01.09.23.
//

#include <jni.h>
#include <android/input.h>
#include "AudioEngine.h"



extern "C" {

JNIEXPORT void JNICALL
Java_ch_sr35_touchsamplesynth_TouchSampleSynthMain_touchEvent(JNIEnv *env, jobject obj, jint action, jint soundGenerator) {
    AudioEngine * audioEngine = getAudioEngine();
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

JNIEXPORT jint JNICALL
Java_ch_sr35_touchsamplesynth_audio_AudioEngineK_addSoundGenerator(JNIEnv *env, jobject obj,jint generatorType)
{
    AudioEngine * audioEngine = getAudioEngine();
    auto sgt = static_cast<SoundGeneratorType>(generatorType);
    int32_t idx = audioEngine->addSoundGenerator(sgt);
    return idx;
}

JNIEXPORT jint JNICALL
Java_ch_sr35_touchsamplesynth_audio_AudioEngineK_removeSoundGenerator(JNIEnv *env, jobject obj,jint idx)
{
    AudioEngine * audioEngine = getAudioEngine();
    audioEngine->removeSoundGenerator(idx);
    return 0;
}

JNIEXPORT void JNICALL
Java_ch_sr35_touchsamplesynth_audio_AudioEngineK_startEngine(JNIEnv *env, jobject /* this */) {
    AudioEngine * audioEngine = getAudioEngine();
    audioEngine->start();
}

JNIEXPORT void JNICALL
Java_ch_sr35_touchsamplesynth_audio_AudioEngineK_stopEngine(JNIEnv *env, jobject /* this */) {
    AudioEngine * audioEngine = getAudioEngine();
    audioEngine->stop();
}

JNIEXPORT int JNICALL
Java_ch_sr35_touchsamplesynth_audio_AudioEngineK_getSamplingRate(JNIEnv *env, jobject)
{
    AudioEngine * audioEngine = getAudioEngine();
    return audioEngine->getSamplingRate();
}



}