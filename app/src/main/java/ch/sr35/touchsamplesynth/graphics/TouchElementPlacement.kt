package ch.sr35.touchsamplesynth.graphics

import kotlin.math.min
import kotlin.math.max
import kotlin.math.sqrt


const val EPS=0.000001

class PlacementException(val msg: String): Exception()
{

}
class Point(var x: Double,var y: Double)
{
    operator fun plus(p: Point): Point
    {
        return Point(x+p.x,y+p.y)
    }

    operator fun minus(p: Point): Point
    {
        return Point(x-p.x,y-p.y)
    }

    fun vMult(p: Point): Double
    {
        return p.x*x + p.y*y
    }

    override fun toString(): String {
        return "Point(x: %.3f, y: %.3f)".format(x,y)
    }
}

enum class Overlap
{
    LEFT, RIGHT, TOP, BOTTOM, FULL, CENTER, NONE, TOPLEFT, TOPRIGHT, BOTTOMLEFT, BOTTOMRIGHT
}

class OverlapAndIntersectionLine(var overlap: Overlap,var interesctingLines: Array<XYLine>)
class SlidingRectangle(var xyLine: XYLine,val r: Rectangle)
{
    var boundingRectangle: Rectangle = if (xyLine.orientation == XYLineOrientation.HORIZONTAL) {
        Rectangle(
            Point(xyLine.sp.x - r.width() / 2, xyLine.sp.y - r.height() / 2),
            Point(xyLine.sp.x + xyLine.d + r.width() / 2, xyLine.sp.y + r.height() / 2)
        )
    } else
    {
       Rectangle(
           Point(xyLine.sp.x - r.width()/2,xyLine.sp.y - r.height()/2),
           Point(xyLine.sp.x + r.width()/2,xyLine.sp.y + xyLine.d + r.height()/2))
    }

    var endPositionRectangle: Rectangle = if (xyLine.orientation ==XYLineOrientation.HORIZONTAL)
    {
        Rectangle(
            Point(xyLine.sp.x + xyLine.d - r.width() / 2, xyLine.sp.y - r.height() / 2),
            Point(xyLine.sp.x + xyLine.d + r.width() / 2, xyLine.sp.y + r.height() / 2)
        )
    }
    else
    {
        Rectangle(
            Point(xyLine.sp.x - r.width() / 2, xyLine.sp.y + xyLine.d - r.height() / 2),
            Point(xyLine.sp.x + r.width() / 2, xyLine.sp.y + xyLine.d + r.height() / 2)
        )
    }
    var startPositionRectangle: Rectangle = Rectangle(
        Point(xyLine.sp.x - r.width() / 2, xyLine.sp.y - r.height() / 2),
        Point(xyLine.sp.x + r.width() / 2, xyLine.sp.y + r.height() / 2))

