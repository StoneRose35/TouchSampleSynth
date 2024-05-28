package ch.sr35.touchsamplesynth.graphics

import org.junit.Assert
import org.junit.Test

class SlidingRectangleTest {

    @Test
    fun slidingRectrangeTestTrimLeft1_1()
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
            Point(2.0,1.0),
            Point(5.0,3.0)
        )
        val trimmedSlidingRectangle = sr.cutOverlappingPartsWith(overlapRectangle)
        Assert.assertTrue(trimmedSlidingRectangle.size == 1)
        Assert.assertTrue(pointEqual(trimmedSlidingRectangle.first().xyLine.sp,Point(6.0,3.0)))
        Assert.assertEquals(trimmedSlidingRectangle.first().xyLine.d,12.0,TEPS)
    }

    @Test
    fun slidingRectrangeTestTrimLeft2_1()
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
            Point(2.1,1.0),
            Point(5.0,3.0)
        )
        val trimmedSlidingRectangle = sr.cutOverlappingPartsWith(overlapRectangle)
        Assert.assertTrue(trimmedSlidingRectangle.size == 1)
        Assert.assertTrue(pointEqual(trimmedSlidingRectangle.first().xyLine.sp,Point(6.0,3.0)))
        Assert.assertEquals(trimmedSlidingRectangle.first().xyLine.d,12.0,TEPS)
    }

    @Test
    fun slidingRectrangeTestTrimLeft3_1()
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
            Point(1.9,1.0),
            Point(5.0,3.0)
        )
        val trimmedSlidingRectangle = sr.cutOverlappingPartsWith(overlapRectangle)
        Assert.assertTrue(trimmedSlidingRectangle.size == 1)
        Assert.assertTrue(pointEqual(trimmedSlidingRectangle.first().xyLine.sp,Point(6.0,3.0)))
        Assert.assertEquals(trimmedSlidingRectangle.first().xyLine.d,12.0,TEPS)
    }

    @Test
    fun slidingRectrangeTestTrimLeft1_2()
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
            Point(2.0,1.0),
            Point(5.0,5.0)
        )
        val trimmedSlidingRectangle = sr.cutOverlappingPartsWith(overlapRectangle)
        Assert.assertTrue(trimmedSlidingRectangle.size == 1)
        Assert.assertTrue(pointEqual(trimmedSlidingRectangle.first().xyLine.sp,Point(6.0,3.0)))
        Assert.assertEquals(trimmedSlidingRectangle.first().xyLine.d,12.0,TEPS)
    }

    @Test
    fun slidingRectrangeTestTrimLeft2_2()
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
            Point(2.1,1.0),
            Point(5.0,5.0)
        )
        val trimmedSlidingRectangle = sr.cutOverlappingPartsWith(overlapRectangle)
        Assert.assertTrue(trimmedSlidingRectangle.size == 1)
        Assert.assertTrue(pointEqual(trimmedSlidingRectangle.first().xyLine.sp,Point(6.0,3.0)))
        Assert.assertEquals(trimmedSlidingRectangle.first().xyLine.d,12.0,TEPS)
    }

    @Test
    fun slidingRectrangeTestTrimLeft3_2()
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
            Point(1.9,1.0),
            Point(5.0,5.0)
        )
        val trimmedSlidingRectangle = sr.cutOverlappingPartsWith(overlapRectangle)
        Assert.assertTrue(trimmedSlidingRectangle.size == 1)
        Assert.assertTrue(pointEqual(trimmedSlidingRectangle.first().xyLine.sp,Point(6.0,3.0)))
        Assert.assertEquals(trimmedSlidingRectangle.first().xyLine.d,12.0,TEPS)
    }

    fun pointEqual(actual: Point, expected: Point): Boolean
    {
        return (actual.x in expected.x - TEPS .. expected.x + TEPS)
                && (actual.y in expected.y - TEPS .. expected.y + TEPS)
    }
}