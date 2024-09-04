package ch.sr35.touchsamplesynth.dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import ch.sr35.touchsamplesynth.BuildConfig
import ch.sr35.touchsamplesynth.CurrentScenesCode
import ch.sr35.touchsamplesynth.DRUMSAMPLES_FOLDER_NAME
import ch.sr35.touchsamplesynth.DefaultSceneInstallerCode
import ch.sr35.touchsamplesynth.DefaultScenesInstaller
import ch.sr35.touchsamplesynth.DefaultScenesState
import ch.sr35.touchsamplesynth.ProgressRunnable
import ch.sr35.touchsamplesynth.R
import ch.sr35.touchsamplesynth.SCENES_FILE_NAME
import ch.sr35.touchsamplesynth.TAG
import ch.sr35.touchsamplesynth.TouchSampleSynthMain
import ch.sr35.touchsamplesynth.model.DataUpdater
import ch.sr35.touchsamplesynth.model.PersistableInstrument
import ch.sr35.touchsamplesynth.model.PersistableInstrumentDeserializer
import ch.sr35.touchsamplesynth.model.SceneListP
import ch.sr35.touchsamplesynth.model.importMode
import com.google.gson.GsonBuilder
import java.io.File
import java.util.concurrent.Executors

enum class State
{
    ASKING_WHETHER_INSTALL,
    ASKING_WHETHER_DOWNLOAD,
    ASKING_WHETHER_OVERRIDE,
    DONE
}
class DefaultScenesInstall(private var context: Context) : Dialog(context) {
    private var textfieldInstallerText: TextView?=null
    var installer = DefaultScenesInstaller(context as TouchSampleSynthMain)
    private var buttonYes: Button?=null
    private var buttonNo: Button?=null
    private var progressBar: ProgressBar?=null
    private var installerText: String=context.getString(R.string.installerPerformInstall)
    private var state: State=State.ASKING_WHETHER_INSTALL
    private var installWithExternalSamples: Boolean =  false
    private var doOverrideExistingPresets: Boolean = false
    val currentScenesState: DefaultScenesState

    init {
        currentScenesState = checkIfScenesAreAvailable()
    }

    override fun onStop() {
        super.onStop()
        if (state == State.DONE) {
            (context as TouchSampleSynthMain).scenesArrayAdapter?.notifyDataSetChanged()
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.fragment_default_scenes_install)
        progressBar = findViewById<ProgressBar>(R.id.installerProgressbar)

        setCancelable(false)
        val progressRunnable = object: ProgressRunnable(null,null)
        {
            override fun run(){
                progress?.let {
                    progressBar?.progress = (it * 1000.0).toInt()
                    progressBar?.invalidate()
                }
                message?.let {
                    setInstallerText(it)
                }
            }
        }
        buttonYes = findViewById(R.id.installerYes)
        buttonNo =  findViewById(R.id.installerNo)
        textfieldInstallerText = findViewById(R.id.installerText)
        textfieldInstallerText?.text = installerText
        val executor = Executors.newSingleThreadExecutor()
        val handler = Handler(Looper.getMainLooper())
        buttonYes?.setOnClickListener {

            when (state) {
                State.ASKING_WHETHER_DOWNLOAD -> {
                    buttonYes?.visibility = View.INVISIBLE
                    buttonNo?.visibility = View.INVISIBLE
                    executor.execute {
                        val downloadState = installer.downloadAndUnpackDrumSamples(progressRunnable)
                        if (downloadState == DefaultSceneInstallerCode.SAMPLES_DOWNLOADED)
                        {
                            installWithExternalSamples = true
                            Log.i(TAG,"Installer: Download and Unpacking done")
                        }
                        else
                        {
                            installWithExternalSamples = false
                            handler.post {
                                setInstallerText(context.getString(R.string.installerDownloadFailed))
                            }
                            Thread.sleep(2000)
                            handler.post {
                                setInstallerText("")
                            }
                            Log.i(TAG,"Installer: Download or unpacking failed")
                        }

                        if (currentScenesState.code ==CurrentScenesCode.PRESET_NO_INSTALL_DONE && !currentScenesState.isEmpty)
                        {
                            handler.post {
                                setInstallerText(context.getString(R.string.installerOverridePresets))
                            }
                            buttonYes?.visibility = View.VISIBLE
                            buttonNo?.visibility = View.VISIBLE
                            state = State.ASKING_WHETHER_OVERRIDE
                        }
                        else {
                            installer.installDefaultScene(
                                installWithExternalSamples,
                                importMode.REPLACE
                            )
                            Log.i(TAG,"Installer: Auto install after agreeing to download samples,")
                            Log.i(TAG," installWithExternalSamples: ${installWithExternalSamples}")
                            state = State.DONE
                            (context as TouchSampleSynthMain).saveToBinaryFiles()
                            dismiss()
                        }
                    }
                    installWithExternalSamples = true

                }
                State.ASKING_WHETHER_INSTALL -> {

                    if (!installer.checkSampleLibrary(installer.samplesFolderLvl1, DRUMSAMPLES_FOLDER_NAME))
                    {
                        setInstallerText(context.getString(R.string.installerAddDrumSamples))
                        state = State.ASKING_WHETHER_DOWNLOAD
                    }
                    else if (currentScenesState.code ==CurrentScenesCode.PRESET_NO_INSTALL_DONE && !currentScenesState.isEmpty)
                    {
                        setInstallerText(context.getString(R.string.installerOverridePresets))
                        state = State.ASKING_WHETHER_OVERRIDE
                        installWithExternalSamples = true
                    }
                    else {
                        installWithExternalSamples = true
                        installer.installDefaultScene(
                            installWithExternalSamples,
                            importMode.REPLACE
                        )
                        state = State.DONE
                        (context as TouchSampleSynthMain).saveToBinaryFiles()
                        dismiss()
                    }
                }
                State.ASKING_WHETHER_OVERRIDE -> {
                    doOverrideExistingPresets = true
                    installer.installDefaultScene(
                        installWithExternalSamples,
                        importMode.REPLACE
                    )
                    state = State.DONE
                    (context as TouchSampleSynthMain).saveToBinaryFiles()
                    dismiss()
                }
                State.DONE -> {}
            }
        }
        buttonNo?.setOnClickListener {
            when (state)
            {
                State.ASKING_WHETHER_INSTALL -> {
                    // add an empty set to have a properly exported json file
                    installer.installDefaultScene(
                        installWithExternalSamples,
                        importMode.NONE
                    )
                    dismiss()
                }
                State.ASKING_WHETHER_DOWNLOAD -> {
                    installWithExternalSamples = false
                    if (currentScenesState.code ==CurrentScenesCode.PRESET_NO_INSTALL_DONE && !currentScenesState.isEmpty)
                    {
                        setInstallerText(context.getString(R.string.installerOverridePresets))
                        state = State.ASKING_WHETHER_OVERRIDE
                    }
                    else {
                        installer.installDefaultScene(
                            installWithExternalSamples,
                            importMode.REPLACE
                        )
                        (context as TouchSampleSynthMain).saveToBinaryFiles()
                        state = State.DONE
                        dismiss()
                    }
                }
                State.ASKING_WHETHER_OVERRIDE -> {
                    installer.installDefaultScene(
                        installWithExternalSamples,
                        importMode.ADD
                    )
                    (context as TouchSampleSynthMain).saveToBinaryFiles()
                    state = State.DONE
                    dismiss()
                }
                State.DONE -> {}
            }
        }
    }

