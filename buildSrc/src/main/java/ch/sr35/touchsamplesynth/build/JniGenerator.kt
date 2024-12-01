package ch.sr35.touchsamplesynth.build

class JniGenerator {

    var className = ""


    fun generateJniFile(props: List<FunctionDescription>): String
    {
        var res = ""
        res += TEMPLATE_HEAD.format(className)
        for (prop in props) {
            if (prop.type == FunctionType.SETTER) {
                if (prop.argumentType.contains("Array")) {
                    val argtype = prop.argumentType.replace("Array", "").replace("j","")
                    res += TEMPLATE_SETTER_ARRAY.format(className, argtype, argtype.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }, prop.functionName)
                } else {
                    res += TEMPLATE_SETTER.format(className, prop.argumentType, prop.functionName)
                }
            } else if (prop.type == FunctionType.GETTER) {
                if (prop.argumentType.contains("Array")) {
                    val rettype = prop.argumentType.replace("Array", "").replace("j","")
                    res += TEMPLATE_GETTER_ARRAY.format(className, rettype, rettype.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }, prop.functionName)
                }
                else {
                    res += TEMPLATE_GETTER.format(
                        className,
                        prop.returnType,
                        prop.functionName,
                        defaultValues[prop.returnType]
                    )
                }
            }
            else
            {
                res += TEMPLATE_OTHER.format(className, prop.argumentType, prop.functionName, prop.cFunctionName)
            }
        }
        res += TEMPLATE_TAIL.format(className)
        return res
    }
    companion object {
        // template for a setter function, argument: Class Name, argument type, function name
        const val TEMPLATE_SETTER = """ 
JNIEXPORT jboolean JNICALL
Java_ch_sr35_touchsamplesynth_audio_voices_%1${'$'}sK_%3${'$'}s(JNIEnv *env, jobject me,%2${'$'}s val) {
    auto msg = getAudioEngine()->getSoundGeneratorFromJni<%1${'$'}s>(env, me);
    if (msg != nullptr) {
        msg->%3${'$'}s(val);
        return true;
    }
    return false;
}
"""

        // template for a getter function, argument: Class Name, return type, function name, default value
        const val TEMPLATE_GETTER = """
JNIEXPORT %2${'$'}s JNICALL
Java_ch_sr35_touchsamplesynth_audio_voices_%1${'$'}sK_%3${'$'}s(JNIEnv *env, jobject me) {
    auto msg = getAudioEngine()->getSoundGeneratorFromJni<%1${'$'}s>(env, me);
    if (msg != nullptr) {
        return msg->%3${'$'}s();
    }
    return %4${'$'}s;
}
"""

        // template for a setter function for arrays, argument: Class Name, argument type, argument type first letter upperscale, function name
        // useage example TEMPLATE_SETTER_ARRAY.format("Sampler","float","Float","setSample""
        const val TEMPLATE_SETTER_ARRAY = """
JNIEXPORT jboolean JNICALL
Java_ch_sr35_touchsamplesynth_audio_voices_%1${'$'}sK_%4${'$'}s(JNIEnv *env, jobject me,
                   j%2${'$'}sArray array_data) {
    auto msg = getAudioEngine()->getSoundGeneratorFromJni<%1${'$'}s>(env, me);
    if (msg != nullptr) {
        jsize data_length = env->GetArrayLength(array_data);
        j%2${'$'}s *arrayPtr = env->Get%3${'$'}sArrayElements(array_data, nullptr);
        msg->%4${'$'}s(arrayPtr, data_length);
        return true;
    }
    return false;
}
"""

        // template for a getter function for arrays, argument: Class Name, return type, return type first letter upperscale, function name
        // useage example TEMPLATE_GETTER_ARRAY.format("Sampler","float","Float","getSample""
        const val TEMPLATE_GETTER_ARRAY = """
JNIEXPORT j%2${'$'}sArray JNICALL
Java_ch_sr35_touchsamplesynth_audio_voices_%1${'$'}sK_%4${'$'}s(JNIEnv *env, jobject me) {
    %2${'$'}s * arrayPtr;
    j%2${'$'}sArray arrayData;
    auto msg = getAudioEngine()->getSoundGeneratorFromJni<%1${'$'}s>(env, me);
    if (msg != nullptr) {
        uint32_t arrayLength = msg->%4${'$'}s(&arrayPtr);
        arrayData = env->New%3${'$'}sArray(arrayLength);
        j%2${'$'}s *jArrayPtr = env->Get%3${'$'}sArrayElements(arrayData, nullptr);
        for (uint32_t c = 0; c < arrayLength; c++) {
            *(jArrayPtr + c) = *(arrayPtr + c);
        }
        return arrayData;
    }
    return nullptr;
}
"""

        // template for a getter function, argument: Class Name, argument type, function name, c function name
        const val TEMPLATE_OTHER = """
JNIEXPORT jboolean JNICALL
Java_ch_sr35_touchsamplesynth_audio_voices_%1${'$'}sK_%3${'$'}s(JNIEnv *env, jobject me,%2${'$'}s val) {
    auto msg = getAudioEngine()->getSoundGeneratorFromJni<%1${'$'}s>(env, me);
    if (msg != nullptr) {
        msg->%4${'$'}s(val);
        return true;
    }
    return false;
}
"""

        const val TEMPLATE_HEAD = """

#include <jni.h>
#include "AudioEngine.h"
#include "%1${'$'}s.h"
extern "C"
{
"""

        const val TEMPLATE_TAIL = """

}
"""
        val defaultValues: Map<String,String> = mapOf(
            Pair("jfloat","-1.0f"),
            Pair("jbyte","-1"),
            Pair("jboolean","false"),
            Pair("jint","-1"),
            Pair("jfloatArray","nullptr"),
            Pair("jintArray","nullptr"))
    }
}