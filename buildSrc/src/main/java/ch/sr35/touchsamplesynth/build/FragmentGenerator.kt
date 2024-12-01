package ch.sr35.touchsamplesynth.build

import java.io.File

class FragmentGenerator {

    var className: String = ""

    fun generateFragment(): String
    {
        val classname_lowercase=className
            .replace(Regex("[A-Z]")) { m -> "_" + m.value.lowercase() }
                .replaceFirst("_","")

        val layoutfile = File("src/main/res/layout/fragment_$classname_lowercase.xml")
        layoutfile.writeText(TEMPLATE_LAYOUT.format(className))
        return TEMPLATE_FRAGMENT.format(className,className.lowercase(),classname_lowercase)
    }

    companion object {
        const val TEMPLATE_FRAGMENT = """
package ch.sr35.touchsamplesynth.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import ch.sr35.touchsamplesynth.R
import ch.sr35.touchsamplesynth.audio.instruments.%1${'$'}sI

class %1${'$'}sFragment() : Fragment() {

    constructor(s: %1${'$'}sI): this()
    {
        synth = s
    }

    private var synth: %1${'$'}sI?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_%2${'$'}s, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        synth?.let {
        }
    }
}
"""
        const val TEMPLATE_LAYOUT = """
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="358dp"
    android:layout_height="match_parent"
    android:theme="@style/Theme.TouchSampleSynth.Instrument"
    android:orientation="vertical"
    tools:context=".fragments.%1${'$'}sFragment">
</LinearLayout>
        """
    }
}