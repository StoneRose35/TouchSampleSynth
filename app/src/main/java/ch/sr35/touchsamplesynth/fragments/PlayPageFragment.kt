package ch.sr35.touchsamplesynth.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ToggleButton
import androidx.constraintlayout.widget.ConstraintLayout
import ch.sr35.touchsamplesynth.MusicalPitch
import ch.sr35.touchsamplesynth.R
import ch.sr35.touchsamplesynth.TouchSampleSynthMain
import ch.sr35.touchsamplesynth.audio.SineMonoSynthK
import ch.sr35.touchsamplesynth.views.TouchElement
import ch.sr35.touchsamplesynth.views.VuMeter


/**
 * A simple [Fragment] subclass.
 */
class PlayPageFragment : Fragment() {

    private val exampleSynth: SineMonoSynthK = SineMonoSynthK()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_play_page, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val startButton = view.findViewById<ImageButton>(R.id.buttonStart)
        val stopButton = view.findViewById<ImageButton>(R.id.buttonStop)
        val newButton = view.findViewById<Button>(R.id.buttonNew)
        val touchElement = view.findViewById<TouchElement>(R.id.touchElement)


        exampleSynth.bindToAudioEngine()
        exampleSynth.setAttack(0.1f)
        exampleSynth.setDecay(0.1f)
        exampleSynth.setSustain(1.0f)
        exampleSynth.setRelease(0.1f)
        exampleSynth.setNote(12.0f)
        touchElement.soundGenerator =exampleSynth
        touchElement.note = MusicalPitch.generateAllNotes()[44]
        (context as TouchSampleSynthMain).soundGenerators.add(exampleSynth)
        (context as TouchSampleSynthMain).touchElements.add(touchElement)

        val toggleButton = view.findViewById<ToggleButton>(R.id.toggleEdit)
        toggleButton.setOnCheckedChangeListener { _, toggleval ->

            if (toggleval)
            {
                for (touchel: TouchElement in (context as TouchSampleSynthMain).touchElements)
                {
                    touchel.setEditmode(true)
                }
                startButton.visibility = View.INVISIBLE
                stopButton.visibility = View.INVISIBLE
                newButton.visibility = View.VISIBLE
            }
            else
            {
                for (touchel: TouchElement in (context as TouchSampleSynthMain).touchElements)
                {
                    touchel.setEditmode(false)
                }
                startButton.visibility = View.VISIBLE
                stopButton.visibility = View.VISIBLE
                newButton.visibility = View.INVISIBLE
            }
        }

        val vuMeter = view.findViewById<VuMeter>(R.id.vuMeter)

        startButton.setOnClickListener {
            (context as TouchSampleSynthMain).audioEngine.startEngine()
            vuMeter.setActive(true)
        }

        stopButton.setOnClickListener{
            (context as TouchSampleSynthMain).audioEngine.stopEngine()
            vuMeter.setActive(false)
        }

        newButton.setOnClickListener {
            val layout=view.findViewById<ConstraintLayout>(R.id.playpage_layout)
            val lp = ConstraintLayout.LayoutParams(320,320)
            lp.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
            lp.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
            lp.marginStart = 32
            lp.topMargin = 32
            val te = TouchElement(context as TouchSampleSynthMain,null)
            te.setEditmode(true)
            te.layoutParams = lp
            layout.addView(te)
            (context as TouchSampleSynthMain).touchElements.add(te)
        }
    }

    companion object {

    }
}