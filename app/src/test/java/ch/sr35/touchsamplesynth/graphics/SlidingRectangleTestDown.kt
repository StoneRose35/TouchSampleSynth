package ch.sr35.touchsamplesynth.graphics

import org.junit.Assert
import org.junit.Test

class SlidingRectangleTestDown {

    @Test
    fun slidingRectangleTestTrimDown1_1()
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
            Point(1.0,16.0),
            Point(3.0,19.0)
        )
        val trimmedSlidingRectangle = sr.cutOverlappingPartsWith(overlapRectangle)
        Assert.assertTrue(trimmedSlidingRectangle.size == 1)
        Assert.assertTrue(pointEqual(trimmedSlidingRectangle.first().xyLine.sp,Point(3.0,3.0)))
        Assert.assertEquals(trimmedSlidingRectangle.first().xyLine.d,12.0,TEPS)
    }

    @Test
    fun slidingRectangleTestTrimDown2_1()
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
            Point(1.0,16.0),
            Point(3.0,18.9)
        )
        val trimmedSlidingRectangle = sr.cutOverlappingPartsWith(overlapRectangle)
        Assert.assertTrue(trimmedSlidingRectangle.size == 1)
        Assert.assertTrue(pointEqual(trimmedSlidingRectangle.first().xyLine.sp,Point(3.0,3.0)))
        Assert.assertEquals(trimmedSlidingRectangle.first().xyLine.d,12.0,TEPS)
    }

    @Test
    fun slidingRectangleTestTrimDown3_1()
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
            Point(1.0,16.0),
            Point(3.0,19.1)
        )
        val trimmedSlidingRectangle = sr.cutOverlappingPartsWith(overlapRectangle)
        Assert.assertTrue(trimmedSlidingRectangle.size == 1)
        Assert.assertTrue(pointEqual(trimmedSlidingRectangle.first().xyLine.sp,Point(3.0,3.0)))
        Assert.assertEquals(trimmedSlidingRectangle.first().xyLine.d,12.0,TEPS)
    }

    @Test
    fun slidingRectangleTestTrimDown1_2()
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
            Point(1.0,16.0),
            Point(5.0,19.0)
        )
        val trimmedSlidingRectangle = sr.cutOverlappingPartsWith(overlapRectangle)
        Assert.assertTrue(trimmedSlidingRectangle.size == 1)
        Assert.assertTrue(pointEqual(trimmedSlidingRectangle.first().xyLine.sp,Point(3.0,3.0)))
        Assert.assertEquals(trimmedSlidingRectangle.first().xyLine.d,12.0,TEPS)
    }

    @Test
    fun slidingRectangleTestTrimDown2_2()
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
            Point(1.0,16.0),
            Point(5.0,18.9)
        )
        val trimmedSlidingRectangle = sr.cutOverlappingPartsWith(overlapRectangle)
        Assert.assertTrue(trimmedSlidingRectangle.size == 1)
        Assert.assertTrue(pointEqual(trimmedSlidingRectangle.first().xyLine.sp,Point(3.0,3.0)))
        Assert.assertEquals(trimmedSlidingRectangle.first().xyLine.d,12.0,TEPS)
    }

    @Test
    fun slidingRectangleTestTrimDown3_2()
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
            Point(1.0,16.0),
            Point(5.0,19.1)
        )
        val trimmedSlidingRectangle = sr.cutOverlappingPartsWith(overlapRectangle)
        Assert.assertTrue(trimmedSlidingRectangle.size == 1)
        Assert.assertTrue(pointEqual(trimmedSlidingRectangle.first().xyLine.sp,Point(3.0,3.0)))
        Assert.assertEquals(trimmedSlidingRectangle.first().xyLine.d,12.0,TEPS)
    }

    @Test
    fun slidingRectangleTestTrimDown1_3()
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
            Point(3.0,16.0),
            Point(5.0,19.0)
        )
        val trimmedSlidingRectangle = sr.cutOverlappingPartsWith(overlapRectangle)
        Assert.assertTrue(trimmedSlidingRectangle.size == 1)
        Assert.assertTrue(pointEqual(trimmedSlidingRectangle.first().xyLine.sp,Point(3.0,3.0)))
        Assert.assertEquals(trimmedSlidingRectangle.first().xyLine.d,12.0,TEPS)
    }

    @Test
    fun slidingRectangleTestTrimDown2_3()
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
            Point(3.0,16.0),
            Point(5.0,18.9)
        )
        val trimmedSlidingRectangle = sr.cutOverlappingPartsWith(overlapRectangle)
        Assert.assertTrue(trimmedSlidingRectangle.size == 1)
        Assert.assertTrue(pointEqual(trimmedSlidingRectangle.first().xyLine.sp,Point(3.0,3.0)))
        Assert.assertEquals(trimmedSlidingRectangle.first().xyLine.d,12.0,TEPS)
    }

    @Test
    fun slidingRectangleTestTrimDown3_3()
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
            Point(3.0,16.0),
            Point(5.0,19.1)
        )
        val trimmedSlidingRectangle = sr.cutOverlappingPartsWith(overlapRectangle)
        Assert.assertTrue(trimmedSlidingRectangle.size == 1)
        Assert.assertTrue(pointEqual(trimmedSlidingRectangle.first().xyLine.sp,Point(3.0,3.0)))
        Assert.assertEquals(trimmedSlidingRectangle.first().xyLine.d,12.0,TEPS)
    }

    fun pointEqual(actual: Point, expected: Point): Boolean
    {
        return (actual.x in expected.x - TEPS .. expected.x + TEPS)
                && (actual.y in expected.y - TEPS .. expected.y + TEPS)
    }
}