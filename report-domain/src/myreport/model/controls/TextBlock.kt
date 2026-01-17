package myreport.model.controls

import kotlinx.serialization.Serializable
import myreport.model.Border
import myreport.model.Color
import myreport.model.FontSlant
import myreport.model.FontWeight
import myreport.model.HorizontalAligment
import myreport.model.Thickness
import myreport.model.VerticalAlignment
import myreport.model.data.FieldKind

@Serializable
class TextBlock : Control(), IResizable, IDataControl {

    override var canGrow: Boolean = true

    override var canShrink: Boolean = false

    override var fieldName: String? = null

    override lateinit var fieldKind: FieldKind

    override var fieldTextFormat: String = ""

    override var text: String = ""

    var border: Border

    lateinit var fontName: String

    var padding: Thickness

    var lineSpan : Float = 0f

    var fontSize : Float = 10f

    var fontSlant: FontSlant

    var fontWeight: FontWeight

    var fontColor: Color

    var horizontalAlignment: HorizontalAligment
    var verticalAlignment: VerticalAlignment

    override fun createControl(): Control {
        val textBlock = TextBlock()
        copyBasicProperties(textBlock)
        textBlock.canGrow = canGrow
        textBlock.canShrink = canShrink
        textBlock.border = border.clone() as Border
        textBlock.fontName = fontName
        textBlock.lineSpan = lineSpan
        textBlock.padding = Thickness(padding.left, padding.top, padding.right, padding.bottom)
        textBlock.fontSize = fontSize
        textBlock.fontSlant = fontSlant
        textBlock.fontWeight = fontWeight
        textBlock.fontColor = Color(fontColor.r, fontColor.g, fontColor.b, fontColor.a)
        textBlock.fieldName = fieldName
        textBlock.fieldKind = fieldKind
        textBlock.fieldTextFormat = fieldTextFormat
        textBlock.horizontalAlignment = horizontalAlignment
        textBlock.verticalAlignment = verticalAlignment
        textBlock.text = text
        return textBlock
    }

    init {
        fontColor = Color(0f, 0f, 0f, 255f)
        fontSlant = FontSlant.NORMAL
        fontWeight = FontWeight.NORMAL
        horizontalAlignment = HorizontalAligment.LEFT
        verticalAlignment = VerticalAlignment.TOP
        padding = Thickness(5f)
        border  = Border(0f).apply {
            color = Color(0f, 0f, 0f, 255f)
        }
        fontName = "Courier New"
        backgroundColor = Color(255f, 255f, 255f, 255f)
    }
}