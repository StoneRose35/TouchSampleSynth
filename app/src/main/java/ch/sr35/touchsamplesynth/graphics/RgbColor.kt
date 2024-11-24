package ch.sr35.touchsamplesynth.graphics


import java.io.Serializable


class RgbColor(var r: Int,var g: Int,var b: Int): Serializable,Cloneable {

    fun toColorInt(): Int
    {
        return 0xff shl 24 or (r and 0xff shl 16) or (g and 0xff shl 8) or (b and 0xff)
    }

    public override fun clone(): RgbColor {
        return RgbColor(this.r,this.g,this.b)
    }

    fun toHsvColor(): HsvColor
    {
        return HsvColor.fromRgbColor(this)

    }

    companion object {
        fun fromHsvColor(hsvColor: HsvColor): RgbColor
        {
            return hsvColor.toRgbColor()
        }

        fun fromColorInt(color: Int): RgbColor
        {
            val r = (color shr 16) and 0xff
            val g = (color shr 8) and 0xff
            val b = color and 0xff
            return RgbColor(r,g,b)
        }
    }

}