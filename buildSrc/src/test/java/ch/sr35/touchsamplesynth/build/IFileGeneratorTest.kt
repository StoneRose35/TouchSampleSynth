package ch.sr35.touchsamplesynth.build

import org.junit.Test
import java.nio.file.Paths

class IFileGeneratorTest {

    @Test
    fun generateIFileTest()
    {
        val parser = HeaderParser()
        val absPath = Paths.get("").toAbsolutePath()
        parser.fileName = "$absPath/../app/src/main/cpp/Sampler.h"
        val props =parser.parseHeaderForProperties()
        val gen = IFileGenerator()
        gen.className = parser.className
        val ifile = gen.generateIFile(props)
        println(ifile)
    }


}