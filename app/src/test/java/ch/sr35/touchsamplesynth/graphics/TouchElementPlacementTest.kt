package ch.sr35.touchsamplesynth.graphics

import org.junit.Assert
import org.junit.Test
import kotlin.math.abs

const val TEPS = 0.000001
class TouchElementPlacementTest {

    @Test
    fun rectangleTest1()
    {
        val r=Rectangle(Point(1.0,1.0),Point(2.0,3.0))
        Assert.assertEquals(r.width(),1.0,TEPS)
        Assert.assertEquals(r.height(),2.0,TEPS)
        Assert.assertEquals(r.topLeft.x,1.0,TEPS)
        Assert.assertEquals(r.topLeft.y,1.0,TEPS)
        Assert.assertEquals(r.bottomRight.x,2.0,TEPS)
        Assert.assertEquals(r.bottomRight.y,3.0,TEPS)
    }

    @Test
    fun rectangleTest2()
    {
        val r=Rectangle(Point(2.0,3.0),Point(1.0,1.0))
        Assert.assertEquals(r.width(),1.0,TEPS)
        Assert.assertEquals(r.height(),2.0,TEPS)
        Assert.assertEquals(r.topLeft.x,1.0,TEPS)
        Assert.assertEquals(r.topLeft.y,1.0,TEPS)
        Assert.assertEquals(r.bottomRight.x,2.0,TEPS)
        Assert.assertEquals(r.bottomRight.y,3.0,TEPS)
    }

    @Test
    fun rectangleTest3()
    {
        val r=Rectangle(Point(2.0,1.0),Point(1.0,3.0))
        Assert.assertEquals(r.width(),1.0,TEPS)
        Assert.assertEquals(r.height(),2.0,TEPS)
        Assert.assertEquals(r.topLeft.x,1.0,TEPS)
        Assert.assertEquals(r.topLeft.y,1.0,TEPS)
        Assert.assertEquals(r.bottomRight.x,2.0,TEPS)
        Assert.assertEquals(r.bottomRight.y,3.0,TEPS)
    }

    @Test
    fun rectangleCenterTest()
    {
        val r=Rectangle(Point(1.0,1.0),Point(2.0,3.0))
        Assert.assertEquals(r.center().x,1.5,TEPS)
        Assert.assertEquals(r.center().y,2.0,TEPS)
    }

    @Test
    fun edgePointsTest()
    {
        val r=Rectangle(Point(2.0,1.0),Point(1.0,3.0))
        val points = r.edgePoints()
        Assert.assertTrue(points.size == 4)
        Assert.assertTrue(points.filter { p -> pointEqual(p,Point(1.0,1.0)) }.size == 1)
        Assert.assertTrue(points.filter { p -> pointEqual(p,Point(2.0,1.0)) }.size == 1)
        Assert.assertTrue(points.filter { p -> pointEqual(p,Point(1.0,3.0)) }.size == 1)
        Assert.assertTrue(points.filter { p -> pointEqual(p,Point(2.0,3.0)) }.size == 1)
    }

    @Test
    fun lineInsideOfCaseATest()
    {
        val thisRectangle=Rectangle(Point(0.0,0.0), Point(5.0,4.0))
        val otherRectangle=Rectangle(Point(1.0,1.0),Point(2.0,2.0))
        val overlap = thisRectangle.lineInsideOf(otherRectangle)
        Assert.assertTrue(overlap.overlap == Overlap.FULL)
        Assert.assertTrue(overlap.intersectingLines.isEmpty())
    }

