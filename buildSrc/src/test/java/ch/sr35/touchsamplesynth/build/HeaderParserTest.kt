package ch.sr35.touchsamplesynth.build

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*

import org.junit.Test
import java.nio.file.Paths

class HeaderParserTest
{

    @Test
    fun testHeaderParser()
    {
        val parser = HeaderParser()
        val absPath = Paths.get("").toAbsolutePath()
        parser.fileName = "$absPath/../app/src/main/cpp/Sampler.h"
        val props = parser.parseHeaderForProperties()
        assertThat(props.size,greaterThan(0))

    }

}