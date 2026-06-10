import myreport.model.Color
import myreport.model.LineMode
import myreport.model.LineType
import myreport.model.Point
import myreport.model.controls.Line
import myreport.renderer.LineRenderer
import java.awt.image.BufferedImage
import java.io.File
import java.io.IOException
import java.nio.channels.ByteChannel
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardOpenOption
import javax.imageio.ImageIO
import kotlin.test.Test


class LineRendeterTest {
    @Test
    fun measure() {
        val image = BufferedImage(400, 400, BufferedImage.TYPE_INT_ARGB)
        val g2d = image.createGraphics()

        val line = Line().apply {
            location = Point(10f, 10f)
            end = Point(30f, 30f)
            lineWidth = 10f
        }

        val lineRenderer = LineRenderer()
        val size = lineRenderer.measure(g2d, line)


    }

    @Test
    fun render() {
        val image = BufferedImage(400, 400, BufferedImage.TYPE_INT_ARGB)
        val g2d = image.createGraphics()

        // Fill background
        g2d.color = java.awt.Color.WHITE
        g2d.fillRect(0, 0, 400, 400)

        val line = Line().apply {
            location = Point(0f, 20f)
            end = Point(400f, 20f)
            lineWidth = 2f
            lineType = LineType.DOTS
            backgroundColor = Color(0f, 255f, 0f, 255f)
            lineMode = LineMode.HORIZONTAL

        }

        g2d.color = java.awt.Color(line.backgroundColor.r.toInt(),
            line.backgroundColor.g.toInt(),
            line.backgroundColor.b.toInt(),
            line.backgroundColor.a.toInt())



        val lineRenderer = LineRenderer()

        lineRenderer.render(g2d, line)

        g2d.dispose()

        ImageIO.write(image, "png", File("output_test.png"))
        println("Test image saved to output_test.png")
    }
}