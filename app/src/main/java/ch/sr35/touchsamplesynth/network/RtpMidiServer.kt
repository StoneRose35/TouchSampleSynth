package ch.sr35.touchsamplesynth.network

import android.util.Log
import ch.sr35.touchsamplesynth.TAG
import java.io.IOException
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import java.net.SocketException
import kotlin.concurrent.thread
import kotlin.random.Random
import kotlin.random.nextUInt
// documentation used: https://developer.apple.com/library/archive/documentation/Audio/Conceptual/MIDINetworkDriverProtocol/MIDI/MIDI.html
class RtpMidiServer {
    var port: Int=0
    var controlSocket: DatagramSocket?=null
    var dataSocket: DatagramSocket?=null
    var packetBuffer= ByteArray(2048)
    var packetBufferData= ByteArray(2048)
    val connections=ArrayList<ClientConnectionData>()
    private var temporalOrigin: Long = System.nanoTime()/100000
    var ssrc: UInt = Random.nextUInt(0xFFFFFFFFu)
    var name: String="TSS Midi Server"
    var isEnabled:Boolean=false
    var journal=RtpMidiJournal(128)
    fun startServer(): Boolean
    {
        var controlPort= 1024
        var portsFound=false
        while (!portsFound && controlPort < 65535)
        {
            try {
                controlSocket = DatagramSocket(controlPort)
                dataSocket = DatagramSocket(controlPort+1)
                portsFound=true
            }
            catch (_: IOException)
            {

            }
            controlPort+=1
        }
        if (controlPort == 65535)
        {
            return false
        }
        port = controlPort-1
        thread(start = true,name="TSS Midi control", priority = 1) {
            val p = DatagramPacket(packetBuffer,packetBuffer.size)
            try {
                while(true) {
                    controlSocket?.receive(p)

                    // no known connection from where this packet is coming from, check if "IN"
                    // and allow if the max number of connections isnt exceeded

                    val cmd = getCommand(p)
                    if (cmd == "IN" && connections.size < 8) {
                        //Log.i(TAG, "got IN on Control from ${p.address} on port ${p.port}")
                        ClientConnectionData().also {
                            it.address = p.address
                            it.port = p.port
                            it.connectionState = ConnectionState.CONNECTING_OK_ON_CONTROL
                            it.initiatorToken = 0u
                            it.initiatorToken = (p.data[8].toUInt() and 0xFFu shl 24) or
                                    (p.data[9].toUInt() and 0xFFu shl 16) or
                                    (p.data[10].toUInt() and 0xFFu shl 8) or
                                    (p.data[11].toUInt() and 0xFFu shl 0)
                            it.itBytes[0] = p.data[8]
                            it.itBytes[1] = p.data[9]
                            it.itBytes[2] = p.data[10]
                            it.itBytes[3] = p.data[11]
                            it.sscr[0] = p.data[12]
                            it.sscr[1] = p.data[13]
                            it.sscr[2] = p.data[14]
                            it.sscr[3] = p.data[15]
                            connections.add(it)

                            sendCommand("OK", it, false, controlSocket!!)
                        }
                    } else if (cmd == "IN") {
                        // send NO
                        ClientConnectionData().also {
                            it.address = p.address
                            it.port = p.port
                            it.connectionState = ConnectionState.UNCONNECTED
                            it.initiatorToken = (p.data[8].toUInt() and 0xFFu shl 24) or
                                    (p.data[9].toUInt() and 0xFFu shl 16) or
                                    (p.data[10].toUInt() and 0xFFu shl 8) or
                                    (p.data[11].toUInt() and 0xFFu shl 0)
                            sendCommand("NO", it, false, controlSocket!!)
                        }
                    }
                    else if (cmd == "RS")// got receiver feedback, resend all commands with a sequence number newer than the one received
                    {
                        for (conn in connections)
                        {
                            if (conn.sscr[0] == p.data[4] &&
                                conn.sscr[1] == p.data[5] &&
                                conn.sscr[2] == p.data[6] &&
                                conn.sscr[3] == p.data[7])
                            {
                                val lastSeqReceived = (p.data[8].toUInt() shr 24) or
                                                      (p.data[9].toUInt() shr 16) or
                                                      (p.data[10].toUInt() shr 8) or
                                                      (p.data[10].toUInt() shr 0)
                                val indexes = journal.getIndexesOfNewerThan(lastSeqReceived)
                                var runningIdx=indexes[0]
                                while(runningIdx!= indexes[1])
                                {
                                    journal.get(runningIdx)?.let {
                                        sendMidiCommand(it.data)
                                    }
                                    runningIdx+=1
                                }
                            }
                        }
                    }
                }
            } catch (e: SocketException)
            {
                Log.i(TAG,"TSS Midi control socket exception occurred, probably on purpose")
            }
        }
        thread(start = true,name="TSS Midi data", priority = 10) {
            val p = DatagramPacket(packetBufferData,packetBufferData.size)
            val previousTimeStamps = ArrayList<ULong>()
            try {
                while(true) {
                    dataSocket?.receive(p)
                    for(conn in connections) {
                        if (conn.address == p.address) {
                            val cmd = getCommand(p)
                            if (cmd == "IN") {
                                //Log.i(TAG, "got IN on Midi Data from known source ${p.address}")
                                sendCommand("OK", conn, true, dataSocket!!)
                                conn.connectionState = ConnectionState.CONNECTING_OK_ON_DATA

                            } else if (cmd == "CK") {
                                //Log.i(TAG, "got CK on Midi Data from known source ${p.address}")
                                val count = p.data[8].toInt()
                                if (count == 0) {
                                    previousTimeStamps.clear()
                                    val senderSsrc = ByteArray(4)
                                    senderSsrc[0] = p.data[4]
                                    senderSsrc[1] = p.data[5]
                                    senderSsrc[2] = p.data[6]
                                    senderSsrc[3] = p.data[7]
                                    if (!senderSsrc.contentEquals(conn.sscr)) {
                                        break
                                    }
                                    conn.timestamp1 = (p.data[12].toULong() and 0xFFu shl 56) or
                                            (p.data[13].toULong() and 0xFFu shl 48) or
                                            (p.data[14].toULong() and 0xFFu shl 40) or
                                            (p.data[15].toULong() and 0xFFu shl 32) or
                                            (p.data[16].toULong() and 0xFFu shl 24) or
                                            (p.data[17].toULong() and 0xFFu shl 16) or
                                            (p.data[18].toULong() and 0xFFu shl 8) or
                                            (p.data[19].toULong() and 0xFFu shl 0)
                                    previousTimeStamps.add(conn.timestamp1)
                                    //Log.i(TAG, "got 1st timestamp ${conn.timestamp1}")
                                    conn.timestamp2 = sendTimestamp(
                                        conn,
                                        dataSocket!!,
                                        previousTimeStamps
                                    )
                                    //Log.i(TAG, "sending second timestamp ${conn.timestamp2}")
                                    conn.connectionState = ConnectionState.CONNECTING_TIME1
                                } else if (count == 2) {
                                    val senderSsrc = ByteArray(4)
                                    senderSsrc[0] = p.data[4]
                                    senderSsrc[1] = p.data[5]
                                    senderSsrc[2] = p.data[6]
                                    senderSsrc[3] = p.data[7]
                                    if (!senderSsrc.contentEquals(conn.sscr)) {
                                        break
                                    }
                                    conn.timestamp3 = (p.data[28].toULong() and 0xffu shl 56) or
                                            (p.data[29].toULong() and 0xffu shl 48) or
                                            (p.data[30].toULong() and 0xffu shl 40) or
                                            (p.data[31].toULong() and 0xffu shl 32) or
                                            (p.data[32].toULong() and 0xffu shl 24) or
                                            (p.data[33].toULong() and 0xffu shl 16) or
                                            (p.data[34].toULong() and 0xffu shl 8) or
                                            (p.data[35].toULong() and 0xffu shl 0)
                                    //Log.i(TAG, "got 3rd timestamp ${conn.timestamp3}")
                                    conn.computeTimeOffset()
                                    conn.connectionState = ConnectionState.CONNECTED
                                }
                            }
                        }
                    }
                }
            }
            catch (e: SocketException)
            {
                Log.i(TAG,"TSS Midi data socket exception occurred: probably on purpose")
            }
        }
        isEnabled=true
        return isEnabled
    }

