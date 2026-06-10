package myreport.designer.ui.controlView

import myreport.model.LineMode
import myreport.model.controls.Control
import myreport.model.controls.Line
import java.awt.Graphics2D
import java.awt.Point
import kotlin.math.abs

class LineView : ControlViewBase {

    override var controlModel: Control?
        get() = super.controlModel!!
        set(value) {
            super.controlModel = value
            line = value as Line
        }

    var line: Line? = null
        get() = controlModel as Line?

    override var parentSection: SectionView? = null

    constructor(line: Line, parentSection: SectionView) : super(line) {
        this.parentSection = parentSection
    }

    //region implement abstract methods
    override val defaultToolName: String
        get() {
            var r = ""
            when (line!!.lineMode) {
                LineMode.VERTICAL -> r = "LineToolV"
                LineMode.HORIZONTAL -> r = "LineToolH"
                else -> r = "LineTool"
            }
            return r
        }

    override fun render(g2d: Graphics2D) {

        g2d.withSavedState {
            val p1: Point = Point(line!!.location.x.toInt(), line!!.location.y.toInt())
            val p2: Point = Point(line!!.end.x.toInt(), line!!.end.y.toInt())
            g2d.drawLine(p1.x, p1.y, p2.x, p2.y)
        }

    }

    override fun containsPoint(x: Double, y: Double): Boolean {
        var span = line!!.lineWidth / 2 + 8
        var p1 = parentSection!!.absolutePointByLocalPoint(line!!.location.x, line!!.location.y)
        var p2 = parentSection!!.absolutePointByLocalPoint(line!!.end.x, line!!.end.y)
        var hitPoint = Point(x.toInt(), y.toInt())
        if (hitPoint.x >= (Math.max(p1.x, p2.x) + span) || hitPoint.x <= (Math.min(p1.x, p2.x) - span) ||
            hitPoint.y >= (Math.max(p1.y, p2.y) + span) || hitPoint.y <= (Math.min(p1.y, p2.y) - span)
        )
            return false

        if (p1.x == p2.x || p1.y == p2.y)
            return true

        var y1: Double
        var y2: Double
        var x1: Double
        var x2: Double
        var m: Double
        var b: Double
        var ny: Double

        if (abs(p1.y - p2.y) <= abs(p1.x - p2.x)) {
            y1 = p1.y.toDouble()
            y2 = p2.y.toDouble()
            x1 = p1.x.toDouble()
            x2 = p2.x.toDouble()

        } else {
            y1 = p1.x.toDouble()
            y2 = p2.x.toDouble()
            x1 = p1.y.toDouble()
            x2 = p2.y.toDouble()

            val temp = hitPoint.y
            hitPoint.y = hitPoint.x
            hitPoint.x = temp
        }

        m = (y2 - y1) / (x2 - x1)
        b = y1 - (m * x1)
        ny = (m * hitPoint.x + b) + 0.5

        if (abs(ny - hitPoint.y) > span)
            return false

        return true
    }

    //endregion
}