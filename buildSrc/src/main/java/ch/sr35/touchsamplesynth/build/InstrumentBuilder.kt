package ch.sr35.touchsamplesynth.build

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import java.io.File
import java.nio.file.Paths

class InstrumentBuilder {

    companion object {
        fun generateAllInstrumentFiles() {
            val classNames = ArrayList<String>()
            var parser = HeaderParser()
            File("${Paths.get("").toAbsolutePath()}/../app/src/main/cpp").walk().forEach { f ->
                var props: List<FunctionDescription> = ArrayList()
                if (f.extension == "h") {
                    // check if the header contains a class derived from MusicalSoundGenerator
                    parser = HeaderParser()
                    parser.fileName = f.absolutePath
                    props = parser.parseHeaderForProperties()
                }
                val rootPath = f.absolutePath.replace("/" + f.name, "").replace("/src/main/cpp", "")
                if (props.isNotEmpty()) {
                    classNames.add(parser.className)
                    // check which files are already generated
                    val kfile =
                        File(rootPath + "/src/main/java/ch/sr35/touchsamplesynth/audio/voices/${f.nameWithoutExtension}K.kt")
                    val ifile =
                        File(rootPath + "/src/main/java/ch/sr35/touchsamplesynth/audio/instruments/${f.nameWithoutExtension}I.kt")
                    val pfile =
                        File(rootPath + "/src/main/java/ch/sr35/touchsamplesynth/model/${f.nameWithoutExtension}P.kt")
                    val fragment =
                        File(rootPath + "/src/main/java/ch/sr35/touchsamplesynth/fragments/${f.nameWithoutExtension}Fragment.kt")
                    val iconFile =
                        File(rootPath + "/src/main/res/drawable/${f.nameWithoutExtension.lowercase()}.xml")
                    // check whether a the SoundGeneratorType already contains the new instrument, add and generate a magic number, retrieve the magic number otherwise
                    val magicNr = parser.obtainMagicNr()
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
                        }.generateFragment())
                    }
                    if (!iconFile.exists()) {
                        IconGenerator().also { it.className = parser.className }.generateIcon()
                    }
                }
            }
        }
    }
}

abstract class InstrumentBuilderTask: DefaultTask() {
    @TaskAction
    fun generateAllInstrumentFiles() {
        InstrumentBuilder.generateAllInstrumentFiles()
    }
}
