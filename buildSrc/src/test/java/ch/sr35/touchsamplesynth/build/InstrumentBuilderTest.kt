package ch.sr35.touchsamplesynth.build

import org.junit.Test

class InstrumentBuilderTest {

    @Test
    fun generateAllInstrumentFilesTest()
    {
        val instrumentBuilder = InstrumentBuilder()
        instrumentBuilder.rootPath = ".."
        instrumentBuilder.generateAllInstrumentFiles()
    }

}
