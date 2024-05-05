package ch.sr35.touchsamplesynth.model

import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.graphics.blue
import androidx.core.graphics.green
import androidx.core.graphics.red
import ch.sr35.touchsamplesynth.MusicalPitch
import ch.sr35.touchsamplesynth.graphics.RgbColor
import ch.sr35.touchsamplesynth.views.TouchElement
import java.io.Serializable

class TouchElementP(var width: Int,
                    var height: Int,
                    var posX:Int,
                    var posY: Int,
                    var actionDir: TouchElement.ActionDir,
                    var note: Int,
                    var voiceNr: Int,
                    var color: RgbColor?,
                    var midiChannel: Int,
                    var midiCC: Int,
                    var soundGenerator: PersistableInstrument?
): Serializable, Cloneable {

    fun fromTouchElement(touchElement: TouchElement)
    {
        width = touchElement.layoutParams.width
        height = touchElement.layoutParams.height
        posX = (touchElement.layoutParams as ConstraintLayout.LayoutParams).leftMargin
        posY = (touchElement.layoutParams as ConstraintLayout.LayoutParams).topMargin
        actionDir = touchElement.actionDir
        note = touchElement.note?.index ?: -1
        midiChannel = touchElement.midiChannel
        midiCC = touchElement.midiCC
        color = RgbColor(touchElement.fillColor.color.red,touchElement.fillColor.color.green,touchElement.fillColor.color.blue )
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
        if (note >= 0 && note < allNotes.size) {
            te.note = allNotes[note]
        }
        color?.let {
            te.fillColor.color = it.toColorInt()
        }
        te.actionDir = actionDir
    }

    override fun toString(): String
    {
        return "TouchElement, w: %d, h: %d, x: %d, y: %d, actionDir: %s, note: %s, voiceNr: %d, soundGen: %s"
            .format(this.width,this.height,this.posX,this.posY,this.actionDir,this.note, this.voiceNr, this.soundGenerator)
    }

    public override fun clone(): Any {
        return TouchElementP(this.width,
            this.height,
            this.posX,
            this.posY,
            this.actionDir,
            this.note,
            this.voiceNr,
            this.color?.clone(),
            this.midiChannel,
            this.midiCC,
            null)
    }
}