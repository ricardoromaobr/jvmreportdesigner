package myreport.renderer

import io.github.humbleui.skija.Canvas
import io.github.humbleui.skija.Color
import io.github.humbleui.skija.Paint
import io.github.humbleui.skija.PaintMode
import io.github.humbleui.types.Point
import myreport.model.Size
import myreport.model.controls.Control
import myreport.model.controls.IControlRenderer
import myreport.model.controls.Line

class LineRenderer : IControlRenderer {

    override fun render(context: Any, control: Control) {
        val canvas = context as Canvas
        val line = control as Line

        val p1 = Point(line.location.x, line.location.y)
        val p2 = Point(line.end.x, line.end.y)

        val paint = Paint()
        paint.color = Color.makeARGB(line.backgroundColor.a.toInt(),
            line.backgroundColor.r.toInt(),
            line.backgroundColor.g.toInt() ,
            line.backgroundColor.b.toInt())
        paint.strokeWidth = line.lineWidth
        paint.isAntiAlias = true
        paint.mode = PaintMode.STROKE

        canvas.drawLine(p1.x, p1.y, p2.x,p2.y,paint)

    }

    override fun measure(context: Any, control: Control)  : Size {

        val line: Line = control as Line

        var height = line.height

        if (line.location.y == line.end.y)
            height = line.lineWidth

        return Size (height, line.width)
    }

    override var dpi: Float = 96f

    override var designMode: Boolean = false

    override fun breakOffControlAtMostAtHeight(
        context: Any,
        control: Control,
        height: Float
    ) : Array<Control?> {
        val canvas = context as Canvas
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