    /**
     * trims a sliding rectangle so that it doesnt overlap rect
     * if rect overlaps the entire sliding range an empty list is returned
     */
    fun cutOverlappingPartsWith(rect: Rectangle): Array<SlidingRectangle>
    {
        val linesInSlidingRect = rect.lineInsideOf(boundingRectangle)
        if (linesInSlidingRect.overlap == Overlap.NONE)
        {
            return arrayOf(this)
        }
        val perpendicularCuts = linesInSlidingRect.interesctingLines.filter {
            il -> il.orientation != xyLine.orientation
        }.toList()
        if (perpendicularCuts.isEmpty())
        {
            return emptyArray()
        }
        // compute the position of the perpendicular cuts
        // return an empty array if a perpedicular cut is along startingPositionRectangle and
        // endPositionRectangle
        else if (perpendicularCuts.size == 1)
        {
            if (xyLine.orientation== XYLineOrientation.HORIZONTAL)
            {
                if (linesInSlidingRect.overlap == Overlap.TOPLEFT ||
                    linesInSlidingRect.overlap == Overlap.BOTTOMLEFT ||
                    linesInSlidingRect.overlap == Overlap.LEFT)
                {
                    return if (perpendicularCuts[0].sp.x + perpendicularCuts[0].d > endPositionRectangle.topLeft.x)
                    {
                        emptyArray()
                    }
                    else
                    {
                        // trimming left
                        arrayOf(SlidingRectangle(XYLine(Point(perpendicularCuts[0].sp.x + r.width()/2,xyLine.sp.y),XYLineOrientation.HORIZONTAL,
                            xyLine.d - (perpendicularCuts[0].sp.x + r.width()/2 - startPositionRectangle.topLeft.x)),r))

                    }
                }
                else
                {
                    return if (perpendicularCuts[0].sp.x < startPositionRectangle.bottomRight.x)
                    {
                        emptyArray()
                    }
                    else
                    {
                        arrayOf(SlidingRectangle(XYLine(xyLine.sp,XYLineOrientation.HORIZONTAL,
                            perpendicularCuts[0].sp.x - r.width()/2 - xyLine.sp.x),r))
                    }
                }
            }
            else
            {
                if (linesInSlidingRect.overlap == Overlap.TOPLEFT ||
                    linesInSlidingRect.overlap == Overlap.TOPRIGHT ||
                    linesInSlidingRect.overlap == Overlap.TOP)
                {
                    return if (perpendicularCuts[0].sp.y + perpendicularCuts[0].d > endPositionRectangle.topLeft.y)
                    {
                        emptyArray()
                    }
                    else
                    {
                        arrayOf(SlidingRectangle(XYLine(Point(xyLine.sp.x,perpendicularCuts[0].sp.y + r.height()/2),XYLineOrientation.VERTICAL,
                            xyLine.d - (endPositionRectangle.bottomRight.y -  (perpendicularCuts[0].sp.y + r.height()/2))),r))
                    }
                }
                else
                {
                    return if (perpendicularCuts[0].sp.y < startPositionRectangle.bottomRight.y)
                    {
                        emptyArray()
                    }
                    else
                    {
                        arrayOf(SlidingRectangle(XYLine(xyLine.sp,XYLineOrientation.VERTICAL,perpendicularCuts[0].sp.y - r.height()/2 - xyLine.sp.y),r))
                    }
                }
            }
        }
        else if (perpendicularCuts.size == 2)
        {
            if (xyLine.orientation== XYLineOrientation.HORIZONTAL) {
                if (perpendicularCuts[0].sp.x < startPositionRectangle.bottomRight.x && perpendicularCuts[1].sp.x > endPositionRectangle.topLeft.x)
                {
                    return emptyArray()
                }

                else if (perpendicularCuts[0].sp.x < startPositionRectangle.bottomRight.x)
                {
                    // trimming left
                    return arrayOf(SlidingRectangle(XYLine(Point(perpendicularCuts[0].sp.x + r.width()/2,xyLine.sp.y),XYLineOrientation.HORIZONTAL,
                        xyLine.d - (perpendicularCuts[0].sp.x + r.width()/2 - startPositionRectangle.topLeft.x)),r))
                }
                else if (perpendicularCuts[1].sp.x > startPositionRectangle.bottomRight.x)
                {
                    // trimming right
                    return arrayOf(SlidingRectangle(XYLine(xyLine.sp,XYLineOrientation.HORIZONTAL,
                        perpendicularCuts[1].sp.x - r.width()/2 - xyLine.sp.x),r))
                }
                else
                {
                    // split in half
                    return arrayOf(
                        SlidingRectangle(
                            XYLine(
                                xyLine.sp,XYLineOrientation.HORIZONTAL,perpendicularCuts[0].sp.x - r.width()/2 - xyLine.sp.x
                            )
                            ,r
                        ),
                        SlidingRectangle(
                            XYLine(
                                Point(perpendicularCuts[1].sp.x + r.width()/2,xyLine.sp.y),
                                XYLineOrientation.HORIZONTAL,
                                endPositionRectangle.bottomRight.x - r.width() - perpendicularCuts[1].sp.x
                            )
                            ,r
                        )
                    )
                }
            }
            else
            {
                if (perpendicularCuts[0].sp.y < startPositionRectangle.bottomRight.y && perpendicularCuts[1].sp.y > endPositionRectangle.topLeft.y)
                {
                    return emptyArray()
                }

                else if (perpendicularCuts[0].sp.y < startPositionRectangle.bottomRight.y)
                {
                    // trimming top
                    return arrayOf(SlidingRectangle(XYLine(Point(xyLine.sp.x,perpendicularCuts[0].sp.y + r.height()/2),XYLineOrientation.VERTICAL,
                        xyLine.d - (endPositionRectangle.bottomRight.y -  (perpendicularCuts[0].sp.y + r.height()/2))),r))
                }
                else if (perpendicularCuts[1].sp.y > startPositionRectangle.bottomRight.y)
                {
                    // trimming bottom
                    return arrayOf(SlidingRectangle(XYLine(xyLine.sp,XYLineOrientation.VERTICAL,perpendicularCuts[1].sp.y - r.height()/2 - xyLine.sp.y),r))
                }
                else
                {
                    // split in half
                    return arrayOf(
                        SlidingRectangle(
                            XYLine(
                                xyLine.sp,XYLineOrientation.VERTICAL,perpendicularCuts[0].sp.y - r.height()/2 - xyLine.sp.y
                            )
                            ,r
                        ),
                        SlidingRectangle(
                            XYLine(
                                Point(xyLine.sp.x,perpendicularCuts[1].sp.y + r.height()/2),
                                XYLineOrientation.VERTICAL,
                                endPositionRectangle.bottomRight.y - r.height() - perpendicularCuts[1].sp.y
                            )
                            ,r
                        )
                    )
                }
            }
        }
        return emptyArray()
    }
}

