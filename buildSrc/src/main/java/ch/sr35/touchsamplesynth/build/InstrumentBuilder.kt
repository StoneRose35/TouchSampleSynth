package ch.sr35.touchsamplesynth.build

import org.gradle.api.DefaultTask
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.FileTree
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputFiles
import org.gradle.api.tasks.TaskAction
import java.io.File

class InstrumentBuilder {

    var rootPath = ""

        fun generateAllInstrumentFiles() {
            val classNames = ArrayList<String>()
            
            File("$rootPath/app/src/main/cpp").walk().forEach { f ->
                generateInstrumentFile(f,classNames)
            }
            generateAudioEngineContent(classNames)
        }

        fun generateAudioEngineContent(classNames: ArrayList<String>)
        {
            val audioEngineGenerator = AudioEngineGenerator()
            val addSoundGeneratorFileContent = audioEngineGenerator.processAudioEngine(classNames)
            val addSoundGeneratorFile = File("$rootPath/app/src/main/cpp/AudioEngineGenerated.cpp")
            addSoundGeneratorFile.writeText(addSoundGeneratorFileContent)
        }

        fun generateInstrumentFile(f: File, classNames: ArrayList<String>)
        {
            val parser = HeaderParser()
            var props: List<FunctionDescription> = ArrayList()
            if (f.extension == "h") {
                // check if the header contains a class derived from MusicalSoundGenerator
                parser.rootPath = rootPath
                parser.fileName = f.absolutePath
                props = parser.parseHeaderForProperties()
            }
            //val rootPath = f.absolutePath.replace("/" + f.name, "").replace("/src/main/cpp", "")
            if (props.isNotEmpty()) {
                classNames.add(parser.className)
                // check which files are already generated
                val toLowerCaseRegex = Regex("[A-Z]")
                toLowerCaseRegex.replace(f.nameWithoutExtension,"-$0").lowercase().substring(1)
                val jniFileName = toLowerCaseRegex.replace(f.nameWithoutExtension,"-$0").lowercase().substring(1) + "-jni.cpp"
                val jniFile = File("$rootPath/app/src/main/cpp/$jniFileName")
                val kfile =
                    File(rootPath + "/app/src/main/java/ch/sr35/touchsamplesynth/audio/voices/${f.nameWithoutExtension}K.kt")
                val ifile =
                    File(rootPath + "/app/src/main/java/ch/sr35/touchsamplesynth/audio/instruments/${f.nameWithoutExtension}I.kt")
                val pfile =
                    File(rootPath + "/app/src/main/java/ch/sr35/touchsamplesynth/model/${f.nameWithoutExtension}P.kt")
                val fragment =
                    File(rootPath + "/app/src/main/java/ch/sr35/touchsamplesynth/fragments/${f.nameWithoutExtension}Fragment.kt")
                val iconFile =
                    File(rootPath + "/app/src/main/res/drawable/${f.nameWithoutExtension.lowercase()}.xml")
                // check whether a the SoundGeneratorType already contains the new instrument, add and generate a magic number, retrieve the magic number otherwise
                val magicNr = parser.obtainMagicNr()
                if (!jniFile.exists())
                {
                    val jniFileGenerator = JniGenerator();
                    jniFileGenerator.className = parser.className
                    jniFile.createNewFile()
                    jniFile.writeText(jniFileGenerator.generateJniFile(props))
                    val cMakeListsUpdater = CMakeListsUpdater()
                    cMakeListsUpdater.rootPath = rootPath
                    cMakeListsUpdater.className = parser.className
                    cMakeListsUpdater.writeFile = true
                    cMakeListsUpdater.updateCMakeLists()
                }

                if (!kfile.exists()) {
                    val kfileGenerator = KFileGenerator()
                    kfileGenerator.className = parser.className
                    kfileGenerator.magicNr = magicNr
                    kfile.createNewFile()
                    kfile.writeText(kfileGenerator.generateKFile(props))
                }
                if (!ifile.exists()) {
                    val ifileGenerator = IFileGenerator()
                    ifileGenerator.className = parser.className
                    ifile.createNewFile()
                    ifile.writeText(ifileGenerator.generateIFile(props))
                }
                if (!pfile.exists()) {
                    val pfileGenerator = PFileGenerator()
                    pfileGenerator.className = parser.className
                    pfile.createNewFile()
                    pfile.writeText(pfileGenerator.generatePFile(props))
                }
                if (!fragment.exists()) {
                    fragment.createNewFile()
                    fragment.writeText(FragmentGenerator().also {
                        it.className = parser.className
                        it.rootPath = rootPath
                        it.className = parser.className
                    }.generateFragment())
                }
                if (!iconFile.exists()) {
                    IconGenerator().also { it.className = parser.className }.generateIcon()
                }
            }
        }

}

abstract class InstrumentBuilderTask: DefaultTask() {
    @get:InputFiles
    val headerFiles: Iterable<File> = project.fileTree("src/main/cpp") {
        include("*.h")
    }

    @get:OutputFiles
    val outputFiles: Iterable<File> = project.fileTree("src/main/java/ch/sr35/touchsamplesynth/audio/voices") {
        include("*.kt")
    }

    @TaskAction
    fun generateAllInstrumentFiles() {

        val instrumentBuilder = InstrumentBuilder()
        instrumentBuilder.rootPath =  this.project.rootDir.absolutePath
        val classNames = ArrayList<String>()
        headerFiles.forEach {
            instrumentBuilder.generateInstrumentFile(it,classNames)
        }
        if (classNames.isNotEmpty())
        {
            instrumentBuilder.generateAudioEngineContent(classNames)
        }
    }
}
