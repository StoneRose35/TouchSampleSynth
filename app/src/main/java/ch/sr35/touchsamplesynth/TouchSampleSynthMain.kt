package ch.sr35.touchsamplesynth

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.fragment.app.Fragment
import ch.sr35.touchsamplesynth.audio.AudioEngineK
import ch.sr35.touchsamplesynth.audio.Instrument
import ch.sr35.touchsamplesynth.audio.MusicalSoundGenerator
import ch.sr35.touchsamplesynth.audio.instruments.SineMonoSynthI
import ch.sr35.touchsamplesynth.audio.voices.SineMonoSynthK
import ch.sr35.touchsamplesynth.databinding.ActivityMainBinding
import ch.sr35.touchsamplesynth.fragments.InstrumentsPageFragment
import ch.sr35.touchsamplesynth.fragments.PlayPageFragment
import ch.sr35.touchsamplesynth.model.SceneP
import ch.sr35.touchsamplesynth.views.TouchElement
import ch.sr35.touchsamplesynth.views.VuMeter
import java.io.File
import java.util.Timer
import java.util.TimerTask

class TouchSampleSynthMain : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    val audioEngine: AudioEngineK=AudioEngineK()
    val soundGenerators=ArrayList<Instrument>()
    val touchElements=ArrayList<TouchElement>()
    var defaultScene: SceneP?=null
    val playPageFragment=PlayPageFragment()
    val instrumentsPageFragment=InstrumentsPageFragment()


    init {

    }

    override fun onStart() {
        super.onStart()
        val allNotes = MusicalPitch.generateAllNotes()

        val fDir = this.filesDir
        fDir.listFiles()?.iterator()?.forEach {
            if (it.isFile && it.name.endsWith("scn")) {
                defaultScene = SceneP.fromFile(it)
            }
        }

        defaultScene?.populate(soundGenerators,touchElements, this)

        if (defaultScene == null) {
            val synth = SineMonoSynthI.generateInstance(this,4,"Basic")
            soundGenerators.add(synth)

            var te = TouchElement(this, null)
            te.soundGenerator = synth
            te.note = allNotes[44]
            te.setEditmode(false)
            touchElements.add(te)

            te = TouchElement(this, null)
            te.soundGenerator = synth
            te.note = allNotes[44 + 5]
            te.setEditmode(false)
            touchElements.add(te)

            te = TouchElement(this, null)
            te.soundGenerator = synth
            te.note = allNotes[44 + 7]
            te.setEditmode(false)
            touchElements.add(te)


            te = TouchElement(this, null)
            te.soundGenerator = synth
            te.note = allNotes[44 + 9]
            te.setEditmode(false)
            touchElements.add(te)
        }


        val playPage = PlayPageFragment()
        putFragment(playPage,"PlayPage0")
        audioEngine.startEngine()

        val timer=Timer()
        val timerTask= object: TimerTask()
        {
            override fun run() {
                val avgVol = audioEngine.getAverageVolume()
                findViewById<VuMeter>(R.id.vuMeter)?.updateVuLevel(avgVol*2.0f)
                val cpuLoad = audioEngine.getCpuLoad()
                findViewById<VuMeter>(R.id.cpuMeter)?.updateVuLevel(cpuLoad)
            }
        }
        timer.schedule(timerTask,0,100)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }


    private fun putFragment(frag: Fragment,tag: String?)
    {
        supportFragmentManager.beginTransaction().let {
            if (supportFragmentManager.findFragmentById(R.id.mainLayout) != null)
            {
                it.replace(R.id.mainLayout,frag,tag)
            }
            else
            {
                it.add(R.id.mainLayout,frag,tag)
            }
            it.commit()
        }
    }

    override fun onDestroy() {

        val f = File("default.scn")
        if (f.exists())
        {
            f.delete()
        }
        soundGenerators.flatMap { sg -> sg.voices!! }.forEach { el -> el.detachFromAudioEngine() }
        audioEngine.stopEngine()
        super.onDestroy()
    }

    override fun onStop() {
        if (defaultScene==null)
        {
            defaultScene= SceneP()

        }
        defaultScene!!.persist(soundGenerators,touchElements)
        val f = File(filesDir.absolutePath + File.separator + "default.scn")
        if (f.exists())
        {
            f.delete()
        }
        defaultScene!!.toFile(f)
        soundGenerators.flatMap { sg -> sg.voices!! }.forEach { el -> el.detachFromAudioEngine() }
        audioEngine.stopEngine()
        super.onStop()
    }
    /**
     * A native method that is implemented by the 'touchsamplesynth' native library,
     * which is packaged with this application.
     */

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.menuitem_play -> {

                putFragment(playPageFragment,"PlayPage0")
            }
            R.id.menuitem_instruments ->
            {
                putFragment(instrumentsPageFragment,"instrumentPage0")
            }
        }
        return true
    }


    companion object {
        // Used to load the 'touchsamplesynth' library on application startup.
        init {
            System.loadLibrary("touchsamplesynth")
        }
    }
}