class Rectangle(p1: Point,p2: Point)
{
    var topLeft: Point
    var bottomRight: Point
    init {
        topLeft = Point(min(p1.x,p2.x),min(p1.y,p2.y))
        bottomRight = Point(max(p1.x,p2.x),max(p1.y,p2.y))
    }

    fun height(): Double
    {
        return bottomRight.y-topLeft.y
    }

    fun width(): Double
    {
        return bottomRight.x-topLeft.x
    }

    fun center(): Point
    {
        return Point((topLeft.x + bottomRight.x)*0.5, (topLeft.y + bottomRight.y)*0.5)
    }

    fun edgePoints(): Array<Point>
    {
        return arrayOf(topLeft,Point(bottomRight.x,topLeft.y),Point(topLeft.x,bottomRight.y),bottomRight)
    }

    fun isInside(p: Point): Boolean
    {
        return p.x >= topLeft.x && p.x <= bottomRight.x && p.y >= topLeft.y && p.y <= bottomRight.y
    }

    override fun toString(): String {
        return "Rectangle(topLeft: %s, bottomRight: %s".format(topLeft,bottomRight)
    }

    /**
     * returns the lines intersecting line of this rectangle with r hich are fully within this rectangle
     *         if only one edge point of r is inside this rectange a line with length 0 is returned
     */
    fun lineInsideOf(r: Rectangle): OverlapAndIntersectionLine
    {
        val edgePoints = edgePoints()
        val pointsInsideR = edgePoints.filter { ep -> r.isInside(ep) }.toList()
        val pointsInsideThis = r.edgePoints().filter { ep -> isInside(ep) }.toList()
        var intersectingPoint: Point
        /*
              --------------------
              |       -----      |
              |       |   |      |
              | this  | r |      |
              |       -----      |
              --------------------
         */
        if (pointsInsideThis.size == 4 && pointsInsideR.isEmpty())
        {
            return OverlapAndIntersectionLine(Overlap.FULL,emptyArray())
        }
        /*
              --------------------
              |                  |
              |                -----------
              |                |     r   |
              | this           |         |
              |                -----------
              |                  |
              --------------------

         */
        else if (pointsInsideThis.size == 2 && pointsInsideR.isEmpty())
        {
            if (pointsInsideThis[0].y - pointsInsideThis[1].y < EPS)
            {
                return if (topLeft.x < r.topLeft.x) {
                    intersectingPoint = Point(this.bottomRight.x,r.topLeft.y)
                    OverlapAndIntersectionLine(Overlap.LEFT,arrayOf(XYLine(intersectingPoint,XYLineOrientation.VERTICAL,r.height())))
                } else {
                    intersectingPoint = Point(this.topLeft.x, r.topLeft.y)
                    OverlapAndIntersectionLine(Overlap.RIGHT,
                    arrayOf(
                        XYLine(
                            intersectingPoint,
                            XYLineOrientation.VERTICAL,
                            r.height()
                        )
                    ))
                }
            }
            else
            {
                return if (topLeft.y > r.topLeft.y) {
                    intersectingPoint = Point(r.topLeft.x,topLeft.y)
                    OverlapAndIntersectionLine(Overlap.TOP,
                    arrayOf(XYLine(intersectingPoint,XYLineOrientation.HORIZONTAL,r.width())))
                } else {
                    intersectingPoint = Point(r.topLeft.x,bottomRight.y)
                    OverlapAndIntersectionLine(Overlap.BOTTOM,
                    arrayOf(XYLine(intersectingPoint,XYLineOrientation.HORIZONTAL,r.width())))
                }
            }
        }
        /*        ------
                  |    |
                  |this|
                  |    |
            -----------------------
            |          r          |
            -----------------------
                  |    |
                  ------
         */
        else if (pointsInsideThis.isEmpty()
            && pointsInsideR.isEmpty())
        {
            return if (topLeft.x > r.topLeft.x && topLeft.x < r.bottomRight.x)
            {
                intersectingPoint = Point(topLeft.x,r.topLeft.y)
                val il1 = XYLine(intersectingPoint,XYLineOrientation.VERTICAL,r.height())
                intersectingPoint = Point(bottomRight.x,r.topLeft.y)
                val il2 = XYLine(intersectingPoint,XYLineOrientation.VERTICAL,r.height())
                OverlapAndIntersectionLine(Overlap.CENTER, arrayOf(il1,il2))
            }
            else if (topLeft.y > r.topLeft.y && topLeft.y < r.bottomRight.y)
            {
                intersectingPoint = Point(r.topLeft.x,topLeft.y)
                val il1 = XYLine(intersectingPoint,XYLineOrientation.HORIZONTAL,r.width())
                intersectingPoint = Point(r.topLeft.x,bottomRight.y)
                val il2 = XYLine(intersectingPoint,XYLineOrientation.HORIZONTAL,r.height())
                OverlapAndIntersectionLine(Overlap.CENTER, arrayOf(il1,il2))
            }
            else
            {
                OverlapAndIntersectionLine(Overlap.NONE, emptyArray())
            }
        }
        /*
      --------------------
      |                  |
      |                -----------
      |                |  this   |
      |     r          |         |
      |                -----------
      |                  |
      --------------------

 */
        else if (pointsInsideR.size == 2 && pointsInsideThis.isEmpty())
        {
            if (pointsInsideR[0].y - pointsInsideR[1].y < EPS)
            {
                if (topLeft.y > r.topLeft.y && topLeft.y < r.bottomRight.y)
                {
                    intersectingPoint = pointsInsideR[0]
                    val il1 = XYLine(intersectingPoint,XYLineOrientation.VERTICAL,r.bottomRight.y - topLeft.y)
                    intersectingPoint = pointsInsideR[1]
                    val il2 = XYLine(intersectingPoint,XYLineOrientation.VERTICAL,r.bottomRight.y - topLeft.y)
                    intersectingPoint = if (pointsInsideR[0].x < pointsInsideR[1].x)
                    {
                        pointsInsideR[0]
                    }
                    else
                    {
                        pointsInsideR[1]
                    }
                    val il3 = XYLine(intersectingPoint,XYLineOrientation.HORIZONTAL,width())
                    return OverlapAndIntersectionLine(Overlap.BOTTOM,arrayOf(il1,il2,il3))
                }
                else
                {
                    intersectingPoint = Point(topLeft.x,r.topLeft.y)
                    val il1 = XYLine(intersectingPoint,XYLineOrientation.VERTICAL,r.topLeft.y - bottomRight.y)
                    intersectingPoint = Point(bottomRight.x,r.topLeft.y)
                    val il2 = XYLine(intersectingPoint,XYLineOrientation.VERTICAL,r.topLeft.y - bottomRight.y)
                    intersectingPoint = if (pointsInsideR[0].x < pointsInsideR[1].x)
                    {
                        pointsInsideR[0]
                    }
                    else
                    {
                        pointsInsideR[1]
                    }
                    val il3 = XYLine(intersectingPoint,XYLineOrientation.HORIZONTAL,width())
                    return OverlapAndIntersectionLine(Overlap.TOP,arrayOf(il1,il2,il3))
                }
            }
            else
            {
                if (bottomRight.x > r.topLeft.x && bottomRight.x < r.bottomRight.x)
                {
                    intersectingPoint = Point(r.topLeft.x,topLeft.y)
                    val il1 = XYLine(intersectingPoint,XYLineOrientation.HORIZONTAL,bottomRight.x - r.topLeft.x)
                    intersectingPoint = Point(r.topLeft.x,bottomRight.y)
                    val il2 = XYLine(intersectingPoint,XYLineOrientation.HORIZONTAL,bottomRight.x - r.topLeft.x)
                    intersectingPoint = if (pointsInsideR[0].y < pointsInsideR[1].y)
                    {
                        pointsInsideR[0]
                    }
                    else
                    {
                        pointsInsideR[1]
                    }
                    val il3 = XYLine(intersectingPoint,XYLineOrientation.VERTICAL,height())
                    return OverlapAndIntersectionLine(Overlap.LEFT, arrayOf(il1,il2,il3))
                }
                else
                {
                    intersectingPoint = pointsInsideR[0]
                    val il1 = XYLine(intersectingPoint,XYLineOrientation.HORIZONTAL,r.bottomRight.x - topLeft.x)
                    intersectingPoint = Point(r.topLeft.x,bottomRight.y)
                    val il2 = XYLine(intersectingPoint,XYLineOrientation.HORIZONTAL,r.bottomRight.x - topLeft.x)
                    intersectingPoint = if (pointsInsideR[0].y < pointsInsideR[1].y)
                    {
                        pointsInsideR[0]
                    }
                    else
                    {
                        pointsInsideR[1]
                    }
                    val il3 = XYLine(intersectingPoint,XYLineOrientation.VERTICAL,height())
                    return OverlapAndIntersectionLine(Overlap.RIGHT,arrayOf(il1,il2,il3))
                }
            }
        }
        /*
                     ---------------
                     |             |
                     |       r     |
                     |             |
           ----------|-----        |
           |         |    |        |
           |         |    |        |
           |         -----|---------
           |    this      |
           |              |
           ----------------


         */
        else if (pointsInsideR.size==1 && pointsInsideThis.size == 1)
        {
            // top left
            if (pointsInsideR[0].x > pointsInsideThis[0].x && pointsInsideR[0].y > pointsInsideThis[0].y)
            {
                intersectingPoint = Point(bottomRight.x,r.topLeft.y)
                val il1 = XYLine(intersectingPoint,XYLineOrientation.VERTICAL,bottomRight.y-r.topLeft.y)
                intersectingPoint = Point(r.topLeft.x,bottomRight.y)
                val il2 = XYLine(intersectingPoint,XYLineOrientation.HORIZONTAL,bottomRight.y-r.topLeft.y)
                return OverlapAndIntersectionLine(Overlap.TOPLEFT,arrayOf(il1,il2))
            }
            // top right
            else if (pointsInsideR[0].x < pointsInsideThis[0].x && pointsInsideR[0].y > pointsInsideThis[0].y)
            {
                intersectingPoint = Point(topLeft.x,r.topLeft.y)
                val il1 = XYLine(intersectingPoint,XYLineOrientation.VERTICAL,r.topLeft.y-bottomRight.y)
                intersectingPoint = pointsInsideR[0]
                val il2 = XYLine(intersectingPoint,XYLineOrientation.HORIZONTAL,r.bottomRight.x - topLeft.x)
                return OverlapAndIntersectionLine(Overlap.TOPRIGHT, arrayOf(il1, il2))
            }
            // bottom left
            else if (pointsInsideR[0].x > pointsInsideThis[0].x && pointsInsideR[0].y < pointsInsideThis[0].y)
            {
                intersectingPoint = Point(r.topLeft.x,topLeft.y)
                val il1 = XYLine(intersectingPoint,XYLineOrientation.HORIZONTAL,bottomRight.x- r.topLeft.x)
                intersectingPoint = pointsInsideR[0]
                val il2 = XYLine(intersectingPoint,XYLineOrientation.VERTICAL,topLeft.y - r.bottomRight.y)
                return OverlapAndIntersectionLine(Overlap.BOTTOMLEFT,arrayOf(il1, il2))
            }
            // bottom right
            else
            {
                intersectingPoint = pointsInsideR[0]
                val il1 = XYLine(intersectingPoint,XYLineOrientation.VERTICAL,r.bottomRight.y - topLeft.y)
                val il2 = XYLine(intersectingPoint,XYLineOrientation.HORIZONTAL, r.bottomRight.x-topLeft.x)
                return OverlapAndIntersectionLine(Overlap.BOTTOMRIGHT,arrayOf(il1,il2))
            }
        }
        else if (pointsInsideR.size==4 && pointsInsideThis.isEmpty())
        {
            intersectingPoint = topLeft
            val il1 = XYLine(intersectingPoint,XYLineOrientation.HORIZONTAL,width())
            val il2 = XYLine(intersectingPoint,XYLineOrientation.VERTICAL,height())
            intersectingPoint = Point(topLeft.x,bottomRight.y)
            val il3 =  XYLine(intersectingPoint,XYLineOrientation.HORIZONTAL,width())
            intersectingPoint = Point(bottomRight.x,topLeft.y)
            val il4 = XYLine(intersectingPoint,XYLineOrientation.VERTICAL,height())
            return OverlapAndIntersectionLine( Overlap.FULL, arrayOf(il1,il2,il3,il4))
        }
        return OverlapAndIntersectionLine(Overlap.NONE, emptyArray())
    }

