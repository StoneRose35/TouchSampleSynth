package ch.sr35.touchsamplesynth

import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import ch.sr35.touchsamplesynth.audio.AudioEngineK
import ch.sr35.touchsamplesynth.audio.Instrument
import ch.sr35.touchsamplesynth.audio.instruments.SineMonoSynthI
import ch.sr35.touchsamplesynth.databinding.ActivityMainBinding
import ch.sr35.touchsamplesynth.fragments.InstrumentsPageFragment
import ch.sr35.touchsamplesynth.fragments.PlayPageFragment
import ch.sr35.touchsamplesynth.fragments.SceneFragment
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
    var touchElementsDisplayMode: TouchElement.TouchElementState=TouchElement.TouchElementState.PLAYING
    private val playPageFragment=PlayPageFragment()
    private val instrumentsPageFragment=InstrumentsPageFragment()
    private val settingsFrament= SettingsFragment()
    private val scenesEditFragment = SceneFragment(allScenes)
    var midiHostHandler :MidiHostHandler?= null
    var mainMenu: Menu?=null
    var populateOnResume=true
    private var oldScenePosition=-1

    init {

    }


    override fun onResume() {
        super.onResume()
        val allNotes = MusicalPitch.generateAllNotes()
        if (populateOnResume) {
            allScenes.clear()
            val fDir = this.filesDir
            val sceneFiles = fDir.listFiles { fn -> fn.isFile && fn.name.endsWith("scn") }
            if (sceneFiles != null) {
                sceneFiles.sort()
                Log.i("TouchSampleSynth", "restoring from Files")
                sceneFiles.forEach {
                    try {
                        Log.i("TouchSampleSynth", "reading file %s".format(it.name))
                        SceneP.fromFile(it)?.let { it1 ->
                            allScenes.add(it1)
                            Log.i("TouchSampleSynth", it1.toString())
                            for (instr in it1.instruments) {
                                Log.i("TouchSampleSynth", instr.toString())
                            }
                            for (te in it1.touchElements) {
                                Log.i("TouchSampleSynth", te.toString())
                            }
                        }
                    } catch (e: Exception) {
                        it.delete()
                    }

                }
            }
        }
        midiHostHandler?.startMidiDeviceListener()
        midiHostHandler?.let {
            if(it.midiDevices.size > 0)
            {
                it.connectMidiDevice(it.midiDevices[0])
            }
        }

        if (mainMenu!=null) {
            if (populateOnResume) {
                allScenes[(mainMenu!!.findItem(R.id.menuitem_scenes)!!.actionView as Spinner).selectedItemPosition].populate(
                    soundGenerators,
                    touchElements,
                    this
                )
            }
            //else
            //{
            //    soundGenerators.flatMap { sg -> sg.voices }.forEach { el -> el.bindToAudioEngine() }
            //}

            if (populateOnResume) {
                if (supportFragmentManager.fragments[0].tag.equals("PlayPage0")) {
                    for (te in touchElements) {
                        supportFragmentManager.fragments[0].view?.findViewById<SwitchCompat>(R.id.toggleEdit)?.isChecked.let {
                            if (it != null) {
                                te.setEditmode(it)
                            }
                        }
                        (supportFragmentManager.fragments[0].view as ViewGroup).addView(te)
                    }
                } else if (supportFragmentManager.fragments[0].tag.equals("instrumentPage0")) {
                    supportFragmentManager.fragments[0].view?.findViewById<ListView>(R.id.instruments_page_instruments_list)
                        ?.invalidateViews()
                }
            }
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

        audioEngine.startEngine()
        if (!populateOnResume)
        {
            populateOnResume=true
        }

    }
    override fun onStart() {
        super.onStart()

        val playPage = PlayPageFragment()
        if (supportFragmentManager.fragments.isEmpty()) {
            putFragment(playPage, "PlayPage0")
        }
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
        {
            ActivityCompat.requestPermissions(this,
                arrayOf(android.Manifest.permission.READ_MEDIA_AUDIO,
                    android.Manifest.permission.READ_MEDIA_IMAGES,
                    android.Manifest.permission.READ_MEDIA_IMAGES,
                    android.Manifest.permission.MANAGE_EXTERNAL_STORAGE,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                ),
                PackageManager.PERMISSION_GRANTED)
        }
        else
        {
            ActivityCompat.requestPermissions(this,
                arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                ),
                PackageManager.PERMISSION_GRANTED)
        }


        midiHostHandler =  MidiHostHandler(this)

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
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

        if (populateOnResume) {
            val mainDir = File(filesDir.absolutePath)
            persistCurrentScene()
            mainDir.listFiles { f -> f.isFile && f.name.endsWith(".scn") }?.forEach {
                it.delete()
            }
            Log.i("TouchSampleSynth", "save to files")
            for ((cnt, scn) in allScenes.withIndex()) {
                val f = File(filesDir.absolutePath + File.separator + "%03dscene.scn".format(cnt))
                Log.i("TouchSampleSynth", "writing file %s".format(f.name))
                Log.i("TouchSampleSynth", scn.toString())
                for (instr in scn.instruments) {
                    Log.i("TouchSampleSynth", instr.toString())
                }
                for (te in scn.touchElements) {
                    Log.i("TouchSampleSynth", te.toString())
                }
                scn.toFile(f)
            }
        }

        midiHostHandler?.stopMidiDeviceListener()

        if (supportFragmentManager.fragments[0].tag!=null
            && supportFragmentManager.fragments[0].tag.equals("PlayPage0"))
        {
            for (te in touchElements) {
                (supportFragmentManager.fragments[0].view as ViewGroup).removeView(te)
            }
        }
        if (populateOnResume) {
            soundGenerators.flatMap { sg -> sg.voices }.forEach { el -> el.detachFromAudioEngine() }
        }
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
        if (oldScenePosition >=0 && oldScenePosition < allScenes.size) {
            spinnerScenes.setSelection(oldScenePosition, false)
        }
        else
        {
            spinnerScenes.setSelection(0, false)
        }
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
            R.id.menuitem_sceneedit ->
            {
                putFragment(scenesEditFragment, "scenesEditPage0")
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
        if (oldScenePosition >= 0) {
            allScenes[oldScenePosition].persist(soundGenerators, touchElements)
        }
        oldScenePosition = position
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
                    supportFragmentManager.fragments[0].view?.findViewById<SwitchCompat>(R.id.toggleEdit)?.isChecked.let {
                        if (it!=null) {
                            te.setEditmode(it)
                        }
                    }
                    (supportFragmentManager.fragments[0].view as ViewGroup).addView(te)
                }
            }
            else if (supportFragmentManager.fragments[0].tag.equals("instrumentPage0"))
            {
                supportFragmentManager.fragments[0].view?.findViewById<ListView>(R.id.instruments_page_instruments_list)?.invalidateViews()
                (supportFragmentManager.fragments[0] as InstrumentsPageFragment).selectInstrument(0,true)
            }
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
        if (mainMenu != null) {
            val scenePos =
                (mainMenu!!.findItem(R.id.menuitem_scenes)!!.actionView as Spinner).selectedItemPosition
            if (scenePos < allScenes.size) {
                allScenes[scenePos].persist(soundGenerators, touchElements)
            }
        }
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