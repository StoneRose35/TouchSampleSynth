package ch.sr35.touchsamplesynth.model

import android.content.Context
import ch.sr35.touchsamplesynth.audio.instruments.InstrumentI
import ch.sr35.touchsamplesynth.views.TouchElement
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.io.Serializable
import java.util.Collections
import java.util.UUID


class SceneP : Serializable, Cloneable {
    var instruments = ArrayList<InstrumentP>()
    var touchElements = Collections.synchronizedList( ArrayList<TouchElementP>())
    var name = ""


    fun populate(
        sg: ArrayList<InstrumentI>,
        tels: ArrayList<TouchElement>,
        context: Context
    ) {

        sg.clear()
        tels.clear()
        val remainingTouchElements=ArrayList<TouchElementP>()
        remainingTouchElements.addAll(touchElements)
        for (pi in instruments) {
            val instr = PersistableInstrumentFactory.toInstrument(pi, context)
            sg.add(instr)
            // generate all touchElements which use the current instrument
            for (c in 0 until touchElements.size)
            {
                val te = touchElements[c]
                if (te.soundGeneratorId == pi.id) {
                    val touchElement = TouchElement(context, null)
                    te.toTouchElement(touchElement)
                    touchElement.soundGenerator = instr
                    tels.add(touchElement)
                }
            }
            remainingTouchElements.removeIf { te -> te.soundGeneratorId == pi.id }
        }

        // only touchElements with no soundGenerator should be created at this point
        for (te in remainingTouchElements) {
            val touchElement = TouchElement(context, null)
            te.toTouchElement(touchElement)
            tels.add(touchElement)
        }
    }

    fun persist(soundGenerators: ArrayList<InstrumentI>,
                touchEls: ArrayList<TouchElement>)
    {

        instruments.clear()
        touchElements.clear()
        var uuid: String
        val instrumentIWithIds = HashMap<InstrumentI,String>()
        for(sg in soundGenerators)
        {
            val pi = PersistableInstrumentFactory.fromInstrument(sg)
            uuid = UUID.randomUUID().toString()
            instrumentIWithIds[sg] = uuid
            pi.id = uuid
            instruments.add(pi)
        }
        for (te in touchEls)
        {
            val pte = TouchElementP(0,0,0,0, TouchElement.ActionDir.HORIZONTAL_LR_VERTICAL_UD,ArrayList<Int>(),null,0,3,4,"")
            pte.fromTouchElement(te)
            pte.soundGeneratorId = instrumentIWithIds[te.soundGenerator].toString()
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
            klon.instruments.add(instr.clone() as InstrumentP)
        }
        for (te in this.touchElements)
        {
            klon.touchElements.add(te.clone() as TouchElementP)
            //klon.touchElements[klon.touchElements.size-1].soundGeneratorId=te.soundGeneratorId//klon.instruments[this.instruments.indexOf(te.soundGenerator)]

        }
        klon.name = this.name
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