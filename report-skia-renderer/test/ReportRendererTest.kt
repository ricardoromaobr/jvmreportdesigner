import io.github.humbleui.skija.Canvas
import io.github.humbleui.skija.Surface
import myreport.model.Border
import myreport.model.Color
import myreport.model.FontSlant
import myreport.model.FontWeight
import myreport.model.Thickness
import myreport.model.controls.TextBlock
import myreport.model.data.FieldKind
import myreport.renderer.RegisterDefaultRenderers
import myreport.renderer.ReportRenderer
import kotlin.test.Test

class ReportRendererTest {

    @Test
    fun measure() {
        val surface: Surface = Surface.makeRasterN32Premul(100, 100)
        val canvas: Canvas = surface.canvas

        val reportRenderer = ReportRenderer()
        RegisterDefaultRenderers().addRenderer(reportRenderer)
        reportRenderer.context = canvas

        val textBlock = TextBlock()
        textBlock.text = "Hello World!\nlina2\nlinha3"
        textBlock.width = 200f
        textBlock.fontSlant = FontSlant.NORMAL
        textBlock.fontWeight = FontWeight.BOLD
        textBlock.fontSize = 20f
        textBlock.fieldKind = FieldKind.EXPRESSION
        textBlock.horizontalAlignment = myreport.model.HorizontalAligment.LEFT
        textBlock.verticalAlignment = myreport.model.VerticalAlignment.TOP
        textBlock.fontName = "Arial"
        textBlock.padding = Thickness(5f)
        textBlock.border = Border(0f)
        textBlock.fontColor = Color(255f, 255f, 255f, 255f)

        val size = reportRenderer.measureControl(textBlock)

    }

}