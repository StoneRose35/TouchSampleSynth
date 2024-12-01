package ch.sr35.touchsamplesynth.build
import java.io.File

enum class FunctionType {
    SETTER,
    GETTER
}
class FunctionDescription {
    var type: FunctionType=FunctionType.GETTER
    var functionName: String=""
    var cFunctionName: String=""
    var argumentType: String=""
    var returnType: String=""
}

class HeaderParser {

    var fileName = ""
    private val filenameSoundGeneratorEnum = "../app/src/main/cpp/SoundGenerator.h"
    var className = ""
    var magicNr: Int = -1

    private val dirtyTypeMapping: Map<String,String> = mapOf(Pair("uint32_t","jint")
                                                ,Pair("uint8_t","jbyte")
                                                ,Pair("float","jfloat")
                                                ,Pair("bool","jboolean")
                                                ,Pair("int","jboolean")
                                                ,Pair("float*","jfloatArray")
                                                ,Pair("float**","jfloatArray")
                                                ,Pair("uint32_t*","jintArray")
                                                ,Pair("uint32_t**","jintArray"))



    fun parseHeaderForProperties(): List<FunctionDescription>
    {
        val properties = ArrayList<FunctionDescription>()
        val file = File(fileName)
        var headerString = file.readText()
        val commentPattern = Regex("""/\*.*?\*/""", RegexOption.DOT_MATCHES_ALL)
        headerString = headerString.replace(commentPattern,"")
        headerString = headerString.replace(Regex("//.*"),"")
        headerString = headerString.replace("\r","")
            .replace("\n","")
            .replace("\t","")
            .replace(Regex("\\s+")," ")
        val clazzname = Regex("class\\s?([a-zA-Z0-9_]*)\\s?:\\s?(public|private|protected)\\s?MusicalSoundGenerator\\s?\\{").find(headerString)
        if (clazzname == null)
        {
            return properties
        }
        className = clazzname.groups[1]?.value.toString()

        val fileSoundGeneratorEnum = File(filenameSoundGeneratorEnum)
        var soundGeneratorEnum = fileSoundGeneratorEnum.readText()
        soundGeneratorEnum = soundGeneratorEnum.replace(commentPattern, "")
        soundGeneratorEnum = soundGeneratorEnum.replace(Regex("//.*"),"")
        soundGeneratorEnum = soundGeneratorEnum.replace("\r","")
            .replace("\n","")
            .replace("\t","")
            .replace(Regex("\\s+")," ")

        val sgEnumPattern = Regex("enum\\sSoundGeneratorType\\s?\\{(.*)\\}")
        val sgEnumMatch = sgEnumPattern.find(soundGeneratorEnum)
            ?: throw Exception("enum SoundGeneratorType not found in $filenameSoundGeneratorEnum")
        val classNameAsCConstant = className.replace(Regex("[A-Z]"), "_$0").uppercase().substring(1)
        sgEnumMatch.groups.get(1)?.value?.let { match ->
            val enumValues = match.split(",")
            val valueAndNumber = enumValues.stream()
                .map { enumValue -> enumValue.replace(" ", "").split("=") }
                .filter { van -> van[0] == classNameAsCConstant }
                .toList()
            if (valueAndNumber.isNotEmpty()) {
                magicNr = valueAndNumber[0][1].toInt()
            }
        }
        if (magicNr == -1)
        {
            throw Exception("magic number for $className not found in $filenameSoundGeneratorEnum, define one using the class name in screaming snake case")
        }


        val headerPattern = Regex("public:([\\s\\t\\r\\na-zA-Z0-9()_*;~,]*)(}|private:|protected:)?")
        val fieldPattern = Regex("\\s?([a-z0-9_]*)\\s([a-zA-Z0-9_]*)\\((\\s?(const)?[\\sa-zA-Z0-9_,*]*)\\)\\s?(override|const)?")
        val publicFieldsMatch = headerPattern.find(headerString)
        val publicFields = publicFieldsMatch?.groups?.get(1)?.value
        publicFields?.let {
            val strippedPublicFields = it.replace("\r","")
                .replace("\n","")
                .replace("\t","")
                .replace(Regex("\\s+")," ")
            val fieldsList = strippedPublicFields.split(";")
            for (field in fieldsList)
            {
                val fieldMatch = fieldPattern.find(field)
                if (fieldMatch != null) {
                    if (!fieldMatch.groups.get(5)?.value.equals("override")) {
                        val functionName = fieldMatch.groups.get(2)?.value
                        var functionType = ""
                        if (functionName != null && functionName.startsWith("set")) {
                            functionType = "set"
                        } else if (functionName != null && functionName.startsWith("get")) {
                            functionType = "get"
                        }
                        val arguments = fieldMatch.groups.get(3)?.value
                        if (!arguments.isNullOrEmpty()) {
                            val argumentsList = arguments.split(",")
                            val argument = argumentsList[0]
                            val argumentsStripped = argument.replace("const", "")
                            val argumentTypeAndName =
                                argumentsStripped.split(" ").filter { argsstripped -> argsstripped.isNotEmpty() }
                                    .toTypedArray()
                            val argumentType = argumentTypeAndName[0]
                            val jniType = dirtyTypeMapping[argumentType]
                            if (arguments.length == 2 && !(functionType == "set" && jniType != null && (jniType == "jfloatArray" || jniType == "jintArray"))) {
                                throw Exception("unknown function signature: $field")
                            }
                            if (jniType != null && functionName != null && (functionType == "set" || functionType == "get") && functionName != className) {
                                FunctionDescription().also { fd ->
                                    fd.type = if (functionType == "set") FunctionType.SETTER else FunctionType.GETTER
                                    fd.functionName = functionName
                                    fd.argumentType = jniType
                                    fd.returnType = "jboolean"
                                    properties.add(fd)
                                }
                            }
                        } else {
                            val returnType = fieldMatch.groups.get(1)?.value
                            val jniReturnType = dirtyTypeMapping[returnType]
                            if (jniReturnType == null && functionType == "get") {
                                throw Exception("void return type for getter: $field")
                            }
                            if (functionName != null && functionName != className && functionType == "get") {
                                FunctionDescription().also { fd ->
                                    fd.type = FunctionType.GETTER
                                    fd.functionName = functionName
                                    fd.argumentType = ""
                                    fd.returnType = jniReturnType!!
                                    properties.add(fd)
                                }
                            }
                        }
                    }
                }
            }

        }
        return properties
    }

