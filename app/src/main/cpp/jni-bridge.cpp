//
// Created by philipp on 01.09.23.
//

#include <jni.h>
#include <android/input.h>
#include "AudioEngine.h"
#include <cmath>


extern "C" {

JNIEXPORT void JNICALL
Java_ch_sr35_touchsamplesynth_audio_AudioEngineK_playFrames(JNIEnv *, jobject , jint nFrames) {
    AudioEngine * audioEngine = getAudioEngine();
    float audioSum;
    float * audioDataFloat;
    audioDataFloat = (float*)malloc(nFrames*sizeof(float));
    for(uint32_t i=0;i<nFrames;i++)
    {
        audioSum=0.0f;
        for (int8_t c=0;c<audioEngine->getNSoundGenerators();c++)
        {
            if (audioEngine->getSoundGenerator(c) != nullptr) {
                audioSum += audioEngine->getSoundGenerator(c)->getNextSample();
            }
        }
        audioSum /= (float)audioEngine->getActiveSoundGenerators();
        audioEngine->averageVolume = audioEngine->averageVolume*AVERAGE_LOWPASS_ALPHA  + fabsf(audioSum)*(1.0f - AVERAGE_LOWPASS_ALPHA);
        *(audioDataFloat + i) = audioSum;
    }
}

JNIEXPORT jbyte JNICALL
Java_ch_sr35_touchsamplesynth_audio_AudioEngineK_addSoundGenerator(JNIEnv *, jobject,jint generatorType)
{
    AudioEngine * audioEngine = getAudioEngine();
    auto sgt = static_cast<SoundGeneratorType>(generatorType);
    int8_t idx = audioEngine->addSoundGenerator(sgt);
    return idx;
}

JNIEXPORT jbyte JNICALL
Java_ch_sr35_touchsamplesynth_audio_AudioEngineK_removeSoundGenerator(JNIEnv *, jobject ,jbyte idx)
{
    AudioEngine * audioEngine = getAudioEngine();
    audioEngine->removeSoundGenerator(idx);
    return 0;
}

JNIEXPORT jboolean JNICALL
Java_ch_sr35_touchsamplesynth_audio_AudioEngineK_startEngine(JNIEnv *, jobject /* this */) {
    AudioEngine * audioEngine = getAudioEngine();
    return audioEngine->start();
}

JNIEXPORT void JNICALL
Java_ch_sr35_touchsamplesynth_audio_AudioEngineK_stopEngine(JNIEnv *, jobject /* this */) {
    AudioEngine * audioEngine = getAudioEngine();
    audioEngine->stop();
}

JNIEXPORT int JNICALL
Java_ch_sr35_touchsamplesynth_audio_AudioEngineK_getSamplingRate(JNIEnv *, jobject)
{
    AudioEngine * audioEngine = getAudioEngine();
    return audioEngine->getSamplingRate();
}

JNIEXPORT float JNICALL
Java_ch_sr35_touchsamplesynth_audio_AudioEngineK_getAverageVolume(JNIEnv *, jobject)
{
    AudioEngine * audioEngine = getAudioEngine();
    return audioEngine->averageVolume;
}

JNIEXPORT float JNICALL
Java_ch_sr35_touchsamplesynth_audio_AudioEngineK_getCpuLoad(JNIEnv *, jobject)
{
    AudioEngine * audioEngine = getAudioEngine();
    return audioEngine->cpuLoad;
}

JNIEXPORT int JNICALL
Java_ch_sr35_touchsamplesynth_audio_AudioEngineK_getFramesPerDataCallback(JNIEnv *, jobject)
{
    AudioEngine * audioEngine = getAudioEngine();
    return audioEngine->getFramesPerDataCallback();
}

JNIEXPORT int JNICALL
Java_ch_sr35_touchsamplesynth_audio_AudioEngineK_setFramesPerDataCallback(JNIEnv *, jobject,jint fpdc)
{
    AudioEngine * audioEngine = getAudioEngine();
    return audioEngine->setFramesPerDataCallback(fpdc);
}

JNIEXPORT int JNICALL
Java_ch_sr35_touchsamplesynth_audio_AudioEngineK_getBufferCapacityInFrames(JNIEnv *, jobject)
{
    AudioEngine * audioEngine = getAudioEngine();
    return audioEngine->getBufferCapacityInFrames();
}

JNIEXPORT int JNICALL
Java_ch_sr35_touchsamplesynth_audio_AudioEngineK_setBufferCapacityInFrames(JNIEnv *, jobject,jint bcif)
{
    AudioEngine * audioEngine = getAudioEngine();
    return audioEngine->setBufferCapacityInFrames(bcif);
}

JNIEXPORT void JNICALL
Java_ch_sr35_touchsamplesynth_audio_AudioEngineK_openMidiDevice(JNIEnv* env, jobject, jobject deviceObj, jint portNumber)
{
    AudioEngine * audioEngine = getAudioEngine();
    AMidiDevice_fromJava(env, deviceObj, &audioEngine->midiDevice);
    AMidiOutputPort_open(audioEngine->midiDevice, portNumber, &audioEngine->midiOutputPort);
}

JNIEXPORT void JNICALL
Java_ch_sr35_touchsamplesynth_audio_AudioEngineK_closeMidiDevice(JNIEnv* , jobject)
{
    AudioEngine * audioEngine = getAudioEngine();
    AMidiOutputPort_close(audioEngine->midiOutputPort);
}
}