//
// Created by philipp on 23.12.23.
//

#include <jni.h>
#include "AudioEngine.h"
#include "Sampler.h"
extern "C"
{

JNIEXPORT jint JNICALL
Java_ch_sr35_touchsamplesynth_audio_voices_SamplerK_getLoopStartIndex(JNIEnv *env, jobject thiz) {
    AudioEngine * audioEngine = getAudioEngine();
    jclass synth=env->GetObjectClass(thiz);
    jmethodID getInstance=env->GetMethodID(synth,"getInstance","()B");
    int8_t instance = env->CallByteMethod(thiz,getInstance);
    MusicalSoundGenerator * msg = audioEngine->getSoundGenerator(instance);
    if  (msg == nullptr)
    {
        return -1;
    }
    if (msg->getType() != SAMPLER)
    {
        return -1;
    }
    return ((Sampler*)msg)->getLoopStartIndex();
}

JNIEXPORT jboolean JNICALL
Java_ch_sr35_touchsamplesynth_audio_voices_SamplerK_setLoopStartIndex(JNIEnv *env, jobject thiz,jint val) {
    AudioEngine * audioEngine = getAudioEngine();
    jclass synth=env->GetObjectClass(thiz);
    jmethodID getInstance=env->GetMethodID(synth,"getInstance","()B");
    int8_t instance = env->CallByteMethod(thiz,getInstance);
    MusicalSoundGenerator * msg = audioEngine->getSoundGenerator(instance);
    if  (msg == nullptr)
    {
        return false;
    }
    if (msg->getType() != SAMPLER)
    {
        return false;
    }
    ((Sampler*)msg)->setLoopStartIndex(val);
    return true;
}

JNIEXPORT jint JNICALL
Java_ch_sr35_touchsamplesynth_audio_voices_SamplerK_getLoopEndIndex(JNIEnv *env, jobject thiz) {
    AudioEngine * audioEngine = getAudioEngine();
    jclass synth=env->GetObjectClass(thiz);
    jmethodID getInstance=env->GetMethodID(synth,"getInstance","()B");
    int8_t instance = env->CallByteMethod(thiz,getInstance);
    MusicalSoundGenerator * msg = audioEngine->getSoundGenerator(instance);
    if  (msg == nullptr)
    {
        return -1;
    }
    if (msg->getType() != SAMPLER)
    {
        return -1;
    }
    return ((Sampler*)msg)->getLoopEndIndex();
}


JNIEXPORT jint JNICALL
Java_ch_sr35_touchsamplesynth_audio_voices_SamplerK_getSampleStartIndex(JNIEnv *env, jobject thiz) {
    AudioEngine * audioEngine = getAudioEngine();
    jclass synth=env->GetObjectClass(thiz);
    jmethodID getInstance=env->GetMethodID(synth,"getInstance","()B");
    int8_t instance = env->CallByteMethod(thiz,getInstance);
    MusicalSoundGenerator * msg = audioEngine->getSoundGenerator(instance);
    if  (msg == nullptr)
    {
        return -1;
    }
    if (msg->getType() != SAMPLER)
    {
        return -1;
    }
    return ((Sampler*)msg)->getSampleStartIndex();
}

JNIEXPORT jboolean JNICALL
Java_ch_sr35_touchsamplesynth_audio_voices_SamplerK_setSampleStartIndex(JNIEnv *env, jobject thiz,jint val) {
    AudioEngine * audioEngine = getAudioEngine();
    jclass synth=env->GetObjectClass(thiz);
    jmethodID getInstance=env->GetMethodID(synth,"getInstance","()B");
    int8_t instance = env->CallByteMethod(thiz,getInstance);
    MusicalSoundGenerator * msg = audioEngine->getSoundGenerator(instance);
    if  (msg == nullptr)
    {
        return false;
    }
    if (msg->getType() != SAMPLER)
    {
        return false;
    }
    ((Sampler*)msg)->setSampleStartIndex(val);
    return true;
}

JNIEXPORT jint JNICALL
Java_ch_sr35_touchsamplesynth_audio_voices_SamplerK_getSampleEndIndex(JNIEnv *env, jobject thiz) {
    AudioEngine * audioEngine = getAudioEngine();
    jclass synth=env->GetObjectClass(thiz);
    jmethodID getInstance=env->GetMethodID(synth,"getInstance","()B");
    int8_t instance = env->CallByteMethod(thiz,getInstance);
    MusicalSoundGenerator * msg = audioEngine->getSoundGenerator(instance);
    if  (msg == nullptr)
    {
        return -1;
    }
    if (msg->getType() != SAMPLER)
    {
        return -1;
    }
    return ((Sampler*)msg)->getSampleEndIndex();
}

JNIEXPORT jboolean JNICALL
Java_ch_sr35_touchsamplesynth_audio_voices_SamplerK_setSampleEndIndex(JNIEnv *env, jobject thiz,jint val) {
    AudioEngine * audioEngine = getAudioEngine();
    jclass synth=env->GetObjectClass(thiz);
    jmethodID getInstance=env->GetMethodID(synth,"getInstance","()B");
    int8_t instance = env->CallByteMethod(thiz,getInstance);
    MusicalSoundGenerator * msg = audioEngine->getSoundGenerator(instance);
    if  (msg == nullptr)
    {
        return false;
    }
    if (msg->getType() != SAMPLER)
    {
        return false;
    }
    ((Sampler*)msg)->setSampleEndIndex(val);
    return true;
}

JNIEXPORT jboolean JNICALL
Java_ch_sr35_touchsamplesynth_audio_voices_SamplerK_setMode(JNIEnv *env, jobject thiz, jbyte val) {
    AudioEngine * audioEngine = getAudioEngine();
    jclass synth=env->GetObjectClass(thiz);
    jmethodID getInstance=env->GetMethodID(synth,"getInstance","()B");
    int8_t instance = env->CallByteMethod(thiz,getInstance);
    MusicalSoundGenerator * msg = audioEngine->getSoundGenerator(instance);
    if  (msg == nullptr)
    {
        return false;
    }
    if (msg->getType() != SAMPLER)
    {
        return false;
    }
    ((Sampler*)msg)->setMode(val);
    return true;
}

JNIEXPORT jbyte JNICALL
Java_ch_sr35_touchsamplesynth_audio_voices_SamplerK_getMode(JNIEnv *env, jobject thiz) {
    AudioEngine * audioEngine = getAudioEngine();
    jclass synth=env->GetObjectClass(thiz);
    jmethodID getInstance=env->GetMethodID(synth,"getInstance","()B");
    int8_t instance = env->CallByteMethod(thiz,getInstance);
    MusicalSoundGenerator * msg = audioEngine->getSoundGenerator(instance);
    if  (msg == nullptr)
    {
        return -1;
    }
    if (msg->getType() != SAMPLER)
    {
        return -1;
    }
    return ((Sampler*)msg)->getMode();
}


JNIEXPORT jboolean JNICALL
Java_ch_sr35_touchsamplesynth_audio_voices_SamplerK_setLoopEndIndex(JNIEnv *env, jobject thiz,jint val) {
    AudioEngine * audioEngine = getAudioEngine();
    jclass synth=env->GetObjectClass(thiz);
    jmethodID getInstance=env->GetMethodID(synth,"getInstance","()B");
    int8_t instance = env->CallByteMethod(thiz,getInstance);
    MusicalSoundGenerator * msg = audioEngine->getSoundGenerator(instance);
    if  (msg == nullptr)
    {
        return false;
    }
    if (msg->getType() != SAMPLER)
    {
        return false;
    }
    ((Sampler*)msg)->setLoopEndIndex(val);
    return true;
}

JNIEXPORT jboolean JNICALL
Java_ch_sr35_touchsamplesynth_audio_voices_SamplerK_setVolume(JNIEnv* env,
                                                                             jobject /* this */me,
                                                                             jfloat volume)
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
    if (msg->getType() != SAMPLER)
    {
        return false;
    }
    ((Sampler*)msg)->setVolume(volume);
    return true;
}

