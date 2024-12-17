package ch.sr35.touchsamplesynth.build

class IFileGenerator {

    var className: String = ""

    fun generateIFile(props: List<FunctionDescription>): String
    {
        val res = StringBuilder()
        res.append(TEMPLATE_HEADER.format(className,className.lowercase()))
        for (prop in props) {
            if (prop.type == FunctionType.SETTER) {
                res.append(TEMPLATE_SETTER.format(prop.functionName,dirtyTypeMapping[prop.argumentType],className))
            }
            else {
                if (prop.argumentType.isNotEmpty()) {
                    res.append(
                        TEMPLATE_GETTER.format(
                            prop.functionName,
                            dirtyTypeMapping[prop.argumentType],
                            className,
                            defaultValues[prop.argumentType]
                        )
                    )
                }
                else
                {
                    res.append(
                        TEMPLATE_GETTER.format(
                            prop.functionName,
                            dirtyTypeMapping[prop.returnType],
                            className,
                            defaultValues[prop.returnType]
                        )
                    )
                }
            }
        }
        res.append("\n}\n")
        return res.toString()
    }


    companion object {
        const val TEMPLATE_HEADER = """
package ch.sr35.touchsamplesynth.audio.instruments

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.appcompat.content.res.AppCompatResources
import ch.sr35.touchsamplesynth.R
import ch.sr35.touchsamplesynth.audio.instruments.InstrumentI
import ch.sr35.touchsamplesynth.audio.MusicalSoundGenerator
import ch.sr35.touchsamplesynth.audio.voices.%1${'$'}sK

class %1${'$'}sI(private val context: Context,
                     name: String
) : InstrumentI(name)  {
    val icon= AppCompatResources.getDrawable(context, R.drawable.%2${'$'}s)

    init {
        voices = ArrayList()
    }
    override fun getType(): String {
        return "%1${'$'}s"
    }

    override fun getInstrumentIcon(): Drawable? {
        return icon
    }

    override fun generateVoices(cnt: Int) {
        val doCopy = voices.isNotEmpty()
        for (i in 0 until cnt) {
            voices.add(MusicalSoundGenerator.generateAttachedInstance<%1${'$'}sK>(context))
            if (doCopy)
            {
                voices[0].copyParamsTo(voices[voices.size-1])
            }
        }
    }

        """

        // first argument: function name, second argument: return type, third argument: class name
        // fourth argument: default value
        const val TEMPLATE_GETTER = """
    fun %1${'$'}s(): %2${'$'}s
    {
        if (voices.isNotEmpty() )
        {
            return (voices[0] as %3${'$'}sK).%1${'$'}s()
        }
        return %4${'$'}s
    }
    """

        // first argument: function name, second argument: argument type, third argument: class name
        const val TEMPLATE_SETTER = """
    fun %1${'$'}s(v: %2${'$'}s)
    {
        for (voice in voices)
        {
            (voice as %3${'$'}sK).%1${'$'}s(v)
        }
    }
        """

val dirtyTypeMapping: Map<String,String> = mapOf(
            Pair("jint","Int")
            ,Pair("jbyte","Byte")
            ,Pair("jfloat","Float")
            ,Pair("jboolean","Boolean")
            ,Pair("jfloatArray","FloatArray")
            ,Pair("jintArray","IntArray"))

val defaultValues: Map<String,String> = mapOf(
            Pair("jint","0")
            ,Pair("jbyte","0")
            ,Pair("jfloat","0.0f")
            ,Pair("jboolean","False")
            ,Pair("jfloatArray","null")
            ,Pair("jintArray","null")
        )
    }
}