    fun adjacentPlacementPossibilities(r: Rectangle): Array<SlidingRectangle?>
    {
        val res = Array<SlidingRectangle?>(4) {null}
        if (r.width() > width())
        {
            res[0] = SlidingRectangle(XYLine(Point((topLeft.x + bottomRight.x)/2,topLeft.y - r.height()/2),XYLineOrientation.HORIZONTAL,0.0),r)
            res[1] = SlidingRectangle(XYLine(Point((topLeft.x + bottomRight.x)/2,bottomRight.y + r.height()/2),XYLineOrientation.HORIZONTAL,0.0),r)
        }
        else
        {
            res[0] = SlidingRectangle(XYLine(Point(topLeft.x + r.width()/2,topLeft.y - r.height()/2),XYLineOrientation.HORIZONTAL,width()-r.width()),r)
            res[1] = SlidingRectangle(XYLine(Point(topLeft.x + r.width()/2,bottomRight.y + r.height()/2),XYLineOrientation.HORIZONTAL,width()-r.width()),r)
        }

        if (r.height() > height())
        {
            res[2] = SlidingRectangle(XYLine(Point(topLeft.x - r.width()/2,(topLeft.y + bottomRight.y)/2),XYLineOrientation.VERTICAL, 0.0),r)
            res[3] = SlidingRectangle(XYLine(Point(bottomRight.x + r.width()/2,(topLeft.y + bottomRight.y)/2),XYLineOrientation.VERTICAL, 0.0),r)
        }
        else
        {
            res[2] = SlidingRectangle(XYLine(Point(topLeft.x - r.width()/2,(topLeft.y + height())/2),XYLineOrientation.VERTICAL,height()-r.height()),r)
            res[3] = SlidingRectangle(XYLine(Point(bottomRight.x + r.width()/2,(topLeft.y + height())/2), XYLineOrientation.VERTICAL,height()-r.height()),r)
        }
        return res
    }

}

