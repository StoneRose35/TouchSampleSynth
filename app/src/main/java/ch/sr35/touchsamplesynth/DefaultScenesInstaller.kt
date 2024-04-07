package ch.sr35.touchsamplesynth

import android.os.Environment
import ch.sr35.touchsamplesynth.model.PersistableInstrument
import ch.sr35.touchsamplesynth.model.PersistableInstrumentDeserializer
import ch.sr35.touchsamplesynth.model.SamplerP
import ch.sr35.touchsamplesynth.model.SceneListP
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.FileOutputStream

enum class DefaultSceneInstallerCode
{
    NO_DCIM_FOLDER,
    NO_SAMPLES_DOWNLOADED,
    INSTALLED_WITH_SAMPLES,
    INSTALLED_WITHOUT_SAMPLES
}

class NestedFolder(val name:String)
{

    var children = ArrayList<NestedFolder>()

    fun checkFolderStructure(rootFolder: File,result: Array<Boolean>)
    {
        if (rootFolder.exists() && rootFolder.isDirectory &&  rootFolder.name == name)
        {
            children.forEach {
                it.checkFolderStructure(rootFolder.listFiles { f -> it.name == f.name && f.isDirectory }!!.first(),result)
            }
        }
        else
        {
            result[0]=false
        }
    }
}


class DefaultScenesInstaller(val appContext: TouchSampleSynthMain) {

    private val samplesFolderLvl1: NestedFolder
    private var percentageDownloaded: Double=0.0
    init {

        val foldersInHiHat = arrayListOf(NestedFolder("Bell"),
            NestedFolder("Closed"),
            NestedFolder("Crash"),
            NestedFolder("Double"),
            NestedFolder("Extra Hita"),
            NestedFolder("Open"),
            NestedFolder("Pedal"))
        val folderHiHat = NestedFolder("Hi-Hat")
        folderHiHat.children = foldersInHiHat
        val foldersInRide = arrayListOf(NestedFolder("Damped"),
            NestedFolder("Ride"),
            NestedFolder("Ride Bell"),
            NestedFolder("Ride Crash"),NestedFolder("Short"))
        val folderRide = NestedFolder("Ride")
        folderRide.children = foldersInRide
        val SAMPLES_FOLDER_NAMES_CYMBALS = arrayListOf(NestedFolder("Cowbell"),
            NestedFolder("Crash"),
            folderRide,
            folderHiHat)
        val folderCymbal = NestedFolder("Cymbals")
        folderCymbal.children = SAMPLES_FOLDER_NAMES_CYMBALS
        val folderInToms = arrayListOf(NestedFolder("Tom High"),
            NestedFolder("Tom Low"),
            NestedFolder("Tome Mid")
        )
        val folderTom = NestedFolder("Tom")
        folderTom.children=folderInToms
        val SAMPLES_FOLDER_LVL1 = arrayListOf(folderCymbal,
            NestedFolder("Kick"),
            NestedFolder("Snare"),
            NestedFolder("Sticks"),
            folderTom)
        samplesFolderLvl1 = NestedFolder("Samples")
        samplesFolderLvl1.children = SAMPLES_FOLDER_LVL1


    }

    fun installDefaultScene(withExternalSamples: Boolean): DefaultSceneInstallerCode
    {
        // get default scenes Asset
        val presetsFile = appContext.assets.open("defaultPresets.json").reader().readText()
        // check is samplelibrary from https://wavbvkery.com/wp-content/uploads/WAVBVKERY-Acoustic-Drum-Samples.zip
        // is in DCIM folder and is unpacked
        if (withExternalSamples)
        {
            val rootExt=Environment.getExternalStorageDirectory()
            val dcimFolder = File(rootExt.absolutePath + "/DCIM")
            if (!dcimFolder.exists())
            {
                return DefaultSceneInstallerCode.NO_DCIM_FOLDER
            }
            val samplesFound = arrayOf(false)
            if (dcimFolder.listFiles()?.any { f -> f.isDirectory && f.name == "Samples" } == true)
            {
                val sampleFolder = dcimFolder.listFiles { f -> f.name == "Samples" && f.isDirectory }!!.first()
                samplesFound[0]=true
                samplesFolderLvl1.checkFolderStructure(sampleFolder,samplesFound)
            }
            if (!samplesFound[0])
            {
                // if not, check internet connection and return status matching status
                // dialog should be
                // "download royalty-free drum samples from https://wavbvkery.com?"
                return DefaultSceneInstallerCode.NO_SAMPLES_DOWNLOADED
            }
            val gson=GsonBuilder().apply {registerTypeAdapter(PersistableInstrument::class.java,PersistableInstrumentDeserializer()) }.create()
            val presets = gson.fromJson(presetsFile,SceneListP::class.java)
            presets.importOntoDevice(appContext)
            return DefaultSceneInstallerCode.INSTALLED_WITH_SAMPLES

        }
        else
        {
            // parse default presets json and remove each scene that contains a sampler"
            val gson=GsonBuilder().apply {registerTypeAdapter(PersistableInstrument::class.java,PersistableInstrumentDeserializer()) }.create()
            val presets = gson.fromJson(presetsFile,SceneListP::class.java)
            presets.scenes.removeIf { scn -> scn.instruments.any { i -> i is SamplerP } }
            presets.importOntoDevice(appContext)
            return DefaultSceneInstallerCode.INSTALLED_WITHOUT_SAMPLES
        }

    }

    fun getPercentageDownloaded(): Double
    {
        return percentageDownloaded
    }

    fun downloadAndUnpackDrumSamples()
    {
        percentageDownloaded=0.0
        val okHttpClient=OkHttpClient()
        val dldFolder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val samplesZipped = File(dldFolder.absolutePath + "/WAVBVKERY-Acoustic-Drum-Samples.zip")
        val downloadBuffer = ByteArray(1024*1024*1) // 1 MB Buffer
        var bytesRead: Int
        if (samplesZipped.exists())
        {
            samplesZipped.delete()
        }
        samplesZipped.createNewFile()
        val zippedFileStream = FileOutputStream(samplesZipped)
        val req = Request.Builder()
            .url("https://wavbvkery.com/wp-content/uploads/WAVBVKERY-Acoustic-Drum-Samples.zip")
            .build()
        try {
            val resp = okHttpClient.newCall(req).execute()
            if (resp.code==200) {
                val totalLength = resp.body!!.contentLength()
                var totalBytesRead=0L
                bytesRead = 0
               while (bytesRead > -1) {
                   bytesRead = resp.body!!.byteStream().read(downloadBuffer)
                   totalBytesRead += bytesRead
                   percentageDownloaded = totalBytesRead.toDouble() / totalLength.toDouble()
                   zippedFileStream.write(downloadBuffer,0,bytesRead)
                }
                resp.body!!.close()
            }
            resp.close()
            zippedFileStream.close()

        }
        catch (_: Exception)
        {

        }
    }

    fun checkIfScenesAreAvailable(): Boolean
    {
        val mainDir = (appContext.filesDir.absolutePath)
        val gson= GsonBuilder().apply { registerTypeAdapter(
            PersistableInstrument::class.java,
            PersistableInstrumentDeserializer()
        ) }.create()
        val f = File(mainDir + File.separator + "touchSampleSynthScenes1.json")
        if (f.exists())
        {
            val jsondata=f.readText()
            try {
                gson.fromJson(jsondata, SceneListP::class.java)
                return true
            }
            catch (_: Exception)
            {
                f.delete()
            }
        }
        return false
    }
}
