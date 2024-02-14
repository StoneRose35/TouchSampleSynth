package ch.sr35.touchsamplesynth.audio

import java.io.InputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.math.floor

class WavReader {

    fun readWaveFile(fis: InputStream): WavFile
    {
        val headerData= ByteArray(32)
        val idData= ByteArray(4)
        fis.read(headerData,0,12) // read header
        fis.read(idData,0,4)
        if (String(idData) == "JUNK")
        {
            fis.read(idData,0,4)
            val junksize = littleEndianConversion(idData)
            if (junksize and 1 != 0)
            {
                fis.skip((junksize + 1).toLong())
            }
            else
            {
                fis.skip((junksize + 0).toLong())
            }
        }
        //else
        //{
        //    fis.skip(-4)
        //}
        if (String(idData)=="fmt ")
        {
            fis.read(headerData,0,20)
            val samplingRate = littleEndianConversion(headerData.copyOfRange(8,12))
            val channels = littleEndianConversion(headerData.copyOfRange(6,8))
            val formatTag = littleEndianConversion(headerData.copyOfRange(4,6))
            val headerLength = littleEndianConversion(headerData.copyOfRange(0,4))
            val bitsPerSample = littleEndianConversion(headerData.copyOfRange(18,20))
            if (formatTag != 1)
            {
                throw WavReaderException("Wav file is compressed, only uncompressed files supported")
            }
            if (!(channels == 1 || channels == 2))
            {
                throw WavReaderException("only mono or stereo files are supported")
            }
            if (!(bitsPerSample==8 || bitsPerSample==16 || bitsPerSample==24 || bitsPerSample==32))
            {
                throw WavReaderException("wrong sample size, 8, 16, 24 ,32 is supported")
            }

            if (headerLength - 16 > 0) {
                val remainingHeader=ByteArray(headerLength-16)
                fis.read(remainingHeader, 0, headerLength - 16)
            }
            fis.read(idData,0,4)
            if (String(idData)!="data")
            {
                throw WavReaderException("expected data tag")
            }
            val dataSizeBytesArray = ByteArray(4)
            fis.read(dataSizeBytesArray,0,4)
            val dataSize = littleEndianConversion(dataSizeBytesArray)
            val rawByteData = ByteArray(dataSize)
            fis.read(rawByteData,0,dataSize)
            fis.close()
            return WavFile(WaveFileMetadata(samplingRate,bitsPerSample,channels),rawByteData)
        }
        else
        {
            throw WavReaderException("expected format tag")
        }
    }


}

class WavReaderException(message: String?): Exception(message)

