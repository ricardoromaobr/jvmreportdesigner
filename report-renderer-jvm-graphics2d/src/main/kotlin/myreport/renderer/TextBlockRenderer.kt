package myreport.renderer

import javafx.scene.shape.StrokeLineCap
import javafx.scene.shape.StrokeLineJoin
import myreport.model.Size
import myreport.model.controls.Control
import myreport.model.controls.IControlRenderer
import myreport.model.controls.IResizable
import myreport.model.controls.TextBlock
import myreport.model.HorizontalAlignment
import myreport.model.VerticalAlignment
import myreport.model.FontSlant
import myreport.model.FontWeight
import java.awt.Graphics2D
import java.awt.Font
import java.awt.RenderingHints
import java.awt.BasicStroke
import java.awt.font.LineBreakMeasurer
import java.awt.font.TextAttribute
import java.awt.font.TextLayout
import java.text.AttributedString

class TextBlockRenderer : IControlRenderer {

    /**
     * Renders the border of the TextBlock control.
     * Draws left, right, top, and bottom borders according to their widths.
     * Preserves the original Graphics2D context state (color and stroke).
     */
    private fun renderBorder(g2d: Graphics2D, textBlock: TextBlock) {
        if (textBlock.border.leftWidth > 0 || textBlock.border.rightWidth > 0 ||
            textBlock.border.topWidth > 0 || textBlock.border.bottomWidth > 0
        ) {
            // Save original context state
            val originalColor = g2d.color
            val originalStroke = g2d.stroke

            val borderColor = textBlock.border.color
            g2d.color = java.awt.Color(borderColor.r / 255f, borderColor.g / 255f, borderColor.b / 255f, borderColor.a / 255f)

            val x = textBlock.location.x
            val y = textBlock.location.y
            val w = textBlock.width
            val h = textBlock.height

            // Left border
            if (textBlock.border.leftWidth > 0) {
                g2d.stroke = BasicStroke(textBlock.border.leftWidth, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND)
                val offset = textBlock.border.leftWidth / 2f
                g2d.drawLine(
                    (x + offset).toInt(), y.toInt(),
                    (x + offset).toInt(), (y + h).toInt()
                )
            }

            // Right border
            if (textBlock.border.rightWidth > 0) {
                g2d.stroke = BasicStroke(textBlock.border.rightWidth, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND)
                val offset = textBlock.border.rightWidth / 2f
                g2d.drawLine(
                    (x + w - offset).toInt(), y.toInt(),
                    (x + w - offset).toInt(), (y + h).toInt()
                )
            }

            // Top border
            if (textBlock.border.topWidth > 0) {
                g2d.stroke = BasicStroke(textBlock.border.topWidth, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND)
                val offset = textBlock.border.topWidth / 2f
                g2d.drawLine(
                    x.toInt(), (y + offset).toInt(),
                    (x + w).toInt(), (y + offset).toInt()
                )
            }

            // Bottom border
            if (textBlock.border.bottomWidth > 0) {
                g2d.stroke = BasicStroke(textBlock.border.bottomWidth, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND)
                val offset = textBlock.border.bottomWidth / 2f
                g2d.drawLine(
                    x.toInt(), (y + h - offset).toInt(),
                    (x + w).toInt(), (y + h - offset).toInt()
                )
            }

            // Restore original context state
            g2d.color = originalColor
            g2d.stroke = originalStroke
        }
    }

    /**
     * Renders the text content of the TextBlock control.
     * Handles text wrapping, horizontal and vertical alignment, font styling (slant and weight),
     * and applies padding. Preserves the original Graphics2D context state.
     */
    private fun renderText(g2d: Graphics2D, textBlock: TextBlock) {
        if (textBlock.text.isEmpty()) return

        val text = formatField(textBlock)

        // Save original context state
        val originalColor = g2d.color
        val originalFont = g2d.font
        val originalAntialiasing = g2d.getRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING)