enum class XYLineOrientation
{
    HORIZONTAL,
    VERTICAL
}
class XYLine(var sp: Point,var orientation: XYLineOrientation,var d: Double)
{
    override fun toString(): String {
        return if (orientation == XYLineOrientation.HORIZONTAL)
        {
            "horizontal line, start: %d, distance: %.3f".format(sp,d)
        }
        else
        {
            "vertical line, start: %d, distance: %.3f".format(sp,d)
        }
    }
}

class Line(var a: Point, var b: Point)
{
    fun length(): Double
    {
        return sqrt((b.x - a.x)*(b.x - a.x) + (b.y-a.y)*(b.y-a.y))
    }

    /*
          a---------------------------b
             q  |
                | p
                x
    */
    fun perpendicularDistance(x: Point): Array<Double>
    {
        val xMinusA = x - a
        val determinant = -(a.y -a.y)*(a.y -a.y) - (b.x-a.x)*(b.x-a.x)
        val pMatrixLine = Point((b.y-a.y)/determinant,(a.x - b.x)/determinant)
        val qMatrixLine = Point((a.x-b.x)/determinant,(a.y-b.y)/determinant)
        val p = pMatrixLine.vMult(xMinusA)
        val q = qMatrixLine.vMult(xMinusA)
        return arrayOf(p/length(),q)
    }
}

