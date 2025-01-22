

#include <jni.h>
#include "AudioEngine.h"
#include "SimpleSubtractiveSynth.h"
extern "C"
{
 
JNIEXPORT jboolean JNICALL
Java_ch_sr35_touchsamplesynth_audio_voices_SimpleSubtractiveSynthK_setVolumeAttack(JNIEnv *env, jobject me,jfloat val) {
    auto msg = getAudioEngine()->getSoundGeneratorFromJni<SimpleSubtractiveSynth>(env, me);
    if (msg != nullptr) {
        msg->setVolumeAttack(val);
        return true;
    }
    return false;
}

JNIEXPORT jfloat JNICALL
Java_ch_sr35_touchsamplesynth_audio_voices_SimpleSubtractiveSynthK_getVolumeAttack(JNIEnv *env, jobject me) {
    auto msg = getAudioEngine()->getSoundGeneratorFromJni<SimpleSubtractiveSynth>(env, me);
    if (msg != nullptr) {
        return msg->getVolumeAttack();
    }
    return -1.0f;
}
 
JNIEXPORT jboolean JNICALL
Java_ch_sr35_touchsamplesynth_audio_voices_SimpleSubtractiveSynthK_setVolumeDecay(JNIEnv *env, jobject me,jfloat val) {
    auto msg = getAudioEngine()->getSoundGeneratorFromJni<SimpleSubtractiveSynth>(env, me);
    if (msg != nullptr) {
        msg->setVolumeDecay(val);
        return true;
    }
    return false;
}

JNIEXPORT jfloat JNICALL
Java_ch_sr35_touchsamplesynth_audio_voices_SimpleSubtractiveSynthK_getVolumeDecay(JNIEnv *env, jobject me) {
    auto msg = getAudioEngine()->getSoundGeneratorFromJni<SimpleSubtractiveSynth>(env, me);
    if (msg != nullptr) {
        return msg->getVolumeDecay();
    }
    return -1.0f;
}
 
JNIEXPORT jboolean JNICALL
Java_ch_sr35_touchsamplesynth_audio_voices_SimpleSubtractiveSynthK_setVolumeSustain(JNIEnv *env, jobject me,jfloat val) {
    auto msg = getAudioEngine()->getSoundGeneratorFromJni<SimpleSubtractiveSynth>(env, me);
    if (msg != nullptr) {
        msg->setVolumeSustain(val);
        return true;
    }
    return false;
}

JNIEXPORT jfloat JNICALL
Java_ch_sr35_touchsamplesynth_audio_voices_SimpleSubtractiveSynthK_getVolumeSustain(JNIEnv *env, jobject me) {
    auto msg = getAudioEngine()->getSoundGeneratorFromJni<SimpleSubtractiveSynth>(env, me);
    if (msg != nullptr) {
        return msg->getVolumeSustain();
    }
    return -1.0f;
}
 
JNIEXPORT jboolean JNICALL
Java_ch_sr35_touchsamplesynth_audio_voices_SimpleSubtractiveSynthK_setVolumeRelease(JNIEnv *env, jobject me,jfloat val) {
    auto msg = getAudioEngine()->getSoundGeneratorFromJni<SimpleSubtractiveSynth>(env, me);
    if (msg != nullptr) {
        msg->setVolumeRelease(val);
        return true;
    }
    return false;
}

JNIEXPORT jfloat JNICALL
Java_ch_sr35_touchsamplesynth_audio_voices_SimpleSubtractiveSynthK_getVolumeRelease(JNIEnv *env, jobject me) {
    auto msg = getAudioEngine()->getSoundGeneratorFromJni<SimpleSubtractiveSynth>(env, me);
    if (msg != nullptr) {
        return msg->getVolumeRelease();
    }
    return -1.0f;
}
 
JNIEXPORT jboolean JNICALL
Java_ch_sr35_touchsamplesynth_audio_voices_SimpleSubtractiveSynthK_setFilterAttack(JNIEnv *env, jobject me,jfloat val) {
    auto msg = getAudioEngine()->getSoundGeneratorFromJni<SimpleSubtractiveSynth>(env, me);
    if (msg != nullptr) {
        msg->setFilterAttack(val);
        return true;
    }
    return false;
}

JNIEXPORT jfloat JNICALL
Java_ch_sr35_touchsamplesynth_audio_voices_SimpleSubtractiveSynthK_getFilterAttack(JNIEnv *env, jobject me) {
    auto msg = getAudioEngine()->getSoundGeneratorFromJni<SimpleSubtractiveSynth>(env, me);
    if (msg != nullptr) {
        return msg->getFilterAttack();
    }
    return -1.0f;
}
 
JNIEXPORT jboolean JNICALL
Java_ch_sr35_touchsamplesynth_audio_voices_SimpleSubtractiveSynthK_setFilterDecay(JNIEnv *env, jobject me,jfloat val) {
    auto msg = getAudioEngine()->getSoundGeneratorFromJni<SimpleSubtractiveSynth>(env, me);
    if (msg != nullptr) {
        msg->setFilterDecay(val);
        return true;
    }
    return false;
}

JNIEXPORT jfloat JNICALL
Java_ch_sr35_touchsamplesynth_audio_voices_SimpleSubtractiveSynthK_getFilterDecay(JNIEnv *env, jobject me) {
    auto msg = getAudioEngine()->getSoundGeneratorFromJni<SimpleSubtractiveSynth>(env, me);
    if (msg != nullptr) {
        return msg->getFilterDecay();
    }
    return -1.0f;
}
 
JNIEXPORT jboolean JNICALL
Java_ch_sr35_touchsamplesynth_audio_voices_SimpleSubtractiveSynthK_setFilterSustain(JNIEnv *env, jobject me,jfloat val) {
    auto msg = getAudioEngine()->getSoundGeneratorFromJni<SimpleSubtractiveSynth>(env, me);
    if (msg != nullptr) {
        msg->setFilterSustain(val);
        return true;
    }
    return false;
}

JNIEXPORT jfloat JNICALL
Java_ch_sr35_touchsamplesynth_audio_voices_SimpleSubtractiveSynthK_getFilterSustain(JNIEnv *env, jobject me) {
    auto msg = getAudioEngine()->getSoundGeneratorFromJni<SimpleSubtractiveSynth>(env, me);
    if (msg != nullptr) {
        return msg->getFilterSustain();
    }
    return -1.0f;
}
 
JNIEXPORT jboolean JNICALL
Java_ch_sr35_touchsamplesynth_audio_voices_SimpleSubtractiveSynthK_setFilterRelease(JNIEnv *env, jobject me,jfloat val) {
    auto msg = getAudioEngine()->getSoundGeneratorFromJni<SimpleSubtractiveSynth>(env, me);
    if (msg != nullptr) {
        msg->setFilterRelease(val);
        return true;
    }
    return false;
}

JNIEXPORT jfloat JNICALL
Java_ch_sr35_touchsamplesynth_audio_voices_SimpleSubtractiveSynthK_getFilterRelease(JNIEnv *env, jobject me) {
    auto msg = getAudioEngine()->getSoundGeneratorFromJni<SimpleSubtractiveSynth>(env, me);
    if (msg != nullptr) {
        return msg->getFilterRelease();
    }
    return -1.0f;
}

JNIEXPORT jfloat JNICALL
Java_ch_sr35_touchsamplesynth_audio_voices_SimpleSubtractiveSynthK_getFilterEnvelopeLevel(JNIEnv *env, jobject me) {
    auto msg = getAudioEngine()->getSoundGeneratorFromJni<SimpleSubtractiveSynth>(env, me);
    if (msg != nullptr) {
        return msg->getFilterEnvelopeLevel();
    }
    return -1.0f;
}
 
JNIEXPORT jboolean JNICALL
Java_ch_sr35_touchsamplesynth_audio_voices_SimpleSubtractiveSynthK_setFilterEnvelopeLevel(JNIEnv *env, jobject me,jfloat val) {
    auto msg = getAudioEngine()->getSoundGeneratorFromJni<SimpleSubtractiveSynth>(env, me);
    if (msg != nullptr) {
        msg->setFilterEnvelopeLevel(val);
        return true;
    }
    return false;
}
 
JNIEXPORT jboolean JNICALL
Java_ch_sr35_touchsamplesynth_audio_voices_SimpleSubtractiveSynthK_setOsc1Type(JNIEnv *env, jobject me,jbyte val) {
    auto msg = getAudioEngine()->getSoundGeneratorFromJni<SimpleSubtractiveSynth>(env, me);
    if (msg != nullptr) {
        msg->setOsc1Type(val);
        return true;
    }
    return false;
}

JNIEXPORT jbyte JNICALL
Java_ch_sr35_touchsamplesynth_audio_voices_SimpleSubtractiveSynthK_getOsc1Type(JNIEnv *env, jobject me) {
    auto msg = getAudioEngine()->getSoundGeneratorFromJni<SimpleSubtractiveSynth>(env, me);
    if (msg != nullptr) {
        return msg->getOsc1Type();
    }
    return -1;
}
 
JNIEXPORT jboolean JNICALL
Java_ch_sr35_touchsamplesynth_audio_voices_SimpleSubtractiveSynthK_setOsc2Type(JNIEnv *env, jobject me,jbyte val) {
    auto msg = getAudioEngine()->getSoundGeneratorFromJni<SimpleSubtractiveSynth>(env, me);
    if (msg != nullptr) {
        msg->setOsc2Type(val);
        return true;
    }
    return false;
}

JNIEXPORT jbyte JNICALL
Java_ch_sr35_touchsamplesynth_audio_voices_SimpleSubtractiveSynthK_getOsc2Type(JNIEnv *env, jobject me) {
    auto msg = getAudioEngine()->getSoundGeneratorFromJni<SimpleSubtractiveSynth>(env, me);
    if (msg != nullptr) {
        return msg->getOsc2Type();
    }
    return -1;
}

JNIEXPORT jbyte JNICALL
Java_ch_sr35_touchsamplesynth_audio_voices_SimpleSubtractiveSynthK_getOsc2Octave(JNIEnv *env, jobject me) {
    auto msg = getAudioEngine()->getSoundGeneratorFromJni<SimpleSubtractiveSynth>(env, me);
    if (msg != nullptr) {
        return msg->getOsc2Octave();
    }
    return -1;
}
 
JNIEXPORT jboolean JNICALL
Java_ch_sr35_touchsamplesynth_audio_voices_SimpleSubtractiveSynthK_setOsc2Octave(JNIEnv *env, jobject me,jbyte val) {
    auto msg = getAudioEngine()->getSoundGeneratorFromJni<SimpleSubtractiveSynth>(env, me);
    if (msg != nullptr) {
        msg->setOsc2Octave(val);
        return true;
    }
    return false;
}

JNIEXPORT jfloat JNICALL
Java_ch_sr35_touchsamplesynth_audio_voices_SimpleSubtractiveSynthK_getOsc2Detune(JNIEnv *env, jobject me) {
    auto msg = getAudioEngine()->getSoundGeneratorFromJni<SimpleSubtractiveSynth>(env, me);
    if (msg != nullptr) {
        return msg->getOsc2Detune();
    }
    return -1.0f;
}
 
JNIEXPORT jboolean JNICALL
Java_ch_sr35_touchsamplesynth_audio_voices_SimpleSubtractiveSynthK_setOsc2Detune(JNIEnv *env, jobject me,jfloat val) {
    auto msg = getAudioEngine()->getSoundGeneratorFromJni<SimpleSubtractiveSynth>(env, me);
    if (msg != nullptr) {
        msg->setOsc2Detune(val);
        return true;
    }
    return false;
}

JNIEXPORT jfloat JNICALL
Java_ch_sr35_touchsamplesynth_audio_voices_SimpleSubtractiveSynthK_getOsc2Volume(JNIEnv *env, jobject me) {
    auto msg = getAudioEngine()->getSoundGeneratorFromJni<SimpleSubtractiveSynth>(env, me);
    if (msg != nullptr) {
        return msg->getOsc2Volume();
    }
    return -1.0f;
}
 
JNIEXPORT jboolean JNICALL
Java_ch_sr35_touchsamplesynth_audio_voices_SimpleSubtractiveSynthK_setOsc2Volume(JNIEnv *env, jobject me,jfloat val) {
    auto msg = getAudioEngine()->getSoundGeneratorFromJni<SimpleSubtractiveSynth>(env, me);
    if (msg != nullptr) {
        msg->setOsc2Volume(val);
        return true;
    }
    return false;
}

JNIEXPORT jfloat JNICALL
Java_ch_sr35_touchsamplesynth_audio_voices_SimpleSubtractiveSynthK_getOsc1PulseWidth(JNIEnv *env, jobject me) {
    auto msg = getAudioEngine()->getSoundGeneratorFromJni<SimpleSubtractiveSynth>(env, me);
    if (msg != nullptr) {
        return msg->getOsc1PulseWidth();
    }
    return -1.0f;
}
 
JNIEXPORT jboolean JNICALL
Java_ch_sr35_touchsamplesynth_audio_voices_SimpleSubtractiveSynthK_setOsc1PulseWidth(JNIEnv *env, jobject me,jfloat val) {
    auto msg = getAudioEngine()->getSoundGeneratorFromJni<SimpleSubtractiveSynth>(env, me);
    if (msg != nullptr) {
        msg->setOsc1PulseWidth(val);
        return true;
    }
    return false;
}

JNIEXPORT jfloat JNICALL
Java_ch_sr35_touchsamplesynth_audio_voices_SimpleSubtractiveSynthK_getOsc2PulseWidth(JNIEnv *env, jobject me) {
    auto msg = getAudioEngine()->getSoundGeneratorFromJni<SimpleSubtractiveSynth>(env, me);
    if (msg != nullptr) {
        return msg->getOsc2PulseWidth();
    }
    return -1.0f;
}
 
JNIEXPORT jboolean JNICALL
Java_ch_sr35_touchsamplesynth_audio_voices_SimpleSubtractiveSynthK_setOsc2PulseWidth(JNIEnv *env, jobject me,jfloat val) {
    auto msg = getAudioEngine()->getSoundGeneratorFromJni<SimpleSubtractiveSynth>(env, me);
    if (msg != nullptr) {
        msg->setOsc2PulseWidth(val);
        return true;
    }
    return false;
}
 
JNIEXPORT jboolean JNICALL
Java_ch_sr35_touchsamplesynth_audio_voices_SimpleSubtractiveSynthK_setCutoff(JNIEnv *env, jobject me,jfloat val) {
    auto msg = getAudioEngine()->getSoundGeneratorFromJni<SimpleSubtractiveSynth>(env, me);
    if (msg != nullptr) {
        msg->setCutoff(val);
        return true;
    }
    return false;
}

JNIEXPORT jfloat JNICALL
Java_ch_sr35_touchsamplesynth_audio_voices_SimpleSubtractiveSynthK_getCutoff(JNIEnv *env, jobject me) {
    auto msg = getAudioEngine()->getSoundGeneratorFromJni<SimpleSubtractiveSynth>(env, me);
    if (msg != nullptr) {
        return msg->getCutoff();
    }
    return -1.0f;
}
 
JNIEXPORT jboolean JNICALL
Java_ch_sr35_touchsamplesynth_audio_voices_SimpleSubtractiveSynthK_setResonance(JNIEnv *env, jobject me,jfloat val) {
    auto msg = getAudioEngine()->getSoundGeneratorFromJni<SimpleSubtractiveSynth>(env, me);
    if (msg != nullptr) {
        msg->setResonance(val);
        return true;
    }
    return false;
}

JNIEXPORT jfloat JNICALL
Java_ch_sr35_touchsamplesynth_audio_voices_SimpleSubtractiveSynthK_getResonance(JNIEnv *env, jobject me) {
    auto msg = getAudioEngine()->getSoundGeneratorFromJni<SimpleSubtractiveSynth>(env, me);
    if (msg != nullptr) {
        return msg->getResonance();
    }
    return -1.0f;
}


}

extern "C"
JNIEXPORT jboolean JNICALL
Java_ch_sr35_touchsamplesynth_audio_voices_SimpleSubtractiveSynthK_setInitialCutoff(JNIEnv *env,
                                        jobject thiz,
                                        jfloat ic) {
    auto msg = getAudioEngine()->getSoundGeneratorFromJni<SimpleSubtractiveSynth>(env, thiz);
    if (msg != nullptr) {
        msg->initialCutoff = ic;
        return true;
    }
    return false;
}
extern "C"
JNIEXPORT jfloat JNICALL
Java_ch_sr35_touchsamplesynth_audio_voices_SimpleSubtractiveSynthK_getInitialCutoff(JNIEnv *env,
                                                                                    jobject thiz) {
    auto msg = getAudioEngine()->getSoundGeneratorFromJni<SimpleSubtractiveSynth>(env, thiz);
    if (msg != nullptr) {
        return msg->initialCutoff;
    }
    return -1.0f;
}