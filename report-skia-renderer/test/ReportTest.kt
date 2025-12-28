import kotlinx.serialization.json.Json
import myreport.model.Border
import myreport.model.Color
import myreport.model.FontSlant
import myreport.model.FontWeight
import myreport.model.PaperSizeType
import myreport.model.Report
import myreport.model.SectionType
import myreport.model.Thickness
import myreport.model.controls.TextBlock
import myreport.model.data.FieldKind
import kotlin.test.Test

class ReportTest {



    @Test
    fun createReport() {

        val report = Report()

        report.paperSizeType = PaperSizeType.A4

        val textBlock = TextBlock()
        textBlock.text = "Ricardo"
        textBlock.width = 150f
        textBlock.fontSlant = FontSlant.ITALIC
        textBlock.fontWeight = FontWeight.BOLD
        textBlock.fontSize = 20f
        textBlock.fieldKind = FieldKind.EXPRESSION
        textBlock.horizontalAlignment = myreport.model.HorizontalAligment.CENTER
        textBlock.verticalAlignment = myreport.model.VerticalAlignment.TOP
        textBlock.fontName = "Courier New"
        textBlock.padding = Thickness(20f)
        textBlock.fontColor = Color(0f,0f,0f,255f)
        textBlock.backgroundColor = Color(255f,255f,255f,255f)
        textBlock.canGrow = false
        textBlock.border = Border(0f).apply {
            color = Color(255f,0f,0f,255f)
        }

        var s = report.sections.find { s -> s.sectionType == SectionType.DETAILS}

        s?.controls?.add(textBlock)

        report.dataSource = listOf<Person>(Person("ricadrdo"))

        println(Json.encodeToString(report))
    }
}

class Person (var name: String? = null, var age: Int = 0)