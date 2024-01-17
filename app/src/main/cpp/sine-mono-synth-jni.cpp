//
// Created by philipp on 07.09.23.
//

#include <jni.h>
#include "AudioEngine.h"
#include "MusicalSoundGenerator.h"
#include "SineMonoSynth.h"
#include "jni-bridge.h"
extern "C"
{
JNIEXPORT jboolean JNICALL
Java_ch_sr35_touchsamplesynth_audio_voices_SineMonoSynthK_setAttack(JNIEnv* env,
                         jobject /* this */me,
                         jfloat attack)
{
    MusicalSoundGenerator * msg = getMusicalSoundGenerator(SINE_MONO_SYNTH,env,me);
    if  (msg == nullptr)
    {
        return false;
    }
    ((SineMonoSynth*)msg)->setAttack(attack);
    return true;
}

JNIEXPORT jfloat JNICALL
Java_ch_sr35_touchsamplesynth_audio_voices_SineMonoSynthK_getAttack(JNIEnv* env,
                                                             jobject /* this */me)
{
    MusicalSoundGenerator * msg = getMusicalSoundGenerator(SINE_MONO_SYNTH,env,me);
    if  (msg == nullptr)
    {
        return -1.0f;
    }
    return ((SineMonoSynth*)msg)->getAttack();
}

JNIEXPORT jboolean JNICALL
Java_ch_sr35_touchsamplesynth_audio_voices_SineMonoSynthK_setDecay(JNIEnv* env,
                                                             jobject /* this */me,
                                                             jfloat attack)
{
    MusicalSoundGenerator * msg = getMusicalSoundGenerator(SINE_MONO_SYNTH,env,me);
    if  (msg == nullptr)
    {
        return false;
    }
    ((SineMonoSynth*)msg)->setDecay(attack);
    return true;
}

JNIEXPORT jfloat JNICALL
Java_ch_sr35_touchsamplesynth_audio_voices_SineMonoSynthK_getDecay(JNIEnv* env,
                                                             jobject /* this */me)
{
    MusicalSoundGenerator * msg = getMusicalSoundGenerator(SINE_MONO_SYNTH,env,me);
    if  (msg == nullptr)
    {
        return -1.0f;
    }
    return ((SineMonoSynth*)msg)->getDecay();
}

JNIEXPORT jboolean JNICALL
Java_ch_sr35_touchsamplesynth_audio_voices_SineMonoSynthK_setSustain(JNIEnv* env,
                                                            jobject /* this */me,
                                                            jfloat attack)
{
    MusicalSoundGenerator * msg = getMusicalSoundGenerator(SINE_MONO_SYNTH,env,me);
    if  (msg == nullptr)
    {
        return false;
    }
    ((SineMonoSynth*)msg)->setSustain(attack);
    return true;
}

JNIEXPORT jfloat JNICALL
Java_ch_sr35_touchsamplesynth_audio_voices_SineMonoSynthK_getSustain(JNIEnv* env,
                                                            jobject /* this */me)
{
    MusicalSoundGenerator * msg = getMusicalSoundGenerator(SINE_MONO_SYNTH,env,me);
    if  (msg == nullptr)
    {
        return -1.0f;
    }
    return ((SineMonoSynth*)msg)->getSustain();
}

JNIEXPORT jboolean JNICALL
Java_ch_sr35_touchsamplesynth_audio_voices_SineMonoSynthK_setRelease(JNIEnv* env,
                                                            jobject /* this */me,
                                                            jfloat attack)
{
    MusicalSoundGenerator * msg = getMusicalSoundGenerator(SINE_MONO_SYNTH,env,me);
    if  (msg == nullptr)
    {
        return false;
    }
    ((SineMonoSynth*)msg)->setRelease(attack);
    return true;
}

JNIEXPORT jfloat JNICALL
Java_ch_sr35_touchsamplesynth_audio_voices_SineMonoSynthK_getRelease(JNIEnv* env,
                                                            jobject /* this */me)
{
    MusicalSoundGenerator * msg = getMusicalSoundGenerator(SINE_MONO_SYNTH,env,me);
    if  (msg == nullptr)
    {
        return -1.0f;
    }
    return ((SineMonoSynth*)msg)->getRelease();
}

JNIEXPORT jboolean JNICALL
Java_ch_sr35_touchsamplesynth_audio_voices_SineMonoSynthK_switchOn(JNIEnv* env,
                                                              jobject /* this */me,
                                                              jfloat vel)
{
    MusicalSoundGenerator * msg = getMusicalSoundGenerator(SINE_MONO_SYNTH,env,me);
    if  (msg == nullptr)
    {
        return false;
    }
    ((SineMonoSynth*)msg)->switchOn(vel);
    return true;
}

JNIEXPORT jboolean JNICALL
Java_ch_sr35_touchsamplesynth_audio_voices_SineMonoSynthK_switchOff(JNIEnv* env,
                                                            jobject /* this */me,
                                                            jfloat vel)
{
    MusicalSoundGenerator * msg = getMusicalSoundGenerator(SINE_MONO_SYNTH,env,me);
    if  (msg == nullptr)
    {
        return false;
    }
    ((SineMonoSynth*)msg)->switchOff(vel);
    return true;
}

JNIEXPORT jboolean JNICALL
Java_ch_sr35_touchsamplesynth_audio_voices_SineMonoSynthK_setNote(JNIEnv* env,
                     jobject /* this */me,
                     jfloat note)
{
    MusicalSoundGenerator * msg = getMusicalSoundGenerator(SINE_MONO_SYNTH,env,me);
    if  (msg == nullptr)
    {
        return false;
    }
    ((SineMonoSynth*)msg)->setNote(note);
    return true;
}

JNIEXPORT jboolean JNICALL
Java_ch_sr35_touchsamplesynth_audio_voices_SineMonoSynthK_isSounding(JNIEnv* env,
                                                                  jobject /* this */me)
{
    MusicalSoundGenerator * msg = getMusicalSoundGenerator(SINE_MONO_SYNTH,env,me);
    if  (msg == nullptr)
    {
        return false;
    }
    return ((SineMonoSynth*)msg)->isSounding();
}

JNIEXPORT void JNICALL
Java_ch_sr35_touchsamplesynth_audio_voices_SineMonoSynthK_setMidiMode(JNIEnv* env,
                                                                               jobject /* this */me,jint midiMode) {
    MusicalSoundGenerator * msg = getMusicalSoundGenerator(SINE_MONO_SYNTH,env,me);
    if  (msg != nullptr)
    {
        msg->availableForMidi = midiMode << 7;
    }
}

JNIEXPORT jint JNICALL
Java_ch_sr35_touchsamplesynth_audio_voices_SineMonoSynthK_getMidiMode(JNIEnv *env, jobject thiz) {
    MusicalSoundGenerator * msg = getMusicalSoundGenerator(SINE_MONO_SYNTH,env,thiz);
    if  (msg == nullptr)
    {
        return -1;
    }
    if (msg->getType() != SINE_MONO_SYNTH)
    {
        return -1;
    }
    return msg->availableForMidi >> 7;
}


}