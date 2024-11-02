package ch.sr35.touchsamplesynth

import ch.sr35.touchsamplesynth.views.TouchElement


interface TouchElementSelectedListener {

    fun onTouchElementSelected(touchElement: TouchElement)
    fun onTouchElementDeselected(touchElement: TouchElement)

}