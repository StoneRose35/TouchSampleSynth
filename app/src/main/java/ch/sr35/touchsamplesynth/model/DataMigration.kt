package ch.sr35.touchsamplesynth.model

import androidx.core.graphics.rotationMatrix
import ch.sr35.touchsamplesynth.BuildConfig
import com.google.gson.JsonArray
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
                    else
                    {
                        return null
                    }
                    startVersion = currentVersion
                }
            }
            return updatedData
        }

        val updatersList= listOf(
            Updater1() as DataUpdater,
           TrivialUpdater(Version(1,9,0),Version(1,9,1)),
            TrivialUpdater(Version(1,9,1),Version(1,9,2)),
            TrivialUpdater(Version(1,9,2),Version(1,9,3)),
            Updater5() as DataUpdater,
            Updater6() as DataUpdater
            )
    }

    private class TrivialUpdater(versionFrom: Version?,versionTo: Version?): DataUpdater(versionFrom,versionTo)
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


    private class Updater5: DataUpdater(versionFrom = Version(1,9,3),versionTo = Version(1,9,4)) {
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


            val scenes = rootObj.getAsJsonArray("scenes")
            scenes.flatMap { scn -> (scn as JsonObject).getAsJsonArray("instruments") }.forEach {
                val isMonophonic = (it as JsonObject)["isMonophonic"].asBoolean
                it.remove("isMonophonic")
                if (isMonophonic)
                {
                    it.addProperty("polyphonyDefinition","MONOPHONIC")
                    it.addProperty("nVoices",1)
                }
                else
                {
                    it.addProperty("polyphonyDefinition","POLY_SATURATE")
                    it.addProperty("nVoices",4)
                }
            }
            return rootObj.toString()
        }

    }


    // TODO Data migration to 1.10.0
    // touchelement.note to touchelement.notes (single value to array)
    // add className to all instruments
    // add actionAmountToPitchBend with value 0
    // change actiondirs: HORIZONTAL_LEFT_RIGHT to: horizontalToActionB=false and HORIZONTAL_LR_VERTICAL_UD
    // HORIZONTAL_RIGHT_LEFT to: horizontalToActionB=false and HORIZONTAL_RL_VERTICAL_UD
    // VERTICAL_UP_DOWN to horizontalToActionB=true and VERTICAL_UD_HORIZONTAL_LR
    // VERTICAL_DOWN_UP to horizontalToActionB=true and VERTICAL_DU_HORIZONTAL_LR
    // change midiCC to midiCCA, add midiCCB (! critical change regarding functionality!)
    private class Updater6: DataUpdater(versionFrom = Version(1,9,4),versionTo = Version(1,10,0)) {
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

            rootObj.getAsJsonArray("scenes")
                .flatMap { scn -> (scn as JsonObject).getAsJsonArray("touchElements") }
                .forEach { te ->
                    val n = (te as JsonObject).get("note")
                    te.remove("note")
                    val notes = JsonArray()
                    notes.add(n.asNumber)
                    te.add("notes",notes)

                    val midiCC = te["midiCC"].asNumber.toInt()
                    val midiCCB: Int
                    if (midiCC >= 0 && midiCC < 127)
                    {
                        midiCCB = midiCC + 1
                    }
                    else
                    {
                        midiCCB = midiCC - 1
                    }
                    te.addProperty("midiCCA",midiCC)
                    te.addProperty("midiCCB",midiCCB)
                    te.remove("midiCC")
            }

            rootObj.getAsJsonArray("scenes")
                .flatMap { scn -> (scn as JsonObject).getAsJsonArray("instruments") }
                .forEach {  i ->
                    if ((i as JsonObject).has("loopStart")){
                        i.addProperty("className","ch.sr35.touchsamplesynth.model.SamplerP")
                    }
                    else if ((i as JsonObject).has("resonance")) {
                        i.addProperty("className","ch.sr35.touchsamplesynth.model.SimpleSubtractiveSynthP")
                    }
                    else {
                        i.addProperty("className","ch.sr35.touchsamplesynth.model.SineMonoSynthP")
                    }
                    i.addProperty("actionAmountToPitchBend", "0.0f")
                }

            val allInstruments = rootObj.getAsJsonArray("scenes").flatMap { scn -> (scn as JsonObject).getAsJsonArray("instruments") }
            rootObj.getAsJsonArray("scenes")
                .flatMap { scn -> (scn as JsonObject).getAsJsonArray("touchElements") }
                .forEach {  te ->
                    val matchingInstrument = allInstruments.first { i ->
                        (i as JsonObject).get("id").asString == (te as JsonObject).get("soundGeneratorId").asString
                    }
                    val currentOrientation = (te as JsonObject).get("actionDir").asString
                    when (currentOrientation)
                    {
                        "HORIZONTAL_LEFT_RIGHT" ->
                        {
                            (matchingInstrument as JsonObject).addProperty("horizontalToActionB",false)
                            te.remove("actionDir")
                            te.addProperty("actionDir","HORIZONTAL_LR_VERTICAL_UD")
                        }
                        "HORIZONTAL_RIGHT_LEFT" ->
                        {
                            (matchingInstrument as JsonObject).addProperty("horizontalToActionB",false)
                            te.remove("actionDir")
                            te.addProperty("actionDir","HORIZONTAL_RL_VERTICAL_UD")
                        }
                        "VERTICAL_UP_DOWN" ->
                        {
                            (matchingInstrument as JsonObject).addProperty("horizontalToActionB",true)
                            te.remove("actionDir")
                            te.addProperty("actionDir","HORIZONTAL_LR_VERTICAL_UD")
                        }
                        "VERTICAL_DOWN_UP" ->
                        {
                            (matchingInstrument as JsonObject).addProperty("horizontalToActionB",true)
                            te.remove("actionDir")
                            te.addProperty("actionDir","HORIZONTAL_LR_VERTICAL_DU")
                        }
                    }
                }


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

