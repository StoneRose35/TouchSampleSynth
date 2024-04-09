package ch.sr35.touchsamplesynth.model

import android.app.Activity
import android.content.Context
import android.os.Build
import android.util.Log
import ch.sr35.touchsamplesynth.BuildConfig
import ch.sr35.touchsamplesynth.TAG
import ch.sr35.touchsamplesynth.TouchSampleSynthMain
import com.google.gson.GsonBuilder
import java.io.File
import java.util.Date

class SceneListP {
    val scenes=ArrayList<SceneP>()
    var screenResolutionX: Int=-1
    var screenResolutionY: Int=-1
    val exportDateTime: Date=Date()
    val model: String=Build.MODEL
    val manufacturer: String=Build.MANUFACTURER
    val product: String=Build.PRODUCT
    val touchSampleSynthVersion: String=BuildConfig.VERSION_NAME


    fun importOntoDevice(appInstance: TouchSampleSynthMain)
    {
        val currentModel =  Build.MODEL
        val currentManufacturer = Build.MANUFACTURER
        val currentProduct = Build.PRODUCT

        // no resolution conversion if everything matches
        if (currentModel == this.model &&
            currentManufacturer == this.manufacturer &&
            currentProduct == this.product)
        {
            appInstance.allScenes.clear()
            appInstance.allScenes.addAll(this.scenes)
            return
        }
        val screenWidth: Int
        val screenHeight: Int
        val finalFactor: Double

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
        val factorX: Double = (screenWidth.toDouble())/(this.screenResolutionX.toDouble())
        val factorY: Double = (screenHeight.toDouble())/(this.screenResolutionY.toDouble())
        finalFactor = if (factorX < factorY) factorX else factorY



        this.scenes.flatMap { scn -> scn.touchElements }.forEach {
            it.posX = (it.posX.toDouble()*finalFactor).toInt()
            it.posY = (it.posY.toDouble()*finalFactor).toInt()
            it.width = (it.width.toDouble()*finalFactor).toInt()
            it.height = (it.height.toDouble()*finalFactor).toInt()
        }
        appInstance.allScenes.clear()
        appInstance.allScenes.addAll(this.scenes)
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
    }
}