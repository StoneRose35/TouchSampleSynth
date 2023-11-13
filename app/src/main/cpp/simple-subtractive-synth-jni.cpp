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
                         jfloat attack)
{
    AudioEngine * audioEngine = getAudioEngine();
    jclass synth=env->GetObjectClass(me);
    jmethodID getInstance=env->GetMethodID(synth,"getInstance","()B");
    int8_t instance = env->CallByteMethod(me,getInstance);
    MusicalSoundGenerator * msg = audioEngine->getSoundGenerator(instance);
    if  (msg == nullptr)
    {
        return false;
    }
    if (msg->getType() != SIMPLE_SUBTRACTIVE_SYNTH)
    {
        return false;
    }
    ((SimpleSubtractiveSynth*)msg)->setAttack(attack);
    return true;
}

JNIEXPORT jfloat JNICALL
Java_ch_sr35_touchsamplesynth_audio_voices_SimpleSubtractiveSynthK_getAttack(JNIEnv* env,
                         jobject /* this */me)
{
    AudioEngine * audioEngine = getAudioEngine();
    jclass synth=env->GetObjectClass(me);
    jmethodID getInstance=env->GetMethodID(synth,"getInstance","()B");
    int8_t instance = env->CallByteMethod(me,getInstance);
    MusicalSoundGenerator * msg = audioEngine->getSoundGenerator(instance);
    if  (msg == nullptr)
    {
        return -1.0f;
    }
    if (msg->getType() != SIMPLE_SUBTRACTIVE_SYNTH)
    {
        return -1.0f;
    }
    return ((SimpleSubtractiveSynth*)msg)->getAttack();
}

JNIEXPORT jboolean JNICALL
Java_ch_sr35_touchsamplesynth_audio_voices_SimpleSubtractiveSynthK_setDecay(JNIEnv* env,
                                                                      jobject /* this */me,
                                                                      jfloat attack)
{
    AudioEngine * audioEngine = getAudioEngine();
    jclass synth=env->GetObjectClass(me);
    jmethodID getInstance=env->GetMethodID(synth,"getInstance","()B");
    int8_t instance = env->CallByteMethod(me,getInstance);
    MusicalSoundGenerator * msg = audioEngine->getSoundGenerator(instance);
    if  (msg == nullptr)
    {
        return false;
    }
    if (msg->getType() != SIMPLE_SUBTRACTIVE_SYNTH)
    {
        return false;
    }
    ((SimpleSubtractiveSynth*)msg)->setDecay(attack);
    return true;
}

JNIEXPORT jfloat JNICALL
Java_ch_sr35_touchsamplesynth_audio_voices_SimpleSubtractiveSynthK_getDecay(JNIEnv* env,
                                                                      jobject /* this */me)
{
    AudioEngine * audioEngine = getAudioEngine();
    jclass synth=env->GetObjectClass(me);
    jmethodID getInstance=env->GetMethodID(synth,"getInstance","()B");
    int8_t instance = env->CallByteMethod(me,getInstance);
    MusicalSoundGenerator * msg = audioEngine->getSoundGenerator(instance);
    if  (msg == nullptr)
    {
        return -1.0f;
    }
    if (msg->getType() != SIMPLE_SUBTRACTIVE_SYNTH)
    {
        return -1.0f;
    }
    return ((SimpleSubtractiveSynth*)msg)->getDecay();
}

JNIEXPORT jboolean JNICALL
Java_ch_sr35_touchsamplesynth_audio_voices_SimpleSubtractiveSynthK_setSustain(JNIEnv* env,
                                                                      jobject /* this */me,
                                                                      jfloat attack)
{
    AudioEngine * audioEngine = getAudioEngine();
    jclass synth=env->GetObjectClass(me);
    jmethodID getInstance=env->GetMethodID(synth,"getInstance","()B");
    int8_t instance = env->CallByteMethod(me,getInstance);
    MusicalSoundGenerator * msg = audioEngine->getSoundGenerator(instance);
    if  (msg == nullptr)
    {
        return false;
    }
    if (msg->getType() != SIMPLE_SUBTRACTIVE_SYNTH)
    {
        return false;
    }
    ((SimpleSubtractiveSynth*)msg)->setSustain(attack);
    return true;
}

JNIEXPORT jfloat JNICALL
Java_ch_sr35_touchsamplesynth_audio_voices_SimpleSubtractiveSynthK_getSustain(JNIEnv* env,
                                                                      jobject /* this */me)
{
    AudioEngine * audioEngine = getAudioEngine();
    jclass synth=env->GetObjectClass(me);
    jmethodID getInstance=env->GetMethodID(synth,"getInstance","()B");
    int8_t instance = env->CallByteMethod(me,getInstance);
    MusicalSoundGenerator * msg = audioEngine->getSoundGenerator(instance);
    if  (msg == nullptr)
    {
        return -1.0f;
    }
    if (msg->getType() != SIMPLE_SUBTRACTIVE_SYNTH)
    {
        return -1.0f;
    }
    return ((SimpleSubtractiveSynth*)msg)->getSustain();
}

