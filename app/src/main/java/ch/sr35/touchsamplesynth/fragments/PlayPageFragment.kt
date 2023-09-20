package ch.sr35.touchsamplesynth.fragments

import android.content.res.Resources
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ToggleButton
import androidx.constraintlayout.widget.ConstraintLayout
import ch.sr35.touchsamplesynth.R
import ch.sr35.touchsamplesynth.TouchSampleSynthMain
import ch.sr35.touchsamplesynth.views.TouchElement


/**
 * A simple [Fragment] subclass.
 */
class PlayPageFragment : Fragment() {

    val Int.px: Int
        get() = (this * Resources.getSystem().displayMetrics.density).toInt()

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
                newButton.visibility = View.VISIBLE
            }
            else
            {
                for (touchel: TouchElement in (context as TouchSampleSynthMain).touchElements)
                {
                    touchel.setEditmode(false)
                }
                newButton.visibility = View.INVISIBLE
            }
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
            val height = view.height
            val width = view.width
            var touchElementWidth = 134.px
            var touchElementSpacingX=10.px
            var touchElementHeight = 166.px
            var teCntr = 0
            for (te in (context as TouchSampleSynthMain).touchElements) {


                if (te.layoutParams == null)
                {
                    if (((134+10)*4).px > width)
                    {
                        touchElementWidth = width / 4 *(134/(134+10)).px
                        touchElementSpacingX = width / 4 * (134/(134+10)).px
                    }
                    if ((166+10).px > height)
                    {
                        touchElementHeight = height - 10.px
                    }

                    val lp = ConstraintLayout.LayoutParams(touchElementWidth,touchElementHeight)
                    lp.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
                    lp.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
                    lp.marginStart = touchElementSpacingX + (touchElementWidth + touchElementSpacingX)*teCntr
                    lp.topMargin = height - touchElementHeight - 10.px
                    te.layoutParams = lp
                    teCntr += 1
                }

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