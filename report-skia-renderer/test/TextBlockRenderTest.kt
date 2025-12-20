import io.github.humbleui.skija.Canvas
import io.github.humbleui.skija.Surface
import myreport.model.FontSlant
import myreport.model.FontWeight
import myreport.model.controls.TextBlock
import myreport.model.data.FieldKind
import myreport.renderer.TextBlockRenderer
import org.intellij.lang.annotations.JdkConstants
import kotlin.test.Test

class TextBlockRenderTest {

    @Test
    fun measure_measured_isNotNull() {

        val surface: Surface = Surface.makeRasterN32Premul(100, 100)
        val canvas: Canvas? = surface.getCanvas()

        val textBlock = TextBlock()
        textBlock.text = "Hello World!\nlina2\nlinha3"
        textBlock.width = 200f
        textBlock.fontSlant = FontSlant.NORMAL
        textBlock.fontWeight = FontWeight.BOLD
        textBlock.fontSize = 10f
        textBlock.fieldKind = FieldKind.EXPRESSION
        textBlock.horizontalAlignment = myreport.model.HorizontalAligment.LEFT
        textBlock.verticalAlignment = myreport.model.VerticalAlignment.TOP
        textBlock.fontName = "Arial"

        val textBlockRenderer = TextBlockRenderer()

        val size = textBlockRenderer.measure(canvas!!, textBlock)


    }
}