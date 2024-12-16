package ch.sr35.touchsamplesynth.handlers

import android.view.MotionEvent
import ch.sr35.touchsamplesynth.views.TouchElement
import ch.sr35.touchsamplesynth.views.TouchElementRecorder

class TouchElementRecorderHandler(te: TouchElement) : TouchElementHandler(te) {

    override fun handleActionDownInPlayMode(event: MotionEvent): Boolean {
        if (!touchElement.isEngaged) {
            if (event.x > touchElement.measuredWidth/2
                && event.x < touchElement.measuredWidth - touchElement.padding
                && event.y < touchElement.measuredHeight/2
                && event.y > touchElement.padding)
            {
                // start recording /overdubbing
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
                }
                else if (event.x > touchElement.measuredWidth/2
                    && event.x < touchElement.measuredWidth - touchElement.padding
                    && event.y > touchElement.measuredHeight/2
                    && event.y < touchElement.measuredHeight - touchElement.padding)
                {
                    // stop and disengage
                }
                else if (event.x < touchElement.measuredWidth/2
                    && event.x > touchElement.padding
                    && event.y > touchElement.measuredHeight/2
                    && event.y < touchElement.measuredHeight - touchElement.padding)
                {
                    // stop, disengage and delete sample
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
                }
                else if (event.x < touchElement.measuredWidth/2
                    && event.x > touchElement.padding
                    && event.y > touchElement.measuredHeight/2
                    && event.y < touchElement.measuredHeight - touchElement.padding)
                {
                    // stop, disengage and delete sample
                }
            }
        }
        return false
    }

    override fun handleActionUpInPlayMode(event: MotionEvent): Boolean {
        if ((touchElement as TouchElementRecorder).isRecording && (touchElement as TouchElementRecorder).touchMode == TouchElement.TouchMode.MOMENTARY) {
            if (event.x > touchElement.measuredWidth/2
                && event.x < touchElement.measuredWidth - touchElement.padding
                && event.y < touchElement.measuredHeight/2
                && event.y > touchElement.padding)
            {
                // switch to playback
                return true
            }
            else if (event.x > touchElement.measuredWidth/2
                && event.x < touchElement.measuredWidth - touchElement.padding
                && event.y > touchElement.measuredHeight/2
                && event.y < touchElement.measuredHeight - touchElement.padding)
            {
                // stop and disengage
                return true
            }
            else if (event.x < touchElement.measuredWidth/2
                && event.x > touchElement.padding
                && event.y > touchElement.measuredHeight/2
                && event.y < touchElement.measuredHeight - touchElement.padding)
            {
                // stop, disengage and delete sample
                return true
            }
        }
        else {
            return super.handleActionUpInPlayMode(event)
        }
        return false
    }
}