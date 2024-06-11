package ch.sr35.touchsamplesynth.audio

import java.io.RandomAccessFile
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.math.floor

class WaveHeaderTag(val description: String,val size:Int,val position: Long)

class WavReader() {

    fun readWaveFile(file: RandomAccessFile): WavFile
    {
        val tags = ArrayList<WaveHeaderTag>()
        val headerData= ByteArray(32)
        val idData= ByteArray(4)
        var samplingRate = -1
        var channels = -1
        var formatTag: Int
        var headerLength: Int
        var bitsPerSample = -1

        file.read(headerData,0,12) // read header

        while (file.filePointer < file.length())
        {
            file.read(idData,0,4)

            if (isValidTagDescriptor(idData))
            {
                val descr = String(idData)
                file.read(idData,0,4)

                val size = littleEndianConversion(idData)
                val wavTag = WaveHeaderTag(descr,size,file.filePointer-8)
                tags.add(wavTag)
                file.skipBytes(size)
            }
        }

        file.seek(0)
        tags.find { tag -> tag.description == "fmt " }?.let {
            file.seek(it.position+4)

            file.read(headerData,0,20)
            samplingRate = littleEndianConversion(headerData.copyOfRange(8,12))
            channels = littleEndianConversion(headerData.copyOfRange(6,8))
            formatTag = littleEndianConversion(headerData.copyOfRange(4,6))
            headerLength = littleEndianConversion(headerData.copyOfRange(0,4))
            bitsPerSample = littleEndianConversion(headerData.copyOfRange(18,20))
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
                file.read(remainingHeader, 0, headerLength - 16)
            }
        }
        if (samplingRate == -1)
        {
            throw WavReaderException("no format tag found")
        }
        else
        {
            file.seek(0)
            tags.find { tag -> tag.description == "data" }?.let {
                file.seek(it.position+8)
                val rawByteData = ByteArray(it.size)
                file.read(rawByteData,0,it.size)
                file.close()
                return WavFile(WaveFileMetadata(samplingRate,bitsPerSample,channels),rawByteData)
            }
            throw WavReaderException("no data tag found")
        }
    }


}

class WavReaderException(message: String?): Exception(message)

