

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

}


