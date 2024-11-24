package ch.sr35.touchsamplesynth.graphics

class HsvColor(var h: Int, var s: Int, var v: Int) {


    fun toRgbColor(): RgbColor {
        val h = this.h.toDouble()
        val s = this.s / 100.0
        val v = this.v / 100.0

        val c = v * s // Chroma
        val x = c * (1 - kotlin.math.abs((h / 60) % 2 - 1)) // Intermediate value
        val m = v - c // Match value to adjust RGB values

        val (rPrime, gPrime, bPrime) = when {
            h in 0.0..60.0 -> Triple(c, x, 0.0)
            h in 60.0..120.0 -> Triple(x, c, 0.0)
            h in 120.0..180.0 -> Triple(0.0, c, x)
            h in 180.0..240.0 -> Triple(0.0, x, c)
            h in 240.0..300.0 -> Triple(x, 0.0, c)
            h in 300.0..360.0 -> Triple(c, 0.0, x)
            else -> Triple(0.0, 0.0, 0.0) // Shouldn't happen for valid hue values
        }

        // Adjust to 0-255 range and return the RGB color
        val red = ((rPrime + m) * 255).toInt()
        val green = ((gPrime + m) * 255).toInt()
        val blue = ((bPrime + m) * 255).toInt()

        return RgbColor(red, green, blue)
    }

    companion object {
        fun fromRgbColor(rgbColor: RgbColor): HsvColor {
            val r = rgbColor.r / 255.0
            val g = rgbColor.g / 255.0
            val b = rgbColor.b / 255.0

            val max = maxOf(r, g, b)
            val min = minOf(r, g, b)
            val delta = max - min

            // Calculate Hue (H)
            val h = when {
                delta == 0.0 -> 0.0
                max == r -> (60 * ((g - b) / delta) + 360) % 360
                max == g -> (60 * ((b - r) / delta) + 120) % 360
                else -> (60 * ((r - g) / delta) + 240) % 360
            }

            // Calculate Saturation (S)
            val s = if (max == 0.0) 0.0 else (delta / max) * 100

            // Calculate Value (V)
            val v = max * 100

            return HsvColor(h.toInt(), s.toInt(), v.toInt())
        }
    }
}