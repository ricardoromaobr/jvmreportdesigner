import io.github.humbleui.skija.Canvas
import io.github.humbleui.skija.ColorAlphaType
import io.github.humbleui.skija.ColorType
import io.github.humbleui.skija.EncodedImageFormat
import io.github.humbleui.skija.Image
import io.github.humbleui.skija.ImageInfo
import io.github.humbleui.skija.Surface
import myreport.model.Border
import myreport.model.Color
import myreport.model.FontSlant
import myreport.model.FontWeight
import myreport.model.Thickness
import myreport.model.controls.TextBlock
import myreport.model.data.FieldKind
import myreport.renderer.TextBlockRenderer
import java.io.IOException
import java.nio.channels.ByteChannel
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardOpenOption
import kotlin.test.Test


class TextBlockRenderTest {

    @Test
    fun measure_measured_isNotNull() {

        val surface: Surface = Surface.makeRasterN32Premul(100, 100)
        val canvas: Canvas = surface.canvas

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

        val textBlockRenderer = TextBlockRenderer()

        val size = textBlockRenderer.measure(canvas, textBlock)


    }

    @Test
    fun render() {

        val surface: Surface = Surface.makeRaster(ImageInfo(500,500,ColorType.RGBA_8888, ColorAlphaType.OPAQUE))
        val canvas: Canvas = surface.canvas
        val zoom: Float = 100f

        canvas.scale(zoom/100,zoom/100)
        val textBlock = TextBlock()
        textBlock.text = "Ricardo"
        textBlock.width = 50f
        //textBlock.height = 21f
        textBlock.fontSlant = FontSlant.NORMAL
        textBlock.fontWeight = FontWeight.BOLD
        textBlock.fontSize = 12f
        textBlock.fieldKind = FieldKind.EXPRESSION
        textBlock.horizontalAlignment = myreport.model.HorizontalAligment.CENTER
        textBlock.verticalAlignment = myreport.model.VerticalAlignment.TOP
        textBlock.fontName = "Courier New"
        textBlock.padding = Thickness(0f)
        textBlock.fontColor = Color(0f,0f,0f,255f)
        textBlock.backgroundColor = Color(255f,255f,255f,255f)
        textBlock.canGrow = false
        textBlock.canShrink = false
        textBlock.border = Border(0f).apply {
            color = Color(255f,0f,0f,255f)
        }

        val textBlockRenderer = TextBlockRenderer()

        val size = textBlockRenderer.measure(canvas, textBlock)

        textBlock.size = size

        textBlockRenderer.render(canvas, textBlock)

        // create a image with the control rendered
        val image: Image = surface.makeImageSnapshot()
        val pgnData = image.encodeToData(EncodedImageFormat.PNG)
        val byteBuffer = pgnData.toByteBuffer()

        try {
            val path = Path.of("output.png")
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
}