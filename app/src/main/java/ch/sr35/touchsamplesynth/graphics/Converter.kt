package ch.sr35.touchsamplesynth.graphics

import android.content.res.Resources

class Converter {

    companion object {
        val dpi = Resources.getSystem().displayMetrics.density
        val dpFloat = Resources.getSystem().displayMetrics.density
        fun toPx(dp: Int): Int
        {
            return (dp.toFloat()* dpi).toInt()
        }
        fun toPx(dpF: Float): Float
        {
            return dpF*dpFloat
        }
        fun toDp(px: Int): Int
        {
            return (px.toFloat()/dpi).toInt()
        }
        fun toDp(pxF: Float): Float
        {
            return pxF/dpFloat
        }
    }
}