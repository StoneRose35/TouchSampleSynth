package ch.sr35.touchsamplesynth.graphics

import org.junit.Assert
import org.junit.Test

class SlidingRectangleTestRight {

    @Test
    fun slidingRectangleTestTrimRight1_1()
    {
        val sr = SlidingRectangle(
            XYLine(
                Point(3.0,3.0),
                XYLineOrientation.HORIZONTAL,
                15.0),
            Rectangle(
                Point(2.0,2.0),
                Point(4.0,4.0)
            )
        )
        val overlapRectangle = Rectangle(
            Point(16.0,1.0),
            Point(19.0,3.0)
        )
        val trimmedSlidingRectangle = sr.cutOverlappingPartsWith(overlapRectangle)
        Assert.assertTrue(trimmedSlidingRectangle.size == 1)
        Assert.assertTrue(pointEqual(trimmedSlidingRectangle.first().xyLine.sp,Point(3.0,3.0)))
        Assert.assertEquals(trimmedSlidingRectangle.first().xyLine.d,12.0,TEPS)
    }

    @Test
    fun slidingRectangleTestTrimRight2_1()
    {
        val sr = SlidingRectangle(
            XYLine(
                Point(3.0,3.0),
                XYLineOrientation.HORIZONTAL,
                15.0),
            Rectangle(
                Point(2.0,2.0),
                Point(4.0,4.0)
            )
        )
        val overlapRectangle = Rectangle(
            Point(16.0,1.0),
            Point(18.9,3.0)
        )
        val trimmedSlidingRectangle = sr.cutOverlappingPartsWith(overlapRectangle)
        Assert.assertTrue(trimmedSlidingRectangle.size == 1)
        Assert.assertTrue(pointEqual(trimmedSlidingRectangle.first().xyLine.sp,Point(3.0,3.0)))
        Assert.assertEquals(trimmedSlidingRectangle.first().xyLine.d,12.0,TEPS)
    }

    @Test
    fun slidingRectangleTestTrimRight3_1()
    {
        val sr = SlidingRectangle(
            XYLine(
                Point(3.0,3.0),
                XYLineOrientation.HORIZONTAL,
                15.0),
            Rectangle(
                Point(2.0,2.0),
                Point(4.0,4.0)
            )
        )
        val overlapRectangle = Rectangle(
            Point(16.0,1.0),
            Point(19.1,3.0)
        )
        val trimmedSlidingRectangle = sr.cutOverlappingPartsWith(overlapRectangle)
        Assert.assertTrue(trimmedSlidingRectangle.size == 1)
        Assert.assertTrue(pointEqual(trimmedSlidingRectangle.first().xyLine.sp,Point(3.0,3.0)))
        Assert.assertEquals(trimmedSlidingRectangle.first().xyLine.d,12.0,TEPS)
    }

    @Test
    fun slidingRectangleTestTrimRight1_2()
    {
        val sr = SlidingRectangle(
            XYLine(
                Point(3.0,3.0),
                XYLineOrientation.HORIZONTAL,
                15.0),
            Rectangle(
                Point(2.0,2.0),
                Point(4.0,4.0)
            )
        )
        val overlapRectangle = Rectangle(
            Point(16.0,1.0),
            Point(19.0,5.0)
        )
        val trimmedSlidingRectangle = sr.cutOverlappingPartsWith(overlapRectangle)
        Assert.assertTrue(trimmedSlidingRectangle.size == 1)
        Assert.assertTrue(pointEqual(trimmedSlidingRectangle.first().xyLine.sp,Point(3.0,3.0)))
        Assert.assertEquals(trimmedSlidingRectangle.first().xyLine.d,12.0,TEPS)
    }

    @Test
    fun slidingRectangleTestTrimRight2_2()
    {
        val sr = SlidingRectangle(
            XYLine(
                Point(3.0,3.0),
                XYLineOrientation.HORIZONTAL,
                15.0),
            Rectangle(
                Point(2.0,2.0),
                Point(4.0,4.0)
            )
        )
        val overlapRectangle = Rectangle(
            Point(16.0,1.0),
            Point(18.9,5.0)
        )
        val trimmedSlidingRectangle = sr.cutOverlappingPartsWith(overlapRectangle)
        Assert.assertTrue(trimmedSlidingRectangle.size == 1)
        Assert.assertTrue(pointEqual(trimmedSlidingRectangle.first().xyLine.sp,Point(3.0,3.0)))
        Assert.assertEquals(trimmedSlidingRectangle.first().xyLine.d,12.0,TEPS)
    }

    @Test
    fun slidingRectangleTestTrimRight3_2()
    {
        val sr = SlidingRectangle(
            XYLine(
                Point(3.0,3.0),
                XYLineOrientation.HORIZONTAL,
                15.0),
            Rectangle(
                Point(2.0,2.0),
                Point(4.0,4.0)
            )
        )
        val overlapRectangle = Rectangle(
            Point(16.0,1.0),
            Point(19.1,5.0)
        )
        val trimmedSlidingRectangle = sr.cutOverlappingPartsWith(overlapRectangle)
        Assert.assertTrue(trimmedSlidingRectangle.size == 1)
        Assert.assertTrue(pointEqual(trimmedSlidingRectangle.first().xyLine.sp,Point(3.0,3.0)))
        Assert.assertEquals(trimmedSlidingRectangle.first().xyLine.d,12.0,TEPS)
    }

    @Test
    fun slidingRectangleTestTrimRight1_3()
    {
        val sr = SlidingRectangle(
            XYLine(
                Point(3.0,3.0),
                XYLineOrientation.HORIZONTAL,
                15.0),
            Rectangle(
                Point(2.0,2.0),
                Point(4.0,4.0)
            )
        )
        val overlapRectangle = Rectangle(
            Point(16.0,3.0),
            Point(19.0,5.0)
        )
        val trimmedSlidingRectangle = sr.cutOverlappingPartsWith(overlapRectangle)
        Assert.assertTrue(trimmedSlidingRectangle.size == 1)
        Assert.assertTrue(pointEqual(trimmedSlidingRectangle.first().xyLine.sp,Point(3.0,3.0)))
        Assert.assertEquals(trimmedSlidingRectangle.first().xyLine.d,12.0,TEPS)
    }

    @Test
    fun slidingRectangleTestTrimRight2_3()
    {
        val sr = SlidingRectangle(
            XYLine(
                Point(3.0,3.0),
                XYLineOrientation.HORIZONTAL,
                15.0),
            Rectangle(
                Point(2.0,2.0),
                Point(4.0,4.0)
            )
        )
        val overlapRectangle = Rectangle(
            Point(16.0,3.0),
            Point(18.9,5.0)
        )
        val trimmedSlidingRectangle = sr.cutOverlappingPartsWith(overlapRectangle)
        Assert.assertTrue(trimmedSlidingRectangle.size == 1)
        Assert.assertTrue(pointEqual(trimmedSlidingRectangle.first().xyLine.sp,Point(3.0,3.0)))
        Assert.assertEquals(trimmedSlidingRectangle.first().xyLine.d,12.0,TEPS)
    }

    @Test
    fun slidingRectangleTestTrimRight3_3()
    {
        val sr = SlidingRectangle(
            XYLine(
                Point(3.0,3.0),
                XYLineOrientation.HORIZONTAL,
                15.0),
            Rectangle(
                Point(2.0,2.0),
                Point(4.0,4.0)
            )
        )
        val overlapRectangle = Rectangle(
            Point(16.0,3.0),
            Point(19.1,5.0)
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