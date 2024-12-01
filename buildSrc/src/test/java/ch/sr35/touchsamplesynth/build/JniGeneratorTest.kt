package ch.sr35.touchsamplesynth.build

import org.junit.Test
import java.nio.file.Paths

class JniGeneratorTest {

    @Test
    fun generateJniFileTest()
    {
        val parser = HeaderParser()
        val absPath = Paths.get("").toAbsolutePath()
        parser.fileName = "$absPath/../app/src/main/cpp/Sampler.h"
        val props =parser.parseHeaderForProperties()
        val gen = JniGenerator()
        gen.className = parser.className
        val jnifile = gen.generateJniFile(props)
        println(jnifile)
    }

}