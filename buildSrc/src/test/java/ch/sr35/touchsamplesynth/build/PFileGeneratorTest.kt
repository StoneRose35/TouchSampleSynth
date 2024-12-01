package ch.sr35.touchsamplesynth.build

import org.junit.Test
import java.nio.file.Paths

class PFileGeneratorTest {

    @Test
    fun generatePFileTest()
    {
        val parser = HeaderParser()
        val absPath = Paths.get("").toAbsolutePath()
        parser.fileName = "$absPath/../app/src/main/cpp/Sampler.h"
        val props =parser.parseHeaderForProperties()
        val gen = PFileGenerator()
        gen.className = parser.className
        val pfile = gen.generatePFile(props)
        println(pfile)
    }

}