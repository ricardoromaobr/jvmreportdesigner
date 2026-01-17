package myreport.designer.ui

import io.github.humbleui.skija.Canvas
import io.github.humbleui.skija.Paint
import io.github.humbleui.skija.Surface
import javafx.beans.property.DoubleProperty
import javafx.beans.property.SimpleDoubleProperty
import javafx.event.EventHandler
import javafx.scene.image.ImageView
import javafx.scene.image.PixelBuffer
import javafx.scene.image.PixelFormat
import javafx.scene.image.WritableImage
import javafx.scene.layout.Region

class Designer {
}


class SkiaCircleRegion(val width: Int, val height: Int) : Region() {
    private val imageView = ImageView()
    private val surface: Surface = Surface.makeRasterN32Premul(width, height)
    private val canvas: Canvas = surface.makeImageSnapshot().let { surface.canvas }
    private val startX: DoubleProperty = SimpleDoubleProperty()

    init {
        children.add(imageView)
        onMouseDragged = EventHandler { event ->
            println("onMouseDragged ${event.x}, ${event.y}")
        }
        onMouseMoved = EventHandler { event ->
            println("onMouseMoved ${event.x}, ${event.y}")
        }

        startX.value = 10.0

        render()
    }

    private fun render() {
        val paint = Paint().apply {
            color = 0xFFFF0000.toInt() // Red
            isAntiAlias = true
        }

        // Draw circle at center of the region
        canvas.clear(0xFFFFFFFF.toInt()) // Clear with white
        canvas.drawCircle(width / 2f, height / 2f, 40f, paint)

        // Transfer Skia pixels to JavaFX
        val image = surface.makeImageSnapshot()
        val data = image.peekPixels() ?: return
        val buffer = data.asReadOnlyBuffer()

        val pixelBuffer = PixelBuffer(width, height, buffer, PixelFormat.getByteBgraPreInstance())
        imageView.image = WritableImage(pixelBuffer)
    }


    override fun layoutChildren() {
        imageView.relocate(0.0, 0.0)
    }

}