class PlacementCandidate(var slidingRectangle: SlidingRectangle, var checked: Boolean)
class TouchElementPlacementCalculator {
    companion object
    {
        fun calculateBestPlacement(rectangle: Rectangle, neighbours: Array<Rectangle>, allRectangles: Array<Rectangle>)
        {
            val placementsToEvaluate= ArrayDeque<PlacementCandidate>()
            // compute all placement possibilities as sliding rectangles along each neighbour
            val allPlacementPossibilities = neighbours.flatMap { n -> n.adjacentPlacementPossibilities(rectangle).asIterable() }.filterNotNull()
            allPlacementPossibilities.forEach {
                placementsToEvaluate.add(PlacementCandidate(it,false))
            }
            while (placementsToEvaluate.any { !it.checked })
            {
                val el = placementsToEvaluate.first { pe -> !pe.checked }
                allRectangles.forEach {r ->
                    val newSlidingRectangles = el.slidingRectangle.cutOverlappingPartsWith(r).map {
                        sr -> PlacementCandidate(sr,false)
                    }
                    if (newSlidingRectangles.size==2) {
                         placementsToEvaluate.remove(el)
                         placementsToEvaluate.addAll(newSlidingRectangles)
                        return@forEach
                    }
                    else if (newSlidingRectangles.size==1)
                    {
                        el.slidingRectangle = newSlidingRectangles[0].slidingRectangle
                    }
                    else if (newSlidingRectangles.isEmpty())
                    {
                        placementsToEvaluate.remove(el)
                    }
                    el.checked = true
                }
            }

        }
    }
}