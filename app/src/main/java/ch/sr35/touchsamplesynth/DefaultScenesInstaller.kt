package ch.sr35.touchsamplesynth

import android.os.Handler
import android.os.Looper
import ch.sr35.touchsamplesynth.model.InstrumentP
import ch.sr35.touchsamplesynth.model.PersistableInstrumentDeserializer
import ch.sr35.touchsamplesynth.model.SamplerP
import ch.sr35.touchsamplesynth.model.SceneListP
import ch.sr35.touchsamplesynth.model.importDoneFlag
import ch.sr35.touchsamplesynth.model.importMode
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
    SAMPLES_DOWNLOADED,
    DOWNLOAD_FAILED,
    UNZIPPING_FAILED,
    INSTALL_FAILED,
    INSTALLED
}

class DefaultScenesState(var code: CurrentScenesCode,val isEmpty: Boolean)


enum class CurrentScenesCode
{
    NO_CURRENT_SCENES,
    PRESETS_NEWER_THAN_BUNDLED,
    PRESET_NO_INSTALL_DONE,
    PRESET_AND_OUTDATED,
    PRESET_INSTALL_DONE
}
const val SCENES_FILE_NAME="touchSampleSynth1.json"
const val DRUMSAMPLES_FOLDER_NAME="tss_demoset"
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

    private var percentageDownloaded: Double = 0.0
    val handler: Handler = Handler(Looper.getMainLooper())

    init {




    }

    fun checkSampleLibrary(directoryName: String): Boolean
    {
        val samplesFolder = appContext.filesDir
        return samplesFolder.listFiles()?.any { f -> f.isDirectory && f.name == directoryName } == true
    }

    fun installDefaultScene(withExternalSamples: Boolean,importMode: importMode): DefaultSceneInstallerCode
    {
        // get default scenes Asset
        val presetsFile = appContext.assets.open("defaultPresets.json").reader().readText()
        // check is samplelibrary from https://wavbvkery.com/wp-content/uploads/WAVBVKERY-Acoustic-Drum-Samples.zip
        // is in DCIM folder and is unpacked
        val gson=GsonBuilder().apply {
            registerTypeAdapter(InstrumentP::class.java,PersistableInstrumentDeserializer())
            setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
        }.create()
        val presets = gson.fromJson(presetsFile,SceneListP::class.java)
        if (!withExternalSamples)
        {
            // parse default presets json and remove each scene that contains a sampler"
            presets.scenes.removeIf { scn -> scn.instruments.any { i -> i is SamplerP } }
        }
        presets.importOntoDevice(appContext,importMode,importDoneFlag.SET)
        appContext.scenesListDirty = true
        val handler = Handler(Looper.getMainLooper())
        handler.post {
            appContext.loadSceneWithWaitIndicator(0)
        }
        presets.touchSampleSynthVersion = BuildConfig.VERSION_NAME
        SceneListP.exportAsJson(SCENES_FILE_NAME,appContext,true)
        return DefaultSceneInstallerCode.INSTALLED

    }


    fun downloadAndUnpackDrumSamples(uiCallback: ProgressRunnable?): DefaultSceneInstallerCode
    {
        percentageDownloaded=0.0
        val okHttpClient=OkHttpClient()
        val dldFolder = appContext.filesDir
        val samplesZipped = File(dldFolder.absolutePath + "/tss_drumsamples.zip")
        val downloadBuffer = ByteArray(1024*1024*1) // 1 MB Buffer
        var bytesRead: Int
        var returnCode = DefaultSceneInstallerCode.SAMPLES_DOWNLOADED
        if (!samplesZipped.exists()) {
            uiCallback?.setProgressMessage(appContext.getString(R.string.installerDownloadingSamples))
            uiCallback?.let {
                handler.post(it)
            }
            val zippedFileStream = FileOutputStream(samplesZipped)
            val req = Request.Builder()
                .url("https://www.stonerose35.ch/dungeon/tss_drumsamples.zip")
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


}