JNIEXPORT jboolean JNICALL
Java_ch_sr35_touchsamplesynth_audio_voices_SimpleSubtractiveSynthK_setRelease(JNIEnv* env,
                                                                      jobject /* this */me,
                                                                      jfloat attack)
{
    AudioEngine * audioEngine = getAudioEngine();
    jclass synth=env->GetObjectClass(me);
    jmethodID getInstance=env->GetMethodID(synth,"getInstance","()B");
    int8_t instance = env->CallByteMethod(me,getInstance);
    MusicalSoundGenerator * msg = audioEngine->getSoundGenerator(instance);
    if  (msg == nullptr)
    {
        return false;
    }
    if (msg->getType() != SIMPLE_SUBTRACTIVE_SYNTH)
    {
        return false;
    }
    ((SimpleSubtractiveSynth*)msg)->setRelease(attack);
    return true;
}

JNIEXPORT jfloat JNICALL
Java_ch_sr35_touchsamplesynth_audio_voices_SimpleSubtractiveSynthK_getRelease(JNIEnv* env,
                                                                      jobject /* this */me)
{
    AudioEngine * audioEngine = getAudioEngine();
    jclass synth=env->GetObjectClass(me);
    jmethodID getInstance=env->GetMethodID(synth,"getInstance","()B");
    int8_t instance = env->CallByteMethod(me,getInstance);
    MusicalSoundGenerator * msg = audioEngine->getSoundGenerator(instance);
    if  (msg == nullptr)
    {
        return -1.0f;
    }
    if (msg->getType() != SIMPLE_SUBTRACTIVE_SYNTH)
    {
        return -1.0f;
    }
    return ((SimpleSubtractiveSynth*)msg)->getRelease();
}

JNIEXPORT jboolean JNICALL
Java_ch_sr35_touchsamplesynth_audio_voices_SimpleSubtractiveSynthK_setCutoff(JNIEnv* env,
                                                                      jobject /* this */me,
                                                                      jfloat attack)
{
    AudioEngine * audioEngine = getAudioEngine();
    jclass synth=env->GetObjectClass(me);
    jmethodID getInstance=env->GetMethodID(synth,"getInstance","()B");
    int8_t instance = env->CallByteMethod(me,getInstance);
    MusicalSoundGenerator * msg = audioEngine->getSoundGenerator(instance);
    if  (msg == nullptr)
    {
        return false;
    }
    if (msg->getType() != SIMPLE_SUBTRACTIVE_SYNTH)
    {
        return false;
    }
    ((SimpleSubtractiveSynth*)msg)->setCutoff(attack);
    return true;
}

JNIEXPORT jfloat JNICALL
Java_ch_sr35_touchsamplesynth_audio_voices_SimpleSubtractiveSynthK_getCutoff(JNIEnv* env,
                                                                      jobject /* this */me)
{
    AudioEngine * audioEngine = getAudioEngine();
    jclass synth=env->GetObjectClass(me);
    jmethodID getInstance=env->GetMethodID(synth,"getInstance","()B");
    int8_t instance = env->CallByteMethod(me,getInstance);
    MusicalSoundGenerator * msg = audioEngine->getSoundGenerator(instance);
    if  (msg == nullptr)
    {
        return -1.0f;
    }
    if (msg->getType() != SIMPLE_SUBTRACTIVE_SYNTH)
    {
        return -1.0f;
    }
    return ((SimpleSubtractiveSynth*)msg)->getCutoff();
}

JNIEXPORT jboolean JNICALL
Java_ch_sr35_touchsamplesynth_audio_voices_SimpleSubtractiveSynthK_setResonance(JNIEnv* env,
                                                                      jobject /* this */me,
                                                                      jfloat attack)
{
    AudioEngine * audioEngine = getAudioEngine();
    jclass synth=env->GetObjectClass(me);
    jmethodID getInstance=env->GetMethodID(synth,"getInstance","()B");
    int8_t instance = env->CallByteMethod(me,getInstance);
    MusicalSoundGenerator * msg = audioEngine->getSoundGenerator(instance);
    if  (msg == nullptr)
    {
        return false;
    }
    if (msg->getType() != SIMPLE_SUBTRACTIVE_SYNTH)
    {
        return false;
    }
    ((SimpleSubtractiveSynth*)msg)->setResonance(attack);
    return true;
}

JNIEXPORT jfloat JNICALL
Java_ch_sr35_touchsamplesynth_audio_voices_SimpleSubtractiveSynthK_getResonance(JNIEnv* env,
                                                                      jobject /* this */me)
{
    AudioEngine * audioEngine = getAudioEngine();
    jclass synth=env->GetObjectClass(me);
    jmethodID getInstance=env->GetMethodID(synth,"getInstance","()B");
    int8_t instance = env->CallByteMethod(me,getInstance);
    MusicalSoundGenerator * msg = audioEngine->getSoundGenerator(instance);
    if  (msg == nullptr)
    {
        return -1.0f;
    }
    if (msg->getType() != SIMPLE_SUBTRACTIVE_SYNTH)
    {
        return -1.0f;
    }
    return ((SimpleSubtractiveSynth*)msg)->getResonance();
}

