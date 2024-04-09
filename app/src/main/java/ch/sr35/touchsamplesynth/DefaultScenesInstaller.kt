package ch.sr35.touchsamplesynth

import android.os.Handler
import android.os.Looper
import ch.sr35.touchsamplesynth.model.PersistableInstrument
import ch.sr35.touchsamplesynth.model.PersistableInstrumentDeserializer
import ch.sr35.touchsamplesynth.model.SamplerP
import ch.sr35.touchsamplesynth.model.SceneListP
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.BufferedInputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipFile
import java.util.zip.ZipInputStream

enum class DefaultSceneInstallerCode
{
    NO_SAMPLES_DOWNLOADED,
    INSTALLED_WITH_SAMPLES,
    INSTALLED_WITHOUT_SAMPLES,
    DOWNLOAD_FAILED,
    UNZIPPING_FAILED
}

const val DRUMSAMPLES_FOLDER_NAME = "WAVBVKERY-AcousticDrumSamples"
class NestedFolder(val name:String)
{

    var children = ArrayList<NestedFolder>()

    fun checkFolderStructure(rootFolder: File,result: Array<Boolean>)
    {
        if (rootFolder.exists() && rootFolder.isDirectory &&  rootFolder.name == name)
        {
            children.forEach {
                rootFolder.listFiles { f -> it.name == f.name && f.isDirectory }?.let { cf ->
                    if (cf.isNotEmpty()) {
                        it.checkFolderStructure(cf.first(), result)
                    }
                }
            }
        }
        else
        {
            result[0]=false
        }
    }
}

abstract class ProgressRunnable(var progress: Double?,var message: String?): Runnable
{
    fun setProgress(p: Double)
    {
        progress=p
        message=null
    }

    fun setProgressMessage(m: String)
    {
        progress=null
        message = m
    }

}

class DefaultScenesInstaller(val appContext: TouchSampleSynthMain) {

    val samplesFolderLvl1: NestedFolder
    private var percentageDownloaded: Double = 0.0
    val handler: Handler = Handler(Looper.getMainLooper())

    init {

        val foldersInHiHat = arrayListOf(
            NestedFolder("Bell"),
            NestedFolder("Closed"),
            NestedFolder("Crash"),
            NestedFolder("Double"),
            NestedFolder("ExtraHita"),
            NestedFolder("Open"),
            NestedFolder("Pedal")
        )
        val folderHiHat = NestedFolder("Hi-Hat")
        folderHiHat.children = foldersInHiHat
        val foldersInRide = arrayListOf(
            NestedFolder("Damped"),
            NestedFolder("Ride"),
            NestedFolder("RideBell"),
            NestedFolder("RideCrash"), NestedFolder("Short")
        )
        val folderRide = NestedFolder("Ride")
        folderRide.children = foldersInRide
        val foldersInCymbals = arrayListOf(
            NestedFolder("Cowbell"),
            NestedFolder("Crash"),
            folderRide,
            folderHiHat
        )
        val folderCymbal = NestedFolder("Cymbals")
        folderCymbal.children = foldersInCymbals
        val folderInToms = arrayListOf(
            NestedFolder("TomHigh"),
            NestedFolder("TomLow"),
            NestedFolder("TomeMid")
        )
        val folderTom = NestedFolder("Tom")
        folderTom.children = folderInToms
        val SAMPLES_FOLDER_LVL1 = arrayListOf(
            folderCymbal,
            NestedFolder("Kick"),
            NestedFolder("Snare"),
            NestedFolder("Sticks"),
            folderTom
        )
        samplesFolderLvl1 = NestedFolder(DRUMSAMPLES_FOLDER_NAME)
        samplesFolderLvl1.children = SAMPLES_FOLDER_LVL1


    }

    fun checkSampleLibrary(folderStructureToCheck: NestedFolder,directoryName: String): Boolean
    {
        val samplesFolder = appContext.filesDir
        val samplesFound = arrayOf(false)
        if (samplesFolder.listFiles()?.any { f -> f.isDirectory && f.name == directoryName } == true)
        {
            val sampleFolder = samplesFolder.listFiles { f -> f.name == directoryName && f.isDirectory }!!.first()
            samplesFound[0]=true
            folderStructureToCheck.checkFolderStructure(sampleFolder,samplesFound)
        }
        return samplesFound[0]
    }

