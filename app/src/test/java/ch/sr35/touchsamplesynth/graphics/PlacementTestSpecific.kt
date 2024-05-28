package ch.sr35.touchsamplesynth.graphics

import org.junit.Assert
import org.junit.Test

class PlacementTestSpecific {

    @Test
    fun linesInsideOfTest1()
    {
        val r = Rectangle(Point(120.0,20.0),Point(388.0,352.0))
        val bb = Rectangle(Point(908.0,336.5),Point(1176.0,813.5))
        val linesInside = r.lineInsideOf(bb)
        Assert.assertNotNull(linesInside)
    }
}