JNIEXPORT jfloat JNICALL
Java_ch_sr35_touchsamplesynth_audio_voices_SamplerK_getVolume(JNIEnv* env,
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
    if (msg->getType() != SAMPLER)
    {
        return -1.0f;
    }
    return ((Sampler*)msg)->getVolume();
}

JNIEXPORT jboolean JNICALL
Java_ch_sr35_touchsamplesynth_audio_voices_SamplerK_setMidiVelocityScaling(JNIEnv* env,
                                                              jobject /* this */me,
                                                              jfloat scaling)
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
    if (msg->getType() != SAMPLER)
    {
        return false;
    }
    (msg)->midiVelocityScaling = scaling;
    return true;
}

JNIEXPORT jboolean JNICALL
        Java_ch_sr35_touchsamplesynth_audio_voices_SamplerK_triggerExt(JNIEnv *env, jobject thiz,
                                                                       jfloat vel) {
    AudioEngine * audioEngine = getAudioEngine();
    jclass synth=env->GetObjectClass(thiz);
    jmethodID getInstance=env->GetMethodID(synth,"getInstance","()B");
    int8_t instance = env->CallByteMethod(thiz,getInstance);
    MusicalSoundGenerator * msg = audioEngine->getSoundGenerator(instance);
    if  (msg == nullptr)
    {
        return false;
    }
    if (msg->getType() != SAMPLER)
    {
        return false;
    }
    ((Sampler*)msg)->trigger(vel);
    return true;
}