    fun stopServer()
    {
        dataSocket?.close()
        controlSocket?.close()
        isEnabled=false
    }

    private fun sendCommand(command: String,client: ClientConnectionData,onDataPort: Boolean,s: DatagramSocket)
    {
        val message=ByteArray(128)
        message[0] = 0xFF.toByte()
        message[1] = 0xFF.toByte()
        message[2] = command[0].code.toByte()
        message[3] = command[1].code.toByte()
        message[4]=0
        message[5]=0
        message[6]=0
        message[7]=2
        message[8]=client.itBytes[0]
        message[9]=client.itBytes[1]
        message[10]=client.itBytes[2]
        message[11]=client.itBytes[3]
        message[12]=(ssrc shr 24).toByte()
        message[13]=(ssrc shr 16).toByte()
        message[14]=(ssrc shr 8).toByte()
        message[15]=(ssrc shr 0).toByte()
        val nameLimit=arrayOf(128-16,name.length).min()
        for (c in 0 until nameLimit)
        {
            message[c+16]=name[c].code.toByte()
        }
        message[nameLimit+16]=0
        val messageLength=nameLimit+16
        var portNr=client.port
        if(onDataPort)
        {
            portNr+=1
        }
        val p = DatagramPacket(message, messageLength, client.address, portNr)
        s.send(p)
    }
    private fun getCommand(packet:DatagramPacket): String
    {
        var result=""
        result = result.plus(packet.data[2].toInt().toChar())
        result = result.plus(packet.data[3].toInt().toChar())
        return result
    }