    @Test
    fun lineInsideOfCaseBTest1()
    {
        val thisRectangle=Rectangle(Point(0.0,0.0), Point(5.0,4.0))
        val otherRectangle=Rectangle(Point(4.0,1.0),Point(6.0,2.0))
        val overlap = thisRectangle.lineInsideOf(otherRectangle)
        Assert.assertTrue(overlap.overlap == Overlap.LEFT)
        Assert.assertTrue(overlap.intersectingLines.size == 1)
        Assert.assertTrue(pointEqual(overlap.intersectingLines[0].sp,Point(5.0,1.0)))
        Assert.assertTrue(overlap.intersectingLines[0].orientation==XYLineOrientation.VERTICAL)
        Assert.assertEquals(overlap.intersectingLines[0].d,1.0,TEPS)
    }

    @Test
    fun lineInsideOfCaseBTest2()
    {
        val thisRectangle=Rectangle(Point(1.0,1.0), Point(5.0,4.0))
        val otherRectangle=Rectangle(Point(0.0,2.0),Point(2.0,3.0))
        val overlap = thisRectangle.lineInsideOf(otherRectangle)
        Assert.assertTrue(overlap.overlap == Overlap.RIGHT)
        Assert.assertTrue(overlap.intersectingLines.size == 1)
        Assert.assertTrue(pointEqual(overlap.intersectingLines[0].sp,Point(1.0,2.0)))
        Assert.assertTrue(overlap.intersectingLines[0].orientation==XYLineOrientation.VERTICAL)
        Assert.assertEquals(overlap.intersectingLines[0].d,1.0,TEPS)
    }

    @Test
    fun lineInsideOfCaseBTest3()
    {
        val thisRectangle=Rectangle(Point(1.0,1.0), Point(5.0,4.0))
        val otherRectangle=Rectangle(Point(2.0,3.0),Point(4.0,5.0))
        val overlap = thisRectangle.lineInsideOf(otherRectangle)
        Assert.assertTrue(overlap.overlap == Overlap.BOTTOM)
        Assert.assertTrue(overlap.intersectingLines.size == 1)
        Assert.assertTrue(pointEqual(overlap.intersectingLines[0].sp,Point(2.0,4.0)))
        Assert.assertTrue(overlap.intersectingLines[0].orientation==XYLineOrientation.HORIZONTAL)
        Assert.assertEquals(overlap.intersectingLines[0].d,2.0,TEPS)
    }

    @Test
    fun lineInsideOfCaseBTest4()
    {
        val thisRectangle=Rectangle(Point(1.0,1.0), Point(5.0,4.0))
        val otherRectangle=Rectangle(Point(2.0,0.0),Point(4.0,2.0))
        val overlap = thisRectangle.lineInsideOf(otherRectangle)
        Assert.assertTrue(overlap.overlap == Overlap.TOP)
        Assert.assertTrue(overlap.intersectingLines.size == 1)
        Assert.assertTrue(pointEqual(overlap.intersectingLines[0].sp,Point(2.0,1.0)))
        Assert.assertTrue(overlap.intersectingLines[0].orientation==XYLineOrientation.HORIZONTAL)
        Assert.assertEquals(overlap.intersectingLines[0].d,2.0,TEPS)
    }


    @Test
    fun lineInsideOfCaseCTest1()
    {
        val thisRectangle=Rectangle(Point(3.0,1.0), Point(4.0,4.0))
        val otherRectangle=Rectangle(Point(1.0,2.0),Point(5.0,3.0))
        val overlap = thisRectangle.lineInsideOf(otherRectangle)
        Assert.assertTrue(overlap.overlap == Overlap.CENTER)
        Assert.assertTrue(overlap.intersectingLines.size == 2)
        Assert.assertTrue(overlap.intersectingLines.any { il -> pointEqual(il.sp,Point(3.0,2.0)) })
        Assert.assertTrue(overlap.intersectingLines.any { il -> pointEqual(il.sp,Point(4.0,2.0))})
        Assert.assertTrue(overlap.intersectingLines.all { il -> il.orientation == XYLineOrientation.VERTICAL })
        Assert.assertEquals(overlap.intersectingLines[0].d,1.0,TEPS)
        Assert.assertEquals(overlap.intersectingLines[1].d,1.0,TEPS)
    }

