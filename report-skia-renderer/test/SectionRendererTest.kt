import io.github.humbleui.skija.Canvas
import io.github.humbleui.skija.EncodedImageFormat
import io.github.humbleui.skija.Image
import io.github.humbleui.skija.Paint
import io.github.humbleui.skija.Surface
import io.github.humbleui.types.Rect
import myreport.model.Color
import myreport.model.Point
import myreport.model.controls.ReportHeaderSection
import myreport.model.controls.Section
import myreport.renderer.SectionRenderer
import java.io.IOException
import java.nio.channels.ByteChannel
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardOpenOption
import kotlin.test.Test
import kotlin.text.Typography.section

class SectionRendererTest {

    @Test
    fun `measure section`() {
        val surface: Surface = Surface.makeRasterN32Premul(100, 100)
        val canvas: Canvas = surface.canvas

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
        val size = renderer.measure(canvas, section)

    }

    @Test
    fun  renderSection() {
        val surface: Surface = Surface.makeRasterN32Premul((8.3f * 96).toInt(), (11.7f * 96).toInt())
        val canvas: Canvas = surface.canvas

        canvas.drawRect(Rect(0f, 0f, 8.3f * 96, 11.7f * 96), Paint().setARGB(255, 255, 0, 0))

        val section = ReportHeaderSection().let {
            it.width =  (8.3f * 96)
            it.height = (2f * 96)
            it.backgroundColor = Color(255f, 255f, 255f, 255f)
            it.location = Point(0f, 0f)
            it.canGrow = true
            it.canShrink = true

            it
        }


        val renderer = SectionRenderer()
        val size = renderer.measure(canvas, section)

        renderer.render(canvas, section)


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