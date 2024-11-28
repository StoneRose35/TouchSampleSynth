//
// Created by philipp on 28.11.24.
//

#include <jni.h>
#include "AudioEngine.h"
#include "MusicalSoundGenerator.h"
extern "C"
{


JNIEXPORT jboolean JNICALL
Java_ch_sr35_touchsamplesynth_audio_MusicalSoundGenerator_setVolume(JNIEnv* env,
                                                              jobject /* this */me,
                                                              jfloat volume)
{
    auto msg = getAudioEngine()->getSoundGeneratorFromJni<MusicalSoundGenerator>(env, me);
    if (msg != nullptr) {
        msg->setVolume(volume);
        return true;
    }
    return false;
}

JNIEXPORT jfloat JNICALL
Java_ch_sr35_touchsamplesynth_audio_MusicalSoundGenerator_getVolume(JNIEnv* env,
                                                              jobject /* this */me)
{
    auto msg = getAudioEngine()->getSoundGeneratorFromJni<MusicalSoundGenerator>(env, me);
    if (msg != nullptr) {
        return msg->getVolume();
    }
    return -1.0f;
}

JNIEXPORT jboolean JNICALL
Java_ch_sr35_touchsamplesynth_audio_MusicalSoundGenerator_setMidiVelocityScaling(JNIEnv* env,
                                                                           jobject /* this */me,
                                                                           jfloat scaling)
{
    auto msg = getAudioEngine()->getSoundGeneratorFromJni<MusicalSoundGenerator>(env, me);
    if (msg != nullptr) {
        msg->midiVelocityScaling = scaling;
        return true;
    }
    return false;
}

JNIEXPORT jboolean JNICALL
Java_ch_sr35_touchsamplesynth_audio_MusicalSoundGenerator_triggerExt(JNIEnv *env, jobject me,
                                                               jfloat vel) {
    auto msg = getAudioEngine()->getSoundGeneratorFromJni<MusicalSoundGenerator>(env, me);
    if (msg != nullptr) {
        msg->trigger(vel);
        return true;
    }
    return false;
}


JNIEXPORT jboolean JNICALL
Java_ch_sr35_touchsamplesynth_audio_MusicalSoundGenerator_switchOnExt(JNIEnv *env, jobject me,
                                                                jfloat vel) {
    auto msg = getAudioEngine()->getSoundGeneratorFromJni<MusicalSoundGenerator>(env, me);
    if (msg != nullptr) {
        msg->switchOn(vel);
        return true;
    }
    return false;
}

JNIEXPORT jboolean JNICALL
Java_ch_sr35_touchsamplesynth_audio_MusicalSoundGenerator_switchOffExt(JNIEnv *env, jobject me,
                                                                 jfloat vel) {
    auto msg = getAudioEngine()->getSoundGeneratorFromJni<MusicalSoundGenerator>(env, me);
    if (msg != nullptr) {
        msg->switchOff(vel);
        return true;
    }
    return false;
}

JNIEXPORT jboolean JNICALL
Java_ch_sr35_touchsamplesynth_audio_MusicalSoundGenerator_isSounding(JNIEnv *env, jobject me) {
    auto msg = getAudioEngine()->getSoundGeneratorFromJni<MusicalSoundGenerator>(env, me);
    if (msg != nullptr) {
        return msg->isSounding();
    }
    return false;
}

JNIEXPORT jint JNICALL
Java_ch_sr35_touchsamplesynth_audio_MusicalSoundGenerator_getMidiMode(JNIEnv *env, jobject me) {
    auto msg = getAudioEngine()->getSoundGeneratorFromJni<MusicalSoundGenerator>(env, me);
    if (msg != nullptr) {
        return msg->availableForMidi >> 7;
    }
    return -1;
}

JNIEXPORT void JNICALL
Java_ch_sr35_touchsamplesynth_audio_MusicalSoundGenerator_setMidiMode(JNIEnv* env,jobject /* this */me,jint midiMode) {
    auto msg = getAudioEngine()->getSoundGeneratorFromJni<MusicalSoundGenerator>(env, me);
    if (msg != nullptr) {
        msg->availableForMidi = midiMode << 7;
    }
}

JNIEXPORT jboolean JNICALL
Java_ch_sr35_touchsamplesynth_audio_MusicalSoundGenerator_setNote(JNIEnv* env,
                                                                  jobject /* this */me,
                                                                  jfloat note)
{
    auto msg = getAudioEngine()->getSoundGeneratorFromJni<MusicalSoundGenerator>(env, me);
    if (msg != nullptr) {
        msg->setNote(note);
        return true;
    }
    return false;
}

}