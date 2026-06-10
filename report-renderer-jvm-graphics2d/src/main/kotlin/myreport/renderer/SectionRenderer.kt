package myreport.renderer

import myreport.model.Size
import myreport.model.controls.Control
import myreport.model.controls.IControlRenderer
import myreport.model.controls.Section
import java.awt.Graphics2D

class SectionRenderer: IControlRenderer {
    override fun render(context: Any, control: Control) {
        val section = control as Section
        val g2d = context as Graphics2D

        section.backgroundColor?.let { bgColor ->
            // Save original color
            val originalColor = g2d.color
            
            g2d.color = java.awt.Color(bgColor.r / 255f, bgColor.g / 255f, bgColor.b / 255f, bgColor.a / 255f)
            g2d.fillRect(
                section.location.x.toInt(),
                section.location.y.toInt(),
                section.size.width.toInt(),
                section.size.height.toInt()
            )
            
            // Restore original color
            g2d.color = originalColor
        }

        // Render child controls
        for (child in section.controls) {
            if (child.isVisible) {
                // TODO: Use a proper renderer registry instead of hardcoded TextBlockRenderer
                val renderer = TextBlockRenderer()
                renderer.render(g2d, child)
            }
        }
    }

    override fun measure(context: Any, control: Control): Size {
        val section = control as Section
        return Size(section.size.width, section.size.height)
    }

    override var dpi: Float = 96f
    override var designMode: Boolean = false

    override fun breakOffControlAtMostAtHeight(
        context: Any,
        control: Control,
        height: Float
    ): Array<Control?> {
        val section = control as Section
        val newControl = section.createControl() as Section
        val newControl1 = section.createControl() as Section
        newControl.height = height
        newControl1.height = section.height - height
        newControl1.top = 0f

        return arrayOf(newControl, newControl1)
    }
}