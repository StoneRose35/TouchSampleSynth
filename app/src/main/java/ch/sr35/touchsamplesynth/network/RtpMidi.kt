package ch.sr35.touchsamplesynth.network

import android.util.Log
import ch.sr35.touchsamplesynth.TAG
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import kotlin.random.Random


// documentation used: https://developer.apple.com/library/archive/documentation/Audio/Conceptual/MIDINetworkDriverProtocol/MIDI/MIDI.html
class RtpMidi {

    private var ssrc: Int= Random.nextInt(0xFFFFFFFF.toInt())
    var name: String="TSS Midi"
    private var initiatorToken=Random.nextInt(0xFFFFFFFF.toInt())
    private var commandPort: Int=0
    private var dataPort: Int=0
    private var temporalOrigin: Long = System.nanoTime()/100000
    private var timeOffset:Long=0

    fun initiateSession(hostname: InetAddress,commandPort: Int,dataPort: Int)
    {
        val messageBuffer=ByteArray(2048)
        // send "IN" in command port
        sendCommand("IN", hostname,commandPort)
        var packetIn:DatagramPacket = receiveOnCommandPort()
        if (!checkInitiatorToken(packetIn))
            return // initiator token doesn't match
        if (getCommand(packetIn)!="OK")
            return // command is not OK
        // send "IN" in data port
        sendCommand("IN", hostname,dataPort)
        packetIn = receiveOnCommandPort()
        if (!checkInitiatorToken(packetIn))
            return
        if (getCommand(packetIn)!="OK")
            return

        // do clock sync
        val myTime = sendTimestamp(messageBuffer,0,0,hostname,dataPort)
        receiveOnDataPort(messageBuffer)
        var othertime = 0
        val count = messageBuffer[8]
        if (count.toInt() != 1) {
            Log.i(TAG, "RTP-Midi time synch failed")
            return
        }
        othertime += messageBuffer[20].toInt() shl 56
        othertime += messageBuffer[21].toInt() shl 48
        othertime += messageBuffer[22].toInt() shl 40
        othertime += messageBuffer[23].toInt() shl 32

        othertime += messageBuffer[24].toInt() shl 24
        othertime += messageBuffer[25].toInt() shl 16
        othertime += messageBuffer[26].toInt() shl 8
        othertime += messageBuffer[27].toInt() shl 0
        val myTime2 = sendTimestamp(messageBuffer,0,2,hostname,dataPort)
        timeOffset = (myTime + myTime2)/2 - othertime
    }

    private fun receiveOnCommandPort(): DatagramPacket
    {
        val buffer = ByteArray(2048)
        val packet = DatagramPacket(buffer, buffer.size)
        val s = DatagramSocket(commandPort)
        s.receive(packet)
        return packet
    }

    private fun receiveOnDataPort(message: ByteArray): DatagramPacket
    {
        val packet = DatagramPacket(message,message.size)
        val s = DatagramSocket(dataPort)
        s.receive(packet)
        return packet
    }
    /**
     * checks of the initiator token of a packet received is the same
     * the the one generated
     */
    private fun checkInitiatorToken(packet: DatagramPacket): Boolean
    {
        if (packet.length < 16)
            return false
        return packet.data[8] == initiatorToken.toByte() &&
                packet.data[9] == (initiatorToken shl 8).toByte() &&
                packet.data[10] == (initiatorToken shl 16).toByte() &&
                packet.data[11] == (initiatorToken shl 24).toByte()
    }

    private fun getCommand(packet:DatagramPacket): String
    {
        var result=""
        result = result.plus(packet.data[2].toInt().toChar())
        result = result.plus(packet.data[3].toInt().toChar())
        return result
    }


    private fun sendCommand(command: String,hostname: InetAddress,portNr: Int)
    {
        val message=ByteArray(128)
        val s = DatagramSocket()
        message[0] = 0xFF.toByte()
        message[1] = 0xFF.toByte()
        message[2] = command[0].code.toByte()
        message[3] = command[1].code.toByte()
        message[4]=0
        message[5]=0
        message[6]=0
        message[7]=2
        message[8]= initiatorToken.toByte()
        message[9]= (initiatorToken shr 8).toByte()
        message[10]=(initiatorToken shr 16).toByte()
        message[11]=(initiatorToken shr 24).toByte()
        message[12]=ssrc.toByte()
        message[13]=(ssrc shr 8).toByte()
        message[14]=(ssrc shr 16).toByte()
        message[15]=(ssrc shr 24).toByte()
        val nameLimit=arrayOf(128-16,name.length).min()
        for (c in 0 ..nameLimit)
        {
            message[c+15]=name[c].code.toByte()
        }
        message[nameLimit+15]=0
        val messageLength=nameLimit+16
        val p = DatagramPacket(message, messageLength, hostname, portNr)
        s.send(p)
    }

    private fun sendTimestamp(message: ByteArray, count: Int, position: Int, hostname: InetAddress, portNr: Int): Long {
        val s = DatagramSocket()
        message[0] = 0xFF.toByte()
        message[1] = 0xFF.toByte()
        message[2] = 'C'.code.toByte()
        message[3] = 'K'.code.toByte()
        message[4] = ssrc.toByte()
        message[5] = (ssrc shr 8).toByte()
        message[6] = (ssrc shr 16).toByte()
        message[7] = (ssrc shr 24).toByte()
        message[8] = count.toByte()
        val currentTime = System.nanoTime() /100000 - temporalOrigin
        message[12+position*8+0] = (currentTime shl 56).toByte()
        message[12+position*8+1] = (currentTime shl 48).toByte()
        message[12+position*8+2] = (currentTime shl 40).toByte()
        message[12+position*8+3] = (currentTime shl 32).toByte()
        message[12+position*8+4] = (currentTime shl 24).toByte()
        message[12+position*8+5] = (currentTime shl 16).toByte()
        message[12+position*8+6] = (currentTime shl 8).toByte()
        message[12+position*8+7] = (currentTime shl 0).toByte()
        val messageLength = 36
        val p = DatagramPacket(message, messageLength, hostname, portNr)
        s.send(p)
        return currentTime
    }

    fun sendMidiCommand(midiData: ByteArray,hostname: InetAddress, portNr: Int)
    {
        val s = DatagramSocket()
        val message=ByteArray(2048)
        message[0]=2
        message[1]=((0x61 shl 1) + 1).toByte()
        message[2]=0
        message[3]="K".toByte()
        val timeval=System.nanoTime()/100000 - temporalOrigin - timeOffset
        message[4]=(timeval shr 24).toByte()
        message[5]=(timeval shr 16).toByte()
        message[6]=(timeval shr 8).toByte()
        message[7]=(timeval shr 0).toByte()
        // B=0, J=0, Z=0, P=0
        message[8]= ((0 shl 0).toByte() + (0 shl  1).toByte() + (0 shl 2).toByte() + (0 shl 3).toByte()).toByte()
        message[8] =(message[8] + (midiData.size shl 4)).toByte()
        for (c in 0 until midiData.size)
        {
            message[9+c]=midiData[c]
        }
        val messageLength = 8 + midiData.size
        val p = DatagramPacket(message, messageLength, hostname, portNr)
        s.send(p)
    }



}