    fun setInstallerText(txt: String)
    {
        installerText = txt
        textfieldInstallerText?.text = installerText
        textfieldInstallerText?.invalidate()
    }

    private fun checkIfScenesAreAvailable(): DefaultScenesState
    {
        val gson= GsonBuilder().apply { registerTypeAdapter(
            PersistableInstrument::class.java,
            PersistableInstrumentDeserializer()
        ) }.create()
        val f = File(context.filesDir, SCENES_FILE_NAME)
        var scenesEmpty = true
        if (f.exists())
        {
            val jsondata=f.readText()
            try {
                val scenesList = gson.fromJson(jsondata, SceneListP::class.java)
                scenesEmpty = scenesList.scenes.isEmpty()
                if (scenesList.touchSampleSynthVersion != BuildConfig.VERSION_NAME)
                {
                    val majorMinorSubScenes = scenesList.touchSampleSynthVersion.split('.')
                    val majorMinorSubCurrent = BuildConfig.VERSION_NAME.split('.')
                    val vdiffMajor = majorMinorSubCurrent[0].toInt() - (majorMinorSubScenes[0].toInt())
                    val vdiffMinor = majorMinorSubCurrent[1].toInt() - (majorMinorSubScenes[1].toInt())
                    val vdiffSub = majorMinorSubCurrent[2].toInt() - (majorMinorSubScenes[2].toInt())
                    if ((vdiffSub < 0 && vdiffMinor ==0 && vdiffMajor == 0) ||
                        (vdiffMinor < 0 && vdiffMajor == 0) ||
                        (vdiffMajor < 0)
                    )
                    {

                        return DefaultScenesState(CurrentScenesCode.PRESETS_NEWER_THAN_BUNDLED,scenesEmpty)
                    }
                    (context as TouchSampleSynthMain).loadFromBinaryFiles()
                    SceneListP.exportAsJson(SCENES_FILE_NAME,context)
                    scenesEmpty = (context as TouchSampleSynthMain).scenesArrayAdapter!!.isEmpty
                    val updatedJson = DataUpdater.upgradeToCurrent(jsondata)

                    return if (updatedJson != null)
                    {
                        f.writeText(updatedJson)
                        DefaultScenesState(CurrentScenesCode.PRESET_INSTALL_DONE,scenesEmpty)
                    }
                    else {
                        DefaultScenesState(
                            CurrentScenesCode.PRESET_AND_OUTDATED,
                            scenesEmpty
                        )
                    }
                }
                if (scenesList.installDone)
                {
                    return DefaultScenesState(CurrentScenesCode.PRESET_INSTALL_DONE,scenesEmpty)
                }
                return DefaultScenesState(CurrentScenesCode.PRESET_NO_INSTALL_DONE,scenesEmpty)
            }
            catch (_: Exception)
            {
                val oldVersion = DataUpdater.getVersion(jsondata)
                if (oldVersion != null)
                {
                    val updatedJson = DataUpdater.upgradeToCurrent(jsondata)
                    return if (updatedJson != null)
                    {
                        f.writeText(updatedJson)
                        DefaultScenesState(CurrentScenesCode.PRESET_INSTALL_DONE,scenesEmpty)
                    }
                    else
                    {
                        DefaultScenesState(
                            CurrentScenesCode.PRESET_AND_OUTDATED,
                            scenesEmpty
                        )
                    }
                } else {
                    f.delete()
                    return DefaultScenesState(CurrentScenesCode.NO_CURRENT_SCENES, scenesEmpty)
                }
            }
        }
        else
        {
            (context as TouchSampleSynthMain).loadFromBinaryFiles()
            SceneListP.exportAsJson(SCENES_FILE_NAME,context)
            scenesEmpty = (context as TouchSampleSynthMain).scenesArrayAdapter!!.isEmpty
            return DefaultScenesState(CurrentScenesCode.PRESET_AND_OUTDATED,scenesEmpty)
        }
    }
}