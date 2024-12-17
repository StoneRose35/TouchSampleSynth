package ch.sr35.touchsamplesynth.build

import java.io.File

class CMakeListsUpdater {
    var rootPath: String = ""
    var className: String = ""
    var writeFile = false

    fun updateCMakeLists(): String
    {
        val cmakelists = File("$rootPath/app/src/main/cpp/CMakeLists.txt")
        var cmakeContent = cmakelists.readText()
        val addLibraryMatcher = Regex("add_library\\(\\$\\{CMAKE_PROJECT_NAME\\}\\sSHARED\\n(.*?)\\)",RegexOption.DOT_MATCHES_ALL)
        val currentMatch = addLibraryMatcher.find(cmakeContent)
        currentMatch?.let {
            var listContent = it.groups[1]?.value
            val classnameAsLower=Regex("[A-Z]").replace(className, "-$0").lowercase().substring(1)
            listContent +=  "$classnameAsLower-jni.cpp\n        "
            cmakeContent = addLibraryMatcher.replace(cmakeContent,"add_library(\\$\\{CMAKE_PROJECT_NAME} SHARED\n$listContent)")
        }
        if (writeFile) {
            cmakelists.writeText(cmakeContent)
        }
        return cmakeContent
    }
}