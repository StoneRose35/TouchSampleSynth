//
// Created by philipp on 17.01.25.
//

#include <jni.h>
#include <cmath>
#include "maths.h"
extern "C"
{

JNIEXPORT jfloat JNICALL
Java_ch_sr35_touchsamplesynth_audio_tools_Maths_toDb(JNIEnv *env, jobject thiz, jfloat x) {
    return toDb(x);
}

JNIEXPORT jfloat JNICALL
Java_ch_sr35_touchsamplesynth_audio_tools_Maths_toLin(JNIEnv *env, jobject thiz, jfloat x) {
    return toLin(x);
}

JNIEXPORT jfloat JNICALL
Java_ch_sr35_touchsamplesynth_audio_tools_Maths_toLn(JNIEnv *env, jobject thiz, jfloat x) {
    return toLn(x);
}
}