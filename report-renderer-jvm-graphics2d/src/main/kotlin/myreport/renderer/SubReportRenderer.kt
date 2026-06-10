package myreport.renderer

import myreport.model.IReportRenderer
import myreport.model.Page
import myreport.model.Size
import myreport.model.controls.Control
import myreport.model.controls.IControlRenderer
import myreport.model.controls.SubReport
import java.awt.Rectangle

class SubReportRenderer : IControlRenderer {
    override fun render(context: Any, control: Control) {
        val subreport = control as SubReport
        val g2d = context as java.awt.Graphics2D
        val borderRect = Rectangle(
            subreport.location.x.toInt(), subreport.location.y.toInt(),
            subreport.width.toInt(), subreport.height.toInt()
        )

        val color = g2d.color
        val clip = g2d.clip

        g2d.clip(borderRect)
        g2d.color = java.awt.Color(subreport.backgroundColor.r.toInt(), subreport.backgroundColor.g.toInt(), subreport.backgroundColor.b.toInt(), subreport.backgroundColor.a.toInt())
        g2d.fillRect(borderRect.x, borderRect.y, borderRect.width, borderRect.height)

        g2d.color = color
        g2d.clip = clip

    }

    override fun measure(context: Any, control: Control): Size {
        val subreport = control as SubReport
        val borderRect = Rectangle(
            subreport.location.x.toInt(), subreport.location.y.toInt(),
            subreport.width.toInt(), subreport.height.toInt()
        )
        return Size(borderRect.width.toFloat(), borderRect.height.toFloat())
    }

    override var dpi: Float = 72f
    override var designMode: Boolean = false


    override fun breakOffControlAtMostAtHeight(
        context: Any,
        control: Control,
        height: Float
    ): Array<Control?> {
        val controls = arrayOfNulls<Control>(2)
        controls[1] = control
        return controls
    }

}