    fun obtainMagicNr(): Int
    {
        val fileSoundGeneratorEnum = File(filenameSoundGeneratorEnum)
        var soundGeneratorEnum = fileSoundGeneratorEnum.readText()
        val commentPattern = Regex("""/\*.*?\*/""", RegexOption.DOT_MATCHES_ALL)
        soundGeneratorEnum = soundGeneratorEnum.replace(commentPattern, "")
        soundGeneratorEnum = soundGeneratorEnum.replace(Regex("//.*"),"")
        soundGeneratorEnum = soundGeneratorEnum.replace("\r","")
            .replace("\n","")
            .replace("\t","")
            .replace(Regex("\\s+")," ")

        val sgEnumPattern = Regex("enum\\sSoundGeneratorType\\s?\\{(.*)\\}")
        val sgEnumMatch = sgEnumPattern.find(soundGeneratorEnum)
            ?: throw Exception("enum SoundGeneratorType not found in $filenameSoundGeneratorEnum")
        val classNameAsCConstant = className.replace(Regex("[A-Z]"), "_$0").uppercase().substring(1)
        sgEnumMatch.groups.get(1)?.value?.let { match ->
            val enumValues = match.split(",")
            val valueAndNumber = enumValues.stream()
                .map { enumValue -> enumValue.replace(" ", "").split("=") }
                .filter { van -> van[0] == classNameAsCConstant }
                .toList()
            if (valueAndNumber.isNotEmpty()) {
                return valueAndNumber[0][1].toInt()
            }
            else
            {
                val newMagicNr = enumValues.stream().map { ev -> ev[1].digitToInt() }.max(Comparator.naturalOrder()).get() + 1
                val soundGeneratorHContent = StringBuilder()
                var enumContent = ""
                for (enumValue in enumValues)
                {
                    enumContent += "    ${enumValue[0]} = ${enumValue[1]},\n"
                }
                enumContent += "    $classNameAsCConstant = ${newMagicNr}\n"
                soundGeneratorHContent.append(TEMPLATE_SOUND_GENERATOR_H.format(enumContent))
                fileSoundGeneratorEnum.writeText(soundGeneratorHContent.toString())
                return newMagicNr
            }
        }
        return -1
    }


    companion object {
        const val TEMPLATE_SOUND_GENERATOR_H = """
//
// Created by philipp on 03.09.23.
//

#ifndef TOUCHSAMPLESYNTH_SOUNDGENERATOR_H
#define TOUCHSAMPLESYNTH_SOUNDGENERATOR_H


class SoundGenerator {
public:
    virtual float getNextSample();
    virtual int getType();
};

enum SoundGeneratorType{
%1${'$'}s
};


#endif //TOUCHSAMPLESYNTH_SOUNDGENERATOR_H

"""

    }
}