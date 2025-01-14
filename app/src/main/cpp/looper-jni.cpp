

#include <jni.h>
#include "AudioEngine.h"
#include "Looper.h"
extern "C"
{

JNIEXPORT jint JNICALL
Java_ch_sr35_touchsamplesynth_audio_voices_LooperK_getReadPointer(JNIEnv *env, jobject me) {
    auto msg = getAudioEngine()->getSoundGeneratorFromJni<Looper>(env, me);
    if (msg != nullptr) {
        return msg->getReadPointer();
    }
    return -1;
}
 
JNIEXPORT jboolean JNICALL
Java_ch_sr35_touchsamplesynth_audio_voices_LooperK_setReadPointer(JNIEnv *env, jobject me,jint val) {
    auto msg = getAudioEngine()->getSoundGeneratorFromJni<Looper>(env, me);
    if (msg != nullptr) {
        msg->setReadPointer(val);
        return true;
    }
    return false;
}

JNIEXPORT jfloat JNICALL
Java_ch_sr35_touchsamplesynth_audio_voices_LooperK_getRecordGain(JNIEnv *env, jobject me) {
    auto msg = getAudioEngine()->getSoundGeneratorFromJni<Looper>(env, me);
    if (msg != nullptr) {
        return msg->getRecordGain();
    }
    return -1.0f;
}


JNIEXPORT jboolean JNICALL
Java_ch_sr35_touchsamplesynth_audio_voices_LooperK_setRecordGain(JNIEnv *env, jobject me,jfloat val) {
    auto msg = getAudioEngine()->getSoundGeneratorFromJni<Looper>(env, me);
    if (msg != nullptr) {
        msg->setRecordGain(val);
        return true;
    }
    return false;
}


JNIEXPORT jint JNICALL
Java_ch_sr35_touchsamplesynth_audio_voices_LooperK_getWritePointer(JNIEnv *env, jobject me) {
    auto msg = getAudioEngine()->getSoundGeneratorFromJni<Looper>(env, me);
    if (msg != nullptr) {
        return msg->getWritePointer();
    }
    return -1;
}
 
JNIEXPORT jboolean JNICALL
Java_ch_sr35_touchsamplesynth_audio_voices_LooperK_setWritePointer(JNIEnv *env, jobject me,jint val) {
    auto msg = getAudioEngine()->getSoundGeneratorFromJni<Looper>(env, me);
    if (msg != nullptr) {
        msg->setWritePointer(val);
        return true;
    }
    return false;
}

JNIEXPORT jint JNICALL
Java_ch_sr35_touchsamplesynth_audio_voices_LooperK_getLoopEnd(JNIEnv *env, jobject me) {
    auto msg = getAudioEngine()->getSoundGeneratorFromJni<Looper>(env, me);
    if (msg != nullptr) {
        return msg->getLoopEnd();
    }
    return -1;
}
 
JNIEXPORT jboolean JNICALL
Java_ch_sr35_touchsamplesynth_audio_voices_LooperK_setLoopEnd(JNIEnv *env, jobject me,jint val) {
    auto msg = getAudioEngine()->getSoundGeneratorFromJni<Looper>(env, me);
    if (msg != nullptr) {
        msg->setLoopEnd(val);
        return true;
    }
    return false;
}

JNIEXPORT jboolean JNICALL
Java_ch_sr35_touchsamplesynth_audio_voices_LooperK_startRecording(JNIEnv* env,
                                                                 jobject /* this */me)
{
    auto msg = getAudioEngine()->getSoundGeneratorFromJni<Looper>(env, me);
    if (msg != nullptr) {
        msg->startRecording();
        return true;
    }
    return false;
}

JNIEXPORT jboolean JNICALL
Java_ch_sr35_touchsamplesynth_audio_voices_LooperK_stopRecording(JNIEnv* env,
                                                                jobject /* this */me)
{
    auto msg = getAudioEngine()->getSoundGeneratorFromJni<Looper>(env, me);
    if (msg != nullptr) {
        msg->stopRecording();
        return true;
    }
    return false;
}

JNIEXPORT jboolean JNICALL
Java_ch_sr35_touchsamplesynth_audio_voices_LooperK_resetSample(JNIEnv *env, jobject thiz) {
    auto msg = getAudioEngine()->getSoundGeneratorFromJni<Looper>(env, thiz);
    if (msg != nullptr) {
        msg->resetSample();
        return true;
    }
    return false;
}

JNIEXPORT jboolean JNICALL
Java_ch_sr35_touchsamplesynth_audio_voices_LooperK_setSample(JNIEnv *env, jobject me,
                                                              jfloatArray sample_data) {
    auto msg = getAudioEngine()->getSoundGeneratorFromJni<Looper>(env, me);
    if (msg != nullptr) {
        jsize sample_data_length = env->GetArrayLength(sample_data);
        jfloat *sampleArrayPtr = env->GetFloatArrayElements(sample_data, nullptr);
        msg->setSample(sampleArrayPtr, sample_data_length);
        return true;
    }
    return false;
}

JNIEXPORT jfloatArray JNICALL
Java_ch_sr35_touchsamplesynth_audio_voices_LooperK_getSample(JNIEnv *env, jobject me) {
    float * samplePtr;
    jfloatArray sampleData;
    auto msg = getAudioEngine()->getSoundGeneratorFromJni<Looper>(env, me);
    if (msg != nullptr) {
        uint32_t sampleLength = msg->getSample(&samplePtr);
        sampleData = env->NewFloatArray(sampleLength);
        jfloat *fltArrayPtr = env->GetFloatArrayElements(sampleData, nullptr);
        for (uint32_t c = 0; c < sampleLength; c++) {
            *(fltArrayPtr + c) = *(samplePtr + c);
        }
        return sampleData;
    }
    return nullptr;
}

JNIEXPORT jboolean JNICALL
Java_ch_sr35_touchsamplesynth_audio_voices_LooperK_hasRecordedContent(JNIEnv *env, jobject me) {
    float * samplePtr;
    jfloatArray sampleData;
    auto msg = getAudioEngine()->getSoundGeneratorFromJni<Looper>(env, me);
    if (msg != nullptr) {

        return msg->getLoopEnd() > 0;
    }
    return false;
}

}


