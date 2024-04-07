package ch.sr35.touchsamplesynth.model

import android.os.Build
import ch.sr35.touchsamplesynth.TouchSampleSynthMain
import java.util.Date

class SceneListP {
    val scenes=ArrayList<SceneP>()
    var screenResolutionX: Int=-1
    var screenResolutionY: Int=-1
    val exportDateTime: Date=Date()
    val model: String=Build.MODEL
    val manufacturer: String=Build.MANUFACTURER
    val product: String=Build.PRODUCT


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
        val factorX: Double
        val factorY: Double
        val finalFactor: Double
        var offsetX=0
        var offsetY=0
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
        factorX = (screenWidth.toDouble())/(this.screenResolutionX.toDouble())
        factorY = (screenHeight.toDouble())/(this.screenResolutionY.toDouble())
        finalFactor = if (factorX < factorY) factorX else factorY
        if (finalFactor > 1)
        {
            offsetX = (screenWidth-this.screenResolutionX)/2
            offsetY = (screenHeight-this.screenResolutionY)/2
        }
        this.scenes.flatMap { scn -> scn.touchElements }.forEach {
            it.posX = (it.posX.toDouble()*finalFactor).toInt() + offsetX
            it.posY = (it.posY.toDouble()*finalFactor).toInt() + offsetY
            it.width = (it.width.toDouble()*finalFactor).toInt()
            it.height = (it.height.toDouble()*finalFactor).toInt()
        }
        appInstance.allScenes.clear()
        appInstance.allScenes.addAll(this.scenes)
    }
}