        // Enable anti-aliasing
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON)

        // Calculate content area (excluding border and padding)
        val x = textBlock.location.x + textBlock.border.leftWidth + textBlock.padding.left
        val y = textBlock.location.y + textBlock.border.topWidth + textBlock.padding.top
        val contentWidth = textBlock.width - textBlock.border.leftWidth - textBlock.border.rightWidth -
                textBlock.padding.left - textBlock.padding.right
        val contentHeight = textBlock.height - textBlock.border.topWidth - textBlock.border.bottomWidth -
                textBlock.padding.top - textBlock.padding.bottom

        // Set font with slant and weight
        val fontStyle = when {
            textBlock.fontSlant == FontSlant.ITALIC && textBlock.fontWeight == FontWeight.BOLD -> Font.BOLD or Font.ITALIC
            textBlock.fontSlant == FontSlant.ITALIC -> Font.ITALIC
            textBlock.fontWeight == FontWeight.BOLD -> Font.BOLD
            else -> Font.PLAIN
        }
        val font = Font(textBlock.fontName, fontStyle, textBlock.fontSize.toInt())
        g2d.font = font

        // Set text color
        g2d.color = java.awt.Color(
            textBlock.fontColor.r / 255f, textBlock.fontColor.g / 255f,
            textBlock.fontColor.b / 255f, textBlock.fontColor.a / 255f
        )

        // Create attributed string for text wrapping
        val attributedString = AttributedString(text)
        attributedString.addAttribute(TextAttribute.FONT, font)

        val iterator = attributedString.iterator
        val measurer = LineBreakMeasurer(iterator, g2d.fontRenderContext)

        // Collect wrapped lines
        val lines = mutableListOf<TextLayout>()
        var totalHeight = 0f

        while (measurer.position < textBlock.text.length) {
            val layout = measurer.nextLayout(contentWidth)
            lines.add(layout)
            totalHeight += layout.ascent + layout.descent + layout.leading
        }

        // Calculate vertical alignment offset
        val verticalOffset = when (textBlock.verticalAlignment) {
            VerticalAlignment.TOP -> 0f
            VerticalAlignment.CENTER -> (contentHeight - totalHeight) / 2
            VerticalAlignment.BOTTOM -> contentHeight - totalHeight
        }

        // Draw text lines with horizontal alignment
        var currentY = y + verticalOffset
        for (layout in lines) {
            val lineWidth = layout.advance
            val horizontalOffset = when (textBlock.horizontalAlignment) {
                HorizontalAlignment.LEFT -> 0f
                HorizontalAlignment.CENTER -> (contentWidth - lineWidth) / 2
                HorizontalAlignment.RIGHT -> contentWidth - lineWidth
            }

            currentY += layout.ascent
            layout.draw(g2d, x + horizontalOffset, currentY)
            currentY += layout.descent + layout.leading
        }

        // Restore original context state
        g2d.color = originalColor
        g2d.font = originalFont
        if (originalAntialiasing != null) {
            g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, originalAntialiasing)
        }
    }

    /**
     * Renders the TextBlock control on the Graphics2D context.
     * First renders the border, then renders the text inside the border area.
     */
    override fun render(context: Any, control: Control) {
        val textBlock = control as TextBlock
        val g2d = context as Graphics2D

        val size = measure(g2d, control)

        textBlock.size = size

        renderBorder(g2d, textBlock)
        renderText(g2d, textBlock)
    }

    /**
     * Measures the size required for the TextBlock control.
     * Considers border widths, padding, text length with wrapping, and canGrow/canShrink properties.
     * Returns the appropriate size based on whether the control can grow or shrink.
     */
    override fun measure(context: Any, control: Control): Size {
        val textBlock = control as TextBlock
        val g2d = context as Graphics2D

        // Calculate border and padding dimensions
        val borderAndPaddingWidth = textBlock.border.leftWidth + textBlock.border.rightWidth +
                textBlock.padding.left + textBlock.padding.right
        val borderAndPaddingHeight = textBlock.border.topWidth + textBlock.border.bottomWidth +
                textBlock.padding.top + textBlock.padding.bottom

        // If text is empty, return minimum size with border and padding
        if (textBlock.text.isEmpty()) {
            return Size(borderAndPaddingWidth, borderAndPaddingHeight)
        }

        var text = formatField(textBlock)

        // Set font with slant and weight
        val fontStyle = when {
            textBlock.fontSlant == FontSlant.ITALIC && textBlock.fontWeight == FontWeight.BOLD -> Font.BOLD or Font.ITALIC
            textBlock.fontSlant == FontSlant.ITALIC -> Font.ITALIC
            textBlock.fontWeight == FontWeight.BOLD -> Font.BOLD
            else -> Font.PLAIN
        }
        val font = Font(textBlock.fontName, fontStyle, textBlock.fontSize.toInt())

        val attributedString = AttributedString(text)
        attributedString.addAttribute(TextAttribute.FONT, font)

        val contentWidth = textBlock.width - borderAndPaddingWidth

        val iterator = attributedString.iterator
        val measurer = LineBreakMeasurer(iterator, g2d.fontRenderContext)

        var totalHeight = 0f
        while (measurer.position < textBlock.text.length) {
            val layout = measurer.nextLayout(contentWidth)
            totalHeight += layout.ascent + layout.descent + layout.leading
        }

        val measuredHeight = totalHeight + borderAndPaddingHeight

        // Consider canGrow and canShrink if TextBlock implements IResizable
        val finalHeight = if (textBlock is IResizable) {
            when {
                textBlock.canGrow && measuredHeight > textBlock.height -> measuredHeight
                textBlock.canShrink && measuredHeight < textBlock.height -> measuredHeight
                else -> textBlock.height
            }
        } else {
            measuredHeight
        }

        return Size(textBlock.width, finalHeight)
    }

    private fun formatField(textBlock: TextBlock): String {
        var text = ""

        if (!textBlock.fieldTextFormat.isNullOrEmpty())
            text = textBlock.fieldTextFormat.format(textBlock.text)
        else
            text = textBlock.text
        return text
    }


    /**
     * Dot per Inches.
     */
    override var dpi: Float = 96f

    /**
     * Design model is enabled or not.
     */
    override var designMode: Boolean = false

    /**
     * Splits the TextBlock control at the specified height.
     * Returns an array with two controls: the first with the specified height,
     * and the second with the remaining height starting at position 0.
     */
    override fun breakOffControlAtMostAtHeight(
        context: Any,
        control: Control,
        height: Float
    ): Array<Control?> {
        val textBlock = control as TextBlock
        val newControl = textBlock.createControl() as TextBlock
        val newControl1 = textBlock.createControl() as TextBlock

        newControl.height = height
        newControl1.height = textBlock.height - height
        newControl1.top = 0f

        return arrayOf(newControl, newControl1)
    }
}