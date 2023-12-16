package ch.sr35.touchsamplesynth.model

import android.content.Context
import ch.sr35.touchsamplesynth.audio.Instrument
import ch.sr35.touchsamplesynth.views.TouchElement
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.io.Serializable

class SceneP : Serializable, Cloneable {
    var instruments = ArrayList<PersistableInstrument>()
    var touchElements = ArrayList<TouchElementP>()
    var name = ""


    fun populate(
        sg: ArrayList<Instrument>,
        tels: ArrayList<TouchElement>,
        context: Context
    ) {

        sg.clear()
        tels.clear()
        val remainingTouchElements=ArrayList<TouchElementP>()
        remainingTouchElements.addAll(touchElements)
        for (pi in instruments) {
            val instr = PersistableInstrumentFactory.toInstrument(pi, context)
            if (instr != null) {
                sg.add(instr)
                var voiceIdx = 0
                // generate all touchElements which use the current instrument
                touchElements.stream().filter { te -> te.soundGenerator == pi }.forEach {
                    val touchElement = TouchElement(context, null)
                    it.toTouchElement(touchElement)
                    touchElement.soundGenerator = instr
                    touchElement.voiceNr = voiceIdx
                    tels.add(touchElement)
                    if (voiceIdx < pi.nVoices-1)
                    {
                        voiceIdx++
                    }
                }
                remainingTouchElements.removeIf { te -> te.soundGenerator == pi }
            }
        }

        // only touchElements with no soundGenerator should be created at this point
        for (te in remainingTouchElements) {
            val touchElement = TouchElement(context, null)
            te.toTouchElement(touchElement)
            tels.add(touchElement)
        }
    }

    fun persist(        soundGenerators: ArrayList<Instrument>,
                        touchEls: ArrayList<TouchElement>)
    {
        instruments.clear()
        touchElements.clear()
        for(sg in soundGenerators)
        {
            val pi = PersistableInstrumentFactory.fromInstrument(sg)
            instruments.add(pi!!)
        }
        for (te in touchEls)
        {
            val pte = TouchElementP(0,0,0,0, TouchElement.ActionDir.HORIZONTAL,0,0,null)
            pte.fromTouchElement(te)
            touchElements.add(pte)
        }
    }

    fun toFile(file: File)
    {
        val fos = FileOutputStream(file)
        val oos = ObjectOutputStream(fos)
        oos.writeObject(this)
        oos.close()
        fos.close()
    }

    override fun toString(): String {
        return name
    }

    public override fun clone(): Any {
        val klon=SceneP()
        for(instr in this.instruments)
        {
            klon.instruments.add(instr.clone() as PersistableInstrument)
        }
        for (te in this.touchElements)
        {
            klon.touchElements.add(te.clone() as TouchElementP)
            klon.touchElements[klon.touchElements.size-1].soundGenerator=klon.instruments[this.instruments.indexOf(te.soundGenerator)]

        }
        return klon
    }

    companion object {
        fun fromFile(file: File): SceneP? {
            val fis = FileInputStream(file)
            val ois = ObjectInputStream(fis)
            val objRead = ois.readObject()
            ois.close()
            fis.close()
            if (objRead is SceneP) {
                return objRead
            }
            return null
        }
    }
}