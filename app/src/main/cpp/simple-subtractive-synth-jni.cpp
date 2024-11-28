//
// Created by philipp on 15.09.23.
//


#include <jni.h>
#include "AudioEngine.h"
#include "MusicalSoundGenerator.h"
#include "SimpleSubtractiveSynth.h"

extern "C"
{

JNIEXPORT jboolean JNICALL
Java_ch_sr35_touchsamplesynth_audio_voices_SimpleSubtractiveSynthK_setAttack(JNIEnv* env,
                         jobject /* this */me,
                         jfloat attack) {
    auto msg = getAudioEngine()->getSoundGeneratorFromJni<SimpleSubtractiveSynth>(env, me);
    if (msg != nullptr) {
        msg->setAttack(attack);
        return true;
    }
    return false;
}

JNIEXPORT jfloat JNICALL
Java_ch_sr35_touchsamplesynth_audio_voices_SimpleSubtractiveSynthK_getAttack(JNIEnv* env,
                         jobject /* this */me)
{
    auto msg = getAudioEngine()->getSoundGeneratorFromJni<SimpleSubtractiveSynth>(env,me);
    if (msg != nullptr)
    {
        return msg->getAttack();
    }
    return -1.0f;
}

JNIEXPORT jboolean JNICALL
Java_ch_sr35_touchsamplesynth_audio_voices_SimpleSubtractiveSynthK_setDecay(JNIEnv* env,
                                  jobject /* this */me,
                                  jfloat decay)
{
    auto msg = getAudioEngine()->getSoundGeneratorFromJni<SimpleSubtractiveSynth>(env, me);
    if (msg != nullptr) {
        msg->setDecay(decay);
        return true;
    }
    return false;
}

JNIEXPORT jfloat JNICALL
Java_ch_sr35_touchsamplesynth_audio_voices_SimpleSubtractiveSynthK_getDecay(JNIEnv* env,
                                  jobject /* this */me)
{
    auto msg = getAudioEngine()->getSoundGeneratorFromJni<SimpleSubtractiveSynth>(env,me);
    if (msg != nullptr) {
        return (msg)->getDecay();
    }
    return -1.0f;
}

JNIEXPORT jboolean JNICALL
Java_ch_sr35_touchsamplesynth_audio_voices_SimpleSubtractiveSynthK_setSustain(JNIEnv* env,
                                  jobject /* this */me,
                                  jfloat sustain)
{
    auto msg = getAudioEngine()->getSoundGeneratorFromJni<SimpleSubtractiveSynth>(env, me);
    if (msg != nullptr) {
        msg->setSustain(sustain);
        return true;
    }
    return false;
}

JNIEXPORT jfloat JNICALL
Java_ch_sr35_touchsamplesynth_audio_voices_SimpleSubtractiveSynthK_getSustain(JNIEnv* env,
                                  jobject /* this */me)
{
    auto msg = getAudioEngine()->getSoundGeneratorFromJni<SimpleSubtractiveSynth>(env,me);
    if (msg != nullptr) {
        return (msg)->getSustain();
    }
    return -1.0f;
}

JNIEXPORT jboolean JNICALL
Java_ch_sr35_touchsamplesynth_audio_voices_SimpleSubtractiveSynthK_setRelease(JNIEnv* env,
                                  jobject /* this */me,
                                  jfloat release)
{
    auto msg = getAudioEngine()->getSoundGeneratorFromJni<SimpleSubtractiveSynth>(env, me);
    if (msg != nullptr) {
        msg->setRelease(release);
        return true;
    }
    return false;
}

JNIEXPORT jfloat JNICALL
Java_ch_sr35_touchsamplesynth_audio_voices_SimpleSubtractiveSynthK_getRelease(JNIEnv* env,
                                  jobject /* this */me)
{
    auto msg = getAudioEngine()->getSoundGeneratorFromJni<SimpleSubtractiveSynth>(env,me);
    if (msg != nullptr) {
        return (msg)->getRelease();
    }
    return -1.0f;
}

JNIEXPORT jboolean JNICALL
Java_ch_sr35_touchsamplesynth_audio_voices_SimpleSubtractiveSynthK_setCutoff(JNIEnv* env,
                                  jobject /* this */me,
                                  jfloat cutoff)
{
    auto msg = getAudioEngine()->getSoundGeneratorFromJni<SimpleSubtractiveSynth>(env, me);
    if (msg != nullptr) {
        msg->setCutoff(cutoff);
        return true;
    }
    return false;
}

JNIEXPORT jfloat JNICALL
Java_ch_sr35_touchsamplesynth_audio_voices_SimpleSubtractiveSynthK_getCutoff(JNIEnv* env,
                                  jobject /* this */me)
{
    auto msg = getAudioEngine()->getSoundGeneratorFromJni<SimpleSubtractiveSynth>(env,me);
    if (msg != nullptr) {
        return (msg)->getCutoff();
    }
    return -1.0f;
}

JNIEXPORT jboolean JNICALL
Java_ch_sr35_touchsamplesynth_audio_voices_SimpleSubtractiveSynthK_setResonance(JNIEnv* env,
                                      jobject /* this */me,
                                      jfloat resonance)
{
    auto msg = getAudioEngine()->getSoundGeneratorFromJni<SimpleSubtractiveSynth>(env, me);
    if (msg != nullptr) {
        msg->setResonance(resonance);
        return true;
    }
    return false;
}

JNIEXPORT jfloat JNICALL
Java_ch_sr35_touchsamplesynth_audio_voices_SimpleSubtractiveSynthK_getResonance(JNIEnv* env,
                                  jobject /* this */me)
{
    auto msg = getAudioEngine()->getSoundGeneratorFromJni<SimpleSubtractiveSynth>(env,me);
    if (msg != nullptr) {
        return (msg)->getResonance();
    }
    return -1.0f;
}

JNIEXPORT jboolean JNICALL
Java_ch_sr35_touchsamplesynth_audio_voices_SimpleSubtractiveSynthK_setVolume(JNIEnv* env,
                                jobject /* this */me,
                                jfloat volume)
{
    auto msg = getAudioEngine()->getSoundGeneratorFromJni<SimpleSubtractiveSynth>(env, me);
    if (msg != nullptr) {
        msg->setVolume(volume);
        return true;
    }
    return false;
}

JNIEXPORT jfloat JNICALL
Java_ch_sr35_touchsamplesynth_audio_voices_SimpleSubtractiveSynthK_getVolume(JNIEnv* env,
                                jobject /* this */me)
{
    auto msg = getAudioEngine()->getSoundGeneratorFromJni<SimpleSubtractiveSynth>(env,me);
    if (msg != nullptr) {
        return (msg)->getVolume();
    }
    return -1.0f;
}

JNIEXPORT jboolean JNICALL
Java_ch_sr35_touchsamplesynth_audio_voices_SimpleSubtractiveSynthK_setMidiVelocityScaling(JNIEnv* env,
                                               jobject /* this */me,
                                               jfloat scaling)
{
    auto msg = getAudioEngine()->getSoundGeneratorFromJni<SimpleSubtractiveSynth>(env, me);
    if (msg != nullptr) {
        msg->midiVelocityScaling = scaling;
        return true;
    }
    return false;
}

JNIEXPORT jboolean JNICALL
Java_ch_sr35_touchsamplesynth_audio_voices_SimpleSubtractiveSynthK_triggerExt(JNIEnv* env,
                                   jobject /* this */me,
                                   jfloat vel)
{
    auto msg = getAudioEngine()->getSoundGeneratorFromJni<SimpleSubtractiveSynth>(env, me);
    if (msg != nullptr) {
        msg->trigger(vel);
        return true;
    }
    return false;
}



JNIEXPORT jboolean JNICALL
Java_ch_sr35_touchsamplesynth_audio_voices_SimpleSubtractiveSynthK_switchOnExt(JNIEnv* env,
                                    jobject /* this */me,
                                    jfloat vel)
{
    auto msg = getAudioEngine()->getSoundGeneratorFromJni<SimpleSubtractiveSynth>(env, me);
    if (msg != nullptr) {
        msg->switchOn(vel);
        return true;
    }
    return false;
}

JNIEXPORT jboolean JNICALL
Java_ch_sr35_touchsamplesynth_audio_voices_SimpleSubtractiveSynthK_switchOffExt(JNIEnv* env,
                                     jobject /* this */me,
                                     jfloat vel)
{
    auto msg = getAudioEngine()->getSoundGeneratorFromJni<SimpleSubtractiveSynth>(env, me);
    if (msg != nullptr) {
        msg->switchOff(vel);
        return true;
    }
    return false;
}

JNIEXPORT jboolean JNICALL
Java_ch_sr35_touchsamplesynth_audio_voices_SimpleSubtractiveSynthK_setNote(JNIEnv* env,
                               jobject /* this */me,
                               jfloat note)
{
    auto msg = getAudioEngine()->getSoundGeneratorFromJni<SimpleSubtractiveSynth>(env, me);
    if (msg != nullptr) {
        msg->setNote(note);
        return true;
    }
    return false;
}

JNIEXPORT jboolean JNICALL
Java_ch_sr35_touchsamplesynth_audio_voices_SimpleSubtractiveSynthK_isSounding(JNIEnv* env,
                                   jobject /* this */me)
{
    auto msg = getAudioEngine()->getSoundGeneratorFromJni<SimpleSubtractiveSynth>(env,me);
    if (msg != nullptr) {
        return (msg)->isSounding();
    }
    return false;
}

JNIEXPORT jboolean JNICALL
Java_ch_sr35_touchsamplesynth_audio_voices_SimpleSubtractiveSynthK_setInitialCutoff(JNIEnv* env,
                                      jobject /* this */me,jfloat ic)
{
    auto msg = getAudioEngine()->getSoundGeneratorFromJni<SimpleSubtractiveSynth>(env, me);
    if (msg != nullptr) {
        msg->initialCutoff = ic;
        return true;
    }
    return false;
}

JNIEXPORT jfloat JNICALL
Java_ch_sr35_touchsamplesynth_audio_voices_SimpleSubtractiveSynthK_getInitialCutoff(JNIEnv* env,
                                        jobject /* this */me)
{
    auto msg = getAudioEngine()->getSoundGeneratorFromJni<SimpleSubtractiveSynth>(env,me);
    if (msg != nullptr) {
        return (msg)->initialCutoff;
    }
    return -1.0f;
}


JNIEXPORT void JNICALL
Java_ch_sr35_touchsamplesynth_audio_voices_SimpleSubtractiveSynthK_setMidiMode(JNIEnv* env,
                                  jobject /* this */me,jint midiMode)
{
    auto msg = getAudioEngine()->getSoundGeneratorFromJni<SimpleSubtractiveSynth>(env, me);
    if (msg != nullptr) {
        msg->availableForMidi = midiMode << 7;
    }
}

JNIEXPORT jint JNICALL
Java_ch_sr35_touchsamplesynth_audio_voices_SimpleSubtractiveSynthK_getMidiMode(JNIEnv *env, jobject thiz) {
    auto msg = getAudioEngine()->getSoundGeneratorFromJni<SimpleSubtractiveSynth>(env, thiz);
    if (msg != nullptr) {
    return msg->availableForMidi >> 7;
    }
    return -1;
}

}