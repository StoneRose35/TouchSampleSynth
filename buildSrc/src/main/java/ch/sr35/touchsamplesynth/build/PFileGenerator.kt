package ch.sr35.touchsamplesynth.build

class PFileGenerator {

    var className: String = ""

    fun generatePFile(props: List<FunctionDescription>): String
    {
        val res = StringBuilder()
        val tempStr=StringBuilder()
        res.append(TEMPLATE_HEADER.format(className))
        for (prop in props.filter { p -> p.type == FunctionType.GETTER }) {
            val propertyNameInPFile = prop.functionName
                .replaceFirst("get","")
                .replaceFirstChar {  if (it.isUpperCase()) it.lowercaseChar() else it }
            if (prop.argumentType.isNotEmpty())
            {
                tempStr.append("    private var $propertyNameInPFile: ${dirtyTypeMapping[prop.argumentType]},\n")
            }
            else {
                tempStr.append("    private var $propertyNameInPFile: ${dirtyTypeMapping[prop.returnType]}=${defaultValues[prop.returnType]},\n")
            }
        }
        res.append(tempStr.toString())
        res.append(TEMPLATE_BODY_1.format(className))
        for (prop in props.filter { p -> p.type == FunctionType.GETTER }) {
            val propertyNameInPFile = prop.functionName
                .replaceFirst("get","")
                .replaceFirstChar {  if (it.isUpperCase()) it.lowercaseChar() else it }
            res.append("            $propertyNameInPFile = i.${prop.functionName}()\n")
        }
        res.append(TEMPLATE_BODY_2.format(className))
        for (prop in props.filter { p -> p.type == FunctionType.SETTER }) {
            val propertyNameInPFile = prop.functionName
                .replaceFirst("get","")
                .replaceFirstChar {  if (it.isUpperCase()) it.lowercaseChar() else it }
            res.append("            i.${prop.functionName}(this.$propertyNameInPFile)\n")
        }
        res.append(TEMPLATE_BODY_3.format(className))

        tempStr.clear()
        for (prop in props.filter { p -> p.type == FunctionType.GETTER }) {
            val propertyNameInPFile = prop.functionName
                .replaceFirst("get","")
                .replaceFirstChar {  if (it.isUpperCase()) it.lowercaseChar() else it }
            if (dirtyTypeMapping[prop.returnType] == "Float")
            {
                tempStr.append("        this.$propertyNameInPFile.toRawBits() +\n")
            }
            else if (dirtyTypeMapping[prop.returnType]!!.contains("Array"))
            {
                tempStr.append("        this.$propertyNameInPFile.contentHashCode() +\n")
            }
            else if (dirtyTypeMapping[prop.returnType] == "Int" || dirtyTypeMapping[prop.returnType] == "Byte")
            {
                tempStr.append("        this.$propertyNameInPFile +\n")
            }
        }
        res.append(tempStr.toString().trimEnd('\n').trimEnd('+'))
        res.append(TEMPLATE_BODY_4.format(className))
        tempStr.clear()
        for (prop in props.filter { p -> p.type == FunctionType.GETTER }) {
            val propertyNameInPFile = prop.functionName
                .replaceFirst("get","")
                .replaceFirstChar {  if (it.isUpperCase()) it.lowercaseChar() else it }
            tempStr.append("            this.$propertyNameInPFile,\n")
        }
        res.append(tempStr.toString().trimEnd('\n'))
        res.append(TEMPLATE_TAIL)
        return res.toString()
    }

    companion object {
    const val TEMPLATE_HEADER = """
package ch.sr35.touchsamplesynth.model
import ch.sr35.touchsamplesynth.audio.InstrumentI
import ch.sr35.touchsamplesynth.audio.PolyphonyDefinition
import ch.sr35.touchsamplesynth.audio.instruments.%1${'$'}sI
import java.io.Serializable

class %1${'$'}sP(
"""

    const val TEMPLATE_BODY_1 = """    actionAmountToVolume: Float=0.0f,
    polyphonyDefinition: PolyphonyDefinition=PolyphonyDefinition.MONOPHONIC,
    nVoices: Int=0,
    name: String=""
): InstrumentP(actionAmountToVolume,polyphonyDefinition,nVoices,name),Serializable, Cloneable {
    override fun fromInstrument(i: InstrumentI) {
        super.fromInstrument(i)
        if (i is %1${'$'}sI)
        {
"""

        const val TEMPLATE_BODY_2 = """        }
    }

    override fun toInstrument(i: InstrumentI) {
        if (i is %1${'$'}sI)
        {
            super.toInstrument(i)
"""

        const val TEMPLATE_BODY_3 = """        }
    }

    override fun equals(other: Any?): Boolean {
        if (other is %1${'$'}sP)
        {
            return this.hashCode() == other.hashCode()
        }
        return false
    }

    override fun hashCode(): Int {
        return super.hashCode() + 
"""

        const val TEMPLATE_BODY_4 = """ 
    }

    override fun toString(): String
    {
        return "%1${'$'}s: %%s, voices: %%b".format(this.name, this.polyphonyDefinition)
    }

    override fun clone(): Any {
        return %1${'$'}sP(
"""

        const val TEMPLATE_TAIL = """ 
            this.actionAmountToVolume,
            this.polyphonyDefinition,
            this.nVoices,
            this.name
        )
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