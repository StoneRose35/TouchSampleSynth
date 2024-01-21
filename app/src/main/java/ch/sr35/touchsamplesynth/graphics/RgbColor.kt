package ch.sr35.touchsamplesynth.graphics

import android.R
import java.io.Serializable


class RgbColor(var r: Int,var g: Int,var b: Int): Serializable,Cloneable {

    fun toColorInt(): Int
    {
        return 0xff shl 24 or (r and 0xff shl 16) or (g and 0xff shl 8) or (g and 0xff);
    }

    public override fun clone(): RgbColor {
        return RgbColor(this.r,this.g,this.b)
    }

}