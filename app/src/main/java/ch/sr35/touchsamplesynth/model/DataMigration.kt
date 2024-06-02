package ch.sr35.touchsamplesynth.model

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonParseException
import com.google.gson.JsonParser
import com.google.gson.JsonSyntaxException
import java.util.UUID


class Version(val major: Int,val minor: Int,val revision: Int) {

    override fun equals(other: Any?): Boolean {
        if (other is Version)
        {
            return other.major == major && other.minor == minor && other.revision == revision
        }
        return false
    }

    override fun hashCode(): Int {
        var result = major
        result = 31 * result + minor
        result = 31 * result + revision
        return result
    }

    override fun toString(): String {
        return "${major}.${minor}.${revision}"
    }
}
abstract class DataUpdater protected constructor(val versionFrom: Version?=null, val versionTo: Version?=null)
{


    abstract fun processData(jsonData: String): String

    companion object
    {
        fun copyJsonProperty(propertyName: String,src: JsonObject,dest: JsonObject)
        {
            dest.add(propertyName,src.get(propertyName))
        }

        fun generateErrorJson(msg: String): String
        {
            val tg = JsonObject()
            tg.addProperty("error", msg)
            return tg.toString()
        }

        fun getVersion(jsonData: String): Version?
        {
            val rootElement = JsonParser.parseString(jsonData)
            val targetJson = JsonObject()
            val rootObj = rootElement.asJsonObject
            if (!rootObj.has("touchSampleSynthVersion")) {
                return null
            }
            val srcVersion = rootObj.get("touchSampleSynthVersion").asString
            val versionNumbers = srcVersion.split(".")
            if (versionNumbers.size != 3)
            {
                return null
            }
            return Version(versionNumbers[0].toInt(),versionNumbers[1].toInt(),versionNumbers[2].toInt())
        }

        val updatersList= listOf(Updater1() as DataUpdater)
    }

    /**
     * Update from 1.8.6 to 1.9.0:
     * * replace nVoices as number with isMonophonic as boolean
     * * replace full instrument declaration with uuid in touchElement
     * * remove voiceNr property in touchElement
     */
    private class Updater1: DataUpdater(Version(1,8,6), Version(1,9,0))
    {

        override fun processData(jsonData: String): String {
            try {
                val rootElement = JsonParser.parseString(jsonData)
                val targetJson = JsonObject()
                val rootObj = rootElement.asJsonObject
                if (!rootObj.has("touchSampleSynthVersion")) {
                    return generateErrorJson("could not find touchSampleSynthVersion")
                }
                val srcVersion = rootObj.get("touchSampleSynthVersion").asString
                val versionNumbers = srcVersion.split(".")
                if (versionNumbers.size != 3)
                {
                    return generateErrorJson("version string could not be parsed")
                }
                val versionFromFile = Version(versionNumbers[0].toInt(),versionNumbers[1].toInt(),versionNumbers[2].toInt())
                if (versionFromFile != this.versionFrom)
                {
                    return generateErrorJson("ile version: $versionFromFile differs from source version of the updater: ${versionFrom}")
                }
                copyJsonProperty("exportDateTime",rootObj,targetJson)
                copyJsonProperty("manufacturer",rootObj,targetJson)
                copyJsonProperty("model",rootObj,targetJson)
                copyJsonProperty("product",rootObj,targetJson)
                copyJsonProperty("screenResolutionX",rootObj,targetJson)
                copyJsonProperty("screenResolutionY",rootObj,targetJson)
                copyJsonProperty("installDone",rootObj,targetJson)
                targetJson.addProperty("touchSampleSynthVersion",versionTo.toString())
                val scenes = rootObj.getAsJsonArray("scenes").deepCopy()
                targetJson.add("scenes",scenes)

                scenes.forEach {
                        scn ->
                    val instr = scn.asJsonObject.getAsJsonArray("instruments")
                    val instrUuids = HashMap<JsonObject,String>()
                    instr.forEach { i ->
                        val instrUuid = UUID.randomUUID().toString()
                        instrUuids[i.deepCopy().asJsonObject] = instrUuid
                        val nv = i.asJsonObject.get("nVoices").asNumber.toInt()
                        if (nv == 1)
                        {
                            i.asJsonObject.addProperty("isMonophonic",true)
                        }
                        else if (nv > 1)
                        {
                            i.asJsonObject.addProperty("isMonophonic",false)
                        }
                        else
                        {
                            return generateErrorJson("illegal number of voices at instrument $i")
                        }
                        i.asJsonObject.remove("nVoices")
                        i.asJsonObject.addProperty("id",instrUuid)
                    }
                    val touchElements = scn.asJsonObject.getAsJsonArray("touchElements")
                    touchElements.forEach {
                            te ->
                        val sg = te.asJsonObject.getAsJsonObject("soundGenerator").deepCopy()
                        te.asJsonObject.remove("soundGenerator")
                        te.asJsonObject.addProperty("soundGeneratorId",instrUuids.filter { el -> JsonComparator.compareJsonObject(el.key,sg) }.entries.first().value)
                        te.asJsonObject.remove("voiceNr")
                    }
                }
                return targetJson.toString()
            }

            catch (e: JsonParseException)
            {
                return "{\"error\": \"JsonParseException: ${e.message}\"}"
            }
            catch (e: JsonSyntaxException)
            {
                return "{\"error\": \"JsonSyntaxException: ${e.message}\"}"
            }
        }
    }

}

class JsonComparator
{
    companion object{
        fun compareJsonObject(a: JsonElement, b: JsonElement): Boolean
        {
            var res = true
            if (a.isJsonArray && b.isJsonArray && a.asJsonArray.size() == b.asJsonArray.size())
            {
                a.asJsonArray.forEach {
                    aEntry ->
                    res = res && b.asJsonArray.any {
                        bEntry ->
                        compareJsonObject(aEntry,bEntry)
                    }

                }
            }
            else if (a.isJsonObject && b.isJsonObject && a.asJsonObject.size() == b.asJsonObject.size())
            {
                val aEntrySet = a.asJsonObject.entrySet()
                val bKeySet = b.asJsonObject.keySet()
                aEntrySet.forEach {
                    aentry ->
                    if (!bKeySet.contains(aentry.key))
                    {
                        return false
                    }
                    res = res && compareJsonObject(aentry.value,b.asJsonObject.get(aentry.key))
                }
            }
            else if (a.isJsonPrimitive && b.isJsonPrimitive)
            {
                return  a.asJsonPrimitive.equals(b.asJsonPrimitive)
            }
            return res

        }
    }
}

