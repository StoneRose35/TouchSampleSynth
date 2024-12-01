package ch.sr35.touchsamplesynth.build

import java.io.File

class IconGenerator {
    var className: String = ""

    fun generateIcon()
    {
        val classname_lowercase=className.lowercase()
        val templateFile = File("src/main/res/drawable/newinstrument.xml")
        val newiconFile = File("src/main/res/drawable/$classname_lowercase.xml")
        templateFile.copyTo(newiconFile)
    }
}