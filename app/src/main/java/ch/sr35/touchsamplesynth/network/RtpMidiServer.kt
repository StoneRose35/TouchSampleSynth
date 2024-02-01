package ch.sr35.touchsamplesynth.network

import android.util.Log
import ch.sr35.touchsamplesynth.TAG
import java.io.IOException
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import kotlin.concurrent.thread
import kotlin.random.Random
import kotlin.random.nextUInt

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
        thread(start = true,name="TSS Midi control") {
            var p = DatagramPacket(packetBuffer,packetBuffer.size)
            while(true)
            {
                controlSocket?.receive(p)
                // dispatch as needed
                for(conn in connections)
                {
                    if (conn.address == p.address)
                    {

                    }
                }

                // no known connection from where this packet is coming from, check if "IN"
                // and allow if the max number of connections isnt exceeded

                val cmd=getCommand(p)
                if (cmd=="IN" && connections.size < 8)
                {
                    Log.i(TAG,"got IN on Control from ${p.address} on port ${p.port}")
                    ClientConnectionData().also {
                        it.address = p.address
                        it.port= p.port
                        it.connectionState=ConnectionState.CONNECTING_OK_ON_CONTROL
                        it.initiatorToken = 0u
                        it.initiatorToken = (p.data[8].toUInt() and 0xFFu shl 24) or
                        (p.data[9].toUInt() and 0xFFu shl 16) or
                        (p.data[10].toUInt() and 0xFFu shl 8) or
                        (p.data[11].toUInt() and 0xFFu shl 0)
                        it.itBytes[0]=p.data[8]
                        it.itBytes[1]=p.data[9]
                        it.itBytes[2]=p.data[10]
                        it.itBytes[3]=p.data[11]
                        it.sscr[0] = p.data[12]
                        it.sscr[1] = p.data[13]
                        it.sscr[2] = p.data[14]
                        it.sscr[3] = p.data[15]
                        connections.add(it)

                        sendCommand("OK",it,false,controlSocket!!)
                    }
                }
                else if (cmd=="IN")
                {
                    // send NO
                    ClientConnectionData().also {
                        it.address = p.address
                        it.port= p.port
                        it.connectionState=ConnectionState.UNCONNECTED
                        it.initiatorToken = (p.data[8].toUInt() and 0xFFu shl 24) or
                        (p.data[9].toUInt() and 0xFFu shl 16) or
                        (p.data[10].toUInt() and 0xFFu shl 8) or
                        (p.data[11].toUInt() and 0xFFu shl 0)
                        sendCommand("NO",it,false,controlSocket!!)
                    }
                }
            }
        }
        thread(start = true,name="TSS Midi data") {
            var p = DatagramPacket(packetBufferData,packetBufferData.size)
            val previousTimeStamps = ArrayList<ULong>()
            while(true) {
                dataSocket?.receive(p)
                for(conn in connections)
                {
                    if (conn.address == p.address)
                    {
                        val cmd=getCommand(p)
                        if (cmd=="IN")
                        {
                            Log.i(TAG,"got IN on Midi Data from known source ${p.address}")
                            sendCommand("OK",conn,true,dataSocket!!)
                            conn.connectionState=ConnectionState.CONNECTING_OK_ON_DATA

                        }
                        else if (cmd=="CK")
                        {
                            Log.i(TAG,"got CK on Midi Data from known source ${p.address}")
                            val count = p.data[8].toInt()
                            if (count==0)
                            {
                                previousTimeStamps.clear()
                                var senderSsrc=ByteArray(4)
                                senderSsrc[0]= p.data[4]
                                senderSsrc[1] = p.data[5]
                                senderSsrc[2] = p.data[6]
                                senderSsrc[3] = p.data[7]
                                if (!senderSsrc.contentEquals(conn.sscr))
                                {
                                    break;
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
                                Log.i(TAG,"got 1st timestamp ${conn.timestamp1}")
                                conn.timestamp2 = sendTimestamp(1,1,conn,dataSocket!!,true,previousTimeStamps)
                                Log.i(TAG,"sending second timestamp ${conn.timestamp2}")
                                conn.connectionState=ConnectionState.CONNECTING_TIME1
                            }
                            else if (count==2)
                            {
                                var senderSsrc=ByteArray(4)
                                senderSsrc[0]= p.data[4]
                                senderSsrc[1] = p.data[5]
                                senderSsrc[2] = p.data[6]
                                senderSsrc[3] = p.data[7]
                                if (!senderSsrc.contentEquals(conn.sscr))
                                {
                                    break;
                                }
                                conn.timestamp3 = (p.data[28].toULong() and 0xffu shl 56) or
                                (p.data[29].toULong() and 0xffu shl 48) or
                                (p.data[30].toULong() and 0xffu shl 40) or
                                (p.data[31].toULong() and 0xffu shl 32) or
                                (p.data[32].toULong() and 0xffu shl 24) or
                                (p.data[33].toULong() and 0xffu shl 16) or
                                (p.data[34].toULong() and 0xffu shl 8) or
                                (p.data[35].toULong() and 0xffu shl 0)
                                Log.i(TAG,"got 3rd timestamp ${conn.timestamp3}")
                                conn.computeTimeOffset()
                                conn.connectionState=ConnectionState.CONNECTED
                            }
                        }
                    }
                }
            }
        }
        return true
    }

    fun stopServer()
    {
        dataSocket?.close()
        controlSocket?.close()
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
        //message[8]= (client.initiatorToken shr 24).toByte()
        //message[9]= (client.initiatorToken shr 16).toByte()
        //message[10]=(client.initiatorToken shr 8).toByte()
        //message[11]=(client.initiatorToken shr 0).toByte()
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

    private fun sendTimestamp(position: Int,count: Int,client: ClientConnectionData,s: DatagramSocket,onDataPort: Boolean,previousTimeStamps: ArrayList<ULong>): ULong {
        val message=ByteArray(128)
        message[0] = 0xFF.toByte()
        message[1] = 0xFF.toByte()
        message[2] = 'C'.code.toByte()
        message[3] = 'K'.code.toByte()
        message[4] = (ssrc shr 24).toByte()
        message[5] = (ssrc shr 16).toByte()
        message[6] = (ssrc shr 8).toByte()
        message[7] = ssrc.toByte()
        message[8] = count.toByte()
        message[9] = 0
        message[10] = 0
        message[11] = 0
        var cnt=0
        while (cnt < position)
        {
            message[12+cnt*8+0] = (previousTimeStamps[cnt] shr 56).toByte()
            message[12+cnt*8+1] = (previousTimeStamps[cnt] shr 48).toByte()
            message[12+cnt*8+2] = (previousTimeStamps[cnt] shr 40).toByte()
            message[12+cnt*8+3] = (previousTimeStamps[cnt] shr 32).toByte()
            message[12+cnt*8+4] = (previousTimeStamps[cnt] shr 24).toByte()
            message[12+cnt*8+5] = (previousTimeStamps[cnt] shr 16).toByte()
            message[12+cnt*8+6] = (previousTimeStamps[cnt] shr 8).toByte()
            message[12+cnt*8+7] = (previousTimeStamps[cnt] shr 0).toByte()
            cnt+=1
        }
        val currentTime = System.nanoTime() /100000 - temporalOrigin
        message[12+cnt*8+0] = (currentTime shr 56).toByte()
        message[12+cnt*8+1] = (currentTime shr 48).toByte()
        message[12+cnt*8+2] = (currentTime shr 40).toByte()
        message[12+cnt*8+3] = (currentTime shr 32).toByte()
        message[12+cnt*8+4] = (currentTime shr 24).toByte()
        message[12+cnt*8+5] = (currentTime shr 16).toByte()
        message[12+cnt*8+6] = (currentTime shr 8).toByte()
        message[12+cnt*8+7] = (currentTime shr 0).toByte()
        val messageLength = 36
        var portNr=client.port
        if(onDataPort)
        {
            portNr+=1
        }
        val p = DatagramPacket(message, messageLength, client.address,portNr)
        s.send(p)
        return currentTime.toULong()
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

    var sequenceNumber = Random.nextInt()

    fun computeTimeOffset()
    {
        timeOffset = (timestamp1 + timestamp3)/2u - timestamp2
    }
}

enum class ConnectionState
{
    UNCONNECTED,
    CONNECTING_OK_ON_CONTROL,
    CONNECTING_OK_ON_DATA,
    CONNECTING_TIME1,
    CONNECTING_TIME2,
    CONNECTED
}