package ch.sr35.touchsamplesynth.model

import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.graphics.blue
import androidx.core.graphics.green
import androidx.core.graphics.red
import ch.sr35.touchsamplesynth.MusicalPitch
import ch.sr35.touchsamplesynth.graphics.RgbColor
import ch.sr35.touchsamplesynth.views.TouchElement
import java.io.Serializable
import java.util.stream.Collectors

class TouchElementP(var width: Int,
                    var height: Int,
                    var posX:Int,
                    var posY: Int,
                    var actionDir: TouchElement.ActionDir,
                    var notes: ArrayList<Int>,
                    var color: RgbColor?,
                    var midiChannel: Int,
                    var midiCC: Int,
                    var soundGeneratorId: String
): Serializable, Cloneable {

    fun fromTouchElement(touchElement: TouchElement) {
        width = touchElement.layoutParams.width
        height = touchElement.layoutParams.height
        posX = (touchElement.layoutParams as ConstraintLayout.LayoutParams).leftMargin
        posY = (touchElement.layoutParams as ConstraintLayout.LayoutParams).topMargin
        actionDir = touchElement.actionDir
        notes.addAll(touchElement.notes.stream().map { it.index }.collect(Collectors.toList()))
        midiChannel = touchElement.midiChannel
        midiCC = touchElement.midiCC
        color = touchElement.getMainColor()
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
        te.notes.clear()
        notes.forEach {
            note ->
            if (note >= 0 && note < allNotes.size) {
                te.notes.add(allNotes[note])
            }
        }
        color?.let {
            te.setColor(it)
        }
        te.midiChannel = midiChannel
        te.midiCC = midiCC
        te.actionDir = actionDir
    }

    override fun toString(): String
    {
        return "TouchElement, w: %d, h: %d, x: %d, y: %d, actionDir: %s, notes: %s, soundGen: %s"
            .format(this.width,this.height,this.posX,this.posY,this.actionDir,this.notes, this.soundGeneratorId)
    }

    public override fun clone(): Any {
        return TouchElementP(this.width,
            this.height,
            this.posX,
            this.posY,
            this.actionDir,
            this.notes,
            this.color?.clone(),
            this.midiChannel,
            this.midiCC,
            this.soundGeneratorId)
    }
}