    fun installDefaultScene(withExternalSamples: Boolean): DefaultSceneInstallerCode
    {
        // get default scenes Asset
        val presetsFile = appContext.assets.open("defaultPresets.json").reader().readText()
        // check is samplelibrary from https://wavbvkery.com/wp-content/uploads/WAVBVKERY-Acoustic-Drum-Samples.zip
        // is in DCIM folder and is unpacked
        if (withExternalSamples)
        {
            if (!checkSampleLibrary(samplesFolderLvl1, DRUMSAMPLES_FOLDER_NAME))
            {
                // if not, check internet connection and return status matching status
                // dialog should be
                // "download royalty-free drum samples from https://wavbvkery.com?"
                return DefaultSceneInstallerCode.NO_SAMPLES_DOWNLOADED
            }
            val gson=GsonBuilder().apply {registerTypeAdapter(PersistableInstrument::class.java,PersistableInstrumentDeserializer()) }.create()
            val presets = gson.fromJson(presetsFile,SceneListP::class.java)
            presets.importOntoDevice(appContext)
            SceneListP.exportAsJson("touchSampleSynth1.json",appContext)
            return DefaultSceneInstallerCode.INSTALLED_WITH_SAMPLES

        }
        else
        {
            // parse default presets json and remove each scene that contains a sampler"
            val gson=GsonBuilder().apply {
                registerTypeAdapter(PersistableInstrument::class.java,PersistableInstrumentDeserializer())
                setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
            }.create()
            val presets = gson.fromJson(presetsFile,SceneListP::class.java)
            presets.scenes.removeIf { scn -> scn.instruments.any { i -> i is SamplerP } }
            presets.importOntoDevice(appContext)
            SceneListP.exportAsJson("touchSampleSynth1.json",appContext)
            return DefaultSceneInstallerCode.INSTALLED_WITHOUT_SAMPLES
        }

    }


    fun downloadAndUnpackDrumSamples(uiCallback: ProgressRunnable?): DefaultSceneInstallerCode
    {
        percentageDownloaded=0.0
        val okHttpClient=OkHttpClient()
        val dldFolder = appContext.filesDir
        val samplesZipped = File(dldFolder.absolutePath + "/WAVBVKERY-Acoustic-Drum-Samples.zip")
        val downloadBuffer = ByteArray(1024*1024*1) // 1 MB Buffer
        var bytesRead: Int
        var returnCode = DefaultSceneInstallerCode.INSTALLED_WITH_SAMPLES
        if (!samplesZipped.exists()) {
            uiCallback?.setProgressMessage(appContext.getString(R.string.installerDownloadingSamples))
            uiCallback?.let {
                handler.post(it)
            }
            val zippedFileStream = FileOutputStream(samplesZipped)
            val req = Request.Builder()
                .url("https://wavbvkery.com/wp-content/uploads/WAVBVKERY-Acoustic-Drum-Samples.zip")
                .build()

            val resp = okHttpClient.newCall(req).execute()
            if (resp.code == 200) {
                val totalLength = resp.body!!.contentLength()
                var totalBytesRead = 0L
                bytesRead = resp.body!!.byteStream().read(downloadBuffer)
                while (bytesRead > -1) {

                    totalBytesRead += bytesRead
                    zippedFileStream.write(downloadBuffer, 0, bytesRead)
                    uiCallback?.setProgress(totalBytesRead.toDouble() / totalLength.toDouble())
                    uiCallback?.let {
                        handler.post(it)
                    }
                    bytesRead = resp.body!!.byteStream().read(downloadBuffer)
                }
                zippedFileStream.close()
                resp.body!!.close()
            }
            else
            {
                returnCode = DefaultSceneInstallerCode.DOWNLOAD_FAILED            }
        }

        val zipFileInputStream = FileInputStream(samplesZipped)
        val unzipStream = ZipInputStream(BufferedInputStream(zipFileInputStream))
        val byteBfr = ByteArray(8192)
        var zipEntry: ZipEntry?
        uiCallback?.setProgressMessage(appContext.getString(R.string.installerUnpackingSamples))
        val totalDecompressedSize = ZipFile(samplesZipped).use { zipFile ->
            zipFile.entries().asSequence().sumOf { zipEntry -> zipEntry.size }
        }
        var totalBytesUnzipped=0L
        try {
            zipEntry = unzipStream.nextEntry
            while (zipEntry != null) {
                val f =
                    File(appContext.filesDir, zipEntry.name.replace("\\ ", "").replace(" ", ""))
                if (zipEntry.isDirectory) {
                    f.mkdirs()
                } else {
                    val parentDir = f.parentFile
                    parentDir?.let {
                        if (!parentDir.exists()) {
                            parentDir.mkdirs()
                        }
                    }

                    val fout = FileOutputStream(f)
                    var bytesReadUnzip = unzipStream.read(byteBfr)
                    while (bytesReadUnzip != -1) {
                        totalBytesUnzipped += bytesReadUnzip
                        fout.write(byteBfr, 0, bytesReadUnzip)
                        bytesReadUnzip = unzipStream.read(byteBfr)
                        uiCallback?.setProgress(totalBytesUnzipped.toDouble()/totalDecompressedSize.toDouble())
                        uiCallback?.let {
                            handler.post(it)
                        }
                    }
                    fout.close()
                    unzipStream.closeEntry()
                }
                zipEntry = unzipStream.nextEntry
            }
        }
        catch (e: Exception)
        {
            return DefaultSceneInstallerCode.UNZIPPING_FAILED
        }
        finally {
            unzipStream.close()
            zipFileInputStream.close()
        }
        return returnCode
    }

    fun checkIfScenesAreAvailable(): Boolean
    {
        val gson= GsonBuilder().apply { registerTypeAdapter(
            PersistableInstrument::class.java,
            PersistableInstrumentDeserializer()
        ) }.create()
        val f = File(appContext.filesDir, "touchSampleSynth1.json")
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
