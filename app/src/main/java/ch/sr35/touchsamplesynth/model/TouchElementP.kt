package ch.sr35.touchsamplesynth.model

import androidx.constraintlayout.widget.ConstraintLayout
import ch.sr35.touchsamplesynth.MusicalPitch
import ch.sr35.touchsamplesynth.views.TouchElement
import java.io.Serializable

class TouchElementP(var width: Int,
                    var height: Int,
                    var posX:Int,
                    var posY: Int,
                    var actionDir: TouchElement.ActionDir,
                    var note: Int,
                    var voiceNr: Int,
                    var soundGenerator: PersistableInstrument?
): Serializable, Cloneable {

    fun fromTouchElement(touchElement: TouchElement)
    {
        width = touchElement.width
        height = touchElement.height
        posX = (touchElement.layoutParams as ConstraintLayout.LayoutParams).leftMargin
        posY = (touchElement.layoutParams as ConstraintLayout.LayoutParams).topMargin
        actionDir = touchElement.actionDir
        note = touchElement.note?.index ?: -1
        voiceNr = touchElement.voiceNr
        soundGenerator = PersistableInstrumentFactory.fromInstrument(touchElement.soundGenerator)
    }

    fun toTouchElement(te: TouchElement)
    {
        val lp = ConstraintLayout.LayoutParams(width, height)
        val allNotes = MusicalPitch.generateAllNotes()
        lp.topToTop = 0
        lp.startToStart = 0
        lp.leftMargin = posX
        lp.topMargin = posY
        te.layoutParams = lp
        te.note = allNotes[note]
        te.actionDir = actionDir
    }

    public override fun clone(): Any {
        return TouchElementP(this.width,this.height,this.posX,this.posY,this.actionDir,this.note,this.voiceNr,null)
    }
}