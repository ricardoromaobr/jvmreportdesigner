import io.github.humbleui.skija.Canvas
import io.github.humbleui.skija.EncodedImageFormat
import io.github.humbleui.skija.Image
import io.github.humbleui.skija.Paint
import io.github.humbleui.skija.Surface
import io.github.humbleui.types.Rect
import myreport.model.Border
import myreport.model.Color
import myreport.model.FontSlant
import myreport.model.FontWeight
import myreport.model.HorizontalAligment
import myreport.model.LineMode
import myreport.model.LineType
import myreport.model.PaperSizeType
import myreport.model.Point
import myreport.model.Report
import myreport.model.SectionType
import myreport.model.Size
import myreport.model.Thickness
import myreport.model.UnitType
import myreport.model.controls.DetailSection
import myreport.model.controls.Line
import myreport.model.controls.PageFooterSection
import myreport.model.controls.PageHeaderSection
import myreport.model.controls.TextBlock
import myreport.model.data.FieldKind
import myreport.model.engine.ReportEngine
import myreport.renderer.RegisterDefaultRenderers
import myreport.renderer.ReportRenderer
import org.intellij.lang.annotations.JdkConstants
import java.io.IOException
import java.nio.channels.ByteChannel
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardOpenOption
import kotlin.test.Test

class ReportEngineTest {

    @Test
    fun process() {

        // arrange
        val surface: Surface = Surface.makeRasterN32Premul((8.3f * 72f).toInt(), (11.7f * 72f).toInt())
        val canvas: Canvas = surface.canvas
        val report = Report()


        val reportRenderer = ReportRenderer()
        RegisterDefaultRenderers().addRenderer(reportRenderer)
        reportRenderer.context = canvas
        report.paperSizeType = PaperSizeType.A4

        val textBlock = TextBlock()
        textBlock.text = ""
        textBlock.width = 150f
        textBlock.fontSlant = FontSlant.ITALIC
        textBlock.fontWeight = FontWeight.BOLD
        textBlock.fontSize = 20f
        textBlock.fieldKind = FieldKind.EXPRESSION
        textBlock.horizontalAlignment = myreport.model.HorizontalAligment.CENTER
        textBlock.verticalAlignment = myreport.model.VerticalAlignment.TOP
        textBlock.fontName = "Courier New"
        textBlock.padding = Thickness(5f)
        textBlock.fontColor = Color(0f, 0f, 0f, 255f)
        textBlock.backgroundColor = Color(255f, 255f, 255f, 255f)
        textBlock.canGrow = false
        textBlock.border = Border(0f).apply {
            color = Color(255f, 0f, 0f, 255f)
        }
        textBlock.fieldName = "name"
        textBlock.fieldKind = FieldKind.DATA


        val numRow = TextBlock().apply {
            fieldName = "#RowNumber"
            fieldKind = FieldKind.EXPRESSION
            location = Point(400f, 0f)
        }

        val h = PageHeaderSection().apply {
            width = report.width
            height = 40f
            backgroundColor = Color(255f, 255f, 0f, 0f)
        }

        val line = Line().apply {
            lineWidth = 1f
            lineType = LineType.SOLID
            width = report.width
            location.y = 35f
            end = Point(report.width, 35f)
            lineMode = LineMode.HORIZONTAL
        }

        val titulo = TextBlock().apply {
            text = "Relátio Teste"
            horizontalAlignment = HorizontalAligment.CENTER
            width = report.width
            fieldKind = FieldKind.DATA
        }

        h.controls.add(titulo)
        h.controls.add(line)

        val s = DetailSection().apply {
            width = report.width
            height = 50f
            canGrow = true
            canShrink = true
        }
        s.controls.add(textBlock)
        s.controls.add(numRow)
        val f = PageFooterSection().apply {
            size = Size(report.width, 21f)
            backgroundColor = Color(255f, 255f, 255f, 255f)
        }
        val pageNumber = TextBlock().apply {
            fieldKind = FieldKind.EXPRESSION
            fieldName = "#PageNumber"
            location.x = report.width - width - 10
            fontWeight = FontWeight.BOLD
            fieldTextFormat = "Página: %s"
            horizontalAlignment = HorizontalAligment.RIGHT
        }

        f.controls.add(pageNumber)

        report.sections.add(h)
        report.sections.add(s)
        report.sections.add(f)
        report.dataSource = listOf<Person>(
            Person("ricadrdo"),
            Person("Maria Abadia"),
            Person("José"),
            Person("João"),
            Person("Pedro"),
            Person("Tiao"),
            Person("Fumaca"),
            Person("Sebastiana"),
            Person("Geraldo")
        )

        val reportEngine = ReportEngine(report, reportRenderer)
        // act
        canvas.drawRect(Rect.makeLTRB(0f, 0f, 8.3f * 72f, 11.7f * 72f), Paint().setARGB(255, 255, 255, 255))
        reportEngine.process()



        fun savePage(path: String) {
            val image: Image = surface.makeImageSnapshot()
            val pgnData = image.encodeToData(EncodedImageFormat.PNG)
            val byteBuffer = pgnData.toByteBuffer()

            try {
                val path = Path.of(path)
                val channel: ByteChannel = Files.newByteChannel(
                    path,
                    StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE
                )
                channel.write(byteBuffer)
                channel.close()
            } catch (e: IOException) {
                println(e)
            }
        }

        for (page in report.pages) {
            canvas.drawRect(Rect.makeLTRB(0f, 0f, 8.3f * 72f, 11.7f * 72f), Paint().setARGB(255, 255, 255, 255))
            reportRenderer.renderPage(page)
            savePage("page_${page.pageNumber}.png")
        }

        // create a image with the control rendered


    }
}


