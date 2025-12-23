package myreport.renderer

import io.github.humbleui.skija.Canvas
import io.github.humbleui.skija.Font
import io.github.humbleui.skija.FontMgr
import io.github.humbleui.skija.FontStyle
import io.github.humbleui.skija.FontWeight
import io.github.humbleui.skija.Paint
import io.github.humbleui.skija.PaintMode
import io.github.humbleui.skija.paragraph.Affinity
import io.github.humbleui.skija.paragraph.Alignment
import io.github.humbleui.skija.paragraph.FontCollection
import io.github.humbleui.skija.paragraph.Paragraph
import io.github.humbleui.skija.paragraph.ParagraphBuilder
import io.github.humbleui.skija.paragraph.ParagraphStyle
import io.github.humbleui.skija.paragraph.TextStyle
import io.github.humbleui.types.Rect
import myreport.model.FontSlant
import myreport.model.HorizontalAligment
import myreport.model.Size
import myreport.model.controls.Control
import myreport.model.controls.IControlRenderer
import myreport.model.controls.TextBlock
import myreport.model.data.FieldKind

class TextBlockRenderer : IControlRenderer {
    override fun render(context: Any, control: Control) {
        val canvas = context as Canvas
        val textBlock = control as TextBlock

        var borderRect: Rect? = null

        canvas.save()

        borderRect = Rect.makeLTRB(
            textBlock.location.x,
            textBlock.location.y,
            textBlock.location.x + textBlock.width,
            textBlock.location.y)

        if (!textBlock.canGrow || designMode)
            canvas.clipRect(borderRect)

        var paint = Paint()

        // background textBlock
        paint.setARGB(
            textBlock.backgroundColor.a.toInt(),
            textBlock.backgroundColor.r.toInt(),
            textBlock.backgroundColor.g.toInt(),
            textBlock.backgroundColor.b.toInt()
        )
        paint.mode = PaintMode.FILL

        var size = textBlock.size

        canvas.drawRect(borderRect, paint)
        drawBorder(textBlock,canvas)
        drawText(textBlock,canvas)

        canvas.restore()
    }

    private fun drawText(textBlock: TextBlock, canvas: Canvas) {
        val paragraph = settingParagraph(textBlock)
        paragraph.layout(textBlock.size.width)

        val x = textBlock.location.x + textBlock.border.leftWidth + textBlock.padding.left
        val y = textBlock.location.y + textBlock.border.topWidth + textBlock.padding.top

        // effectively draw the text
        paragraph.paint(canvas, x,y)
    }

    private fun settingParagraph(textBlock: TextBlock): Paragraph {
        val foregroundColor = Paint()
        val backgroundColor = Paint()
        foregroundColor.setARGB(
            textBlock.fontColor.a.toInt(),
            textBlock.fontColor.r.toInt(),
            textBlock.fontColor.g.toInt(),
            textBlock.fontColor.b.toInt()
        )
        backgroundColor.setARGB(
            textBlock.backgroundColor.a.toInt(),
            textBlock.backgroundColor.r.toInt(),
            textBlock.backgroundColor.g.toInt(),
            textBlock.backgroundColor.b.toInt()
        )

        val fontMgr = FontMgr.getDefault()

        val paragraphStyle = ParagraphStyle()
        paragraphStyle.alignment = defineAlingment(textBlock.horizontalAlignment)

        val fontCollection = FontCollection()
        fontCollection.setDefaultFontManager(fontMgr)
        val paragraphBuilder = ParagraphBuilder(paragraphStyle, fontCollection)
        paragraphBuilder.addText(textBlock.text)

        // set paragraph, color and font
        paragraphBuilder.pushStyle(TextStyle().apply {
            background = backgroundColor
            foreground = foregroundColor
            fontStyle = defineFontStyle(textBlock)
            fontSize = textBlock.fontSize
        })

        val paragraph = paragraphBuilder.build()
        return paragraph
    }

    private fun defineFontStyle(textBlock: TextBlock): FontStyle {
        return when (textBlock.fontSlant) {
            FontSlant.NORMAL -> {
                when (textBlock.fontWeight) {
                    myreport.model.FontWeight.NORMAL -> FontStyle.NORMAL
                    myreport.model.FontWeight.BOLD -> FontStyle.BOLD
                }
            }

            FontSlant.ITALIC -> {
                when (textBlock.fontWeight) {
                    myreport.model.FontWeight.NORMAL -> FontStyle.ITALIC
                    myreport.model.FontWeight.BOLD -> FontStyle.BOLD_ITALIC
                }
            }

            //TODO: VERIFICAR
            FontSlant.OBLIQUE -> {
                 when (textBlock.fontWeight) {
                     myreport.model.FontWeight.NORMAL -> FontStyle.NORMAL
                     myreport.model.FontWeight.BOLD -> FontStyle.NORMAL
                 }
            }
        }
    }

