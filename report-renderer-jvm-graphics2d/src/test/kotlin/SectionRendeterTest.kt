import myreport.model.Color
import myreport.model.Point
import myreport.model.controls.ReportHeaderSection
import myreport.renderer.SectionRenderer
import java.awt.image.BufferedImage
import java.io.File
import java.io.IOException
import java.nio.channels.ByteChannel
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardOpenOption
import javax.imageio.ImageIO
import kotlin.test.Test

class SectionRendeterTest {

    @Test
    fun `measure section`() {
        val image = BufferedImage(400, 400, BufferedImage.TYPE_INT_ARGB)
        val g2d = image.createGraphics()

        val section = ReportHeaderSection().let {
            it.width =  (8.3f * 96)
            it.height = (11.7f * 96)
            it.backgroundColor = Color(255f, 255f, 255f, 255f)
            it.location = Point(0f, 0f)
            it.canGrow = true
            it.canShrink = true

            it
        }

        val renderer = SectionRenderer()
        val size = renderer.measure(g2d, section)

    }

    @Test
    fun  renderSection() {
        val image = BufferedImage((8.5f * 130f).toInt(), (11.5f * 130).toInt(), BufferedImage.TYPE_INT_ARGB)
        val g2d = image.createGraphics()

        // Fill background
        g2d.color = java.awt.Color.YELLOW
        g2d.fillRect(0, 0, (8.5f * 130).toInt(), (11.5 * 130).toInt())

        val section = ReportHeaderSection().let {
            it.width =  (8.5f * 130)
            it.height = (2f * 130)
            it.backgroundColor = Color(255f, 255f, 255f, 255f)
            it.location = Point(0f, 0f)
            it.canGrow = true
            it.canShrink = true

            it
        }


        val renderer = SectionRenderer()
        val size = renderer.measure(g2d, section)
        renderer.render(g2d, section)

        g2d.dispose()

        ImageIO.write(image, "png", File("output_test.png"))
        println("Test image saved to output_test.png")

    }
}