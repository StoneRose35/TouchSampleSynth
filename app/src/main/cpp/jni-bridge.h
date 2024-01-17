//
// Created by philipp on 17.01.24.
//

#ifndef TOUCHSAMPLESYNTH_JNI_BRIDGE_H
#define TOUCHSAMPLESYNTH_JNI_BRIDGE_H
#include <jni.h>
#include "AudioEngine.h"
extern "C" MusicalSoundGenerator * getMusicalSoundGenerator(SoundGeneratorType soundGeneratorType,JNIEnv * env,jobject thiz);

#endif //TOUCHSAMPLESYNTH_JNI_BRIDGE_H
