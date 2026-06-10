package myreport.renderer

import io.github.humbleui.skija.Canvas
import io.github.humbleui.skija.Color
import io.github.humbleui.skija.Paint
import io.github.humbleui.skija.PaintMode
import io.github.humbleui.types.Rect
import myreport.model.Size
import myreport.model.controls.Control
import myreport.model.controls.IControlRenderer
import myreport.model.controls.Section

class SectionRenderer : IControlRenderer {
    override fun render(context: Any, control: Control) {
        val canvas = context as Canvas
        val section = control as Section

        canvas.save()

        // define the section border
        val borderRect = Rect.makeLTRB(
            section.location.x,
            section.location.y,
            section.location.x + section.width,
            section.location.y + section.height)

        canvas.clipRect(borderRect)

        // create a paint
        val paint = Paint().apply {
            mode = PaintMode.STROKE_AND_FILL
            color = Color.makeARGB(section.backgroundColor.a.toInt(),
                section.backgroundColor.r.toInt(), section.backgroundColor.g.toInt(),
                section.backgroundColor.b.toInt())
        }

        // draw a section
        canvas.drawRect(borderRect, paint)
        canvas.restore()
    }

    override fun measure(context: Any, control: Control): Size {
        val section = control as Section
        val borderRect = Rect(section.location.x, section.location.x, section.width, section.height)
        return Size(borderRect.width, borderRect.height)
    }

    override var dpi: Float = 96f

    override var designMode: Boolean = false

    override fun breakOffControlAtMostAtHeight(
        context: Any,
        control: Control,
        height: Float
    ): Array<Control?> {
        val newControl = control.createControl()
        val newControl1 = control.createControl()
        newControl.height = height
        newControl1.height = control.height - height
        newControl1.top = 0f

        return arrayOf(newControl, newControl1)
    }

}