package ch.sr35.touchsamplesynth.views

import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator
import ch.sr35.touchsamplesynth.R
import ch.sr35.touchsamplesynth.graphics.Converter

const val SWEEP_ANGLE_BASE =67.4f
const val SWEEP_ANGLE_SIZE = 34.0f
class WaitAnimation(context: Context,attributeSet: AttributeSet?): View(context,attributeSet) {

    private val circleColor: Paint = Paint()
    private val circleColorHighlight: Paint = Paint()
    private val transparentPaint: Paint = Paint()
    private var bitmap: Bitmap?=null
    private var bufferCanvas: Canvas?=null
    var angle = 0.0f
    var sweepAngle = 0.0f
    val animatorSet: AnimatorSet= AnimatorSet()
    init {
        circleColor.color = context.getColor(R.color.vu_meter_background)
        circleColor.style = Paint.Style.FILL

        circleColorHighlight.color = context.getColor(R.color.vu_meter_green)
        circleColorHighlight.style = Paint.Style.FILL

        transparentPaint.color = context.getColor(R.color.transparent)
        transparentPaint.style = Paint.Style.FILL
        transparentPaint.setXfermode(PorterDuffXfermode(PorterDuff.Mode.CLEAR))

        bitmap = Bitmap.createBitmap((context as Activity).windowManager.currentWindowMetrics.bounds.width(),(context as Activity).windowManager.currentWindowMetrics.bounds.height(),Bitmap.Config.ARGB_8888)
        bufferCanvas = Canvas(bitmap!!)


    }


    override fun onDraw(canvas: Canvas) {

        bufferCanvas?.drawColor(context.getColor(R.color.transparent))

        val rad = if(height < width) {
            height.toFloat()/2
        }
         else {
             width.toFloat()/2
         }
        bufferCanvas?.drawCircle(width.toFloat()/2.0f,height.toFloat()/2.0f,rad,circleColor)
        val circleSize = Converter.toPx(15.0f)

        bufferCanvas?.drawArc(width.toFloat()/2.0f-rad,height.toFloat()/2.0f-rad,width.toFloat()/2.0f+rad,height.toFloat()/2.0f+rad,angle, SWEEP_ANGLE_BASE + sweepAngle,true,circleColorHighlight)
        bufferCanvas?.drawCircle(width.toFloat()/2.0f,height.toFloat()/2.0f,rad-circleSize,transparentPaint)
        bitmap?.let { canvas.drawBitmap(it,0.0f,0.0f,null)}
    }

    fun startAnimation()
    {
        val spinningCircleAnimation = ValueAnimator.ofFloat(0.0f,360.0f).apply {
            duration = 1243
            repeatMode = ValueAnimator.RESTART
            repeatCount = ValueAnimator.INFINITE
            interpolator = LinearInterpolator()
            addUpdateListener {
                angle = this.animatedValue as Float
                invalidate()
            }
        }
        val arcWidthAnimation = ValueAnimator.ofFloat(-1.0f,1.0f).apply {
            duration = 1764
            repeatMode = ValueAnimator.REVERSE
            repeatCount = ValueAnimator.INFINITE
            interpolator = LinearInterpolator()
            addUpdateListener {
                sweepAngle = (this.animatedValue as Float)* SWEEP_ANGLE_SIZE
                invalidate()
            }
        }
        animatorSet.play(spinningCircleAnimation).with(arcWidthAnimation)
        animatorSet.start()
    }

    fun stopAnimation()
    {
        if (animatorSet.isStarted)
        {
            animatorSet.end()
        }
    }
    companion object {

    }
}