class WavFile(val header:WaveFileMetadata,val rawData: ByteArray)
{
    fun getFloatData(expectedSamplingRate: Int,channels:WaveFileChannel): ArrayList<Float>
    {
        val result =ArrayList<Float>()
        val nFrames = rawData.size/(header.bitDepth/8)/header.nChannels
        val channelSize = header.bitDepth/8
        var idx=0
        var sampleL: Float
        var sampleR: Float
        val bbfr = ByteBuffer.allocate(4)
        bbfr.order(ByteOrder.LITTLE_ENDIAN)
        val renorm = (1 shl (header.bitDepth - 1)).toFloat()
        if (this.header.nChannels == 2)
        {
            if (channels == WaveFileChannel.BOTH)
            {
                if (header.bitDepth != 8) {
                    for (c in 0 until nFrames) {
                        bbfr.clear()
                        bbfr.put(rawData, idx, channelSize)
                        bbfr.rewind()
                        sampleL = bbfr.getInt().toFloat() / renorm
                        //sampleL = (littleEndianConversion(
                        //    rawData.copyOfRange(
                        //        idx,
                        //        idx + channelSize
                        //    )
                        //).toFloat() / (1 shl (header.bitDepth - 1)).toFloat())
                        bbfr.clear()
                        bbfr.put(rawData, idx + channelSize, channelSize)
                        bbfr.rewind()
                        sampleR = bbfr.getInt().toFloat() / renorm
                        //sampleR = (littleEndianConversion(
                        //    rawData.copyOfRange(
                        //        idx + channelSize,
                        //        idx + 2 * channelSize
                        //    )
                        //).toFloat() / (1 shl (header.bitDepth - 1)).toFloat())
                        result.add((sampleR + sampleL)*0.5f)
                        idx += channelSize*2
                    }
                }
                else
                {
                    for (c in 0 until nFrames) {
                        sampleL = (rawData.copyOfRange(
                            idx,
                            idx + channelSize
                        )[0].toFloat() + 128.0f) / 256.0f
                        sampleR = (rawData.copyOfRange(
                            idx + channelSize,
                            idx + 2 * channelSize
                        )[0].toFloat() + 128.0f) / 256.0f
                        result.add((sampleR + sampleL)*0.5f)
                        idx += channelSize*2
                    }
                }
            }
            else if (channels==WaveFileChannel.LEFT)
            {

                if (header.bitDepth != 8) {
                    for (c in 0 until nFrames) {
                        bbfr.clear()
                        bbfr.put(rawData, idx, channelSize)
                        bbfr.rewind()
                        sampleL = bbfr.getInt().toFloat() / renorm
                        //sampleL = (littleEndianConversion(
                        //    rawData.copyOfRange(
                        //        idx,
                        //        idx + channelSize
                        //    )
                        //).toFloat() / (1 shl (header.bitDepth - 1)).toFloat())
                        result.add(sampleL)
                        idx += 2 * channelSize
                    }
                }
                else
                {
                    for (c in 0 until nFrames) {
                        sampleL = (rawData.copyOfRange(
                            idx,
                            idx + channelSize
                        )[0].toFloat() + 128.0f) / 256.0f
                        result.add(sampleL)
                        idx += 2 * channelSize
                    }
                }


            }
            else if (channels==WaveFileChannel.RIGHT)
            {

                    if (header.bitDepth != 8) {
                        for (c in 0 until nFrames) {
                            bbfr.clear()
                            bbfr.put(rawData, idx, channelSize)
                            bbfr.rewind()
                            sampleR =
                                bbfr.getInt().toFloat() / renorm
                            //sampleR = (littleEndianConversion(
                            //    rawData.copyOfRange(
                            //        idx + channelSize,
                            //        idx + 2 * channelSize
                            //    )
                            //).toFloat() / (1 shl (header.bitDepth - 1)).toFloat())
                            result.add(sampleR)
                            idx += 2*channelSize
                        }
                    }
                    else
                    {
                        for (c in 0 until nFrames) {
                            sampleR = (rawData.copyOfRange(
                                idx + channelSize,
                                idx + 2 * channelSize
                            )[0].toFloat() + 128.0f) / 256.0f
                            result.add(sampleR)
                            idx += 2*channelSize
                        }
                    }

                }

        }
        else
        {

            if (header.bitDepth != 8) {
                for (c in 0 until  nFrames) {
                    bbfr.clear()
                    bbfr.put(rawData, idx, channelSize)
                    sampleL = bbfr.getInt().toFloat() / renorm
                    //sampleL = (littleEndianConversion(
                    //    rawData.copyOfRange(
                    //        idx,
                    //        idx + channelSize
                    //    )
                    //).toFloat() / (1 shl (header.bitDepth - 1)).toFloat())
                    result.add(sampleL)
                    idx += channelSize
                }
            }
            else
            {
                for (c in 0 until  nFrames) {
                    sampleL = (rawData.copyOfRange(idx, idx + channelSize)[0].toUByte()
                        .toFloat() - 128.0f) / 256.0f
                    result.add(sampleL)
                    idx += channelSize
                }
            }

        }

        if (expectedSamplingRate != header.sampleRate)
        {
            val interpolatedResult=ArrayList<Float>()
            // sample rate conversion / interpolation
            var fractionalIndex=0.0f
            val relativeIncrement = header.sampleRate.toFloat()/expectedSamplingRate.toFloat()
            var interp: Float
            var dt: Float
            while (floor(fractionalIndex).toInt()+1 < result.size)
            {
                dt = fractionalIndex -  floor(fractionalIndex)
                interp = result[floor(fractionalIndex).toInt()] + (result[floor(fractionalIndex).toInt()+1] - result[floor(fractionalIndex).toInt()])*dt
                interpolatedResult.add(interp)
                fractionalIndex += relativeIncrement
            }

            return interpolatedResult
        }

        return result
    }
}

enum class WaveFileChannel
{
    BOTH,
    LEFT,
    RIGHT
}
class WaveFileMetadata(var sampleRate: Int,var bitDepth: Int,var nChannels: Int)

fun littleEndianConversion(bytes: ByteArray): Int {
    var result = 0
    for (i in bytes.indices) {
        result = if (i < bytes.size-1) {
            result or (bytes[i].toUByte().toInt() shl 8 * i)
        } else {
            result or (bytes[i].toInt() shl 8 * i)
        }
    }
    return result
}