    private fun sendTimestamp(client: ClientConnectionData,s: DatagramSocket,previousTimeStamps: ArrayList<ULong>): ULong {
        val message=ByteArray(128)
        message[0] = 0xFF.toByte()
        message[1] = 0xFF.toByte()
        message[2] = 'C'.code.toByte()
        message[3] = 'K'.code.toByte()
        message[4] = (ssrc shr 24).toByte()
        message[5] = (ssrc shr 16).toByte()
        message[6] = (ssrc shr 8).toByte()
        message[7] = ssrc.toByte()
        message[8] = 1
        message[9] = 0
        message[10] = 0
        message[11] = 0

            message[12+0] = (previousTimeStamps[0] shr 56).toByte()
            message[12+1] = (previousTimeStamps[0] shr 48).toByte()
            message[12+2] = (previousTimeStamps[0] shr 40).toByte()
            message[12+3] = (previousTimeStamps[0] shr 32).toByte()
            message[12+4] = (previousTimeStamps[0] shr 24).toByte()
            message[12+5] = (previousTimeStamps[0] shr 16).toByte()
            message[12+6] = (previousTimeStamps[0] shr 8).toByte()
            message[12+7] = (previousTimeStamps[0] shr 0).toByte()


        val currentTime = System.nanoTime() /100000 - temporalOrigin
        message[12+8+0] = (currentTime shr 56).toByte()
        message[12+8+1] = (currentTime shr 48).toByte()
        message[12+8+2] = (currentTime shr 40).toByte()
        message[12+8+3] = (currentTime shr 32).toByte()
        message[12+8+4] = (currentTime shr 24).toByte()
        message[12+8+5] = (currentTime shr 16).toByte()
        message[12+8+6] = (currentTime shr 8).toByte()
        message[12+8+7] = (currentTime shr 0).toByte()
        val messageLength = 36
        val portNr=client.port+1
        val p = DatagramPacket(message, messageLength, client.address,portNr)
        s.send(p)
        return currentTime.toULong()
    }

