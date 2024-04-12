package ch.sr35.touchsamplesynth.model

import android.app.Activity
import android.content.Context
import android.os.Build
import android.util.Log
import ch.sr35.touchsamplesynth.BuildConfig
import ch.sr35.touchsamplesynth.SCENES_FILE_NAME
import ch.sr35.touchsamplesynth.TAG
import ch.sr35.touchsamplesynth.TouchSampleSynthMain
import com.google.gson.GsonBuilder
import com.google.gson.JsonParseException
import com.google.gson.JsonSyntaxException
import java.io.File
import java.util.Date

enum class importMode
{
    ADD,
    REPLACE,
    NONE
}

enum class importDoneFlag
{
    DO_NOT_CHANGE,
    SET,
    UNSET
}
class SceneListP {
    val scenes=ArrayList<SceneP>()
    var screenResolutionX: Int=-1
    var screenResolutionY: Int=-1
    private var exportDateTime: Date=Date()
    var model: String=Build.MODEL
    private var manufacturer: String=Build.MANUFACTURER
    private var product: String=Build.PRODUCT
    var touchSampleSynthVersion: String=BuildConfig.VERSION_NAME
    var installDone: Boolean = false


    fun importOntoDevice(appInstance: TouchSampleSynthMain,importMode: importMode,importDoneFlag: importDoneFlag)
    {
        val currentModel =  Build.MODEL
        val currentManufacturer = Build.MANUFACTURER
        val currentProduct = Build.PRODUCT

        val screenWidth: Int
        val screenHeight: Int
        // no resolution conversion if everything matches
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.Q) {
            screenWidth =
                appInstance.windowManager.currentWindowMetrics.bounds.width()
            screenHeight =
                appInstance.windowManager.currentWindowMetrics.bounds.height()
        }
        else
        {
            screenWidth = appInstance.windowManager.defaultDisplay.width
            screenHeight = appInstance.windowManager.defaultDisplay.height
        }
        if (! (this.screenResolutionX== screenWidth &&
            this.screenResolutionY == screenHeight))
        {
            val finalFactor: Double


            val factorX: Double = (screenWidth.toDouble())/(this.screenResolutionX.toDouble())
            val factorY: Double = (screenHeight.toDouble())/(this.screenResolutionY.toDouble())
            finalFactor = if (factorX < factorY) factorX else factorY

            this.scenes.flatMap { scn -> scn.touchElements }.forEach {
                it.posX = (it.posX.toDouble()*finalFactor).toInt()
                it.posY = (it.posY.toDouble()*finalFactor).toInt()
                it.width = (it.width.toDouble()*finalFactor).toInt()
                it.height = (it.height.toDouble()*finalFactor).toInt()
            }
        }
        when (importDoneFlag)
        {
            ch.sr35.touchsamplesynth.model.importDoneFlag.DO_NOT_CHANGE -> {
            }
            ch.sr35.touchsamplesynth.model.importDoneFlag.SET -> {
                installDone = true
            }
            ch.sr35.touchsamplesynth.model.importDoneFlag.UNSET ->
            {
                installDone = false
            }
        }
        if (importMode == ch.sr35.touchsamplesynth.model.importMode.REPLACE) {
            appInstance.allScenes.clear()
        }
        else if (importMode == ch.sr35.touchsamplesynth.model.importMode.NONE)
        {
            scenes.clear()
        }
        model = currentModel
        manufacturer = currentManufacturer
        product = currentProduct
        touchSampleSynthVersion = BuildConfig.VERSION_NAME
        exportDateTime = Date()
        appInstance.allScenes.addAll(this.scenes)
    }

    fun exportAsJson(fileName: String, context: Context): Boolean
    {
        try {
            val mainDir = (context.filesDir.absolutePath)
            val gson = GsonBuilder().apply {
                registerTypeAdapter(PersistableInstrument::class.java,PersistableInstrumentDeserializer())
                setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
            }.create()
            val screenWidth: Int
            val screenHeight: Int
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.Q) {
                screenWidth =
                    (context as Activity).windowManager.currentWindowMetrics.bounds.width()
                screenHeight =
                    context.windowManager.currentWindowMetrics.bounds.height()
            } else {
                screenWidth = (context as Activity).windowManager.defaultDisplay.width
                screenHeight = context.windowManager.defaultDisplay.height
            }

            screenResolutionX = screenWidth
            screenResolutionY = screenHeight
            scenes.addAll((context as TouchSampleSynthMain).allScenes)
            val jsonOut = gson.toJson(this)
            Log.i(TAG, "exporting scenes as json")
            val f = File(mainDir + File.separator + fileName)
            if (f.exists()) {
                f.delete()
            }
            f.writeText(jsonOut)
        }
        catch (_: Exception)
        {
            return false
        }
        return true
    }



    companion object {
        fun exportAsJson(fileName: String, context: Context): Boolean
        {
            try {
                val mainDir = (context.filesDir.absolutePath)
                val gson = GsonBuilder().apply {
                    registerTypeAdapter(PersistableInstrument::class.java,PersistableInstrumentDeserializer())
                    setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
                }.create()
                val screenWidth: Int
                val screenHeight: Int
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.Q) {
                    screenWidth =
                        (context as Activity).windowManager.currentWindowMetrics.bounds.width()
                    screenHeight =
                        context.windowManager.currentWindowMetrics.bounds.height()
                } else {
                    screenWidth = (context as Activity).windowManager.defaultDisplay.width
                    screenHeight = context.windowManager.defaultDisplay.height
                }
                val sceneList = SceneListP()
                sceneList.screenResolutionX = screenWidth
                sceneList.screenResolutionY = screenHeight
                sceneList.scenes.addAll((context as TouchSampleSynthMain).allScenes)
                val jsonOut = gson.toJson(sceneList)
                Log.i(TAG, "exporting scenes as json")
                val f = File(mainDir + File.separator + fileName)
                if (f.exists()) {
                    f.delete()
                }
                f.writeText(jsonOut)
            }
            catch (_: Exception)
            {
                return false
            }
            return true
        }

        fun importFromJson(context: Context): SceneListP?
        {
            val gson=GsonBuilder().apply {
                registerTypeAdapter(PersistableInstrument::class.java,PersistableInstrumentDeserializer())
                setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
            }.create()
            val f = File((context as TouchSampleSynthMain).filesDir, SCENES_FILE_NAME)
            if (f.exists())
            {
                val jsondata=f.readText()
                try {
                    val jsonobj = gson.fromJson(jsondata, SceneListP::class.java)
                    return jsonobj
                } catch (e: Exception)
                {
                    when(e) {is JsonSyntaxException, is JsonParseException -> {
                        return null
                    }
                    }
                }
            }
            return null
        }
    }
}