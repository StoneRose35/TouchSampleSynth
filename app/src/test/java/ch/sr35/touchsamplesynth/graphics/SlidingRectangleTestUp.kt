package ch.sr35.touchsamplesynth.graphics

import org.junit.Assert
import org.junit.Test

class SlidingRectangleTestUp {

    @Test
    fun slidingRectangleTestTrimUp1_1()
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
            Point(1.0,2.0),
            Point(3.0,5.0)
        )
        val trimmedSlidingRectangle = sr.cutOverlappingPartsWith(overlapRectangle)
        Assert.assertTrue(trimmedSlidingRectangle.size == 1)
        Assert.assertTrue(pointEqual(trimmedSlidingRectangle.first().xyLine.sp,Point(3.0,6.0)))
        Assert.assertEquals(trimmedSlidingRectangle.first().xyLine.d,12.0,TEPS)
    }

    @Test
    fun slidingRectangleTestTrimUp2_1()
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
            Point(1.0,2.1),
            Point(3.0,5.0)
        )
        val trimmedSlidingRectangle = sr.cutOverlappingPartsWith(overlapRectangle)
        Assert.assertTrue(trimmedSlidingRectangle.size == 1)
        Assert.assertTrue(pointEqual(trimmedSlidingRectangle.first().xyLine.sp,Point(3.0,6.0)))
        Assert.assertEquals(trimmedSlidingRectangle.first().xyLine.d,12.0,TEPS)
    }

    @Test
    fun slidingRectangleTestTrimUp3_1()
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
            Point(1.0,1.9),
            Point(3.0,5.0)
        )
        val trimmedSlidingRectangle = sr.cutOverlappingPartsWith(overlapRectangle)
        Assert.assertTrue(trimmedSlidingRectangle.size == 1)
        Assert.assertTrue(pointEqual(trimmedSlidingRectangle.first().xyLine.sp,Point(3.0,6.0)))
        Assert.assertEquals(trimmedSlidingRectangle.first().xyLine.d,12.0,TEPS)
    }

    @Test
    fun slidingRectangleTestTrimUp1_2()
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
            Point(1.0,2.0),
            Point(5.0,5.0)
        )
        val trimmedSlidingRectangle = sr.cutOverlappingPartsWith(overlapRectangle)
        Assert.assertTrue(trimmedSlidingRectangle.size == 1)
        Assert.assertTrue(pointEqual(trimmedSlidingRectangle.first().xyLine.sp,Point(3.0,6.0)))
        Assert.assertEquals(trimmedSlidingRectangle.first().xyLine.d,12.0,TEPS)
    }

    @Test
    fun slidingRectangleTestTrimUp2_2()
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
            Point(1.0,2.1),
            Point(5.0,5.0)
        )
        val trimmedSlidingRectangle = sr.cutOverlappingPartsWith(overlapRectangle)
        Assert.assertTrue(trimmedSlidingRectangle.size == 1)
        Assert.assertTrue(pointEqual(trimmedSlidingRectangle.first().xyLine.sp,Point(3.0,6.0)))
        Assert.assertEquals(trimmedSlidingRectangle.first().xyLine.d,12.0,TEPS)
    }

    @Test
    fun slidingRectangleTestTrimUp3_2()
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
            Point(1.0,1.9),
            Point(5.0,5.0)
        )
        val trimmedSlidingRectangle = sr.cutOverlappingPartsWith(overlapRectangle)
        Assert.assertTrue(trimmedSlidingRectangle.size == 1)
        Assert.assertTrue(pointEqual(trimmedSlidingRectangle.first().xyLine.sp,Point(3.0,6.0)))
        Assert.assertEquals(trimmedSlidingRectangle.first().xyLine.d,12.0,TEPS)
    }

    @Test
    fun slidingRectangleTestTrimUp1_3()
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
            Point(3.0,2.0),
            Point(5.0,5.0)
        )
        val trimmedSlidingRectangle = sr.cutOverlappingPartsWith(overlapRectangle)
        Assert.assertTrue(trimmedSlidingRectangle.size == 1)
        Assert.assertTrue(pointEqual(trimmedSlidingRectangle.first().xyLine.sp,Point(3.0,6.0)))
        Assert.assertEquals(trimmedSlidingRectangle.first().xyLine.d,12.0,TEPS)
    }

    @Test
    fun slidingRectangleTestTrimUp2_3()
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
            Point(3.0,2.1),
            Point(5.0,5.0)
        )
        val trimmedSlidingRectangle = sr.cutOverlappingPartsWith(overlapRectangle)
        Assert.assertTrue(trimmedSlidingRectangle.size == 1)
        Assert.assertTrue(pointEqual(trimmedSlidingRectangle.first().xyLine.sp,Point(3.0,6.0)))
        Assert.assertEquals(trimmedSlidingRectangle.first().xyLine.d,12.0,TEPS)
    }

    @Test
    fun slidingRectangleTestTrimLeft3_3()
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
            Point(3.0,1.9),
            Point(5.0,5.0)
        )
        val trimmedSlidingRectangle = sr.cutOverlappingPartsWith(overlapRectangle)
        Assert.assertTrue(trimmedSlidingRectangle.size == 1)
        Assert.assertTrue(pointEqual(trimmedSlidingRectangle.first().xyLine.sp,Point(3.0,6.0)))
        Assert.assertEquals(trimmedSlidingRectangle.first().xyLine.d,12.0,TEPS)
    }

    fun pointEqual(actual: Point, expected: Point): Boolean
    {
        return (actual.x in expected.x - TEPS .. expected.x + TEPS)
                && (actual.y in expected.y - TEPS .. expected.y + TEPS)
    }
}