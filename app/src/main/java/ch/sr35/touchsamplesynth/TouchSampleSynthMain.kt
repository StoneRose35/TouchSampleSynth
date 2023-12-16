package ch.sr35.touchsamplesynth

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import ch.sr35.touchsamplesynth.audio.AudioEngineK
import ch.sr35.touchsamplesynth.audio.Instrument
import ch.sr35.touchsamplesynth.audio.instruments.SineMonoSynthI
import ch.sr35.touchsamplesynth.databinding.ActivityMainBinding
import ch.sr35.touchsamplesynth.fragments.InstrumentsPageFragment
import ch.sr35.touchsamplesynth.fragments.PlayPageFragment
import ch.sr35.touchsamplesynth.fragments.SettingsFragment
import ch.sr35.touchsamplesynth.model.SceneP
import ch.sr35.touchsamplesynth.views.TouchElement
import ch.sr35.touchsamplesynth.views.VuMeter
import com.google.android.material.snackbar.Snackbar
import java.io.File
import java.util.Timer
import java.util.TimerTask


class TouchSampleSynthMain : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    private lateinit var binding: ActivityMainBinding
    val audioEngine: AudioEngineK=AudioEngineK()
    val soundGenerators=ArrayList<Instrument>()
    val touchElements=ArrayList<TouchElement>()
    val allScenes = ArrayList<SceneP>()
    val playPageFragment=PlayPageFragment()
    val instrumentsPageFragment=InstrumentsPageFragment()
    val settingsFrament= SettingsFragment()
    var mainMenu: Menu?=null

    init {

    }

    override fun onResume() {
        super.onResume()
        val allNotes = MusicalPitch.generateAllNotes()
        allScenes.clear()
        val fDir = this.filesDir
        val sceneFiles = fDir.listFiles { fn -> fn.isFile && fn.name.endsWith("scn") }
        if (sceneFiles != null){
            sceneFiles.sort()
            sceneFiles.forEach {
                SceneP.fromFile(it)?.let { it1 -> allScenes.add(it1) }
            }
        }
        try {
            allScenes[0].populate(soundGenerators, touchElements, this)
        }
        catch (e: Exception)
        {
            allScenes.clear()
        }

        if (allScenes.isEmpty()) {
            allScenes.add(SceneP())
            allScenes[0].name = "Default"
            val synth = SineMonoSynthI(this,"Basic")
            synth.generateVoices(4)
            soundGenerators.add(synth)

            var te = TouchElement(this, null)
            te.soundGenerator = synth
            te.voiceNr = 0
            te.note = allNotes[44]
            te.setEditmode(false)
            touchElements.add(te)

            te = TouchElement(this, null)
            te.soundGenerator = synth
            te.voiceNr = 1
            te.note = allNotes[44 + 5]
            te.setEditmode(false)
            touchElements.add(te)

            te = TouchElement(this, null)
            te.soundGenerator = synth
            te.voiceNr = 2
            te.note = allNotes[44 + 7]
            te.setEditmode(false)
            touchElements.add(te)


            te = TouchElement(this, null)
            te.soundGenerator = synth
            te.voiceNr = 3
            te.note = allNotes[44 + 9]
            te.setEditmode(false)
            touchElements.add(te)
        }


        val playPage = PlayPageFragment()
        putFragment(playPage,"PlayPage0")
        if (!audioEngine.startEngine())
        {
            playPage.view?.let { Snackbar.make(it,"Audio Engine failed to start",10) }
        }

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
    override fun onStart() {
        super.onStart()

    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        /*
        StrictMode.setVmPolicy(
            VmPolicy.Builder(StrictMode.getVmPolicy())
                .detectLeakedClosableObjects()
                .build()
        )*/
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(findViewById(R.id.mainToolBar))
        window.decorView.apply {
            systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
        }


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
        soundGenerators.flatMap { sg -> sg.voices }.forEach { el -> el.detachFromAudioEngine() }
        audioEngine.stopEngine()
        super.onDestroy()
    }

    override fun onStop() {

        super.onStop()
    }

    override fun onPause() {

        val spinnerScenes = mainMenu?.findItem(R.id.menuitem_scenes)?.actionView as Spinner
        spinnerScenes.let {
            val f = File(filesDir.absolutePath + File.separator + "%03dscene.scn".format(it.selectedItemPosition))
            if (f.exists())
            {
                f.delete()
            }
            allScenes[it.selectedItemPosition].persist(soundGenerators,touchElements)
            allScenes[it.selectedItemPosition].toFile(f)
        }
        soundGenerators.flatMap { sg -> sg.voices }.forEach { el -> el.detachFromAudioEngine() }
        audioEngine.stopEngine()
        super.onPause()
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu,menu)
        mainMenu=menu
        val spinnerScenes = menu?.findItem(R.id.menuitem_scenes)?.actionView as Spinner
        val sceneArrayAdapter = ArrayAdapter<SceneP>(this, android.R.layout.simple_spinner_item,allScenes)
        sceneArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerScenes.adapter = sceneArrayAdapter
        spinnerScenes.setSelection(0,false)
        spinnerScenes.onItemSelectedListener=this
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
            R.id.menuitem_settings ->
            {
                putFragment(settingsFrament, "settingsPage0")
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

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

        //unload the current scene
        soundGenerators.flatMap { sg -> sg.voices }.forEach { el -> el.detachFromAudioEngine() }
        if (supportFragmentManager.fragments[0].tag!=null
            && supportFragmentManager.fragments[0].tag.equals("PlayPage0"))
        {
            for (te in touchElements) {
                (supportFragmentManager.fragments[0].view as ViewGroup).removeView(te)
            }
        }

        // load the new scene
        allScenes[position].populate(soundGenerators, touchElements, this)
        if (supportFragmentManager.fragments[0].tag!=null) {
            if (supportFragmentManager.fragments[0].tag.equals("PlayPage0"))
            {
                for (te in touchElements) {
                    te.setEditmode(true)
                    (supportFragmentManager.fragments[0].view as ViewGroup).addView(te)
                }
            }
            //else if (supportFragmentManager.fragments[0].tag.equals("instrumentPage0"))
            //{
            //
            //}
            supportFragmentManager.fragments[0].view?.invalidate()
        }

    }

    fun getCurrentSceneName(): String?
    {
        if (mainMenu != null) {
            val scenePos = (mainMenu!!.findItem(R.id.menuitem_scenes)!!.actionView as Spinner).selectedItemPosition
            if (scenePos >= 0 && scenePos < allScenes.size) {
                return allScenes[scenePos].name
            }
        }
        return null
    }

    fun setCurrentSceneName(sceneName: String)
    {
        if (mainMenu != null) {
            val scenePos = (mainMenu!!.findItem(R.id.menuitem_scenes)!!.actionView as Spinner).selectedItemPosition
            if (scenePos >= 0 && scenePos < allScenes.size) {
                allScenes[scenePos].name = sceneName
            }
        }
    }

    fun persistCurrentScene()
    {
        val scenePos = (mainMenu!!.findItem(R.id.menuitem_scenes)!!.actionView as Spinner).selectedItemPosition
        allScenes[scenePos].persist(soundGenerators,touchElements)
    }
    fun lockSceneSelection()
    {
        (mainMenu?.findItem(R.id.menuitem_scenes)?.actionView as Spinner).isEnabled = false
    }

    fun unlockSceneSelection()
    {
        (mainMenu?.findItem(R.id.menuitem_scenes)?.actionView as Spinner).isEnabled = true
    }
    override fun onNothingSelected(parent: AdapterView<*>?) {

    }
}