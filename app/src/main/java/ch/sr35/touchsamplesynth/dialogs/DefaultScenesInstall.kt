package ch.sr35.touchsamplesynth.dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.Window
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import ch.sr35.touchsamplesynth.DefaultScenesInstaller
import ch.sr35.touchsamplesynth.ProgressRunnable
import ch.sr35.touchsamplesynth.R
import ch.sr35.touchsamplesynth.TouchSampleSynthMain
import java.util.concurrent.Executors


class DefaultScenesInstall(private var context: Context) : Dialog(context) {
    private var textfieldInstallerText: TextView?=null
    var installer = DefaultScenesInstaller(context as TouchSampleSynthMain)
    private var buttonYes: Button?=null
    private var buttonNo: Button?=null
    private var progressBar: ProgressBar?=null
    private var installerText: String=""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.fragment_default_scenes_install)
        progressBar = findViewById<ProgressBar>(R.id.installerProgressbar)
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
        buttonYes?.setOnClickListener {

            executor.execute {
                installer.downloadAndUnpackDrumSamples(progressRunnable)
                installer.installDefaultScene(true)
                (context as TouchSampleSynthMain).saveToBinaryFiles()
                dismiss()
            }

        }
        buttonNo?.setOnClickListener {
            executor.execute {
                installer.installDefaultScene(false)
                (context as TouchSampleSynthMain).saveToBinaryFiles()
                dismiss()
            }
        }
    }


    fun setInstallerText(txt: String)
    {
        installerText = txt
        textfieldInstallerText?.text = installerText
        textfieldInstallerText?.invalidate()
    }

    companion object {
    }
}