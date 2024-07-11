package ch.sr35.touchsamplesynth.graphics

import org.junit.Assert
import org.junit.Test

class RectangleLineIntersectionTest {

    @Test
    fun topBottomIntersectionTest1()
    {
        val rect = Rectangle(Point(1.0,1.0),Point(2.0,2.0))
        val line = Line(Point(1.5,0.5),Point(1.5,2.5))
        val intersections = rect.getIntersectingSides(line)
        Assert.assertTrue(intersections.size == 2)
        Assert.assertTrue(intersections.contains(Overlap.TOP))
        Assert.assertTrue(intersections.contains(Overlap.BOTTOM))
    }

    @Test
    fun topBottomIntersectionTest2()
    {
        val rect = Rectangle(Point(1.0,1.0),Point(2.0,2.0))
        val line = Line(Point(1.5,2.5),Point(1.5,0.5))
        val intersections = rect.getIntersectingSides(line)
        Assert.assertTrue(intersections.size == 2)
        Assert.assertTrue(intersections.contains(Overlap.TOP))
        Assert.assertTrue(intersections.contains(Overlap.BOTTOM))
    }

    @Test
    fun leftRightIntersectionTest1()
    {
        val rect = Rectangle(Point(1.0,1.0),Point(2.0,2.0))
        val line = Line(Point(0.5,1.5),Point(2.5,1.5))
        val intersections = rect.getIntersectingSides(line)
        Assert.assertTrue(intersections.size == 2)
        Assert.assertTrue(intersections.contains(Overlap.LEFT))
        Assert.assertTrue(intersections.contains(Overlap.RIGHT))
    }

    @Test
    fun leftRightIntersectionTest2()
    {
        val rect = Rectangle(Point(1.0,1.0),Point(2.0,2.0))
        val line = Line(Point(2.5,1.5),Point(0.5,1.5))
        val intersections = rect.getIntersectingSides(line)
        Assert.assertTrue(intersections.size == 2)
        Assert.assertTrue(intersections.contains(Overlap.LEFT))
        Assert.assertTrue(intersections.contains(Overlap.RIGHT))
    }

    @Test
    fun topIntersectionTest1()
    {
        val rect = Rectangle(Point(1.0,1.0),Point(2.0,2.0))
        val line = Line(Point(1.5,0.5),Point(1.5,1.5))
        val intersections = rect.getIntersectingSides(line)
        Assert.assertTrue(intersections.size == 1)
        Assert.assertTrue(intersections.contains(Overlap.TOP))
    }

    @Test
    fun topIntersectionTest2()
    {
        val rect = Rectangle(Point(1.0,1.0),Point(2.0,2.0))
        val line = Line(Point(1.5,1.5),Point(1.5,0.5))
        val intersections = rect.getIntersectingSides(line)
        Assert.assertTrue(intersections.size == 1)
        Assert.assertTrue(intersections.contains(Overlap.TOP))
    }

    @Test
    fun bottomIntersectionTest1()
    {
        val rect = Rectangle(Point(1.0,1.0),Point(2.0,2.0))
        val line = Line(Point(1.5,1.5),Point(1.5,2.5))
        val intersections = rect.getIntersectingSides(line)
        Assert.assertTrue(intersections.size == 1)
        Assert.assertTrue(intersections.contains(Overlap.BOTTOM))
    }

    @Test
    fun bottomIntersectionTest2()
    {
        val rect = Rectangle(Point(1.0,1.0),Point(2.0,2.0))
        val line = Line(Point(1.5,2.5),Point(1.5,1.5))
        val intersections = rect.getIntersectingSides(line)
        Assert.assertTrue(intersections.size == 1)
        Assert.assertTrue(intersections.contains(Overlap.BOTTOM))
    }

    @Test
    fun leftIntersectionTest1()
    {
        val rect = Rectangle(Point(1.0,1.0),Point(2.0,2.0))
        val line = Line(Point(0.5,1.5),Point(1.5,1.5))
        val intersections = rect.getIntersectingSides(line)
        Assert.assertTrue(intersections.size == 1)
        Assert.assertTrue(intersections.contains(Overlap.LEFT))
    }

    @Test
    fun leftIntersectionTest2()
    {
        val rect = Rectangle(Point(1.0,1.0),Point(2.0,2.0))
        val line = Line(Point(1.5,1.5),Point(0.5,1.5))
        val intersections = rect.getIntersectingSides(line)
        Assert.assertTrue(intersections.size == 1)
        Assert.assertTrue(intersections.contains(Overlap.LEFT))
    }

    @Test
    fun rightIntersectionTest1()
    {
        val rect = Rectangle(Point(1.0,1.0),Point(2.0,2.0))
        val line = Line(Point(1.5,1.5),Point(2.5,1.5))
        val intersections = rect.getIntersectingSides(line)
        Assert.assertTrue(intersections.size == 1)
        Assert.assertTrue(intersections.contains(Overlap.RIGHT))
    }

    @Test
    fun rightIntersectionTest2()
    {
        val rect = Rectangle(Point(1.0,1.0),Point(2.0,2.0))
        val line = Line(Point(2.5,1.5),Point(1.5,1.5))
        val intersections = rect.getIntersectingSides(line)
        Assert.assertTrue(intersections.size == 1)
        Assert.assertTrue(intersections.contains(Overlap.RIGHT))
    }

    @Test
    fun noIntersectionTest()
    {
        val rect = Rectangle(Point(1.0,1.0),Point(2.0,2.0))
        val line = Line(Point(0.5,0.5),Point(0.5,0.75))
        val intersections = rect.getIntersectingSides(line)
        Assert.assertTrue(intersections.isEmpty())

    }
}