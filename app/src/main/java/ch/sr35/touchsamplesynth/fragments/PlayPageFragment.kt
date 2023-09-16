package ch.sr35.touchsamplesynth.fragments

import android.content.res.Resources
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ToggleButton
import androidx.constraintlayout.widget.ConstraintLayout
import ch.sr35.touchsamplesynth.R
import ch.sr35.touchsamplesynth.TouchSampleSynthMain
import ch.sr35.touchsamplesynth.views.TouchElement
import ch.sr35.touchsamplesynth.views.VuMeter


/**
 * A simple [Fragment] subclass.
 */
class PlayPageFragment : Fragment() {

    val Int.px: Int
        get() = (this * Resources.getSystem().displayMetrics.density).toInt()
    val Int.dp: Int
        get() = (this / Resources.getSystem().displayMetrics.density).toInt()

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
        val playPageLayout = view.findViewById<ConstraintLayout>(R.id.playpage_layout)


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

            val lp = ConstraintLayout.LayoutParams(134.px,166.px)
            lp.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
            lp.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
            lp.marginStart = 10.px
            lp.topMargin = 10.px
            val te = TouchElement(context as TouchSampleSynthMain,null)
            te.setEditmode(true)
            te.layoutParams = lp
            playPageLayout.addView(te)
            (context as TouchSampleSynthMain).touchElements.add(te)
        }

        view.post {
            for (te in (context as TouchSampleSynthMain).touchElements) {
                //(te.layoutParams as ConstraintLayout.LayoutParams).topMargin = (playPageLayout.height.dp - te.layoutParams.height.dp - 10).px
                (view as ConstraintLayout).addView(te)
            }
            view.invalidate()
        }
    }

    override fun onDestroyView() {
        for (te in (context as TouchSampleSynthMain).touchElements) {
            (view as ConstraintLayout).removeView(te)
        }
        super.onDestroyView()
    }



    companion object {

    }
}