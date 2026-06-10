package myreport.renderer

import io.github.humbleui.skija.Bitmap
import io.github.humbleui.skija.Canvas
import io.github.humbleui.skija.Color
import io.github.humbleui.skija.ColorAlphaType
import io.github.humbleui.skija.ColorType
import io.github.humbleui.skija.Data
import io.github.humbleui.skija.FilterMode
import io.github.humbleui.skija.ImageFilter
import io.github.humbleui.skija.ImageInfo
import io.github.humbleui.skija.Paint
import io.github.humbleui.skija.Pixmap
import io.github.humbleui.skija.SamplingMode
import io.github.humbleui.skija.Surface
import io.github.humbleui.types.Rect
import myreport.model.Size
import myreport.model.controls.Control
import myreport.model.controls.IControlRenderer
import myreport.model.controls.Image
import java.nio.ByteBuffer

class ImageRenderer : IControlRenderer {
    override fun render(context: Any, control: Control) {

        val canvas = context as Canvas
        val image = control as Image

        canvas.save()

        val borderRect = Rect(image.location.x, image.location.y,
            image.location.x + image.width,
            image.bottom)

        canvas.clipRect(borderRect)


        val newWidth = image.width -image.border.leftWidth - image.border.rightWidth
        val newHeight = image.height - image.border.bottomWidth - image.border.topWidth

        val point = io.github.humbleui.types.Point(
            image.location.x + image.border.leftWidth,
            image.location.y + image.border.topWidth
        )

        var paint = Paint().apply {
            color = Color.makeARGB(
                image.backgroundColor.a.toInt(),
                image.backgroundColor.r.toInt(),
                image.backgroundColor.g.toInt(),
                image.backgroundColor.b.toInt()
            )
        }

        canvas.drawRect(borderRect, paint)

        paint =  Paint().apply {
            color = Color.makeARGB(
                image.border.color.a.toInt(),
                image.border.color.r.toInt(),
                image.border.color.g.toInt(),
                image.border.color.b.toInt()
            )
        }

        val p1 = io.github.humbleui.types.Point(image.location.x, image.location.y)
        val p2 = io.github.humbleui.types.Point(image.location.x + image.width, image.location.y)

        if (image.border.topWidth > 0)
            canvas.drawLine(p1.x,p1.x, p2.x, p2.y, paint)

        if (image.border.leftWidth > 0) {
            paint.strokeWidth = image.border.leftWidth as Float

            val p1 = io.github.humbleui.types.Point(image.location.x, image.location.y)
            val p2 = io.github.humbleui.types.Point(p1.x, image.bottom)

            canvas.drawLine(p1.x, p1.y, p2.x, p2.y, paint)
        }

        if (image.border.rightWidth > 0) {
            paint.strokeWidth = image.border.rightWidth
            val p1 = io.github.humbleui.types.Point(image.location.x + image.width, image.location.y)
            val p2 = io.github.humbleui.types.Point(p1.x, image.bottom)
            canvas.drawLine(p1.x, p1.y, p2.x, p2.y, paint)
        }

        if (image.border.bottomWidth > 0) {
            paint.strokeWidth = image.border.bottomWidth
            val p1 = io.github.humbleui.types.Point(image.location.x, image.location.y)
            val p2 = io.github.humbleui.types.Point(p1.x, image.bottom)
            canvas.drawLine(p1.x, p1.y, p2.x, p2.y, paint)
        }


        var skImage = io.github.humbleui.skija.Image.makeDeferredFromEncodedBytes(image.data)


        val pixmapResized = resizeRawPixels(image.data, skImage.width,
            skImage.height, newWidth.toInt(), newHeight.toInt())

        skImage = io.github.humbleui.skija.Image.makeRasterFromPixmap(pixmapResized!!)


        canvas.drawImage(skImage,point.x, point.y)
    }

    fun resizeRawPixels(
        rawPixels: ByteArray,
        originalWidth: Int,
        originalHeight: Int,
        newWidth: Int,
        newHeight: Int
    ): Pixmap? {
        // 1. Define the original ImageInfo (you must know the format)
        val srcInfo = ImageInfo(
            originalWidth,
            originalHeight,
            ColorType.RGBA_8888, // Must match your raw data format
            ColorAlphaType.PREMUL
        )
        val srcStride = originalWidth * 4 // 4 bytes per pixel for BGRA_8888
        val buffer = ByteBuffer.wrap(rawPixels)
        val srcPixmap = Pixmap.make(srcInfo, buffer , srcStride)


        // 2. Define the destination Pixmap
        val dstInfo = ImageInfo (newWidth, newHeight, srcInfo.colorType, srcInfo.colorAlphaType)
        val dstPixmap = Pixmap()

        // 3. Scale the pixels from source to destination
        val success = srcPixmap.scalePixels(dstPixmap, SamplingMode.LINEAR)

        if (!success) {
            println("Failed to scale raw pixels.")
            dstPixmap.close()
            return null
        }

        // The original srcPixmap merely wraps the input array, no need to close it.
        return dstPixmap // Return the new Pixmap with resized raw data
    }

    override fun measure(context: Any, control: Control): Size {

        val image = control as Image

        val borderRect = Rect(image.location.x, image.location.y,
            image.location.x + image.width,
            image.bottom)

        return Size (borderRect.width, borderRect.height)
    }

    override var dpi: Float = 96f

    override var designMode: Boolean = false


    override fun breakOffControlAtMostAtHeight(
        context: Any,
        control: Control,
        height: Float
    ): Array<Control?> {

        val controls = arrayOfNulls<Control>(2)
        var newControl = control.createControl();
        var newControl1 = control.createControl() as Image;
        newControl.height = height;
        newControl1.height = control.height - height;
        newControl1.offset =  myreport.model.Point(0f, -height);
        newControl1.top = 0f;
        controls[1] = newControl1;
        controls[0] = newControl;
        return controls;

    }
}