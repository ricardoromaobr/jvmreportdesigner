import io.github.humbleui.skija.Canvas
import io.github.humbleui.skija.EncodedImageFormat
import io.github.humbleui.skija.Image
import io.github.humbleui.skija.Paint
import io.github.humbleui.skija.Surface
import io.github.humbleui.types.Rect
import myreport.model.Point
import myreport.model.controls.Line
import myreport.renderer.LineRenderer
import java.io.IOException
import java.nio.channels.ByteChannel
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardOpenOption
import kotlin.test.Test

class LineRendererTest {
    @Test
    fun measure() {
        val surface: Surface = Surface.makeRasterN32Premul(100, 100)
        val canvas: Canvas = surface.canvas

        val line = Line().apply {
            location = Point(10f, 10f)
            end = Point(30f, 30f)
            lineWidth = 10f
        }

        val lineRenderer = LineRenderer()
        val size = lineRenderer.measure(canvas, line)


    }

    @Test
    fun render() {
        val surface: Surface = Surface.makeRasterN32Premul(100, 100)
        val canvas: Canvas = surface.canvas

        canvas.drawRect(Rect(0f, 0f, 100f, 100f), Paint().setARGB(255, 255, 255, 0))

        val line = Line().apply {
            location = Point(0f, 0f)
            end = Point(30f, 30f)
            lineWidth = 10f
        }

        val lineRenderer = LineRenderer()

        lineRenderer.render(canvas, line)

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