package ch.sr35.touchsamplesynth

import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
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
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.children
import androidx.fragment.app.Fragment
import ch.sr35.touchsamplesynth.audio.AudioEngineK
import ch.sr35.touchsamplesynth.audio.Instrument
import ch.sr35.touchsamplesynth.databinding.ActivityMainBinding
import ch.sr35.touchsamplesynth.dialogs.DefaultScenesInstall
import ch.sr35.touchsamplesynth.fragments.InstrumentsPageFragment
import ch.sr35.touchsamplesynth.fragments.PlayPageFragment
import ch.sr35.touchsamplesynth.fragments.SceneFragment
import ch.sr35.touchsamplesynth.fragments.SettingsFragment
import ch.sr35.touchsamplesynth.graphics.Converter
import ch.sr35.touchsamplesynth.model.SceneP
import ch.sr35.touchsamplesynth.network.NetworkDiscoveryHandler
import ch.sr35.touchsamplesynth.network.RtpMidiServer
import ch.sr35.touchsamplesynth.views.PlayArea
import ch.sr35.touchsamplesynth.views.TouchElement
import ch.sr35.touchsamplesynth.views.WaitAnimation
import java.io.File
import java.util.concurrent.Executors


const val TAG="TouchSampleSynth"
class TouchSampleSynthMain : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    private lateinit var binding: ActivityMainBinding
    val audioEngine: AudioEngineK=AudioEngineK()
    val soundGenerators=ArrayList<Instrument>()
    val touchElements=ArrayList<TouchElement>()
    val allScenes = ArrayList<SceneP>()
    private val playPageFragment=PlayPageFragment()
    private val instrumentsPageFragment=InstrumentsPageFragment()
    private val settingsFrament= SettingsFragment()
    private val scenesEditFragment = SceneFragment(allScenes)
    var midiHostHandler :MidiHostHandler?= null
    var nsdHandler: NetworkDiscoveryHandler?=null
    var rtpMidiServer: RtpMidiServer?=null
    var mainMenu: Menu?=null
    private var oldScenePosition=-1
    var scenesListDirty=false
    var scenesArrayAdapter: ArrayAdapter<SceneP>?=null

    // global settings
    var rtpMidiNotesRepeat=1 // defines how many times note on and note off commands are repeated over rtp midi
    var touchElementsDisplayMode: TouchElement.TouchElementState=TouchElement.TouchElementState.PLAYING
    var connectorDisplay = false
    var isInEditMode = false

    override fun onStart() {
        super.onStart()

        val playPage = PlayPageFragment()
        if (supportFragmentManager.fragments.isEmpty()) {
            putFragment(playPage, "PlayPage0")
        }

        val defaultScenesInstall=DefaultScenesInstall(this)
        if (defaultScenesInstall.currentScenesState.code != CurrentScenesCode.PRESET_INSTALL_DONE)
        {
            defaultScenesInstall.show()
        }

        /*val timer=Timer()
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
        */

        loadFromBinaryFiles()
        midiHostHandler?.startMidiDeviceListener()
        midiHostHandler?.let {
            if(it.midiDevicesIn.size > 0)
            {
                it.connectMidiDeviceIn(it.midiDevicesIn[0])
            }
            if(it.midiDevicesOut.size > 0)
            {
                it.connectMidiDeviceOut(it.midiDevicesOut[0])
            }
        }
    }

    override fun onResume() {
        super.onResume()
        audioEngine.startEngine()
        if (mainMenu!=null) {
            (mainMenu!!.findItem(R.id.menuitem_scenes)!!.actionView as Spinner).adapter
            oldScenePosition=-1
            loadSceneWithWaitIndicator((mainMenu!!.findItem(R.id.menuitem_scenes)!!.actionView as Spinner).selectedItemPosition)
        }
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
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window, findViewById(R.id.mainLayout)).let { controller ->
            controller.hide(WindowInsetsCompat.Type.systemBars())
            controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
        scenesArrayAdapter = ArrayAdapter<SceneP>(this, android.R.layout.simple_spinner_item,allScenes)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
        {
            ActivityCompat.requestPermissions(this,
                arrayOf(android.Manifest.permission.READ_MEDIA_AUDIO,
                    android.Manifest.permission.READ_MEDIA_IMAGES,
                    android.Manifest.permission.READ_MEDIA_IMAGES,
                    android.Manifest.permission.MANAGE_EXTERNAL_STORAGE,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    android.Manifest.permission.INTERNET
                ),
                PackageManager.PERMISSION_GRANTED)
        }
        else
        {
            ActivityCompat.requestPermissions(this,
                arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    android.Manifest.permission.INTERNET
                ),
                PackageManager.PERMISSION_GRANTED)
        }


        midiHostHandler =  MidiHostHandler(this)
        nsdHandler= NetworkDiscoveryHandler(this)
        rtpMidiServer=RtpMidiServer()

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
        super.onDestroy()
        val f = File("default.scn")
        if (f.exists())
        {
            f.delete()
        }
        soundGenerators.flatMap { sg -> sg.voices }.forEach { el -> el.detachFromAudioEngine() }
        audioEngine.stopEngine()

    }

    override fun onPause() {
        super.onPause()

        persistCurrentScene()
        saveToBinaryFiles()
        midiHostHandler?.stopMidiDeviceListener()

        if (supportFragmentManager.fragments[0].tag!=null
            && supportFragmentManager.fragments[0].tag.equals("PlayPage0"))
        {
            for (te in touchElements) {
                (supportFragmentManager.fragments[0].view as PlayArea).removeView(te)
            }
        }
        soundGenerators.flatMap { sg -> sg.voices }.forEach { el -> el.detachFromAudioEngine() }
        audioEngine.stopEngine()
    }


    override fun onStop() {
        super.onStop()


    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu,menu)
        mainMenu=menu
        val spinnerScenes = menu?.findItem(R.id.menuitem_scenes)?.actionView as Spinner

        scenesArrayAdapter?.let {
            it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            it.setNotifyOnChange(true)
            spinnerScenes.adapter = it
        }

        if (oldScenePosition >=0 && oldScenePosition < allScenes.size) {
            spinnerScenes.setSelection(oldScenePosition, false)
        }
        else
        {
            spinnerScenes.setSelection(0, false)
        }
        loadSceneWithWaitIndicator((mainMenu!!.findItem(R.id.menuitem_scenes)!!.actionView as Spinner).selectedItemPosition)
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
        if ((position != oldScenePosition || scenesListDirty) && position > -1) {
            loadSceneWithWaitIndicator(position)
            scenesListDirty=false
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

    fun saveToBinaryFiles()
    {
        val mainDir = File(filesDir.absolutePath)
        mainDir.listFiles { f -> f.isFile && f.name.endsWith(".scn") }?.forEach {
            it.delete()
        }
        Log.i(TAG, "save to files")
        for ((cnt, scn) in allScenes.withIndex()) {
            val f = File(filesDir.absolutePath + File.separator + "%03dscene.scn".format(cnt))
            Log.i(TAG, "writing file %s".format(f.name))
            Log.i(TAG, scn.toString())
            for (instr in scn.instruments) {
                Log.i(TAG, instr.toString())
            }
            for (te in scn.touchElements) {
                Log.i(TAG, te.toString())
            }
            scn.toFile(f)
        }
    }

    fun loadFromBinaryFiles()
    {
        allScenes.clear()
        val fDir = this.filesDir
        val sceneFiles = fDir.listFiles { fn -> fn.isFile && fn.name.endsWith("scn") }
        if (sceneFiles != null) {
            sceneFiles.sort()
            Log.i(TAG, "restoring from Files")
            sceneFiles.forEach {
                try {
                    Log.i(TAG, "reading file %s".format(it.name))
                    SceneP.fromFile(it)?.let { it1 ->
                        allScenes.add(it1)
                        Log.i(TAG, it1.toString())
                        for (instr in it1.instruments) {
                            Log.i(TAG, instr.toString())
                        }
                        for (te in it1.touchElements) {
                            Log.i(TAG, te.toString())
                        }
                    }
                } catch (e: Exception) {
                    it.delete()
                }
            }
        }
    }

    fun loadSceneWithWaitIndicator(position: Int)
    {
        if (allScenes.size==0 || position==oldScenePosition || position < 0 || position > allScenes.size-1)
        {
            return
        }
        val mainLayout = findViewById<ConstraintLayout>(R.id.mainLayout)
        val waitAnimation= WaitAnimation(this,null)
        val constraintLayout = ConstraintLayout.LayoutParams(Converter.toPx(64),Converter.toPx(64))
        constraintLayout.topToTop= ConstraintLayout.LayoutParams.PARENT_ID
        constraintLayout.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
        constraintLayout.startToStart  = ConstraintLayout.LayoutParams.PARENT_ID
        constraintLayout.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
        waitAnimation.layoutParams = constraintLayout
        (mainLayout as ViewGroup).addView(waitAnimation)
        waitAnimation.startAnimation()
        oldScenePosition = position
        val executor=Executors.newSingleThreadExecutor()
        val handler = Handler(Looper.getMainLooper())
        //unload the current scene
        soundGenerators.flatMap { sg -> sg.voices }
            .forEach { el -> el.detachFromAudioEngine() }

        executor.execute {
            allScenes[position].populate(soundGenerators, touchElements, this)

            handler.post {

                // load the new scene
                if (supportFragmentManager.fragments[0].tag != null) {
                    if (supportFragmentManager.fragments[0].tag.equals("PlayPage0")) {
                        val remainingChildren = ArrayList<View>()
                        (supportFragmentManager.fragments[0].view as ViewGroup).children.filter { v  -> v !is TouchElement }.forEach {
                            el -> remainingChildren.add(el)
                        }
                        (supportFragmentManager.fragments[0].view as ViewGroup).removeAllViews()
                        remainingChildren.asIterable().forEach {
                            rc -> (supportFragmentManager.fragments[0].view as ViewGroup).addView(rc)
                        }
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
                        (supportFragmentManager.fragments[0] as InstrumentsPageFragment).selectInstrument(
                            0,
                            true
                        )
                    }
                    supportFragmentManager.fragments[0].view?.invalidate()
                }
                waitAnimation.stopAnimation()
                (mainLayout as ViewGroup).removeView(waitAnimation)
            }
        }
    }

    fun persistCurrentScene()
    {
        if (mainMenu != null) {
            val scenePos =
                (mainMenu!!.findItem(R.id.menuitem_scenes)!!.actionView as Spinner).selectedItemPosition
            if (scenePos < allScenes.size && scenePos > -1) {
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

    fun getScenesList(): List<SceneP>
    {
        return allScenes.toList()
    }


}