JNIEXPORT jboolean JNICALL
Java_ch_sr35_touchsamplesynth_audio_voices_SamplerK_switchOnExt(JNIEnv *env, jobject thiz,
                                                                jfloat vel) {
    AudioEngine * audioEngine = getAudioEngine();
    jclass synth=env->GetObjectClass(thiz);
    jmethodID getInstance=env->GetMethodID(synth,"getInstance","()B");
    int8_t instance = env->CallByteMethod(thiz,getInstance);
    MusicalSoundGenerator * msg = audioEngine->getSoundGenerator(instance);
    if  (msg == nullptr)
    {
        return false;
    }
    if (msg->getType() != SAMPLER)
    {
        return false;
    }
    ((Sampler*)msg)->switchOn(vel);
    return true;
}

JNIEXPORT jboolean JNICALL
Java_ch_sr35_touchsamplesynth_audio_voices_SamplerK_switchOffExt(JNIEnv *env, jobject thiz,
                                                                jfloat vel) {
    AudioEngine * audioEngine = getAudioEngine();
    jclass synth=env->GetObjectClass(thiz);
    jmethodID getInstance=env->GetMethodID(synth,"getInstance","()B");
    int8_t instance = env->CallByteMethod(thiz,getInstance);
    MusicalSoundGenerator * msg = audioEngine->getSoundGenerator(instance);
    if  (msg == nullptr)
    {
        return false;
    }
    if (msg->getType() != SAMPLER)
    {
        return false;
    }
    ((Sampler*)msg)->switchOff(vel);
    return true;
}

JNIEXPORT jboolean JNICALL
Java_ch_sr35_touchsamplesynth_audio_voices_SamplerK_isSounding(JNIEnv *env, jobject thiz) {
    AudioEngine * audioEngine = getAudioEngine();
    jclass synth=env->GetObjectClass(thiz);
    jmethodID getInstance=env->GetMethodID(synth,"getInstance","()B");
    int8_t instance = env->CallByteMethod(thiz,getInstance);
    MusicalSoundGenerator * msg = audioEngine->getSoundGenerator(instance);
    if  (msg == nullptr)
    {
        return false;
    }
    if (msg->getType() != SAMPLER)
    {
        return false;
    }
    return ((Sampler*)msg)->isSounding();
}

JNIEXPORT jboolean JNICALL
Java_ch_sr35_touchsamplesynth_audio_voices_SamplerK_loadSample(JNIEnv *env, jobject thiz,
                                                               jfloatArray sample_data) {
    AudioEngine * audioEngine = getAudioEngine();
    jclass synth=env->GetObjectClass(thiz);
    jmethodID getInstance=env->GetMethodID(synth,"getInstance","()B");
    int8_t instance = env->CallByteMethod(thiz,getInstance);
    MusicalSoundGenerator * msg = audioEngine->getSoundGenerator(instance);
    if  (msg == nullptr)
    {
        return false;
    }
    if (msg->getType() != SAMPLER)
    {
        return false;
    }
    jsize sample_data_length = env->GetArrayLength(sample_data);
    jfloat* sampleArrayPtr = env->GetFloatArrayElements(sample_data, nullptr);
    ((Sampler*)msg)->loadSample(sampleArrayPtr,sample_data_length);
    return true;
}

JNIEXPORT jfloatArray JNICALL
Java_ch_sr35_touchsamplesynth_audio_voices_SamplerK_copySample(JNIEnv *env, jobject thiz) {
    AudioEngine * audioEngine = getAudioEngine();
    jclass synth=env->GetObjectClass(thiz);
    jfloatArray sampleData;
    jmethodID getInstance=env->GetMethodID(synth,"getInstance","()B");
    int8_t instance = env->CallByteMethod(thiz,getInstance);
    MusicalSoundGenerator * msg = audioEngine->getSoundGenerator(instance);
    if  (msg == nullptr)
    {
        return nullptr;
    }
    if (msg->getType() != SAMPLER)
    {
        return nullptr;
    }
    float * samplePtr;
    uint32_t sampleLength=((Sampler*)msg)->getSample(&samplePtr);
    sampleData=env->NewFloatArray(sampleLength);
    jfloat * fltArrayPtr = env->GetFloatArrayElements(sampleData, nullptr);
    for (uint32_t c=0;c<sampleLength;c++)
    {
        *(fltArrayPtr + c) = *(samplePtr + c);
    }
    return sampleData;
}

JNIEXPORT void JNICALL
Java_ch_sr35_touchsamplesynth_audio_voices_SamplerK_setMidiMode(JNIEnv* env,
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

JNIEXPORT jint JNICALL
Java_ch_sr35_touchsamplesynth_audio_voices_SamplerK_getMidiMode(JNIEnv *env, jobject thiz) {
    AudioEngine * audioEngine = getAudioEngine();
    jclass synth=env->GetObjectClass(thiz);
    jmethodID getInstance=env->GetMethodID(synth,"getInstance","()B");
    int8_t instance = env->CallByteMethod(thiz,getInstance);
    MusicalSoundGenerator * msg = audioEngine->getSoundGenerator(instance);
    if  (msg == nullptr)
    {
        return -1;
    }
    if (msg->getType() != SAMPLER)
    {
        return -1;
    }
    return msg->availableForMidi >> 7;
}


}


