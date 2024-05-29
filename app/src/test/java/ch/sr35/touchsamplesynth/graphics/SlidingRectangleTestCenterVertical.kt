package ch.sr35.touchsamplesynth.graphics

import org.junit.Assert
import org.junit.Test
import kotlin.math.abs

class SlidingRectangleTestCenterVertical {


    @Test
    fun slidingRectangleTestFullLeft()
    {
        val sr = SlidingRectangle(
            XYLine(
                Point(3.0,3.0),
                XYLineOrientation.VERTICAL,
                15.0),
            Rectangle(
                Point(2.0,2.0),
                Point(4.0,4.0)
            )
        )
        val overlapRectangle = Rectangle(
            Point(1.0,1.0),
            Point(3.0,20.0)
        )
        val trimmedSlidingRectangle = sr.cutOverlappingPartsWith(overlapRectangle)
        Assert.assertTrue(trimmedSlidingRectangle.isEmpty())
    }

    @Test
    fun slidingRectangleTestFullCenter()
    {
        val sr = SlidingRectangle(
            XYLine(
                Point(3.0,3.0),
                XYLineOrientation.VERTICAL,
                15.0),
            Rectangle(
                Point(2.0,2.0),
                Point(4.0,4.0)
            )
        )
        val overlapRectangle = Rectangle(
            Point(2.1,1.0),
            Point(2.9,20.0)
        )
        val trimmedSlidingRectangle = sr.cutOverlappingPartsWith(overlapRectangle)
        Assert.assertTrue(trimmedSlidingRectangle.isEmpty())
    }

    @Test
    fun slidingRectangleTestFullRight()
    {
        val sr = SlidingRectangle(
            XYLine(
                Point(3.0,3.0),
                XYLineOrientation.VERTICAL,
                15.0),
            Rectangle(
                Point(2.0,2.0),
                Point(4.0,4.0)
            )
        )
        val overlapRectangle = Rectangle(
            Point(3.0,1.0),
            Point(5.0,20.0)
        )
        val trimmedSlidingRectangle = sr.cutOverlappingPartsWith(overlapRectangle)
        Assert.assertTrue(trimmedSlidingRectangle.isEmpty())
    }

    @Test
    fun slidingRectangleTestFullLeft_2()
    {
        val sr = SlidingRectangle(
            XYLine(
                Point(3.0,3.0),
                XYLineOrientation.VERTICAL,
                15.0),
            Rectangle(
                Point(2.0,2.0),
                Point(4.0,4.0)
            )
        )
        val overlapRectangle = Rectangle(
            Point(1.0,3.0),
            Point(3.0,18.0)
        )
        val trimmedSlidingRectangle = sr.cutOverlappingPartsWith(overlapRectangle)
        Assert.assertTrue(trimmedSlidingRectangle.isEmpty())
    }

    @Test
    fun slidingRectangleTestFullCenter_2()
    {
        val sr = SlidingRectangle(
            XYLine(
                Point(3.0,3.0),
                XYLineOrientation.VERTICAL,
                15.0),
            Rectangle(
                Point(3.0,2.0),
                Point(18.0,4.0)
            )
        )
        val overlapRectangle = Rectangle(
            Point(2.1,1.0),
            Point(2.9,20.0)
        )
        val trimmedSlidingRectangle = sr.cutOverlappingPartsWith(overlapRectangle)
        Assert.assertTrue(trimmedSlidingRectangle.isEmpty())
    }

    @Test
    fun slidingRectangleTestFullBottom_2()
    {
        val sr = SlidingRectangle(
            XYLine(
                Point(3.0,3.0),
                XYLineOrientation.VERTICAL,
                15.0),
            Rectangle(
                Point(2.0,2.0),
                Point(4.0,4.0)
            )
        )
        val overlapRectangle = Rectangle(
            Point(3.0,3.0),
            Point(5.0,18.0)
        )
        val trimmedSlidingRectangle = sr.cutOverlappingPartsWith(overlapRectangle)
        Assert.assertTrue(trimmedSlidingRectangle.isEmpty())
    }

    @Test
    fun slidingRectangleTestFull()
    {
        val sr = SlidingRectangle(
            XYLine(
                Point(3.0,3.0),
                XYLineOrientation.VERTICAL,
                15.0),
            Rectangle(
                Point(2.0,2.0),
                Point(4.0,4.0)
            )
        )
        val overlapRectangle = Rectangle(
            Point(0.0,0.0),
            Point(10.0,30.0)
        )
        val trimmedSlidingRectangle = sr.cutOverlappingPartsWith(overlapRectangle)
        Assert.assertTrue(trimmedSlidingRectangle.isEmpty())
    }

    @Test
    fun slidingRectangleTestCut()
    {
        val sr = SlidingRectangle(
            XYLine(
                Point(3.0,3.0),
                XYLineOrientation.VERTICAL,
                15.0),
            Rectangle(
                Point(2.0,2.0),
                Point(4.0,4.0)
            )
        )
        val overlapRectangle = Rectangle(
            Point(1.0,7.0),
            Point(5.0,9.0)
        )
        val trimmedSlidingRectangle = sr.cutOverlappingPartsWith(overlapRectangle)
        Assert.assertTrue(trimmedSlidingRectangle.size==2)
        Assert.assertTrue(trimmedSlidingRectangle.any { el -> pointEqual(el.xyLine.sp,Point(3.0,3.0)) })
        Assert.assertTrue(trimmedSlidingRectangle.any { el -> pointEqual(el.xyLine.sp,Point(3.0,10.0)) })
        Assert.assertTrue(trimmedSlidingRectangle.any { el -> abs(el.xyLine.d-3.0) < TEPS })
        Assert.assertTrue(trimmedSlidingRectangle.any { el -> abs(el.xyLine.d-8.0) < TEPS })
    }

    fun pointEqual(actual: Point, expected: Point): Boolean
    {
        return (actual.x in expected.x - TEPS .. expected.x + TEPS)
                && (actual.y in expected.y - TEPS .. expected.y + TEPS)
    }
}