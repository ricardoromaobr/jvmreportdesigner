import com.sun.org.apache.xalan.internal.lib.ExsltStrings.padding
import javafx.scene.paint.Color.color
import myreport.model.Border
import myreport.model.Color
import myreport.model.Point
import myreport.model.Size
import myreport.model.controls.Image
import myreport.renderer.ImageRenderer
import java.awt.AlphaComposite
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import java.io.File
import javax.imageio.ImageIO
import kotlin.test.Test

class ImageRendererTest {

    @Test
    fun measure() {

        val image = BufferedImage(400, 400, BufferedImage.TYPE_INT_ARGB)
        val g2d = image.createGraphics()
        val fotoAngelina = ImageIO.read(File("angelina.jpeg"))
        val byteArray = ByteArrayOutputStream()
        ImageIO.write(fotoAngelina, "png", byteArray)

        val imageControl = Image().apply {
            data = byteArray.toByteArray()
            border = Border(1f).apply {
                color = Color(255f, 0f, 0f, 255f)
            }
            location = Point(10f, 10f)
            size = Size(200f, 200f)

        }

        val renderer = ImageRenderer()
        val size = renderer.measure(g2d, imageControl)

        g2d.dispose()

        println(size)

    }

    @Test
    fun render() {
        val image = BufferedImage(400, 400, BufferedImage.TYPE_INT_ARGB)
        val g2d = image.createGraphics()
        val fotoAngelina = ImageIO.read(File("angelina.jpeg"))
        val byteArray = ByteArrayOutputStream()
        ImageIO.write(fotoAngelina, "png", byteArray)

        val imageControl = Image().apply {
            data = byteArray.toByteArray()
            border = Border(1f).apply {
                color = Color(255f, 0f, 0f, 255f)
            }
            location = Point(10f, 10f)
            size = Size(200f, 200f)
            backgroundColor = Color(255f, 0f, 0f, 255f)

        }

        val renderer = ImageRenderer()
        val size = renderer.render(g2d, imageControl)

        g2d.dispose()

        ImageIO.write(image, "png", File("output_test.png"))
        println("Test image saved to output_test.png")


    }

}