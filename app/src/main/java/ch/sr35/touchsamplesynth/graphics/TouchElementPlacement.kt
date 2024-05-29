package ch.sr35.touchsamplesynth.graphics

import kotlin.math.abs
import kotlin.math.min
import kotlin.math.max
import kotlin.math.sqrt


const val EPS=0.000001
const val PLACEMENT_DELTA = 3.0

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

class PointAndDistance(val point: Point, val d: Double)

enum class Overlap
{
    LEFT, RIGHT, TOP, BOTTOM, FULL, CENTER, NONE, TOPLEFT, TOPRIGHT, BOTTOMLEFT, BOTTOMRIGHT
}

class OverlapAndIntersectionLine(var overlap: Overlap,var intersectingLines: Array<XYLine>)
class SlidingRectangle(var xyLine: XYLine,val r: Rectangle)
{
    private var boundingRectangle: Rectangle = if (xyLine.orientation == XYLineOrientation.HORIZONTAL) {
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

    private var endPositionRectangle: Rectangle = if (xyLine.orientation ==XYLineOrientation.HORIZONTAL)
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
    private var startPositionRectangle: Rectangle = Rectangle(
        Point(xyLine.sp.x - r.width() / 2, xyLine.sp.y - r.height() / 2),
        Point(xyLine.sp.x + r.width() / 2, xyLine.sp.y + r.height() / 2))

    /**
     * trims a sliding rectangle so that it doesnt overlap rect
     * if rect overlaps the entire sliding range an empty list is returned
     */
    fun cutOverlappingPartsWith(rect: Rectangle): Array<SlidingRectangle>
    {
        val linesInSlidingRect = rect.lineInsideOf(boundingRectangle)
        if (boundingRectangle.lineInsideOf(rect).overlap == Overlap.FULL)
        {
            return emptyArray()
        }
        if (linesInSlidingRect.overlap == Overlap.NONE)
        {
            return arrayOf(this)
        }
        val perpendicularCuts = linesInSlidingRect.intersectingLines.filter {
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
                    return if (perpendicularCuts[0].sp.x  > endPositionRectangle.topLeft.x)
                    {
                        emptyArray()
                    }
                    else
                    {
                        // trimming left
                        arrayOf(SlidingRectangle(XYLine(Point(perpendicularCuts[0].sp.x + r.width()/2,xyLine.sp.y),XYLineOrientation.HORIZONTAL,
                            xyLine.d - (perpendicularCuts[0].sp.x - startPositionRectangle.topLeft.x)),r))

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
                    return if (perpendicularCuts[0].sp.y  > endPositionRectangle.topLeft.y)
                    {
                        emptyArray()
                    }
                    else
                    {
                        // trim up
                        arrayOf(SlidingRectangle(XYLine(Point(xyLine.sp.x,perpendicularCuts[0].sp.y + r.height()/2),XYLineOrientation.VERTICAL,
                            (endPositionRectangle.topLeft.y -  perpendicularCuts[0].sp.y)),r))
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
                        // trim down
                        arrayOf(SlidingRectangle(XYLine(xyLine.sp,XYLineOrientation.VERTICAL,
                            perpendicularCuts[0].sp.y - r.height()/2 - xyLine.sp.y),r))
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
                    return arrayOf(SlidingRectangle(XYLine(Point(perpendicularCuts[1].sp.x + r.width()/2,xyLine.sp.y),XYLineOrientation.HORIZONTAL,
                        xyLine.d - (perpendicularCuts[1].sp.x - startPositionRectangle.topLeft.x)),r))
                }
                else if (perpendicularCuts[1].sp.x > endPositionRectangle.topLeft.x)
                {
                    // trimming right
                    return arrayOf(SlidingRectangle(XYLine(xyLine.sp,XYLineOrientation.HORIZONTAL,
                        perpendicularCuts[0].sp.x - r.width()/2 - xyLine.sp.x),r))
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
                    return arrayOf(SlidingRectangle(XYLine(Point(xyLine.sp.x,perpendicularCuts[1].sp.y + r.height()/2),XYLineOrientation.VERTICAL,
                        xyLine.d - (perpendicularCuts[1].sp.y - startPositionRectangle.topLeft.y)),r))
                }
                else if (perpendicularCuts[1].sp.y > endPositionRectangle.topLeft.y)
                {
                    // trimming bottom
                    return arrayOf(SlidingRectangle(XYLine(xyLine.sp,XYLineOrientation.VERTICAL,
                        perpendicularCuts[0].sp.y - r.height()/2 - xyLine.sp.y),r))
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

    fun centerAt(p: Point): Rectangle
    {
        return Rectangle(topLeft-center()+p,bottomRight-center()+p)
    }

    fun isInside(p: Point,strict: Boolean=true): Boolean
    {

        return if (strict) {
            p.x > topLeft.x && p.x < bottomRight.x && p.y > topLeft.y && p.y < bottomRight.y
        }
        else
        {
            p.x >= topLeft.x && p.x <= bottomRight.x && p.y >= topLeft.y && p.y <= bottomRight.y
        }
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
        val pointsInsideThis = r.edgePoints().filter { ep -> isInside(ep,false) }.toList()
        var intersectingPoint: Point
        /*   Case a
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
        /*   Case B
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
            if (abs(pointsInsideThis[0].x - pointsInsideThis[1].x) < EPS)
            {
                return if (bottomRight.x < r.bottomRight.x) {
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
                return if (bottomRight.y < r.bottomRight.y) {
                    intersectingPoint = Point(r.topLeft.x,bottomRight.y)
                    OverlapAndIntersectionLine(Overlap.TOP,
                    arrayOf(XYLine(intersectingPoint,XYLineOrientation.HORIZONTAL,r.width())))
                } else {
                    intersectingPoint = Point(r.topLeft.x,topLeft.y)
                    OverlapAndIntersectionLine(Overlap.BOTTOM,
                    arrayOf(XYLine(intersectingPoint,XYLineOrientation.HORIZONTAL,r.width())))
                }
            }
        }
        /* Case C
                  ------
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
            return if (topLeft.x > r.topLeft.x && bottomRight.x < r.bottomRight.x)
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
                val il2 = XYLine(intersectingPoint,XYLineOrientation.HORIZONTAL,r.width())
                OverlapAndIntersectionLine(Overlap.CENTER, arrayOf(il1,il2))
            }
            else
            {
                OverlapAndIntersectionLine(Overlap.NONE, emptyArray())
            }
        }
        /* Case D
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
            if (abs(pointsInsideR[0].y - pointsInsideR[1].y) < EPS)
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
                    val il1 = XYLine(intersectingPoint,XYLineOrientation.VERTICAL,-(r.topLeft.y - bottomRight.y))
                    intersectingPoint = Point(bottomRight.x,r.topLeft.y)
                    val il2 = XYLine(intersectingPoint,XYLineOrientation.VERTICAL,-(r.topLeft.y - bottomRight.y))
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
                    intersectingPoint = pointsInsideR[1]
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
        /* Case E
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
                val il2 = XYLine(intersectingPoint,XYLineOrientation.HORIZONTAL,bottomRight.x-r.topLeft.x)
                return OverlapAndIntersectionLine(Overlap.TOPLEFT,arrayOf(il1,il2))
            }
            // top right
            else if (pointsInsideR[0].x < pointsInsideThis[0].x && pointsInsideR[0].y > pointsInsideThis[0].y)
            {
                intersectingPoint = Point(topLeft.x,r.topLeft.y)
                val il1 = XYLine(intersectingPoint,XYLineOrientation.VERTICAL,-(r.topLeft.y-bottomRight.y))
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
                val il2 = XYLine(intersectingPoint,XYLineOrientation.VERTICAL,-(topLeft.y - r.bottomRight.y))
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
        /*
        * Case F
        * */
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
            res[0] = SlidingRectangle(XYLine(Point((topLeft.x + bottomRight.x)/2,topLeft.y - r.height()/2 - PLACEMENT_DELTA),XYLineOrientation.HORIZONTAL,0.0),r)
            res[1] = SlidingRectangle(XYLine(Point((topLeft.x + bottomRight.x)/2,bottomRight.y + r.height()/2 + PLACEMENT_DELTA),XYLineOrientation.HORIZONTAL,0.0),r)
        }
        else
        {
            res[0] = SlidingRectangle(XYLine(Point(topLeft.x + r.width()/2,topLeft.y - r.height()/2 - PLACEMENT_DELTA),XYLineOrientation.HORIZONTAL,width()-r.width()),r)
            res[1] = SlidingRectangle(XYLine(Point(topLeft.x + r.width()/2,bottomRight.y + r.height()/2 + PLACEMENT_DELTA),XYLineOrientation.HORIZONTAL,width()-r.width()),r)
        }

        if (r.height() > height())
        {
            res[2] = SlidingRectangle(XYLine(Point(topLeft.x - r.width()/2- PLACEMENT_DELTA,(topLeft.y + bottomRight.y)/2),XYLineOrientation.VERTICAL, 0.0),r)
            res[3] = SlidingRectangle(XYLine(Point(bottomRight.x + r.width()/2+ PLACEMENT_DELTA,(topLeft.y + bottomRight.y)/2),XYLineOrientation.VERTICAL, 0.0),r)
        }
        else
        {
            res[2] = SlidingRectangle(XYLine(Point(topLeft.x - r.width()/2- PLACEMENT_DELTA,(topLeft.y + height())/2),XYLineOrientation.VERTICAL,height()-r.height()),r)
            res[3] = SlidingRectangle(XYLine(Point(bottomRight.x + r.width()/2+ PLACEMENT_DELTA,(topLeft.y + height())/2), XYLineOrientation.VERTICAL,height()-r.height()),r)
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
            "horizontal line, start: %s, distance: %.3f".format(sp,d)
        }
        else
        {
            "vertical line, start: %s, distance: %.3f".format(sp,d)
        }
    }

    fun calculatePointAndMinDistanceFrom(p: Point): PointAndDistance
    {
        if (orientation == XYLineOrientation.HORIZONTAL)
        {
            val x0 = sp.x
            val x1 = sp.x + d
            val qAtMinimum = (p.x - (x0*(x1-x0)))/(x1-x0)/(x1-x0)
            if (qAtMinimum in 0.0..1.0)
            {
                return PointAndDistance(Point(sp.x + qAtMinimum*d,sp.y), sqrt((p.y - sp.y)*(p.y - sp.y) + (p.x - (sp.x + qAtMinimum*d))*(p.x - (sp.x + qAtMinimum*d))))
            }
            else
            {
                val d1 = sqrt((p.y - sp.y)*(p.y - sp.y) +(p.x - sp.x)*(p.x - sp.x))
                val d2 = sqrt((p.y - sp.y)*(p.y - sp.y) +(p.x - sp.x - d)*(p.x - sp.x - d))
                return if (d1 < d2)
                {
                    PointAndDistance(sp,d1)
                }
                else
                {
                    PointAndDistance(Point(sp.x+d,sp.y),d2)
                }
            }
        }
        else
        {
            val y0 = sp.y
            val y1 = sp.y + d
            val qAtMinimum = (p.y - (y0*(y1-y0)))/(y1-y0)/(y1-y0)
            if (qAtMinimum in 0.0..1.0)
            {
                return PointAndDistance(Point(sp.x,sp.y+qAtMinimum*d), sqrt((p.x - sp.x)*(p.x - sp.x) + (p.y - (sp.y + qAtMinimum*d))*(p.y - (sp.y + qAtMinimum*d))))
            }
            else
            {
                val d1 = sqrt((p.x - sp.x)*(p.x - sp.x) +(p.y - sp.y)*(p.y - sp.y))
                val d2 = sqrt((p.x - sp.x)*(p.x - sp.x) +(p.y - sp.y - d)*(p.y - sp.y - d))
                return if (d1 < d2)
                {
                    PointAndDistance(sp,d1)
                }
                else
                {
                    PointAndDistance(Point(sp.x,sp.y+d),d2)
                }
            }
        }
    }
}


class PlacementCandidate(var slidingRectangle: SlidingRectangle, var checked: Boolean)
class TouchElementPlacementCalculator {
    companion object
    {
        fun calculateBestPlacement(rectangle: Rectangle, neighbours: Array<Rectangle>, allRectangles: Array<Rectangle>,placementArea: Rectangle?): Rectangle
        {
            if (neighbours.isEmpty())
            {
                return Rectangle(Point(Converter.toPx(50.0f).toDouble(),Converter.toPx(50.0f).toDouble()),
                    Point(Converter.toPx(50.0f).toDouble() + rectangle.width(),Converter.toPx(50.0f).toDouble() + rectangle.height()))
            }
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
            val avgPoint = Point(allRectangles.map { r -> r.center() }.sumOf { c -> c.x }/allRectangles.size,
                allRectangles.map { r -> r.center() }.sumOf { c -> c.y }/allRectangles.size
                )
            return if (placementsToEvaluate.isNotEmpty())
            {
                val bestCenterPoint = placementsToEvaluate
                    .map { pe ->
                        pe.slidingRectangle.xyLine.calculatePointAndMinDistanceFrom(
                            avgPoint
                        )
                    }
                    .filter { ptMinDist ->
                        if (placementArea != null) {
                            val ptTopLeft = Point(
                                ptMinDist.point.x - rectangle.width() / 2,
                                ptMinDist.point.y - rectangle.height() / 2
                            )
                            val ptTopRight = Point(
                                ptMinDist.point.x + rectangle.width() / 2,
                                ptMinDist.point.y - rectangle.height() / 2
                            )
                            val ptBottomLeft = Point(
                                ptMinDist.point.x - rectangle.width() / 2,
                                ptMinDist.point.y + rectangle.height() / 2
                            )
                            val ptBottomRight = Point(
                                ptMinDist.point.x + rectangle.width() / 2,
                                ptMinDist.point.y + rectangle.height() / 2
                            )
                            placementArea.isInside(ptTopLeft)
                                    && placementArea.isInside(ptTopRight)
                                    && placementArea.isInside(ptBottomLeft)
                                    && placementArea.isInside(ptBottomRight)
                        } else {
                            true
                        }
                    }.minByOrNull { ptAndDist -> ptAndDist.d }
                if (bestCenterPoint != null) {
                    val bestTopLeft = Point(
                        bestCenterPoint.point.x - rectangle.width() / 2,
                        bestCenterPoint.point.y - rectangle.height() / 2
                    )
                    val bestBottomRight = Point(
                        bestCenterPoint.point.x + rectangle.width() / 2,
                        bestCenterPoint.point.y + rectangle.height() / 2
                    )
                    Rectangle(bestTopLeft, bestBottomRight)
                }
                else
                {
                    Rectangle(Point(Converter.toPx(50.0f).toDouble(),Converter.toPx(50.0f).toDouble()),
                        Point(Converter.toPx(50.0f).toDouble() + rectangle.width(),Converter.toPx(50.0f).toDouble() + rectangle.height()))
                }
            }
            else
            {
                // bailing point, choose default location at a margin distance from top left
                Rectangle(Point(Converter.toPx(50.0f).toDouble(),Converter.toPx(50.0f).toDouble()),
                    Point(Converter.toPx(50.0f).toDouble() + rectangle.width(),Converter.toPx(50.0f).toDouble() + rectangle.height()))
            }
        }
    }
}