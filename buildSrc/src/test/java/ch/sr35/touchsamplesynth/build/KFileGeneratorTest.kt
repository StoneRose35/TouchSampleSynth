package ch.sr35.touchsamplesynth.build

import org.junit.Test
import java.nio.file.Paths

class KFileGeneratorTest {

    @Test
    fun generateKFileTest()
    {
        val parser = HeaderParser()
        val absPath = Paths.get("").toAbsolutePath()
        parser.fileName = "$absPath/../app/src/main/cpp/Sampler.h"
        val props =parser.parseHeaderForProperties()
        val gen = KFileGenerator()
        gen.className = parser.className
        gen.magicNr = parser.magicNr
        val kfile = gen.generateKFile(props)
        println(kfile)
    }

}