    @Test
    fun lineInsideOfCaseCTest2()
    {
        val thisRectangle=Rectangle(Point(1.0,2.0),Point(5.0,3.0))
        val otherRectangle=Rectangle(Point(3.0,1.0), Point(4.0,4.0))
        val overlap = thisRectangle.lineInsideOf(otherRectangle)
        Assert.assertTrue(overlap.overlap == Overlap.CENTER)
        Assert.assertTrue(overlap.intersectingLines.size == 2)
        Assert.assertTrue(overlap.intersectingLines.any { il -> pointEqual(il.sp,Point(3.0,2.0)) })
        Assert.assertTrue(overlap.intersectingLines.any { il -> pointEqual(il.sp,Point(3.0,3.0))})
        Assert.assertTrue(overlap.intersectingLines.all { il -> il.orientation == XYLineOrientation.HORIZONTAL })
        Assert.assertEquals(overlap.intersectingLines[0].d,1.0,TEPS)
        Assert.assertEquals(overlap.intersectingLines[1].d,1.0,TEPS)
    }

    @Test
    fun lineInsideOfCaseDTest1()
    {
        val thisRectangle=Rectangle(Point(0.0,2.0),Point(2.0,4.0))
        val otherRectangle=Rectangle(Point(1.0,1.0), Point(9.0,9.0))
        val overlap = thisRectangle.lineInsideOf(otherRectangle)
        Assert.assertTrue(overlap.overlap == Overlap.LEFT)
        Assert.assertTrue(overlap.intersectingLines.size == 3)
        Assert.assertTrue(overlap.intersectingLines.any { il -> pointEqual(il.sp,Point(1.0,2.0)) })
        Assert.assertTrue(overlap.intersectingLines.any { il -> pointEqual(il.sp,Point(1.0,4.0)) })
        Assert.assertTrue(overlap.intersectingLines.any { il -> pointEqual(il.sp,Point(2.0,2.0)) })
        Assert.assertTrue(overlap.intersectingLines.count { il -> il.orientation==XYLineOrientation.HORIZONTAL && abs(il.d-1.0)<TEPS }==2)
        Assert.assertTrue(overlap.intersectingLines.count { il -> il.orientation==XYLineOrientation.VERTICAL && abs(il.d-2.0)<TEPS }==1)
    }

    @Test
    fun lineInsideOfCaseDTest2()
    {
        val thisRectangle=Rectangle(Point(8.0,2.0),Point(10.0,4.0))
        val otherRectangle=Rectangle(Point(1.0,1.0), Point(9.0,9.0))
        val overlap = thisRectangle.lineInsideOf(otherRectangle)
        Assert.assertTrue(overlap.overlap == Overlap.RIGHT)
        Assert.assertTrue(overlap.intersectingLines.size == 3)
        Assert.assertTrue(overlap.intersectingLines.any { il -> pointEqual(il.sp,Point(8.0,2.0)) })
        Assert.assertTrue(overlap.intersectingLines.any { il -> pointEqual(il.sp,Point(8.0,4.0)) })
        Assert.assertTrue(overlap.intersectingLines.any { il -> pointEqual(il.sp,Point(8.0,2.0)) })
        Assert.assertTrue(overlap.intersectingLines.count { il -> il.orientation==XYLineOrientation.HORIZONTAL && abs(il.d-1.0)<TEPS }==2)
        Assert.assertTrue(overlap.intersectingLines.count { il -> il.orientation==XYLineOrientation.VERTICAL && abs(il.d-2.0)<TEPS }==1)
    }

