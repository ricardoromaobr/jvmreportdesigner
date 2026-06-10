package myreport.designer.tools


import myreport.designer.ui.controlView.withSavedState
import myreport.model.Border
import java.awt.Color
import java.awt.Graphics2D
import java.awt.Point
import java.awt.Rectangle
import java.awt.BasicStroke

class Gripper {

    companion object {
        val gripperSize = 6
        val gripperColor = Color(1f, 0.2f,0.2f)

        fun drawGripper(context: Graphics2D,point: Point) {
            val color = context.color
            context.color = gripperColor
            context.fillRect(point.x - gripperSize, point.y - gripperSize  , gripperSize, gripperSize)
            context.color = color
        }

        fun drawInsideBorder(context: Graphics2D, absoluteBounds: Rectangle?, selectBoder: Border?) {
           val g = context

            with(g) {
                color = Color(selectBoder!!.color.r.toInt(),
                    selectBoder.color.g.toInt(),
                    selectBoder.color.b.toInt(),
                    selectBoder.color.a.toInt())

                stroke = BasicStroke(selectBoder.topWidth, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND,
                    10f,floatArrayOf(1f,3f,2f,5f), 0f)

                drawRect(absoluteBounds!!.x, absoluteBounds.y, absoluteBounds.width, absoluteBounds.height)
            }
        }

        fun drawSelectBox(context: Graphics2D, absoluteBounds: Rectangle?) {
            context.withSavedState {
                color = gripperColor

                //left upper
                val r = absoluteBounds!!
                var box = Rectangle(r.x, r.y,
                    gripperSize, gripperSize)

                fillRect(box.x, box.y, box.width, box.height)

                //right upper
                box = Rectangle(r.x + r.width - gripperSize, r.y,
                    gripperSize, gripperSize)

                fillRect(box.x, box.y, box.width, box.height)

                //left lower
                fillRect(r.x, r.y + r.height - gripperSize, gripperSize, gripperSize)

                //right lower
                fillRect(r.x + r.width - gripperSize,
                    r.y + r.height - gripperSize,
                    gripperSize, gripperSize)

            }
        }
    }
}