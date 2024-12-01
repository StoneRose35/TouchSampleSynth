package ch.sr35.touchsamplesynth.build

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import org.junit.Test
import java.io.File

class InstrumentBuilderTest {

    @Test
    fun generateAllInstrumentFilesTest()
    {
        InstrumentBuilder.generateAllInstrumentFiles()
    }

}
