package ch.sr35.touchsamplesynth.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
import ch.sr35.touchsamplesynth.MusicalPitch
import ch.sr35.touchsamplesynth.R
import ch.sr35.touchsamplesynth.TouchSampleSynthMain
import ch.sr35.touchsamplesynth.graphics.Converter
import ch.sr35.touchsamplesynth.graphics.Point
import ch.sr35.touchsamplesynth.graphics.Rectangle
import ch.sr35.touchsamplesynth.graphics.TouchElementPlacementCalculator
import ch.sr35.touchsamplesynth.views.InstrumentChip
import ch.sr35.touchsamplesynth.views.PlayArea
import ch.sr35.touchsamplesynth.views.TouchElement
import ch.sr35.touchsamplesynth.views.TouchElementRecorder


/**
 * A simple [Fragment] subclass.
 */
class PlayPageFragment : Fragment() {


    class DeletableGlobalLayoutListener(val view: View,var playPageAreaRect: Rectangle): ViewTreeObserver.OnGlobalLayoutListener
    {
        override fun onGlobalLayout() {
            playPageAreaRect.topLeft.x=0.0
            playPageAreaRect.topLeft.y=0.0
            playPageAreaRect.bottomRight.x=view.width.toDouble()
            playPageAreaRect.bottomRight.y=view.height.toDouble()
            view.viewTreeObserver.removeOnGlobalLayoutListener(this)
        }

    }

    var playPageAreaRect: Rectangle?=null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        val playPageView = inflater.inflate(R.layout.fragment_play_page, container, false)
        playPageAreaRect= Rectangle(Point(0.0,0.0), Point(1.0,1.0))

        return playPageView
    }


    fun setEditMode(toggleval: Boolean)
    {
        val playPageLayout = view?.findViewById<PlayArea>(R.id.playpage_layout)
        val instrumentChipsContainer= view?.findViewById<LinearLayout>(R.id.playpage_instrument_chips)
        if (toggleval)
        {
            for (touchel: TouchElement in (context as TouchSampleSynthMain).touchElements)
            {
                touchel.setEditmode(TouchElement.TouchElementState.EDITING)
            }

            instrumentChipsContainer?.removeAllViewsInLayout()

            (context as TouchSampleSynthMain).soundGenerators.forEach { it ->
                val instrChip = InstrumentChip(context as TouchSampleSynthMain,null)
                instrChip.setInstrument(it)
                instrChip.invalidate()
                instrChip.setOnClickListener { ic ->
                    val lp = ConstraintLayout.LayoutParams(Converter.toPx(134),Converter.toPx(166))
                    lp.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
                    lp.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
                    lp.marginStart = Converter.toPx(60)
                    lp.topMargin = Converter.toPx(10)
                    val te: TouchElement = if (instrChip.getInstrument()!!.getType() == "Sampler") {
                        TouchElementRecorder(context as TouchSampleSynthMain, null)
                    } else {
                        TouchElement(context as TouchSampleSynthMain, null)
                    }
                    te.notes.add(MusicalPitch.generateAllNotes()[63])
                    te.soundGenerator = (ic as InstrumentChip).getInstrument()
                    te.defineDefaultMode((context as TouchSampleSynthMain).touchElementsDisplayMode)
                    te.setEditmode(TouchElement.TouchElementState.EDITING)
                    te.layoutParams = lp


                    val allrectrangles = (context as TouchSampleSynthMain).touchElements.map {
                        it.asRectangle() }.toTypedArray()
                    val neighbouringRectangles = (context as TouchSampleSynthMain).touchElements.filter {
                            te1 ->
                        te1.soundGenerator!! == te.soundGenerator
                    }.map { it.asRectangle() }.toTypedArray()



                    val finalLocation = TouchElementPlacementCalculator.calculateBestPlacement(te.asRectangle(),neighbouringRectangles,allrectrangles,playPageAreaRect)
                    (te.layoutParams as ConstraintLayout.LayoutParams).topMargin= finalLocation.topLeft.y.toInt()
                    (te.layoutParams as ConstraintLayout.LayoutParams).marginStart = finalLocation.topLeft.x.toInt()
                    (context as TouchSampleSynthMain).touchElements.add(te)
                    playPageLayout?.addView(te)
                    te.onSelectedListener = playPageLayout

                }
                instrumentChipsContainer?.addView(instrChip)
            }

            (context as TouchSampleSynthMain).lockSceneSelection()
        }
        else
        {
            for (touchel: TouchElement in (context as TouchSampleSynthMain).touchElements)
            {
                touchel.setDefaultmode()
            }
            playPageLayout?.invalidate()
            instrumentChipsContainer?.removeAllViewsInLayout()
            (context as TouchSampleSynthMain).persistCurrentScene()
            (context as TouchSampleSynthMain).unlockSceneSelection()
            playPageLayout?.clearTouchElementSelection()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (context==null)
        {
            return
        }
        val playPageLayout = view.findViewById<PlayArea>(R.id.playpage_layout)
        val instrumentChipsContainer= view.findViewById<LinearLayout>(R.id.playpage_instrument_chips)
        playPageLayout.instrumentChipContainer = instrumentChipsContainer

        val globalLayoutListener =
            playPageAreaRect?.let { DeletableGlobalLayoutListener(this.requireView(), it) }
        view.viewTreeObserver.addOnGlobalLayoutListener(globalLayoutListener)

        view.post {
            if (!(context as TouchSampleSynthMain).sceneIsLoading.get()) {
                val height = view.height
                val width = view.width
                var touchElementWidth = Converter.toPx(134)
                var touchElementSpacingX = Converter.toPx(10)
                var touchElementHeight = Converter.toPx(166)
                var teCntr = 0
                for (te in (context as TouchSampleSynthMain).touchElements) {

                    Log.i(
                        "TouchSampleSynth",
                        "drawing %d TouchElements".format((context as TouchSampleSynthMain).touchElements.size)
                    )
                    if (te.layoutParams == null) {
                        if (Converter.toPx((134 + 10) * 4) > width) {
                            touchElementWidth = width / 4 * Converter.toPx(134 / (134 + 10))
                            touchElementSpacingX = width / 4 * Converter.toPx(134 / (134 + 10))
                        }
                        if (Converter.toPx(166 + 10) > height) {
                            touchElementHeight = height - Converter.toPx(10)
                        }

                        val lp =
                            ConstraintLayout.LayoutParams(touchElementWidth, touchElementHeight)
                        lp.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
                        lp.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
                        lp.marginStart =
                            touchElementSpacingX + (touchElementWidth + touchElementSpacingX) * teCntr
                        lp.topMargin = height - touchElementHeight - Converter.toPx(10)
                        te.layoutParams = lp
                        teCntr += 1
                    }
                    view.findViewById<ConstraintLayout>(R.id.playpage_layout).addView(te)
                    te.onSelectedListener = playPageLayout
                }

                view.invalidate()
            }
        }
    }

    override fun onDestroyView() {
        for (te in (context as TouchSampleSynthMain).touchElements) {
            view?.findViewById<ConstraintLayout>(R.id.playpage_layout)?.removeView(te)
        }
        super.onDestroyView()
    }



}