    @Test
    fun lineInsideOfCaseDTest3()
    {
        val thisRectangle=Rectangle(Point(3.0,0.0),Point(5.0,2.0))
        val otherRectangle=Rectangle(Point(1.0,1.0), Point(9.0,9.0))
        val overlap = thisRectangle.lineInsideOf(otherRectangle)
        Assert.assertTrue(overlap.overlap == Overlap.TOP)
        Assert.assertTrue(overlap.intersectingLines.size == 3)
        Assert.assertTrue(overlap.intersectingLines.any { il -> pointEqual(il.sp,Point(3.0,1.0)) })
        Assert.assertTrue(overlap.intersectingLines.any { il -> pointEqual(il.sp,Point(5.0,1.0)) })
        Assert.assertTrue(overlap.intersectingLines.any { il -> pointEqual(il.sp,Point(3.0,2.0)) })
        Assert.assertTrue(overlap.intersectingLines.count { il -> il.orientation==XYLineOrientation.VERTICAL && abs(il.d-1.0)<TEPS }==2)
        Assert.assertTrue(overlap.intersectingLines.count { il -> il.orientation==XYLineOrientation.HORIZONTAL && abs(il.d-2.0)<TEPS }==1)
    }

    @Test
    fun lineInsideOfCaseDTest4()
    {
        val thisRectangle=Rectangle(Point(3.0,8.0),Point(5.0,10.0))
        val otherRectangle=Rectangle(Point(1.0,1.0), Point(9.0,9.0))
        val overlap = thisRectangle.lineInsideOf(otherRectangle)
        Assert.assertTrue(overlap.overlap == Overlap.BOTTOM)
        Assert.assertTrue(overlap.intersectingLines.size == 3)
        Assert.assertTrue(overlap.intersectingLines.count { il -> pointEqual(il.sp,Point(3.0,8.0)) }==2)
        Assert.assertTrue(overlap.intersectingLines.any { il -> pointEqual(il.sp,Point(5.0,8.0)) })
        Assert.assertTrue(overlap.intersectingLines.count { il -> il.orientation==XYLineOrientation.VERTICAL && abs(il.d-1.0)<TEPS }==2)
        Assert.assertTrue(overlap.intersectingLines.count { il -> il.orientation==XYLineOrientation.HORIZONTAL && abs(il.d-2.0)<TEPS }==1)
    }

    @Test
    fun lineInsideOfCaseETest1()
    {
        val thisRectangle=Rectangle(Point(0.0,0.0),Point(2.0,2.0))
        val otherRectangle=Rectangle(Point(1.0,1.0), Point(9.0,9.0))
        val overlap = thisRectangle.lineInsideOf(otherRectangle)
        Assert.assertTrue(overlap.overlap == Overlap.TOPLEFT)
        Assert.assertTrue(overlap.intersectingLines.size == 2)
        Assert.assertTrue(overlap.intersectingLines.count { il -> il.orientation==XYLineOrientation.VERTICAL
                && abs(il.d-1.0)<TEPS
                && pointEqual(il.sp,Point(2.0,1.0))}==1)
        Assert.assertTrue(overlap.intersectingLines.count { il -> il.orientation==XYLineOrientation.HORIZONTAL
                && abs(il.d-1.0)<TEPS
                && pointEqual(il.sp,Point(1.0,2.0))}==1)

    }

    @Test
    fun lineInsideOfCaseETest2()
    {
        val thisRectangle=Rectangle(Point(8.0,0.0),Point(10.0,2.0))
        val otherRectangle=Rectangle(Point(1.0,1.0), Point(9.0,9.0))
        val overlap = thisRectangle.lineInsideOf(otherRectangle)
        Assert.assertTrue(overlap.overlap == Overlap.TOPRIGHT)
        Assert.assertTrue(overlap.intersectingLines.size == 2)
        Assert.assertTrue(overlap.intersectingLines.count { il -> il.orientation==XYLineOrientation.VERTICAL
                && abs(il.d-1.0)<TEPS
                && pointEqual(il.sp,Point(8.0,1.0))}==1)
        Assert.assertTrue(overlap.intersectingLines.count { il -> il.orientation==XYLineOrientation.HORIZONTAL
                && abs(il.d-1.0)<TEPS
                && pointEqual(il.sp,Point(8.0,2.0))}==1)
    }