    private fun drawBorder(textBlock: TextBlock, canvas: Canvas) {
        // paint for border
        val paint = Paint()
        paint.setARGB(
            textBlock.border.color.a.toInt(),
            textBlock.border.color.r.toInt(),
            textBlock.border.color.g.toInt(),
            textBlock.border.color.b.toInt()
        )

        paint.mode = PaintMode.STROKE

        // horizontal lines
        var x1 = 0f
        var y1 = 0f
        var x2 = x1
        var y2 = y1

        //draw top line
        if (textBlock.border.topWidth > 0) {
            paint.strokeWidth = textBlock.border.topWidth.toFloat()
            x1 = textBlock.location.x
            x2 = textBlock.location.x + textBlock.width
            y1 = textBlock.location.y + 1
            y2 = y1
            canvas.drawLine(x1, y1, x2, y2, paint)
        }

        // draw bottom line
        if (textBlock.border.bottomWidth > 0) {
            y1 = textBlock.bottom - textBlock.border.bottomWidth + 1
            y2 = y1
            paint.strokeWidth = textBlock.border.bottomWidth
            canvas.drawLine(x1, y1, x2, y2, paint)
        }

        // vertical lines
        // draw left line

        if (textBlock.border.leftWidth > 0) {
            paint.strokeWidth = textBlock.border.leftWidth
            x1 = textBlock.location.x + 1
            y1 =  textBlock.location.y
            x2 = x1
            y2 = y1 + textBlock.height
            canvas.drawLine(x1, y1, x2, y2, paint)
        }

        // draw right line
        if (textBlock.border.rightWidth > 0) {
            paint.strokeWidth = textBlock.border.rightWidth
            x1 = textBlock.location.x + textBlock.width - textBlock.border.rightWidth + 1
            y1 = textBlock.location.y
            x2 = x1
            y2 = y1 + textBlock.height
            canvas.drawLine(x1, y1, x2, y2, paint)
        }
    }

    /**
     * Measure the control to determine the Size
     * @param context
     * The canvas to be used to measure and setting the paint and etc.
     * @param control
     * The control that will be measured
     * @return Size
     */
    override fun measure(context: Any, control: Control): Size {
        val canvas = context as Canvas
        var textBlock = control as TextBlock

        val paint = Paint()

        val fontMgr = FontMgr.getDefault()

        val fontSlant = defineFontStyleSlant(textBlock.fontSlant)
        val fontStyle = FontStyle(defineFontStyleWeight(textBlock.fontWeight))

        val typeface = fontMgr.matchFamilyStyle(textBlock.fontName,fontStyle)
        val font =  Font(typeface, textBlock.fontSize)

        if (textBlock.fieldKind == FieldKind.EXPRESSION && textBlock.fieldName == "#NumberOfPages")
            return Size(textBlock.width, font.spacing)

        val paragraphStyle = ParagraphStyle()
        paragraphStyle.alignment = defineAlingment(textBlock.horizontalAlignment)

        val fontCollection = FontCollection()
        fontCollection.setTestFontManager(fontMgr)
        val paragraphBuilder = ParagraphBuilder(paragraphStyle, fontCollection)
        // format text if it was informed
        var text = ""
        if (textBlock.fieldTextFormat.isNullOrEmpty())
            text = textBlock.fieldTextFormat.format(textBlock.text)
        else text = textBlock.text

        paragraphBuilder.addText(text)

        val paragraph = paragraphBuilder.build()

        paragraph.layout(textBlock.width)

        var size = Size(textBlock.width, paragraph.height)

        size.height += textBlock.padding.top + textBlock.padding.bottom
        size.width += textBlock.padding.left + textBlock.padding.right

        size.height += textBlock.border.topWidth + textBlock.border.bottomWidth
        size.width += textBlock.border.leftWidth + textBlock.border.rightWidth

        if (textBlock.canShrink && (size.width < textBlock.width || size.height < textBlock.height)) {
            var largestLineWidth = paragraph.longestLine

            if (size.width < textBlock.width)
            size.width = largestLineWidth

            if (paragraph.lineNumber > 1 && paragraph.height < textBlock.height)
                size.height = paragraph.height
        }
        else if (!textBlock.canGrow) {
            size.width = textBlock.width
            size.height = textBlock.height
        }

        return size
    }

    private fun defineAlingment(horizontalAlignment: HorizontalAligment) : Alignment {
        return when (horizontalAlignment) {
            HorizontalAligment.LEFT ->  Alignment.LEFT
            HorizontalAligment.RIGHT ->  Alignment.RIGHT
            HorizontalAligment.CENTER -> Alignment.CENTER
            else -> Alignment.JUSTIFY
        }
    }

    override var dpi: Float = 96f

    override var designMode: Boolean = false

    override fun breakOffControlAtMostAtHeight(
        context: Any,
        control: Control,
        height: Float
    ): Array<Control?> {

        val canvas = context as Canvas
        var textBlock = control as TextBlock

        val paragraph = settingParagraph(textBlock)
        paragraph.layout(textBlock.width)

        var controls: Array<Control?> = arrayOfNulls(0)

        if (paragraph.height > height) {
            var positionOnTheText = paragraph.getGlyphPositionAtCoordinate(textBlock.width,height)
            var text1 = textBlock.text.substring(0,positionOnTheText.position)
            var text2 = textBlock.text.substring(positionOnTheText.position)
            var textBlock2 = textBlock.createControl() as TextBlock

            controls = arrayOfNulls(2)
            textBlock2.text = text2
            textBlock.text = text1
            controls[0] = textBlock
            controls[1] = textBlock2
        } else
        {
            controls = arrayOfNulls(1)
            controls[0] = textBlock
        }

        return controls
    }

    private fun defineFontStyleWeight(fontWeight: myreport.model.FontWeight): Int {

        return when (fontWeight) {
            myreport.model.FontWeight.NORMAL -> FontWeight.NORMAL
            myreport.model.FontWeight.BOLD -> FontWeight.BOLD
        }
    }

    private fun defineFontStyleSlant(fontSlant: FontSlant): io.github.humbleui.skija.FontSlant {
        return when (fontSlant) {
            FontSlant.NORMAL -> io.github.humbleui.skija.FontSlant.UPRIGHT
            FontSlant.ITALIC -> io.github.humbleui.skija.FontSlant.ITALIC
            FontSlant.OBLIQUE -> io.github.humbleui.skija.FontSlant.OBLIQUE
        }
    }
}