JNIEXPORT jboolean JNICALL
Java_ch_sr35_touchsamplesynth_audio_voices_SimpleSubtractiveSynthK_switchOnExt(JNIEnv* env,
                                                            jobject /* this */me,
                                                            jfloat vel)
{
    AudioEngine * audioEngine = getAudioEngine();
    jclass synth=env->GetObjectClass(me);
    jmethodID getInstance=env->GetMethodID(synth,"getInstance","()B");
    int8_t instance = env->CallByteMethod(me,getInstance);
    MusicalSoundGenerator * msg = audioEngine->getSoundGenerator(instance);
    if  (msg == nullptr)
    {
        return false;
    }
    ((SimpleSubtractiveSynth*)msg)->switchOn(vel);
    return true;
}

JNIEXPORT jboolean JNICALL
Java_ch_sr35_touchsamplesynth_audio_voices_SimpleSubtractiveSynthK_switchOffExt(JNIEnv* env,
                                                             jobject /* this */me,
                                                             jfloat vel)
{
    AudioEngine * audioEngine = getAudioEngine();
    jclass synth=env->GetObjectClass(me);
    jmethodID getInstance=env->GetMethodID(synth,"getInstance","()B");
    int8_t instance = env->CallByteMethod(me,getInstance);
    MusicalSoundGenerator * msg = audioEngine->getSoundGenerator(instance);
    if  (msg == nullptr)
    {
        return false;
    }
    ((SimpleSubtractiveSynth*)msg)->switchOff(vel);
    return true;
}

JNIEXPORT jboolean JNICALL
Java_ch_sr35_touchsamplesynth_audio_voices_SimpleSubtractiveSynthK_setNote(JNIEnv* env,
                                                           jobject /* this */me,
                                                           jfloat note)
{
    AudioEngine * audioEngine = getAudioEngine();
    jclass synth=env->GetObjectClass(me);
    jmethodID getInstance=env->GetMethodID(synth,"getInstance","()B");
    int8_t instance = env->CallByteMethod(me,getInstance);
    MusicalSoundGenerator * msg = audioEngine->getSoundGenerator(instance);
    if  (msg == nullptr)
    {
        return false;
    }
    ((SimpleSubtractiveSynth*)msg)->setNote(note);
    return true;
}

JNIEXPORT jboolean JNICALL
Java_ch_sr35_touchsamplesynth_audio_voices_SimpleSubtractiveSynthK_isSounding(JNIEnv* env,
                                                                           jobject /* this */me)
{
    AudioEngine * audioEngine = getAudioEngine();
    jclass synth=env->GetObjectClass(me);
    jmethodID getInstance=env->GetMethodID(synth,"getInstance","()B");
    int8_t instance = env->CallByteMethod(me,getInstance);
    MusicalSoundGenerator * msg = audioEngine->getSoundGenerator(instance);
    if  (msg == nullptr)
    {
        return false;
    }
    return ((SimpleSubtractiveSynth*)msg)->isSounding();
}

JNIEXPORT jboolean JNICALL
Java_ch_sr35_touchsamplesynth_audio_voices_SimpleSubtractiveSynthK_setInitialCutoff(JNIEnv* env,
                                                                              jobject /* this */me,jfloat ic)
{
    AudioEngine * audioEngine = getAudioEngine();
    jclass synth=env->GetObjectClass(me);
    jmethodID getInstance=env->GetMethodID(synth,"getInstance","()B");
    int8_t instance = env->CallByteMethod(me,getInstance);
    MusicalSoundGenerator * msg = audioEngine->getSoundGenerator(instance);
    if  (msg == nullptr)
    {
        return false;
    }
    ((SimpleSubtractiveSynth*)msg)->initialCutoff = ic;
    return true;
}

JNIEXPORT jfloat JNICALL
Java_ch_sr35_touchsamplesynth_audio_voices_SimpleSubtractiveSynthK_getInitialCutoff(JNIEnv* env,
                                                                                    jobject /* this */me)
{
    AudioEngine * audioEngine = getAudioEngine();
    jclass synth=env->GetObjectClass(me);
    jmethodID getInstance=env->GetMethodID(synth,"getInstance","()B");
    int8_t instance = env->CallByteMethod(me,getInstance);
    MusicalSoundGenerator * msg = audioEngine->getSoundGenerator(instance);
    if  (msg == nullptr)
    {
        return 1.0f;
    }
    return ((SimpleSubtractiveSynth*)msg)->initialCutoff;
}


JNIEXPORT void JNICALL
Java_ch_sr35_touchsamplesynth_audio_voices_SimpleSubtractiveSynthK_setMidiMode(JNIEnv* env,
                                                                              jobject /* this */me,jint midiMode) {
    AudioEngine * audioEngine = getAudioEngine();
    jclass synth=env->GetObjectClass(me);
    jmethodID getInstance=env->GetMethodID(synth,"getInstance","()B");
    int8_t instance = env->CallByteMethod(me,getInstance);
    MusicalSoundGenerator * msg = audioEngine->getSoundGenerator(instance);
    if  (msg != nullptr)
    {
        msg->availableForMidi = midiMode << 7;
    }
}

}