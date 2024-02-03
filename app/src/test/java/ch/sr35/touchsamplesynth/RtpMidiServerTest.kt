package ch.sr35.touchsamplesynth

import ch.sr35.touchsamplesynth.network.RtpMidiJournal
import org.junit.Assert
import org.junit.Test
import kotlin.random.Random

class RtpMidiServerTest {

    @Test
    fun journalTestNoNewerThanFuture()
    {
        val journal = RtpMidiJournal(128)

        journal.push(byteArrayOf(0x90.toByte(),0x01,0x40),3452u)
        journal.push(byteArrayOf(0x80.toByte(),0x11,0x40),3453u)
        journal.push(byteArrayOf(0x90.toByte(),0x21,0x40),3454u)
        journal.push(byteArrayOf(0x80.toByte(),0x31,0x40),3455u)
        journal.push(byteArrayOf(0x90.toByte(),0x41,0x40),3456u)


        val idxes = journal.getIndexesOfNewerThan(7675u)
        Assert.assertEquals(idxes[0],idxes[1])
    }

    @Test
    fun journalTestNoNewerThan()
    {
        val journal = RtpMidiJournal(128)

        journal.push(byteArrayOf(0x90.toByte(),0x01,0x40),3452u)
        journal.push(byteArrayOf(0x80.toByte(),0x11,0x40),3453u)
        journal.push(byteArrayOf(0x90.toByte(),0x21,0x40),3454u)
        journal.push(byteArrayOf(0x80.toByte(),0x31,0x40),3455u)
        journal.push(byteArrayOf(0x90.toByte(),0x41,0x40),3456u)


        val idxes = journal.getIndexesOfNewerThan(3454u)
        Assert.assertTrue(idxes[0]<idxes[1])
    }


    @Test
    fun convertShortToIntTimestamp()
    {   val midiData=ByteArray(3)
        val journal = RtpMidiJournal(128)
        var sequenceNumber=65534u

        for (c in 0..10) {
            Random.nextBytes(midiData)
            journal.push(midiData,sequenceNumber)
            sequenceNumber += 1u
            sequenceNumber = sequenceNumber and 0xFFFFu
        }
        journal.current()?.let {
            Assert.assertTrue(it.get32bitTimestamp()>0xFFFFu)
        }
    }
}