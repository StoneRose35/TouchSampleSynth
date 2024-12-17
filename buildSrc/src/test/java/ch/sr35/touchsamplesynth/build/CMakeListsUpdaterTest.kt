package ch.sr35.touchsamplesynth.build

import org.hamcrest.MatcherAssert.assertThat
import org.junit.Assert
import org.junit.Test

class CMakeListsUpdaterTest {


    @Test
    fun updateCMakeListsTest() {
        val cmakeUpdater = CMakeListsUpdater()
        cmakeUpdater.rootPath = ".."
        cmakeUpdater.className = "FmSynth"
        val newCmakeContent = cmakeUpdater.updateCMakeLists()
        Assert.assertTrue(newCmakeContent.contains("fm-synth-jni.cpp"))

    }

}