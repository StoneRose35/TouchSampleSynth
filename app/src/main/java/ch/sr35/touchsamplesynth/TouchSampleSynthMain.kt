package ch.sr35.touchsamplesynth

import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.ListView
import android.widget.Spinner
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.children
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import ch.sr35.touchsamplesynth.audio.AudioEngineK
import ch.sr35.touchsamplesynth.audio.InstrumentI
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
import java.util.concurrent.atomic.AtomicBoolean


const val TAG="TouchSampleSynth"
const val TSS_BUNDLE_LAST_PROGRAM = "TSS_BUNDLE_LAST_PROGRAM"
const val TSS_BUNDLE_LAST_FRAGMENT = "TSS_BUNDLE_LAST_FRAGMENT"
const val TSS_BUNDLE_EDIT_MODE = "TSS_BUNDLE_EDIT_MODE"
class TouchSampleSynthMain : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    private lateinit var binding: ActivityMainBinding
    val audioEngine: AudioEngineK=AudioEngineK()
    val soundGenerators=ArrayList<InstrumentI>()
    val touchElements=ArrayList<TouchElement>()
    val allScenes = ArrayList<SceneP>()
    var midiHostHandler :MidiHostHandler?= null
    var nsdHandler: NetworkDiscoveryHandler?=null
    var rtpMidiServer: RtpMidiServer?=null
    private var oldScenePosition=-1
    private var lastFragmentTag = ""
    var scenesListDirty=false
    var sceneIsLoading= AtomicBoolean(false)
    var scenesArrayAdapter: ArrayAdapter<SceneP>?=null

    // global settings
    var rtpMidiNotesRepeat=1 // defines how many times note on and note off commands are repeated over rtp midi
    var touchElementsDisplayMode: TouchElement.TouchElementState=TouchElement.TouchElementState.PLAYING
    var connectorDisplay = false
    private var isInEditMode = false

    override fun onStart() {
        super.onStart()

        if (lastFragmentTag.isNotEmpty())
        {
            putFragment(lastFragmentTag)
        }
        if (supportFragmentManager.fragments.isEmpty()) {
            putFragment("PlayPage0")
        }

        while(sceneIsLoading.get())
        {
            SystemClock.sleep(30)
        }
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
        val spinnerScenes = supportActionBar?.customView?.findViewById<Spinner>(R.id.toolbar_scenes)
        scenesArrayAdapter = ArrayAdapter<SceneP>(this, android.R.layout.simple_spinner_item,allScenes)
        scenesArrayAdapter?.let {
            spinnerScenes?.adapter = it
            it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            it.setNotifyOnChange(true)

        }

        spinnerScenes?.onItemSelectedListener=this
        spinnerScenes?.isEnabled = !isInEditMode
        if (oldScenePosition >=0 && oldScenePosition < allScenes.size) {
            spinnerScenes?.setSelection(oldScenePosition, false)
        }
        else
        {
            spinnerScenes?.setSelection(0, false)
            oldScenePosition = -1
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

    }

    override fun onResume() {
        super.onResume()
        audioEngine.startEngine()

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
        supportActionBar!!.displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM
        supportActionBar!!.setDisplayShowCustomEnabled(true)
        supportActionBar?.setCustomView( R.layout.top_tool_bar)

        supportActionBar?.customView?.findViewById<ImageButton>(R.id.toolbar_playpage)?.setOnClickListener {
            putFragment("PlayPage0")
            supportActionBar?.customView?.findViewById<SwitchCompat>(R.id.toolbar_edit)?.isVisible = true
        }
        supportActionBar?.customView?.findViewById<ImageButton>(R.id.toolbar_instrumentspage)?.setOnClickListener {
            putFragment("instrumentPage0")
            supportActionBar?.customView?.findViewById<SwitchCompat>(R.id.toolbar_edit)?.isVisible = false
        }
        supportActionBar?.customView?.findViewById<ImageButton>(R.id.toolbar_scenespage)?.setOnClickListener {
            putFragment("scenesEditPage0")
            supportActionBar?.customView?.findViewById<SwitchCompat>(R.id.toolbar_edit)?.isVisible = false
        }
        supportActionBar?.customView?.findViewById<ImageButton>(R.id.toolbar_settingspage)?.setOnClickListener {
            putFragment("settingsPage0")
            supportActionBar?.customView?.findViewById<SwitchCompat>(R.id.toolbar_edit)?.isVisible = false
        }
        supportActionBar?.customView?.findViewById<SwitchCompat>(R.id.toolbar_edit)?.let {
            it.setOnCheckedChangeListener { buttonView, isChecked ->
                isInEditMode = isChecked
                if (supportFragmentManager.fragments[0].tag == "PlayPage0") {
                    (supportFragmentManager.fragments[0] as PlayPageFragment).setEditMode(isChecked)
                }
                supportActionBar?.customView?.findViewById<Spinner>(R.id.toolbar_scenes)?.isEnabled = !isInEditMode
                if (!isChecked)
                {
                    findViewById<ImageButton>(R.id.toolbar_alignleft).visibility = ImageButton.INVISIBLE
                    findViewById<ImageButton>(R.id.toolbar_alignright).visibility = ImageButton.INVISIBLE
                    findViewById<ImageButton>(R.id.toolbar_aligntop).visibility = ImageButton.INVISIBLE
                    findViewById<ImageButton>(R.id.toolbar_alignbottom).visibility = ImageButton.INVISIBLE
                }
            }
        }

        supportActionBar?.customView?.findViewById<ImageButton>(R.id.toolbar_alignleft)?.setOnClickListener {
            val playArea = supportFragmentManager.fragments[0].view?.findViewById<PlayArea>(R.id.playpage_layout)
            playArea?.touchElementsSelection?.let {
                val leftmostmargin = it.stream().mapToInt { te -> (te.layoutParams as ConstraintLayout.LayoutParams).leftMargin }.min().asInt
                it.forEach {
                    te ->
                    val layoutparams = te.layoutParams as ConstraintLayout.LayoutParams
                    layoutparams.leftMargin -= layoutparams.leftMargin - leftmostmargin
                    te.layoutParams = layoutparams
                }
            }
            playArea?.invalidate()
        }
        supportActionBar?.customView?.findViewById<ImageButton>(R.id.toolbar_alignright)?.setOnClickListener {
            val playArea = supportFragmentManager.fragments[0].view?.findViewById<PlayArea>(R.id.playpage_layout)
            playArea?.touchElementsSelection?.let {
                val rightmostmargin = it.stream().mapToInt { te -> (te.layoutParams as ConstraintLayout.LayoutParams).leftMargin +  (te.layoutParams as ConstraintLayout.LayoutParams).width}.max().asInt
                it.forEach {
                        te ->
                    val layoutparams = te.layoutParams as ConstraintLayout.LayoutParams
                    layoutparams.leftMargin -= layoutparams.leftMargin  + layoutparams.width - rightmostmargin
                    te.layoutParams = layoutparams
                }
            }
            playArea?.invalidate()
        }

        supportActionBar?.customView?.findViewById<ImageButton>(R.id.toolbar_aligntop)?.setOnClickListener {
            val playArea = supportFragmentManager.fragments[0].view?.findViewById<PlayArea>(R.id.playpage_layout)
            playArea?.touchElementsSelection?.let {
                val topmostmargin = it.stream().mapToInt { te -> (te.layoutParams as ConstraintLayout.LayoutParams).topMargin }.min().asInt
                it.forEach {
                        te ->
                    val layoutparams = te.layoutParams as ConstraintLayout.LayoutParams
                    layoutparams.topMargin -= layoutparams.topMargin - topmostmargin
                    te.layoutParams = layoutparams
                }
            }
            playArea?.invalidate()
        }

        supportActionBar?.customView?.findViewById<ImageButton>(R.id.toolbar_alignbottom)?.setOnClickListener {
            val playArea = supportFragmentManager.fragments[0].view?.findViewById<PlayArea>(R.id.playpage_layout)
            playArea?.touchElementsSelection?.let {
                val bottommostmargin = it.stream().mapToInt { te -> (te.layoutParams as ConstraintLayout.LayoutParams).topMargin +  (te.layoutParams as ConstraintLayout.LayoutParams).height }.max().asInt
                it.forEach {
                        te ->
                    val layoutparams = te.layoutParams as ConstraintLayout.LayoutParams
                    layoutparams.topMargin -= layoutparams.topMargin  + layoutparams.height - bottommostmargin
                    te.layoutParams = layoutparams
                }
            }
            playArea?.invalidate()
        }




        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window, findViewById(R.id.mainLayout)).let { controller ->
            controller.hide(WindowInsetsCompat.Type.systemBars())
            controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }

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

        savedInstanceState?.let {
            scenesListDirty = true
            oldScenePosition = it.getInt(TSS_BUNDLE_LAST_PROGRAM)
            lastFragmentTag = it.getString(TSS_BUNDLE_LAST_FRAGMENT).toString()
            isInEditMode = it.getBoolean(TSS_BUNDLE_EDIT_MODE)
        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        supportActionBar?.customView?.findViewById<Spinner>(R.id.toolbar_scenes)?.let {
            outState.putInt(TSS_BUNDLE_LAST_PROGRAM,it.selectedItemPosition)
        }
            outState.putString(TSS_BUNDLE_LAST_FRAGMENT, supportFragmentManager.fragments[0].tag)
            outState.putBoolean(TSS_BUNDLE_EDIT_MODE,isInEditMode)

    }

    private fun putFragment(tag: String?)
    {
        var frag: Fragment? = null
        when(tag)
        {
            "PlayPage0" ->
            {
                frag = PlayPageFragment()
            }
            "instrumentPage0" ->
            {
                frag = InstrumentsPageFragment()
            }
            "scenesEditPage0" ->
            {
                frag = SceneFragment()
            }
            "settingsPage0"  -> {
                frag = SettingsFragment()
            }
        }
        if (supportFragmentManager.fragments.size > 0 && supportFragmentManager.fragments[0].tag == tag)
        {
            return
        }

        if (frag != null) {
            supportFragmentManager.beginTransaction().let {
                if (supportFragmentManager.findFragmentById(R.id.mainLayout) != null) {
                    it.replace(R.id.mainLayout, frag, tag)
                } else {
                    it.add(R.id.mainLayout, frag, tag)
                }
                it.commit()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        while (sceneIsLoading.get())
        {
            SystemClock.sleep(30)
        }
        soundGenerators.flatMap { sg -> sg.voices }.forEach { el -> el.detachFromAudioEngine() }
        //audioEngine.stopEngine()

    }

    override fun onPause() {
        super.onPause()
        audioEngine.stopEngine()
    }


    override fun onStop() {
        super.onStop()
        persistCurrentScene()
        saveToBinaryFiles()
        midiHostHandler?.stopMidiDeviceListener()
        if (nsdHandler?.hasStarted==true) {
            nsdHandler?.tearDown()
        }
        rtpMidiServer?.stopServer()

    }






    companion object {
        // Used to load the 'touchsamplesynth' library on application startup.
        init {
            System.loadLibrary("touchsamplesynth")
        }
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        if ((position != oldScenePosition || scenesListDirty)
            && position in 0 until allScenes.size
            ) {
            if (oldScenePosition in 0 until allScenes.size
                && !sceneIsLoading.get() && touchElements.size > 0 ) {
                allScenes[oldScenePosition].persist(soundGenerators, touchElements)
            }
            loadSceneWithWaitIndicator(position)
        }
    }


    fun saveToBinaryFiles()
    {
        while (sceneIsLoading.get())
        {
            Thread.sleep(30)
        }
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
        while (sceneIsLoading.get())
        {
            Thread.sleep(30)
        }
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
        val wasSceneLoading = sceneIsLoading.getAndSet(true)
        if (wasSceneLoading)
        {
            return
        }
        else if (allScenes.size == 0 || position < 0 || position > allScenes.size-1 || (position==oldScenePosition && !scenesListDirty))
        {
            sceneIsLoading.set(false)
            return
        }
        scenesListDirty = false
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
                            (supportActionBar!!.customView.findViewById<SwitchCompat>(R.id.toolbar_edit)).isChecked.let {
                                    if (it) {
                                        te.setEditmode(TouchElement.TouchElementState.EDITING)
                                    }
                                    else
                                    {
                                        te.setDefaultmode()
                                    }
                            }
                            te.defineDefaultMode(touchElementsDisplayMode)
                            (supportFragmentManager.fragments[0].view as ViewGroup).addView(te)
                            te.onSelectedListener = (supportFragmentManager.fragments[0] as PlayPageFragment).view?.findViewById<PlayArea>(R.id.playpage_layout)
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
                sceneIsLoading.set(false)
            }
        }
    }

    fun reloadCurrentScene()
    {
        scenesListDirty = true
        loadSceneWithWaitIndicator(oldScenePosition)
    }

    fun getCurrentScene(): SceneP?
    {
        if (oldScenePosition in 0 until allScenes.size) {
            return allScenes[oldScenePosition]
        }
        return null
    }

    fun persistCurrentScene()
    {
        if (supportActionBar?.customView != null) {
            val scenePos =
                (supportActionBar!!.customView.findViewById<Spinner>(R.id.toolbar_scenes) as Spinner).selectedItemPosition
            if (scenePos < allScenes.size && scenePos > -1) {
                allScenes[scenePos].persist(soundGenerators, touchElements)
            }
        }
    }

    fun lockSceneSelection()
    {
        isInEditMode = true
        if (supportActionBar!!.customView.findViewById<Spinner>(R.id.toolbar_scenes) != null)
        {
            (supportActionBar!!.customView.findViewById<Spinner>(R.id.toolbar_scenes) as Spinner).isEnabled = false
        }

    }

    fun unlockSceneSelection()
    {
        isInEditMode = false
        if (supportActionBar!!.customView.findViewById<Spinner>(R.id.toolbar_scenes) != null) {
            (supportActionBar!!.customView.findViewById<Spinner>(R.id.toolbar_scenes) as Spinner).isEnabled = false
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {}

    fun getScenesList(): List<SceneP>
    {
        return allScenes.toList()
    }


}