package ch.sr35.touchsamplesynth.build


class KFileGenerator {

    var className: String = ""
    var magicNr: Int = 0

    val dirtyTypeMapping: Map<String,String> = mapOf(
        Pair("jint","Int")
        ,Pair("jbyte","Byte")
        ,Pair("jfloat","Float")
        ,Pair("jboolean","Boolean")
        ,Pair("jfloatArray","FloatArray")
        ,Pair("jintArray","IntArray"))


    fun generateKFile(props: List<FunctionDescription>): String
    {
        val res = StringBuilder()
        res.append(TEMPLATE_HEADER.format(className.lowercase(),className))
        for (prop in props) {
            if (prop.type == FunctionType.SETTER) {
                res.append("    external fun ${prop.functionName}(v: ${dirtyTypeMapping[prop.argumentType]}): Boolean\n")
            }
            else if (prop.type == FunctionType.GETTER) {
                if (prop.argumentType.isNotEmpty()) {
                    res.append("    external fun ${prop.functionName}(): ${dirtyTypeMapping[prop.argumentType]}\n")
                }
                else
                {
                    res.append("    external fun ${prop.functionName}(): ${dirtyTypeMapping[prop.returnType]}\n")
                }
            }
        }
        res.append("\n\n    override fun copyParamsTo(other: MusicalSoundGenerator) {\n" +
                "        super.copyParamsTo(other)\n")
        var firstParam = true
        for (prop in props.stream().filter { p -> p.type == FunctionType.SETTER }) {
            if (firstParam) {
                res.append("        (other as ${className}K).${prop.functionName}(this.${prop.functionName.replaceFirst("set","get")}())\n")
                firstParam = false
            }
            else
            {
                res.append("        other.${prop.functionName}(this.${
                    prop.functionName.replaceFirst(
                        "set",
                        "get"
                    )
                })\n")
            }
        }
        res.append("    }\n")
        res.append(TEMPLATE_TAIL.format(magicNr, className))
        return res.toString()
    }

    companion object {
        // first argument: class name lowercase, second argument: class name capitalized
        const val TEMPLATE_HEADER = """
package ch.sr35.touchsamplesynth.audio.voices

import android.content.Context
import androidx.appcompat.content.res.AppCompatResources
import ch.sr35.touchsamplesynth.R
import ch.sr35.touchsamplesynth.audio.AudioEngineK
import ch.sr35.touchsamplesynth.audio.MusicalSoundGenerator

class %2${'$'}s(context: Context): MusicalSoundGenerator() {

    override fun bindToAudioEngine() {
        val audioEngine= AudioEngineK()
        if (instance == (-1).toByte()) {
            instance = audioEngine.addSoundGenerator(MAGIC_NR)
        }
    }

    override fun hashCode(): Int {
        return (MAGIC_NR*1000) + instance
    }

    val icon= AppCompatResources.getDrawable(context, R.drawable.%1${'$'}s)"""

        // first argument: magic nr, second argument: class name capitalized
        const val TEMPLATE_TAIL = """ 
    override fun equals(other: Any?): Boolean {
        if (other is %2${'$'}sK)
        {
            return other.hashCode() == this.hashCode()
        }
        return false
    }

    companion object
    {
        const val MAGIC_NR = %1${'$'}s
    }

}
    """

    }
}