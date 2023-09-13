package ch.sr35.touchsamplesynth

import android.content.Context
import android.content.res.Resources
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.AttributeSet
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import ch.sr35.touchsamplesynth.audio.AudioEngineK
import ch.sr35.touchsamplesynth.audio.MusicalSoundGenerator
import ch.sr35.touchsamplesynth.audio.SineMonoSynthK
import ch.sr35.touchsamplesynth.databinding.ActivityMainBinding
import ch.sr35.touchsamplesynth.fragments.InstrumentsPageFragment
import ch.sr35.touchsamplesynth.fragments.PlayPageFragment
import ch.sr35.touchsamplesynth.views.TouchElement
import ch.sr35.touchsamplesynth.views.VuMeter
import java.util.Timer
import java.util.TimerTask

class TouchSampleSynthMain : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    val audioEngine: AudioEngineK=AudioEngineK()
    val soundGenerators=ArrayList<MusicalSoundGenerator>()
    val touchElements=ArrayList<TouchElement>()
    val Int.px: Int
        get() = (this * Resources.getSystem().displayMetrics.density).toInt()

    init {

    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val allNotes = MusicalPitch.generateAllNotes()
        val synth = SineMonoSynthK()
        synth.bindToAudioEngine()
        soundGenerators.add(synth)
        var lp = ConstraintLayout.LayoutParams(134.px,166.px)
        lp.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
        lp.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
        lp.marginStart = 92.px
        lp.topMargin = 596.px

        var te = TouchElement(this,null)
        te.soundGenerator = synth
        te.note = allNotes[44]
        te.layoutParams = lp
        te.setEditmode(false)
        touchElements.add(te)

        lp = ConstraintLayout.LayoutParams(134.px,166.px)
        lp.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
        lp.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
        lp.marginStart = 288.px
        lp.topMargin = 596.px

        te = TouchElement(this,null)
        te.soundGenerator = synth
        te.note = allNotes[44+5]
        te.layoutParams = lp
        te.setEditmode(false)
        touchElements.add(te)

        lp = ConstraintLayout.LayoutParams(134.px,166.px)
        lp.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
        lp.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
        lp.marginStart = 484.px
        lp.topMargin = 596.px

        te = TouchElement(this,null)
        te.soundGenerator = synth
        te.note = allNotes[44+7]
        te.layoutParams = lp
        te.setEditmode(false)
        touchElements.add(te)

        lp = ConstraintLayout.LayoutParams(134.px,166.px)
        lp.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
        lp.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
        lp.marginStart = 680.px
        lp.topMargin = 596.px

        te = TouchElement(this,null)
        te.soundGenerator = synth
        te.note = allNotes[44+9]
        te.layoutParams = lp
        te.setEditmode(false)
        touchElements.add(te)

        val playPage = PlayPageFragment()
        putFragment(playPage,"PlayPage0")
        audioEngine.startEngine()

        val timer=Timer()
        val timerTask= object: TimerTask()
        {
            override fun run() {
                val avgVol = audioEngine.getAverageVolume()
                findViewById<VuMeter>(R.id.vuMeter)?.updateVuLevel(avgVol*2.0f)
            }
        }
        timer.schedule(timerTask,0,100)
    }

    override fun onCreateView(name: String, context: Context, attrs: AttributeSet): View? {
        return super.onCreateView(name, context, attrs)
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
        audioEngine.stopEngine()
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
                val playPage = PlayPageFragment()
                putFragment(playPage,"PlayPage0")
            }
            R.id.menuitem_instruments ->
            {
                val instrumentPage = InstrumentsPageFragment()
                putFragment(instrumentPage,"instrumentPage0")
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