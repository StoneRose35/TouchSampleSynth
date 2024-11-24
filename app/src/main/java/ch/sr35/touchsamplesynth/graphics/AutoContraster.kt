package ch.sr35.touchsamplesynth.graphics

import kotlin.math.max

class AutoContraster {
    var deltaHue = 180
    var desaturateValue = 40
    var valueThreshhold = 40

    fun generateContrastingColor(color: RgbColor): RgbColor {
        val hsvColor = color.toHsvColor()
        hsvColor.h += deltaHue
        hsvColor.h %= 360
        if (hsvColor.s > desaturateValue)
        {
            hsvColor.s -= desaturateValue
        }
        if (hsvColor.v > valueThreshhold)
        {
            hsvColor.v -= valueThreshhold
        }
        else {
            hsvColor.v += valueThreshhold
            hsvColor.v = max(hsvColor.v, 100)
        }

        return hsvColor.toRgbColor()
    }

}