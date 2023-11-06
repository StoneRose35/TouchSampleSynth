//
// Created by philipp on 01.09.23.
//

#include <jni.h>
#include <android/input.h>
#include "AudioEngine.h"
#include <cmath>


extern "C" {

JNIEXPORT void JNICALL
Java_ch_sr35_touchsamplesynth_audio_AudioEngineK_playFrames(JNIEnv *env, jobject obj, jint nFrames) {
    AudioEngine * audioEngine = getAudioEngine();
    float audioSum;
    float * audioDataFloat;
    audioDataFloat = (float*)malloc(nFrames*sizeof(float));
    for(uint32_t i=0;i<nFrames;i++)
    {
        audioSum=0.0f;
        for (int8_t c=0;c<N_SOUND_GENERATORS;c++)
        {
            if (audioEngine->getSoundGenerator(c) != nullptr) {
                audioSum += audioEngine->getSoundGenerator(c)->getNextSample();
            }
        }
        audioSum /= (float)audioEngine->getNSoundGenerators();
        audioEngine->averageVolume = audioEngine->averageVolume*AVERAGE_LOWPASS_ALPHA  + fabsf(audioSum)*(1.0f - AVERAGE_LOWPASS_ALPHA);
        *(audioDataFloat + i) = audioSum;
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

JNIEXPORT float JNICALL
Java_ch_sr35_touchsamplesynth_audio_AudioEngineK_getAverageVolume(JNIEnv *env, jobject)
{
    AudioEngine * audioEngine = getAudioEngine();
    return audioEngine->averageVolume;
}

JNIEXPORT float JNICALL
Java_ch_sr35_touchsamplesynth_audio_AudioEngineK_getCpuLoad(JNIEnv *env, jobject)
{
    AudioEngine * audioEngine = getAudioEngine();
    return audioEngine->cpuLoad;
}

}