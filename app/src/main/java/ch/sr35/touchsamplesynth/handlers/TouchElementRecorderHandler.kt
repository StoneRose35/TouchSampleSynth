package ch.sr35.touchsamplesynth.handlers

import android.view.MotionEvent
import ch.sr35.touchsamplesynth.audio.SoundRecorder
import ch.sr35.touchsamplesynth.views.OUTLINE_STROKE_WIDTH_DEFAULT
import ch.sr35.touchsamplesynth.views.OUTLINE_STROKE_WIDTH_ENGAGED
import ch.sr35.touchsamplesynth.views.TouchElement
import ch.sr35.touchsamplesynth.views.TouchElementRecorder

class TouchElementRecorderHandler(te: TouchElementRecorder) : TouchElementHandler(te) {

    override fun handleActionDownInPlayMode(event: MotionEvent): Boolean {
        if (!touchElement.isEngaged) {
            if (event.x > touchElement.measuredWidth/2
                && event.x < touchElement.measuredWidth - touchElement.padding
                && event.y < touchElement.measuredHeight/2
                && event.y > touchElement.padding)
            {
                // start recording /overdubbing
                touchElement.soundGenerator?.getNextFreeVoice()?.let {
                    touchElement.currentVoices.add(it)
                    (touchElement.currentVoices[0] as SoundRecorder).startRecording()
                    touchElement.isEngaged = true
                    touchElement.outLine.strokeWidth = OUTLINE_STROKE_WIDTH_ENGAGED
                    (touchElement as TouchElementRecorder).isRecording = true
                    touchElement.hasRecordedContent = true
                    touchElement.invalidate()
                }
            }
            else if (event.x < touchElement.measuredWidth/2
                && event.x >  touchElement.padding
                && event.y > touchElement.measuredHeight/2
                && event.y > touchElement.measuredHeight - touchElement.padding
                && (touchElement as TouchElementRecorder).hasRecordedContent)
            {
                // delete sample
                (touchElement.currentVoices[0] as SoundRecorder).resetSample()
                touchElement.hasRecordedContent = false
                touchElement.invalidate()
            }
            else
            {
                return super.handleActionDownInPlayMode(event)
            }
        }
        else
        {
            if ((touchElement as TouchElementRecorder).isRecording) {
                if (event.x > touchElement.measuredWidth/2
                    && event.x < touchElement.measuredWidth - touchElement.padding
                    && event.y < touchElement.measuredHeight/2
                    && event.y > touchElement.padding)
                {
                    // switch to playback
                    (touchElement.currentVoices[0] as SoundRecorder).stopRecording()
                    touchElement.currentVoices[0].switchOn(1.0f)
                    touchElement.isRecording = false
                    touchElement.invalidate()
                }
                else if (event.x > touchElement.measuredWidth/2
                    && event.x < touchElement.measuredWidth - touchElement.padding
                    && event.y > touchElement.measuredHeight/2
                    && event.y < touchElement.measuredHeight - touchElement.padding)
                {
                    // stop and disengage
                    (touchElement.currentVoices[0] as SoundRecorder).stopRecording()
                    touchElement.currentVoices[0].switchOff(1.0f)
                    touchElement.isRecording = false
                    touchElement.outLine.strokeWidth = OUTLINE_STROKE_WIDTH_DEFAULT
                    touchElement.isEngaged = false
                    touchElement.invalidate()
                }
                else if (event.x < touchElement.measuredWidth/2
                    && event.x > touchElement.padding
                    && event.y > touchElement.measuredHeight/2
                    && event.y < touchElement.measuredHeight - touchElement.padding)
                {
                    // stop, disengage and delete sample
                    (touchElement.currentVoices[0] as SoundRecorder).stopRecording()
                    touchElement.currentVoices[0].switchOff(1.0f)
                    (touchElement.currentVoices[0] as SoundRecorder).resetSample()
                    touchElement.isRecording = false
                    touchElement.outLine.strokeWidth = OUTLINE_STROKE_WIDTH_DEFAULT
                    touchElement.isEngaged = false
                    touchElement.hasRecordedContent = false
                    touchElement.invalidate()
                }
                else
                {
                    return super.handleActionDownInPlayMode(event)
                }
            }
            else
            {
                if (event.x > touchElement.measuredWidth/2
                    && event.x < touchElement.measuredWidth - touchElement.padding
                    && event.y < touchElement.measuredHeight/2
                    && event.y > touchElement.padding)
                {
                    // switch to overdub
                    //touchElement.currentVoices[0].switchOff(1.0f)
                    (touchElement.currentVoices[0] as SoundRecorder).startRecording()
                    touchElement.isRecording = true
                    touchElement.invalidate()
                }
                else if (event.x < touchElement.measuredWidth/2
                    && event.x > touchElement.padding
                    && event.y > touchElement.measuredHeight/2
                    && event.y < touchElement.measuredHeight - touchElement.padding)
                {
                    // stop, disengage and delete sample
                    (touchElement.currentVoices[0] as SoundRecorder).stopRecording()
                    touchElement.currentVoices[0].switchOff(1.0f)
                    (touchElement.currentVoices[0] as SoundRecorder).resetSample()
                    touchElement.isRecording = false
                    touchElement.outLine.strokeWidth = OUTLINE_STROKE_WIDTH_DEFAULT
                    touchElement.isEngaged = false
                    touchElement.hasRecordedContent = false
                    touchElement.invalidate()
                }
                else
                {
                    return super.handleActionDownInPlayMode(event)
                }
            }
        }
        return false
    }

    override fun handleActionUpInPlayMode(event: MotionEvent): Boolean {
        if ((touchElement as TouchElementRecorder).isRecording && touchElement.touchMode == TouchElement.TouchMode.MOMENTARY) {
            if (event.x > touchElement.measuredWidth/2
                && event.x < touchElement.measuredWidth - touchElement.padding
                && event.y < touchElement.measuredHeight/2
                && event.y > touchElement.padding)
            {
                // switch to playback
                (touchElement.currentVoices[0] as SoundRecorder).stopRecording()
                touchElement.currentVoices[0].switchOn(1.0f)
                touchElement.isRecording = false
                touchElement.invalidate()
                return true
            }
            else if (event.x > touchElement.measuredWidth/2
                && event.x < touchElement.measuredWidth - touchElement.padding
                && event.y > touchElement.measuredHeight/2
                && event.y < touchElement.measuredHeight - touchElement.padding)
            {
                // stop and disengage
                (touchElement.currentVoices[0] as SoundRecorder).stopRecording()
                touchElement.currentVoices[0].switchOff(1.0f)
                touchElement.isRecording = false
                touchElement.isEngaged = false
                touchElement.invalidate()
                return true
            }
            else if (event.x < touchElement.measuredWidth/2
                && event.x > touchElement.padding
                && event.y > touchElement.measuredHeight/2
                && event.y < touchElement.measuredHeight - touchElement.padding)
            {
                // stop, disengage and delete sample
                (touchElement.currentVoices[0] as SoundRecorder).stopRecording()
                touchElement.currentVoices[0].switchOff(1.0f)
                (touchElement.currentVoices[0] as SoundRecorder).resetSample()
                touchElement.isRecording = false
                touchElement.isEngaged = false
                touchElement.invalidate()
                return true
            }
        }
        else {
            return super.handleActionUpInPlayMode(event)
        }
        return false
    }
}