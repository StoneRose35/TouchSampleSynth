//
// Created by philipp on 23.12.23.
//

#include <jni.h>
#include "AudioEngine.h"
#include "Sampler.h"
extern "C"
{

JNIEXPORT jint JNICALL
Java_ch_sr35_touchsamplesynth_audio_voices_SamplerK_getLoopStartIndex(JNIEnv *env, jobject me) {
    auto msg = getAudioEngine()->getSoundGeneratorFromJni<Sampler>(env, me);
    if (msg != nullptr) {
        return msg->getLoopStartIndex();
    }
    return -1;
}

JNIEXPORT jboolean JNICALL
Java_ch_sr35_touchsamplesynth_audio_voices_SamplerK_setLoopStartIndex(JNIEnv *env, jobject me,jint val) {
    auto msg = getAudioEngine()->getSoundGeneratorFromJni<Sampler>(env, me);
    if (msg != nullptr) {
        msg->setLoopStartIndex(val);
        return true;
    }
   return false;
}

JNIEXPORT jint JNICALL
Java_ch_sr35_touchsamplesynth_audio_voices_SamplerK_getLoopEndIndex(JNIEnv *env, jobject me) {
    auto msg = getAudioEngine()->getSoundGeneratorFromJni<Sampler>(env, me);
    if (msg != nullptr) {
        return msg->getLoopEndIndex();
    }
    return -1;
}


JNIEXPORT jint JNICALL
Java_ch_sr35_touchsamplesynth_audio_voices_SamplerK_getSampleStartIndex(JNIEnv *env, jobject me) {
    auto msg = getAudioEngine()->getSoundGeneratorFromJni<Sampler>(env, me);
    if (msg != nullptr) {
        return msg->getSampleStartIndex();
    }
    return -1;
}

JNIEXPORT jboolean JNICALL
Java_ch_sr35_touchsamplesynth_audio_voices_SamplerK_setSampleStartIndex(JNIEnv *env, jobject me,jint val) {
    auto msg = getAudioEngine()->getSoundGeneratorFromJni<Sampler>(env, me);
    if (msg != nullptr) {
        msg->setSampleStartIndex(val);
        return true;
    }
    return false;
}

JNIEXPORT jint JNICALL
Java_ch_sr35_touchsamplesynth_audio_voices_SamplerK_getSampleEndIndex(JNIEnv *env, jobject me) {
    auto msg = getAudioEngine()->getSoundGeneratorFromJni<Sampler>(env, me);
    if (msg != nullptr) {
        return msg->getSampleEndIndex();
    }
    return -1;
}

JNIEXPORT jboolean JNICALL
Java_ch_sr35_touchsamplesynth_audio_voices_SamplerK_setSampleEndIndex(JNIEnv *env, jobject me,jint val) {
    auto msg = getAudioEngine()->getSoundGeneratorFromJni<Sampler>(env, me);
    if (msg != nullptr) {
        msg->setSampleEndIndex(val);
        return true;
    }
    return false;
}

JNIEXPORT jboolean JNICALL
Java_ch_sr35_touchsamplesynth_audio_voices_SamplerK_setMode(JNIEnv *env, jobject me, jbyte val) {
    auto msg = getAudioEngine()->getSoundGeneratorFromJni<Sampler>(env, me);
    if (msg != nullptr) {
        msg->setMode(val);
        return true;
    }
    return false;
}

JNIEXPORT jbyte JNICALL
Java_ch_sr35_touchsamplesynth_audio_voices_SamplerK_getMode(JNIEnv *env, jobject me) {
    auto msg = getAudioEngine()->getSoundGeneratorFromJni<Sampler>(env, me);
    if (msg != nullptr) {
        return msg->getMode();
    }
    return -1;
}


JNIEXPORT jboolean JNICALL
Java_ch_sr35_touchsamplesynth_audio_voices_SamplerK_setLoopEndIndex(JNIEnv *env, jobject me,jint val) {
    auto msg = getAudioEngine()->getSoundGeneratorFromJni<Sampler>(env, me);
    if (msg != nullptr) {
        msg->setLoopEndIndex(val);
        return true;
    }
}


JNIEXPORT jboolean JNICALL
Java_ch_sr35_touchsamplesynth_audio_voices_SamplerK_setSample(JNIEnv *env, jobject me,
                   jfloatArray sample_data) {
    auto msg = getAudioEngine()->getSoundGeneratorFromJni<Sampler>(env, me);
    if (msg != nullptr) {
        jsize sample_data_length = env->GetArrayLength(sample_data);
        jfloat *sampleArrayPtr = env->GetFloatArrayElements(sample_data, nullptr);
        ((Sampler *) msg)->setSample(sampleArrayPtr, sample_data_length);
        return true;
    }
    return false;
}

JNIEXPORT jfloatArray JNICALL
Java_ch_sr35_touchsamplesynth_audio_voices_SamplerK_getSample(JNIEnv *env, jobject me) {
    float * samplePtr;
    jfloatArray sampleData;
    auto msg = getAudioEngine()->getSoundGeneratorFromJni<Sampler>(env, me);
    if (msg != nullptr) {
        uint32_t sampleLength = ((Sampler *) msg)->getSample(&samplePtr);
        sampleData = env->NewFloatArray(sampleLength);
        jfloat *fltArrayPtr = env->GetFloatArrayElements(sampleData, nullptr);
        for (uint32_t c = 0; c < sampleLength; c++) {
            *(fltArrayPtr + c) = *(samplePtr + c);
        }
        return sampleData;
    }
    return nullptr;
}



}


