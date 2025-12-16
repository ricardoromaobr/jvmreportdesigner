package myreport.model.controls

import myreport.model.LineMode
import myreport.model.LineType
import myreport.model.Point


class Line : Control() {

    lateinit var end: Point
    var lineWidth: Float = 0f
    var extendToBottom: Boolean = false
    lateinit var lineType: LineType
    lateinit var lineMode: LineMode

    override var top: Float
        get() = Math.min(location.y, end.y)
        set(value) {
            if (location.y == end.y) {
                location = Point(location.x, value)
                end = Point(end.x, value)
            } else if (location.y < end.y) {
                end = Point(end.x, end.y + (value - location.y))
                location = Point(location.x, value)
            }
        }

    override var height: Float
        get() = bottom - top + lineWidth
        set (value) {}

    override var width: Float
        get()  {
            if (location.x == end.x)
                return Math.max(location.y, end.y) - Math.min(location.y, end.y)
            else if (location.y == end.y)
                return Math.max(location.x, end.x) - Math.min(location.x, end.x)
            else
                return 0f
        }
        set(value) {}

    override var left: Float
        get() = Math.min(location.x, end.x)
        set(value) {
            val v = Math.min(location.x, end.x) - value
            location = Point(location.x - v, location.y)
            end = Point(end.x, end.y)
        }

    override var bottom: Float
        get() = Math.max(location.y, end.y) + lineWidth / 2
        set(value) {
            val halfLineWidth = lineWidth / 2
            if (location.x == end.y) {
                location = Point(location.x, value - halfLineWidth)
                end = Point(end.x, value - halfLineWidth)
            } else if (location.y < end.y) {
                location = Point(location.x, location.y + value - (end.y + halfLineWidth))
                end = Point(end.x, value - halfLineWidth)
            } else {
                end = Point(end.x, end.y + value - (location.y + halfLineWidth))
                location = Point(location.x, value - halfLineWidth)
            }
        }

    override fun createControl(): Control {
        val line = Line()
        copyBasicProperties(line)
        line.end = Point(end.x, end.y)
        line.lineWidth = lineWidth
        line.lineType = lineType
        line.lineMode = line.lineMode
        line.extendToBottom = extendToBottom
        return line
    }


}