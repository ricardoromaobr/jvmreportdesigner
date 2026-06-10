package myreport.renderer

import myreport.model.LineMode
import myreport.model.LineType
import myreport.model.Size
import myreport.model.controls.Control
import myreport.model.controls.IControlRenderer
import myreport.model.controls.Line
import java.awt.BasicStroke
import java.awt.Color

class LineRenderer: IControlRenderer {
    override fun render(context: Any, control: Control) {
        val g = context as java.awt.Graphics2D
        val line = control as Line

        // save state
        val color = g.color
        val clip = g.clipBounds
        val stroke = g.stroke

        // define line style
        val lineStroke: BasicStroke =
        when (line.lineType) {
            LineType.SOLID -> BasicStroke(line.lineWidth)
            LineType.DASH_DOT_DOT -> BasicStroke(line.lineWidth,
                BasicStroke.CAP_BUTT,
                BasicStroke.JOIN_ROUND,
                10f,
                floatArrayOf(5f,2f,2f,2f,2f,2f),
                0f)

            LineType.DASH -> BasicStroke(line.lineWidth,
                BasicStroke.CAP_BUTT,
                BasicStroke.JOIN_ROUND,
                10f,
                floatArrayOf(5f,2f),
                0f)

            LineType.DASH_DOT -> BasicStroke(line.lineWidth,
                BasicStroke.CAP_BUTT,
                BasicStroke.JOIN_ROUND,
                10f,
                floatArrayOf(5f,2f, 2f,2f),
                0f)

            LineType.DOTS  -> BasicStroke(line.lineWidth,
            BasicStroke.CAP_BUTT,
            BasicStroke.JOIN_ROUND,
            10f,
            floatArrayOf(2f,2f),
            0f)
        }

        val drawStroke = BasicStroke()

        g.color = Color(line.backgroundColor.r / 255f,
            line.backgroundColor.g / 255f,
            line.backgroundColor.b / 255f,
            line.backgroundColor.a / 255f)

        g.stroke = lineStroke

        // draw line
        g.drawLine(
            line.location.x.toInt(),
            line.location.y.toInt(),
            line.end.x.toInt(),
            line.end.y.toInt()
        )

        // restore state
        g.color = color
        g.clip = clip
        g.stroke = stroke

    }

    override fun measure(context: Any, control: Control): Size {
        val line: Line = control as Line

        var height = line.height

        if (line.location.y == line.end.y)
            height = line.lineWidth

        return Size (height, line.width)
    }

    override var dpi: Float= 96f

    override var designMode: Boolean  = false

    override fun breakOffControlAtMostAtHeight(
        context: Any,
        control: Control,
        height: Float
    ): Array<Control?> {
        var controls: Array<Control?> = arrayOfNulls(2)
        var first = control.createControl () as Line;
        var second = control.createControl () as Line;
        var newX: Float = 0.0f

        if (first.location.x != first.end.x) {
            if (first.location.y > first.end.y) {
                newX = CalculateXAtYZero(first.end.x, height, first.location.x, -(first.height - height))
                first.location = myreport.model.Point(newX, first.end.y + height)
                val deltaW: Float = second.end.x - newX
                second.left -= deltaW
                second.top = 0.0f
                second.location = myreport.model.Point(second.location.x + deltaW, second.location.y - height)
            } else if (first.location.y < first.end.y) {
                newX = CalculateXAtYZero(first.location.x, height, first.end.x, -(first.height - height))
                first.end = myreport.model.Point(newX, first.location.y + height)
                val deltaW: Float = second.location.x - newX
                second.left -= deltaW
                second.top = 0.0f
                second.end = myreport.model.Point(second.end.x + deltaW, second.end.y - height)
            }
        } else {
            if (first.location.y > first.end.y) {
                first.location = myreport.model.Point(first.location.x, first.end.y + height)
                second.top = 0.0f
                second.location = myreport.model.Point(second.location.x, second.location.y - height)
            } else if (first.location.y < first.end.y) {
                first.end = myreport.model.Point(first.end.x, first.location.y + height)
                second.top = 0.0f
                second.end = myreport.model.Point(second.end.x, second.end.y - height)
            }
        }
        controls[0] = first
        controls[1] = second
        return controls
    }

    fun CalculateXAtYZero(x1: Float, y1: Float, x2: Float, y2: Float): Float {
        if (y1 == y2) {
            return x1
        } else {
            return x1 - (((y1 * x1) - (y1 * x2)) / (y1 - y2))
        }
    }
}