class WavFile(val header:WaveFileMetadata,val rawData: ByteArray)
{
    fun getFloatData(expectedSamplingRate: Int,channels:WaveFileChannel): FloatArray {

        val nFrames = rawData.size / (header.bitDepth / 8) / header.nChannels
        val result = FloatArray(nFrames)
        val channelSize = header.bitDepth / 8
        var idx = 0
        var sampleL: Float
        var sampleR: Float
        var bbfr = ByteBuffer.allocate(4)
        bbfr.order(ByteOrder.LITTLE_ENDIAN)
        val renorm = (1 shl (header.bitDepth - 1)).toFloat()
        if (this.header.nChannels == 2) {
            when (channels) {
                WaveFileChannel.BOTH -> {
                    when (header.bitDepth) {
                        32 -> {
                            for (c in 0 until nFrames) {
                                bbfr.clear()
                                bbfr.put(rawData, idx, channelSize)
                                bbfr.rewind()
                                sampleL = bbfr.getInt().toFloat() / renorm
                                bbfr.clear()
                                bbfr.put(rawData, idx + channelSize, channelSize)
                                bbfr.rewind()
                                sampleR = bbfr.getInt().toFloat() / renorm
                                result[c] = ((sampleR + sampleL) * 0.5f)
                                idx += channelSize * 2
                            }
                        }

                        24 -> {
                            for (c in 0 until nFrames) {
                                bbfr.clear()
                                bbfr.put(0)
                                bbfr.put(rawData, idx, channelSize)
                                bbfr.rewind()
                                sampleL = (bbfr.getInt() shr 8).toFloat() / renorm
                                bbfr.clear()
                                bbfr.put(0)
                                bbfr.put(rawData, idx + channelSize, channelSize)
                                bbfr.rewind()
                                sampleR = (bbfr.getInt() shr 8).toFloat() / renorm

                                result[c] = ((sampleR + sampleL) * 0.5f)
                                idx += channelSize * 2
                            }
                        }

                        16 -> {
                            bbfr = ByteBuffer.allocate(2)
                            bbfr.order(ByteOrder.LITTLE_ENDIAN)
                            for (c in 0 until nFrames) {
                                bbfr.clear()
                                bbfr.put(rawData, idx, channelSize)
                                bbfr.rewind()
                                sampleL = bbfr.getShort().toFloat() / renorm

                                bbfr.clear()
                                bbfr.put(rawData, idx + channelSize, channelSize)
                                bbfr.rewind()
                                sampleR = bbfr.getShort().toFloat() / renorm

                                result[c] = ((sampleR + sampleL) * 0.5f)
                                idx += channelSize * 2
                            }
                        }

                        8 -> {
                            for (c in 0 until nFrames) {
                                sampleL = (rawData[idx].toUByte().toFloat())
                                sampleR = (rawData[idx + channelSize].toUByte().toFloat())
                                result[c] = ((sampleR + sampleL - 256.0f) * 0.5f / 256.0f)
                                idx += channelSize * 2
                            }
                        }
                    }
                }

                WaveFileChannel.LEFT -> {
                    when (header.bitDepth) {
                        32 -> {
                            for (c in 0 until nFrames) {
                                bbfr.clear()
                                bbfr.put(rawData, idx, channelSize)
                                bbfr.rewind()
                                sampleL = bbfr.getInt().toFloat() / renorm

                                result[c] = (sampleL)
                                idx += channelSize * 2
                            }
                        }

                        24 -> {
                            for (c in 0 until nFrames) {
                                bbfr.clear()
                                bbfr.put(0)
                                bbfr.put(rawData, idx, channelSize)
                                bbfr.rewind()
                                sampleL = (bbfr.getInt() shr 8).toFloat() / renorm

                                result[c] = (sampleL)
                                idx += channelSize * 2
                            }
                        }

                        16 -> {
                            bbfr = ByteBuffer.allocate(2)
                            bbfr.order(ByteOrder.LITTLE_ENDIAN)
                            for (c in 0 until nFrames) {
                                bbfr.clear()
                                bbfr.put(rawData, idx, channelSize)
                                bbfr.rewind()
                                sampleL = bbfr.getShort().toFloat() / renorm


                                result[c] = (sampleL)
                                idx += channelSize * 2
                            }
                        }

                        8 -> {
                            for (c in 0 until nFrames) {
                                sampleL = (rawData[idx].toUByte().toFloat() - 128.0f) / 256.0f
                                result[c] = (sampleL)
                                idx += channelSize * 2
                            }
                        }
                    }
                }

                WaveFileChannel.RIGHT -> {
                    when (header.bitDepth) {
                        32 -> {
                            for (c in 0 until nFrames) {
                                bbfr.clear()
                                bbfr.put(rawData, idx + channelSize, channelSize)
                                bbfr.rewind()
                                sampleR = bbfr.getInt().toFloat() / renorm
                                result[c] = (sampleR)
                                idx += channelSize * 2
                            }
                        }

                        24 -> {
                            for (c in 0 until nFrames) {
                                bbfr.clear()
                                bbfr.put(0)
                                bbfr.put(rawData, idx + channelSize, channelSize)
                                bbfr.rewind()
                                sampleR = (bbfr.getInt() shr 8).toFloat() / renorm
                                result[c] = (sampleR)
                                idx += channelSize * 2
                            }
                        }

                        16 -> {
                            bbfr = ByteBuffer.allocate(2)
                            bbfr.order(ByteOrder.LITTLE_ENDIAN)
                            for (c in 0 until nFrames) {
                                bbfr.clear()
                                bbfr.put(rawData, idx + channelSize, channelSize)
                                bbfr.rewind()
                                sampleR = bbfr.getShort().toFloat() / renorm
                                result[c] = (sampleR)
                                idx += channelSize * 2
                            }
                        }

                        8 -> {
                            for (c in 0 until nFrames) {
                                sampleR = (rawData[idx + channelSize].toUByte()
                                    .toFloat() - 128.0f) / 256.0f
                                result[c] = (sampleR)
                                idx += channelSize * 2
                            }
                        }
                    }
                }
            }
        }
                else  {
                    when (header.bitDepth) {
                        32 -> {
                            for (c in 0 until nFrames) {
                                bbfr.clear()
                                bbfr.put(rawData, idx, channelSize)
                                bbfr.rewind()
                                sampleL = bbfr.getInt().toFloat() / renorm
                                result[c] = (sampleL)
                                idx += channelSize
                            }
                        }

                        24 -> {
                            for (c in 0 until nFrames) {
                                bbfr.clear()
                                bbfr.put(0)
                                bbfr.put(rawData, idx, channelSize)
                                bbfr.rewind()
                                sampleL = (bbfr.getInt() shr 8).toFloat() / renorm
                                result[c] = (sampleL)
                                idx += channelSize
                            }
                        }

                        16 -> {
                            bbfr = ByteBuffer.allocate(2)
                            bbfr.order(ByteOrder.LITTLE_ENDIAN)
                            for (c in 0 until nFrames) {
                                bbfr.clear()
                                bbfr.put(rawData, idx, channelSize)
                                bbfr.rewind()
                                sampleL = bbfr.getShort().toFloat() / renorm
                                result[c] = (sampleL)
                                idx += channelSize
                            }
                        }

                        8 -> {
                            for (c in 0 until nFrames) {
                                sampleL = (rawData[c].toUByte().toFloat() - 128.0f) / 256.0f
                                result[c] = sampleL
                                idx += channelSize
                            }
                        }
                    }
                }


            if (expectedSamplingRate != header.sampleRate) {
                val interpolatedResult = ArrayList<Float>()
                // sample rate conversion / interpolation
                var fractionalIndex = 0.0f
                val relativeIncrement = header.sampleRate.toFloat() / expectedSamplingRate.toFloat()
                var interp: Float
                var dt: Float
                while (floor(fractionalIndex).toInt() + 1 < result.size) {
                    dt = fractionalIndex - floor(fractionalIndex)
                    interp =
                        result[floor(fractionalIndex).toInt()] + (result[floor(fractionalIndex).toInt() + 1] - result[floor(
                            fractionalIndex
                        ).toInt()]) * dt
                    interpolatedResult.add(interp)
                    fractionalIndex += relativeIncrement
                }
                return interpolatedResult.toFloatArray()
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

fun isValidTagDescriptor(bytes: ByteArray): Boolean
{
    var result=true
    for (i in bytes.indices)
    {
        result = result.and((bytes[i].toUByte() > 96u && bytes[i].toUByte() < 122u) || (bytes[i].toUByte() > 64u && bytes[i].toUByte() < 91u) || bytes[i].toUByte()
            .toUInt() == 32u)
    }
    return result
}