    @Test
    fun lineInsideOfCaseETest3()
    {
        val thisRectangle=Rectangle(Point(0.0,8.0),Point(2.0,10.0))
        val otherRectangle=Rectangle(Point(1.0,1.0), Point(9.0,9.0))
        val overlap = thisRectangle.lineInsideOf(otherRectangle)
        Assert.assertTrue(overlap.overlap == Overlap.BOTTOMLEFT)
        Assert.assertTrue(overlap.intersectingLines.size == 2)
        Assert.assertTrue(overlap.intersectingLines.count { il -> il.orientation==XYLineOrientation.VERTICAL
                && abs(il.d-1.0)<TEPS
                && pointEqual(il.sp,Point(2.0,8.0))}==1)
        Assert.assertTrue(overlap.intersectingLines.count { il -> il.orientation==XYLineOrientation.HORIZONTAL
                && abs(il.d-1.0)<TEPS
                && pointEqual(il.sp,Point(1.0,8.0))}==1)
    }

    @Test
    fun lineInsideOfCaseETest4()
    {
        val thisRectangle=Rectangle(Point(8.0,8.0),Point(10.0,10.0))
        val otherRectangle=Rectangle(Point(1.0,1.0), Point(9.0,9.0))
        val overlap = thisRectangle.lineInsideOf(otherRectangle)
        Assert.assertTrue(overlap.overlap == Overlap.BOTTOMRIGHT)
        Assert.assertTrue(overlap.intersectingLines.size == 2)
        Assert.assertTrue(overlap.intersectingLines.count { il -> il.orientation==XYLineOrientation.VERTICAL
                && abs(il.d-1.0)<TEPS
                && pointEqual(il.sp,Point(8.0,8.0))}==1)
        Assert.assertTrue(overlap.intersectingLines.count { il -> il.orientation==XYLineOrientation.HORIZONTAL
                && abs(il.d-1.0)<TEPS
                && pointEqual(il.sp,Point(8.0,8.0))}==1)
    }

    @Test
    fun lineInsideOfCaseFTest()
    {
        val thisRectangle=Rectangle(Point(4.0,4.0),Point(5.0,5.0))
        val otherRectangle=Rectangle(Point(1.0,1.0), Point(9.0,9.0))
        val overlap = thisRectangle.lineInsideOf(otherRectangle)
        Assert.assertTrue(overlap.overlap == Overlap.FULL)
        Assert.assertTrue(overlap.intersectingLines.size == 4)
        Assert.assertTrue(overlap.intersectingLines.count { il -> il.orientation==XYLineOrientation.VERTICAL
                && abs(il.d-1.0)<TEPS
                && pointEqual(il.sp,Point(4.0,4.0))}==1)
        Assert.assertTrue(overlap.intersectingLines.count { il -> il.orientation==XYLineOrientation.HORIZONTAL
                && abs(il.d-1.0)<TEPS
                && pointEqual(il.sp,Point(4.0,4.0))}==1)
        Assert.assertTrue(overlap.intersectingLines.count { il -> il.orientation==XYLineOrientation.HORIZONTAL
                && abs(il.d-1.0)<TEPS
                && pointEqual(il.sp,Point(4.0,5.0))}==1)
        Assert.assertTrue(overlap.intersectingLines.count { il -> il.orientation==XYLineOrientation.VERTICAL
                && abs(il.d-1.0)<TEPS
                && pointEqual(il.sp,Point(5.0,4.0))}==1)
    }

    fun pointEqual(actual: Point, expected: Point): Boolean
    {
        return (actual.x in expected.x - TEPS .. expected.x + TEPS)
                && (actual.y in expected.y - TEPS .. expected.y + TEPS)
    }

}