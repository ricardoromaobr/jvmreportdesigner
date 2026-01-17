package myreport.designer.myreport.designer.ui


import javafx.scene.canvas.Canvas
import javafx.scene.layout.Region
import org.jfree.fx.FXGraphics2D
import java.awt.Color
import java.awt.Font
import java.awt.font.TextLayout

class DesignerG2 : Region() {

    private val g2fx: FXGraphics2D
    private val canvas: Canvas

    var dpi: Int = 0

    init {

        canvas = Canvas()

        canvas.widthProperty().bind(widthProperty())
        canvas.heightProperty().bind(heightProperty())

        canvas.widthProperty().addListener { observable -> drawContent() }
        canvas.heightProperty().addListener { observable -> drawContent() }

        g2fx = FXGraphics2D(canvas.graphicsContext2D)
        children.addAll(canvas)
        drawContent()
    }

    fun drawContent() {


        g2fx.color = Color.RED
        g2fx.drawOval(100, 100, 100, 100)
        g2fx.drawString("RICARDO", 210, 10)

        with(g2fx) {
            color = Color.YELLOW
            fillOval(100, 100, 100, 100)
            font = Font("Courier New", 100, 20)
            color = Color.BLACK
            drawString("fazenda", 10, 75)
            drawLine(10, 75, 150, 75)
            drawString("papelaria", 10, 90)

            val text =
                "Agora que o mundo parecia está em paz... É tudo parece ter voltado para as origens medieval, tudo é gurerra. Gostaria que as pessoas fossem mais cooperativass."
            val frc = fontRenderContext
            val layout = TextLayout(text, this.font, frc)
            val hit = layout.hitTestChar(150f, 100f)
            println(hit.charIndex)
            println(hit.isLeadingEdge)
            println(hit.insertionIndex)
            println(text.substring(0, hit.charIndex))
            val y = layout.ascent + layout.descent + layout.leading
            drawString(text.substring(0, hit.charIndex), 0, y.toInt())
        }
    }




    override fun layoutChildren() {
        canvas.relocate(0.0, 0.0)
        canvas.widthProperty().bind(widthProperty())
    }


}