    fun sendMidiCommand(midiData: ByteArray)
    {
        for (conn in connections)
        {
            dataSocket?.let {sendMidiCommand(midiData,it,conn)}
        }
    }
    fun sendMidiCommand(midiData: ByteArray,s:DatagramSocket, conn: ClientConnectionData)
    {
        val message=ByteArray(2048)
        message[0]=0x80.toByte()
        message[1]=0x61.toByte()//((0x61 shl 1) + 1).toByte()
        message[2]=(conn.sequenceNumber shr 8).toByte()
        message[3]=(conn.sequenceNumber shr 0).toByte()
        val timeval=System.nanoTime()/100000 - temporalOrigin
        message[4]=(timeval shr 24).toByte()
        message[5]=(timeval shr 16).toByte()
        message[6]=(timeval shr 8).toByte()
        message[7]=(timeval shr 0).toByte()
        message[8] = (ssrc shr 24).toByte()
        message[9] = (ssrc shr 16).toByte()
        message[10] = (ssrc shr 8).toByte()
        message[11] = ssrc.toByte()

        // B=0, J=0, Z=0, P=0
        message[12]= ((0 shl 0).toByte() + (0 shl  1).toByte() + (0 shl 2).toByte() + (0 shl 3).toByte()).toByte()
        message[12] =(midiData.size).toByte()
        for (c in midiData.indices)
        {
            message[13+c]=midiData[c]
        }
        val messageLength = 13 + midiData.size
        val p = DatagramPacket(message, messageLength, conn.address, conn.port+1)
        s.send(p)

        journal.push(midiData,conn.sequenceNumber)
        conn.sequenceNumber += 1u
        conn.sequenceNumber = conn.sequenceNumber and 0xFFFFu
    }
}



class ClientConnectionData
{
    var address: InetAddress?=null
    var port: Int=0
    var connectionState: ConnectionState=ConnectionState.UNCONNECTED
    var initiatorToken: UInt =0u
    var sscr= ByteArray(4)
    var timestamp1: ULong=0u
    var timestamp2: ULong=0u
    var timestamp3: ULong=0u
    var timeOffset: ULong=0u
    val itBytes=ByteArray(4)

    var sequenceNumber = Random.nextUInt() and 0xFFFFu

    fun computeTimeOffset()
    {
        timeOffset = (timestamp1 + timestamp3)/2u - timestamp2
    }
}


class RtpMidiJournal(private val size: Int)
{
    private var internalArrayList=Array<RtpMidiJournalEntry?>(size){null}
    private var idx=0
    private var idxBottom=0


    //fun clearOlderThan(timestamp: Int)
    //{
    //    internalArrayList = internalArrayList.iterator().

    fun push(data: ByteArray,sequenceNumber: UInt)
    {
        val journalEntry=RtpMidiJournalEntry()
        journalEntry.data = data
        journalEntry.timestamp = sequenceNumber
        current()?.let {
            journalEntry.timestampHigh = it.timestampHigh
            if (it.timestamp > sequenceNumber) {
                journalEntry.timestampHigh += 1u
            }
        }
        idx+=1
        if (idx == size)
        {
            idx=0
        }
        internalArrayList[idx]=journalEntry

    }

    fun getIndexesOfNewerThan(timestamp: UInt): Array<Int>
    {
        var hasEnded=false
        while(!hasEnded) {
            if (internalArrayList[idxBottom] != null) {
                if (internalArrayList[idxBottom]?.get32bitTimestamp()!! < timestamp) {
                    idxBottom += 1
                    if (idxBottom != idx) {
                        if (idxBottom == size) {
                            idxBottom = 0
                        }
                    }
                    else {
                        hasEnded=true
                    }
                }
                else
                {
                    hasEnded=true
                }
            }
            else
            {
                idxBottom += 1
                if (idxBottom != idx) {
                    if (idxBottom == size) {
                        idxBottom = 0
                    }
                }
            }
        }
        return arrayOf(idxBottom,idx)
    }

    fun current(): RtpMidiJournalEntry?
    {
        return internalArrayList[idx]
    }

    fun get(idx: Int): RtpMidiJournalEntry?
    {
        return internalArrayList[idx]
    }
}
class RtpMidiJournalEntry
{
    var data= ByteArray(3)
    var timestamp: UInt =0u
    var timestampHigh: UInt=0u
    fun get32bitTimestamp(): UInt
    {
        return timestamp + (timestampHigh and 0xFFFFu shl 16 )
    }
}

enum class ConnectionState
{
    UNCONNECTED,
    CONNECTING_OK_ON_CONTROL,
    CONNECTING_OK_ON_DATA,
    CONNECTING_TIME1,
    CONNECTED
}