package ch.sr35.touchsamplesynth.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.widget.SwitchCompat
import androidx.constraintlayout.widget.ConstraintLayout
import ch.sr35.touchsamplesynth.MusicalPitch
import ch.sr35.touchsamplesynth.R
import ch.sr35.touchsamplesynth.TouchSampleSynthMain
import ch.sr35.touchsamplesynth.graphics.Converter
import ch.sr35.touchsamplesynth.graphics.TouchElementPlacementCalculator
import ch.sr35.touchsamplesynth.views.InstrumentChip
import ch.sr35.touchsamplesynth.views.TouchElement


/**
 * A simple [Fragment] subclass.
 */
class PlayPageFragment : Fragment() {





    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        val playPageView = inflater.inflate(R.layout.fragment_play_page, container, false)
        playPageView.setOnTouchListener { v, event ->
            when (event?.action)
            {
                MotionEvent.ACTION_DOWN-> {
                    v.performClick()
                }
                MotionEvent.ACTION_UP-> {
                    view?.findViewById<SwitchCompat>(R.id.toggleEdit)?.isChecked=false
                    return@setOnTouchListener true
                }
                else -> {
                    return@setOnTouchListener false
                }
            }
        }
        playPageView.setOnClickListener {

        }
        return playPageView
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (context==null)
        {
            return
        }
        //val newButton = view.findViewById<ImageButton>(R.id.buttonNew)
        val playPageLayout = view.findViewById<ConstraintLayout>(R.id.playpage_layout)
        val instrumentChipsContainer= view.findViewById<LinearLayout>(R.id.playpage_instrument_chips)
        //val sceneNameEditText = view.findViewById<EditText>(R.id.editTextSceneName)

        val toggleSwitch = view.findViewById<SwitchCompat>(R.id.toggleEdit)
        toggleSwitch.setOnCheckedChangeListener { _, toggleval ->
            if (toggleval)
            {
                for (touchel: TouchElement in (context as TouchSampleSynthMain).touchElements)
                {
                    touchel.setEditmode(true)
                }
                //newButton.visibility = View.VISIBLE
                //sceneNameEditText.setText((context as TouchSampleSynthMain).getCurrentSceneName())
                //sceneNameEditText.visibility = View.VISIBLE
                instrumentChipsContainer.removeAllViewsInLayout()
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
                        val te = TouchElement(context as TouchSampleSynthMain,null)
                        te.note = MusicalPitch.generateAllNotes()[63]
                        te.soundGenerator = (ic as InstrumentChip).getInstrument()
                        te.setDefaultMode((context as TouchSampleSynthMain).touchElementsDisplayMode)
                        te.setEditmode(true)
                        te.layoutParams = lp



                        val allrectrangles = (context as TouchSampleSynthMain).touchElements.map {
                            it.asRectangle() }.toTypedArray()
                        val neighbouringRectangles = (context as TouchSampleSynthMain).touchElements.filter {
                            te1 ->
                            te1.soundGenerator!! == te.soundGenerator
                        }.map { it.asRectangle() }.toTypedArray()
                        val finalLocation = TouchElementPlacementCalculator.calculateBestPlacement(te.asRectangle(),neighbouringRectangles,allrectrangles)
                        (te.layoutParams as ConstraintLayout.LayoutParams).topMargin= finalLocation!!.topLeft.y.toInt()
                        (te.layoutParams as ConstraintLayout.LayoutParams).marginStart = finalLocation.topLeft.x.toInt()
                        (context as TouchSampleSynthMain).touchElements.add(te)
                        playPageLayout.addView(te)

                    }
                    instrumentChipsContainer.addView(instrChip)
                }

                (context as TouchSampleSynthMain).lockSceneSelection()
            }
            else
            {
                for (touchel: TouchElement in (context as TouchSampleSynthMain).touchElements)
                {
                    touchel.setEditmode(false)
                }
                instrumentChipsContainer.removeAllViewsInLayout()
                //newButton.visibility = View.INVISIBLE
                //sceneNameEditText.visibility = View.INVISIBLE
                (context as TouchSampleSynthMain).persistCurrentScene()
                (context as TouchSampleSynthMain).unlockSceneSelection()
            }
        }

/*
        newButton.setOnClickListener {

            val lp = ConstraintLayout.LayoutParams(Converter.toPx(134),Converter.toPx(166))
            lp.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
            lp.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
            lp.marginStart = Converter.toPx(10)
            lp.topMargin = Converter.toPx(10)
            val te = TouchElement(context as TouchSampleSynthMain,null)
            te.setDefaultMode((context as TouchSampleSynthMain).touchElementsDisplayMode)
            te.setEditmode(true)
            te.layoutParams = lp
            playPageLayout.addView(te)
            (context as TouchSampleSynthMain).touchElements.add(te)

        }
        */
        /*
        sceneNameEditText.setOnEditorActionListener { _, actionId, _ ->
            if(actionId==EditorInfo.IME_ACTION_DONE)
            {
                (context as TouchSampleSynthMain).setCurrentSceneName(sceneNameEditText.text.toString())
                (context as TouchSampleSynthMain).persistCurrentScene()
                ((context as Context).getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(sceneNameEditText.windowToken,0)

                (((context as TouchSampleSynthMain).mainMenu?.findItem(R.id.menuitem_scenes)?.actionView as Spinner).adapter as ArrayAdapter<*>).notifyDataSetChanged()
                return@setOnEditorActionListener true
            }
            return@setOnEditorActionListener false
        }
         */

        view.post {
            val height = view.height
            val width = view.width
            var touchElementWidth = Converter.toPx(134)
            var touchElementSpacingX = Converter.toPx(10)
            var touchElementHeight = Converter.toPx(166)
            var teCntr = 0
            var hasDoneInitialLayout= false
            for (te in (context as TouchSampleSynthMain).touchElements) {

                Log.i("TouchSampleSynth","drawing %d TouchElements".format((context as TouchSampleSynthMain).touchElements.size))
                if (te.layoutParams == null)
                {
                    if (Converter.toPx((134+10)*4) > width)
                    {
                        touchElementWidth = width / 4 *Converter.toPx(134/(134+10))
                        touchElementSpacingX = width / 4 * Converter.toPx(134/(134+10))
                    }
                    if (Converter.toPx(166+10) > height)
                    {
                        touchElementHeight = height - Converter.toPx(10)
                    }

                    val lp = ConstraintLayout.LayoutParams(touchElementWidth,touchElementHeight)
                    lp.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
                    lp.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
                    lp.marginStart = touchElementSpacingX + (touchElementWidth + touchElementSpacingX)*teCntr
                    lp.topMargin = height - touchElementHeight - Converter.toPx(10)
                    te.layoutParams = lp
                    teCntr += 1
                    hasDoneInitialLayout = true
                }

                view.findViewById<ConstraintLayout>(R.id.playpage_layout).addView(te)
            }
            if (hasDoneInitialLayout)
            {
                (context as TouchSampleSynthMain).persistCurrentScene()
            }
            view.invalidate()
        }
    }

    override fun onDestroyView() {
        for (te in (context as TouchSampleSynthMain).touchElements) {
            view?.findViewById<ConstraintLayout>(R.id.playpage_layout)?.removeView(te)
        }
        super.onDestroyView()
    }


}