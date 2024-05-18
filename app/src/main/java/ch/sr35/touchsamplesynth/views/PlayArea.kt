package ch.sr35.touchsamplesynth.views

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet

import androidx.constraintlayout.widget.ConstraintLayout

class PlayArea(context: Context,attributeSet: AttributeSet): ConstraintLayout(context,attributeSet) {

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // TODO  draw lines from instrument visualizations to touchElement when in edit mode
    }
}