package ch.sr35.touchsamplesynth.model

import ch.sr35.touchsamplesynth.BuildConfig
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

    companion object
    {
        fun fromString(string: String): Version?
        {
            val substrings = string.split(".")
            if (substrings.size==3)
            {
                return try {
                    Version(substrings[0].toInt(),substrings[1].toInt(),substrings[2].toInt())
                }
                catch (e: NumberFormatException)
                {
                    null
                }
            }
            return null
        }
    }
}
abstract class DataUpdater protected constructor(val versionFrom: Version?=null, val versionTo: Version?=null)
{


    abstract fun processData(jsonData: String): String?

    companion object
    {
        fun copyJsonProperty(propertyName: String,src: JsonObject,dest: JsonObject)
        {
            dest.add(propertyName,src.get(propertyName))
        }

        fun getVersion(jsonData: String): Version?
        {
            val rootElement = JsonParser.parseString(jsonData)
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

        fun upgradeToCurrent(jsonData: String): String?
        {
            var updatedData:String = jsonData
            var startVersion = getVersion(jsonData)
            var currentVersion: Version?=null
            val finalVersion: Version? = Version.fromString(BuildConfig.VERSION_NAME)
            while (currentVersion != finalVersion) {
                updatersList.firstOrNull { el -> el.versionFrom == startVersion }.also {
                    currentVersion = it?.versionTo
                    if (currentVersion != null) {
                        updatedData = it?.processData(updatedData).toString()
                    }
                    startVersion = currentVersion
                }
            }
            return updatedData
        }

        val updatersList= listOf(
            Updater1() as DataUpdater,
            Updater2() as DataUpdater,
            Updater3() as DataUpdater,
            Updater4() as DataUpdater,
            Updater5() as DataUpdater)
    }

    /**
     * Update from 1.8.6 to 1.9.0:
     * * replace nVoices as number with isMonophonic as boolean
     * * replace full instrument declaration with uuid in touchElement
     * * remove voiceNr property in touchElement
     */
    private class Updater1: DataUpdater(Version(1,8,6), Version(1,9,0))
    {

        override fun processData(jsonData: String): String? {
            try {
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
                val versionFromFile = Version(versionNumbers[0].toInt(),versionNumbers[1].toInt(),versionNumbers[2].toInt())
                if (versionFromFile != this.versionFrom)
                {
                    return null
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
                            return null
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
                return null
            }
            catch (e: JsonSyntaxException)
            {
                return null
            }
        }
    }

    private class Updater2: DataUpdater(versionFrom = Version(1,9,0),versionTo = Version(1,9,1))
    {
        override fun processData(jsonData: String): String? {
            val rootElement = JsonParser.parseString(jsonData)
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
            val versionFromFile = Version(versionNumbers[0].toInt(),versionNumbers[1].toInt(),versionNumbers[2].toInt())
            if (versionFromFile != this.versionFrom)
            {
                return null
            }
            rootObj.remove("touchSampleSynthVersion")
            rootObj.addProperty("touchSampleSynthVersion",versionTo.toString())
            return rootObj.toString()
        }

    }

    private class Updater3: DataUpdater(versionFrom = Version(1,9,1),versionTo = Version(1,9,2))
    {
        override fun processData(jsonData: String): String? {
            val rootElement = JsonParser.parseString(jsonData)
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
            val versionFromFile = Version(versionNumbers[0].toInt(),versionNumbers[1].toInt(),versionNumbers[2].toInt())
            if (versionFromFile != this.versionFrom)
            {
                return null
            }
            rootObj.remove("touchSampleSynthVersion")
            rootObj.addProperty("touchSampleSynthVersion",versionTo.toString())
            return rootObj.toString()
        }

    }

    private class Updater4: DataUpdater(versionFrom = Version(1,9,2),versionTo = Version(1,9,3))
    {
        override fun processData(jsonData: String): String? {
            val rootElement = JsonParser.parseString(jsonData)
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
            val versionFromFile = Version(versionNumbers[0].toInt(),versionNumbers[1].toInt(),versionNumbers[2].toInt())
            if (versionFromFile != this.versionFrom)
            {
                return null
            }
            rootObj.remove("touchSampleSynthVersion")
            rootObj.addProperty("touchSampleSynthVersion",versionTo.toString())
            return rootObj.toString()
        }
    }

    private class Updater5: DataUpdater(versionFrom = Version(1,9,3),versionTo = Version(1,9,4))
    {
        override fun processData(jsonData: String): String? {
            val rootElement = JsonParser.parseString(jsonData)
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
            val versionFromFile = Version(versionNumbers[0].toInt(),versionNumbers[1].toInt(),versionNumbers[2].toInt())
            if (versionFromFile != this.versionFrom)
            {
                return null
            }
            rootObj.remove("touchSampleSynthVersion")
            rootObj.addProperty("touchSampleSynthVersion",versionTo.toString())
            return rootObj.toString()
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

