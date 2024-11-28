//
// Created by philipp on 07.09.23.
//

#include <jni.h>
#include "AudioEngine.h"
#include "MusicalSoundGenerator.h"
#include "SineMonoSynth.h"
extern "C"
{
JNIEXPORT jboolean JNICALL
Java_ch_sr35_touchsamplesynth_audio_voices_SineMonoSynthK_setAttack(JNIEnv* env,
                         jobject /* this */me,
                         jfloat attack)
{
    auto msg = getAudioEngine()->getSoundGeneratorFromJni<SineMonoSynth>(env, me);
    if (msg != nullptr) {
        msg->setAttack(attack);
        return true;
    }
    return false;
}

JNIEXPORT jfloat JNICALL
Java_ch_sr35_touchsamplesynth_audio_voices_SineMonoSynthK_getAttack(JNIEnv* env,
                         jobject /* this */me)
{
    auto msg = getAudioEngine()->getSoundGeneratorFromJni<SineMonoSynth>(env, me);
    if (msg != nullptr) {
        return msg->getAttack();
    }
    return -1.0;
}

JNIEXPORT jboolean JNICALL
Java_ch_sr35_touchsamplesynth_audio_voices_SineMonoSynthK_setDecay(JNIEnv* env,
                     jobject /* this */me,
                     jfloat attack)
{
    auto msg = getAudioEngine()->getSoundGeneratorFromJni<SineMonoSynth>(env, me);
    if (msg != nullptr) {
        msg->setDecay(attack);
        return true;
    }
    return false;
}

JNIEXPORT jfloat JNICALL
Java_ch_sr35_touchsamplesynth_audio_voices_SineMonoSynthK_getDecay(JNIEnv* env,
                     jobject /* this */me)
{
    auto msg = getAudioEngine()->getSoundGeneratorFromJni<SineMonoSynth>(env, me);
    if (msg != nullptr) {
        return msg->getDecay();
    }
    return -1.0f;
}

JNIEXPORT jboolean JNICALL
Java_ch_sr35_touchsamplesynth_audio_voices_SineMonoSynthK_setSustain(JNIEnv* env,
                        jobject /* this */me,
                        jfloat attack)
{
    auto msg = getAudioEngine()->getSoundGeneratorFromJni<SineMonoSynth>(env, me);
    if (msg != nullptr) {
        msg->setSustain(attack);
        return true;
    }
    return false;
}

JNIEXPORT jfloat JNICALL
Java_ch_sr35_touchsamplesynth_audio_voices_SineMonoSynthK_getSustain(JNIEnv* env,
                        jobject /* this */me)
{
    auto msg = getAudioEngine()->getSoundGeneratorFromJni<SineMonoSynth>(env, me);
    if (msg != nullptr) {
        return ((SineMonoSynth *) msg)->getSustain();
    }
    return -1.0;
}

JNIEXPORT jboolean JNICALL
Java_ch_sr35_touchsamplesynth_audio_voices_SineMonoSynthK_setRelease(JNIEnv* env,
                        jobject /* this */me,
                        jfloat attack)
{
    auto msg = getAudioEngine()->getSoundGeneratorFromJni<SineMonoSynth>(env, me);
    if (msg != nullptr) {
        msg->setRelease(attack);
        return true;
    }
    return false;
}

JNIEXPORT jfloat JNICALL
Java_ch_sr35_touchsamplesynth_audio_voices_SineMonoSynthK_getRelease(JNIEnv* env,
                        jobject /* this */me)
{
    auto msg = getAudioEngine()->getSoundGeneratorFromJni<SineMonoSynth>(env, me);
    if (msg != nullptr) {
        return msg->getRelease();
    }
    return -1.0f;
}

}