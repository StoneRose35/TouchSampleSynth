package ch.sr35.touchsamplesynth.build

import java.io.File

class AudioEngineGenerator {



    fun processAudioEngine(classNames: List<String>): String
    {
        val cases = StringBuilder()

        for (className in classNames) {
            val classNameAsCConstant = className.replace(Regex("[A-Z]"), "_$0").uppercase().substring(1)
            cases.append(TEMPLATE_ADD_SOUND_GENERATOR_CASE.format(classNameAsCConstant,className))
        }
        val addSoundGeneratorFunction = TEMPLATE_ADD_SOUND_GENERATOR.format(cases.toString())
        //audioEngineImplementation.replace(addSoundGeneratorPattern,addSoundGeneratorFunction)
        val fileContent = StringBuilder()
        fileContent.append("#include \"AudioEngine.h\"\n")
        for (className in classNames) {
            fileContent.append("#include \"$className.h\"\n")
        }
        fileContent.append("\n")
        fileContent.append(addSoundGeneratorFunction)
        return fileContent.toString()

    }

    companion object {
        val TEMPLATE_ADD_SOUND_GENERATOR = """
int8_t AudioEngine::addSoundGenerator(SoundGeneratorType sgt) {
    MusicalSoundGenerator *  sg;
    int8_t idx=nSoundGenerators;
    // get next free slot
    for (int8_t c=0;c<nSoundGenerators;c++)
    {
        if(*(soundGenerators+c)==nullptr)
        {
            idx=c;
            break;
        }
    }
    switch(sgt)
    {
        %1${'$'}s
    }

    return idx; 
}
        """
        // first argument: instrument name in screaming snake case, second argument: instrument name in camel case
        val TEMPLATE_ADD_SOUND_GENERATOR_CASE = """
        case %1${'$'}s:
            sg=new %2${'$'}s((float) samplingRate);
            sg->midiInputPort = midiInputPort;
            if (idx < nSoundGenerators) {
                soundGenerators[idx] = sg;
            }
            break;
        """
    }


}