package ch.sr35.touchsamplesynth.model

import android.content.Context
import android.util.Log
import ch.sr35.touchsamplesynth.TAG
import ch.sr35.touchsamplesynth.audio.InstrumentI
import ch.sr35.touchsamplesynth.views.TouchElement
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.io.Serializable
import java.util.UUID


class SceneP : Serializable, Cloneable {
    var instruments = ArrayList<PersistableInstrument>()
    var touchElements = ArrayList<TouchElementP>()
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
            if (instr != null) {
                sg.add(instr)
                // generate all touchElements which use the current instrument
                touchElements.stream().filter { te -> te.soundGeneratorId == pi.id }.forEach {
                    val touchElement = TouchElement(context, null)
                    it.toTouchElement(touchElement)
                    touchElement.soundGenerator = instr
                    tels.add(touchElement)
                }
                remainingTouchElements.removeIf { te -> te.soundGeneratorId == pi.id }
            }
            else
            {
                Log.e(TAG,"failed to instantiate ${pi}")
            }
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
            pi!!.id = uuid
            instruments.add(pi)
        }
        for (te in touchEls)
        {
            val pte = TouchElementP(0,0,0,0, TouchElement.ActionDir.HORIZONTAL_LEFT_RIGHT,0,null,0,3,"")
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
            klon.instruments.add(instr.clone() as PersistableInstrument)
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