import myreport.model.controls.TextBlock
import myreport.renderer.TextBlockRenderer
import java.awt.Color
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO
import kotlin.test.Test
import kotlin.test.assertTrue

class TextBlockRendererTest {

    @Test
    fun measure() {
        val image = BufferedImage(400, 400, BufferedImage.TYPE_INT_ARGB)
        val g2d = image.createGraphics()
        
        val textBlock = TextBlock()
        textBlock.text = "This is a test of the text block renderer measurement logic. It should wrap across multiple lines."
        textBlock.width = 100f
        textBlock.fontSize = 12f
        
        val renderer = TextBlockRenderer()
        val size = renderer.measure(g2d, textBlock)
        
        println("Measured height: ${size.height}")
        assertTrue(size.height > 21f, "Height should be greater than default because of wrapping")
        g2d.dispose()
    }

    @Test
    fun render() {
        val image = BufferedImage(400, 400, BufferedImage.TYPE_INT_ARGB)
        val g2d = image.createGraphics()
        
        // Fill background
        g2d.color = Color.WHITE
        g2d.fillRect(0, 0, 400, 400)

        val textBlock = TextBlock()
        textBlock.text = "Ricardo Romão Soares e Rogéria Abreu"
        textBlock.backgroundColor = myreport.model.Color(255f, 0f, 0f, 0f)
        textBlock.width = 200f
        textBlock.height = 50f
        textBlock.fontSize = 20f
        textBlock.canGrow = true
        textBlock.location = myreport.model.Point(50f, 50f)
        textBlock.fontColor = myreport.model.Color(0f, 255f, 0f, 255f)
        textBlock.border = myreport.model.Border(2f).apply {
            color = myreport.model.Color(0f, 0f, 0f, 255f)
        }

        val textBlockRenderer = TextBlockRenderer()
        textBlockRenderer.render(g2d, textBlock)
        
        g2d.dispose()
        
        ImageIO.write(image, "png", File("output_test.png"))
        println("Test image saved to output_test.png")
    }
}