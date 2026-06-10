package myreport.renderer

import myreport.model.Size
import myreport.model.controls.Control
import myreport.model.controls.IControlRenderer
import myreport.model.controls.Image
import java.awt.BasicStroke
import java.awt.Color
import java.awt.Rectangle
import java.awt.image.BufferedImage
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import javax.imageio.ImageIO

class ImageRenderer : IControlRenderer {
    override fun render(context: Any, control: Control) {
        val g = context as java.awt.Graphics2D
        val image = control as Image

        val borderRect = Rectangle(
            image.location.x.toInt(),
            image.location.y.toInt(),
            image.width.toInt(),
            image.height.toInt()
        )

        val clip = g.clip
        val color = g.color
        val stroke = g.stroke


        g.clip(borderRect)
        g.color = Color(
            control.backgroundColor.r.toInt(),
            control.backgroundColor.g.toInt(),
            control.backgroundColor.b.toInt(),
            control.backgroundColor.a.toInt())

        g.clearRect(borderRect.x, borderRect.y, borderRect.width, borderRect.height)

        var lineStroke: BasicStroke
        var lineColor: Color =
            Color(
                image.border.color.r.toInt(),
                image.border.color.g.toInt(),
                image.border.color.b.toInt(),
                image.border.color.a.toInt()
            )

        g.color = lineColor

        if (image.border.topWidth > 0) {
            lineStroke = BasicStroke(image.border.topWidth)
            g.stroke = lineStroke
            g.drawLine(
                image.location.x.toInt(),
                image.location.y.toInt() +  (lineStroke.lineWidth / 2).toInt(),
                image.location.x.toInt() + image.width.toInt(),
                image.location.y.toInt() + (lineStroke.lineWidth / 2).toInt()
            )
        }

        if (image.border.leftWidth > 0) {

            lineStroke = BasicStroke(image.border.leftWidth)
            g.stroke = lineStroke
            g.drawLine(
                image.location.x.toInt() + (lineStroke.lineWidth / 2).toInt(),
                image.location.y.toInt(),
                image.location.x.toInt() + (lineStroke.lineWidth / 2).toInt(),
                (image.location.y + image.height).toInt()
            )
        }

        if (image.border.rightWidth > 0) {
            lineStroke = BasicStroke(image.border.rightWidth)
            g.stroke = lineStroke
            g.drawLine(
                (image.location.x + image.width - image.border.rightWidth).toInt() + (lineStroke.lineWidth / 2).toInt(),
                image.location.y.toInt(),
                (image.location.x + image.width - image.border.rightWidth).toInt() + (lineStroke.lineWidth / 2).toInt(),
                (image.location.y + image.height).toInt()
            )
        }

        if (image.border.bottomWidth > 0) {
            lineStroke = BasicStroke(image.border.bottomWidth)
            g.stroke = lineStroke
            g.drawLine(
                image.location.x.toInt(),
                (image.location.y + image.height - image.border.bottomWidth).toInt() + (lineStroke.lineWidth / 2).toInt(),
                (image.location.x + image.width).toInt(),
                (image.location.y + image.height - image.border.bottomWidth).toInt() + (lineStroke.lineWidth / 2).toInt()
            )
        }


        // Draw the image data inside the border
        if (image.data != null && image.data!!.isNotEmpty()) {
            try {
                val inputStream = ByteArrayInputStream(image.data)
                val bufferedImage = ImageIO.read(inputStream)
                if (bufferedImage != null) {
                    val imageX = image.location.x.toInt() + image.border.leftWidth.toInt()
                    val imageY = image.location.y.toInt() + image.border.topWidth.toInt()
                    val imageWidth =
                        image.width.toInt() - image.border.leftWidth.toInt() - image.border.rightWidth.toInt()
                    val imageHeight =
                        image.height.toInt() - image.border.topWidth.toInt() - image.border.bottomWidth.toInt()
                    g.drawImage(bufferedImage, imageX, imageY, imageWidth, imageHeight, null)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        // restpre
        g.clip = clip
        g.color = color
    }

    override fun measure(context: Any, control: Control): Size {
        val image = control as Image
        val totalWidth = image.width + image.border.leftWidth + image.border.rightWidth
        val totalHeight = image.height + image.border.topWidth + image.border.bottomWidth
        return Size(totalWidth, totalHeight)
    }

    override var dpi: Float = 96f

    override var designMode: Boolean = false

    override fun breakOffControlAtMostAtHeight(
        context: Any,
        control: Control,
        height: Float
    ): Array<Control?> {
        val image = control as Image

        val totalHeight = image.height + image.border.topWidth + image.border.bottomWidth

        if (totalHeight <= height) {
            return arrayOf(image, null)
        }

        val availableHeight = height - image.border.topWidth - image.border.bottomWidth

        if (availableHeight <= 0) {
            return arrayOf(null, image)
        }

        // Load and crop original image if data exists
        var firstPartData: ByteArray? = null
        var secondPartData: ByteArray? = null

        if (image.data != null && image.data!!.isNotEmpty()) {
            try {
                val inputStream = ByteArrayInputStream(image.data)
                val bufferedImage = ImageIO.read(inputStream)

                if (bufferedImage != null) {
                    val originalHeight = bufferedImage.height
                    val originalWidth = bufferedImage.width

                    // Calculate crop point based on available height vs total image height
                    val cropRatio = availableHeight / image.height
                    val firstPartImageHeight =
                        (originalHeight * cropRatio).toInt().coerceAtLeast(1).coerceAtMost(originalHeight)
                    val secondPartImageHeight = originalHeight - firstPartImageHeight

                    // Crop first part (top portion)
                    if (firstPartImageHeight > 0) {
                        val firstPartImage = bufferedImage.getSubimage(0, 0, originalWidth, firstPartImageHeight)
                        val baos1 = ByteArrayOutputStream()
                        ImageIO.write(firstPartImage, "png", baos1)
                        firstPartData = baos1.toByteArray()
                    }

                    // Crop second part (bottom portion)
                    if (secondPartImageHeight > 0) {
                        val secondPartImage =
                            bufferedImage.getSubimage(0, firstPartImageHeight, originalWidth, secondPartImageHeight)
                        val baos2 = ByteArrayOutputStream()
                        ImageIO.write(secondPartImage, "png", baos2)
                        secondPartData = baos2.toByteArray()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        val firstPart = Image().apply {
            location.x = image.location.x
            location.y = image.location.y
            width = image.width
            this.height = availableHeight
            border.topWidth = image.border.topWidth
            border.leftWidth = image.border.leftWidth
            border.rightWidth = image.border.rightWidth
            border.bottomWidth = 0f
            backgroundColor = image.backgroundColor
            data = firstPartData!!
        }

        val remainingHeight = image.height - availableHeight

        val secondPart = Image().apply {
            location.x = image.location.x
            location.y = 0f
            width = image.width
            this.height = remainingHeight
            border.topWidth = 0f
            border.leftWidth = image.border.leftWidth
            border.rightWidth = image.border.rightWidth
            border.bottomWidth = image.border.bottomWidth
            backgroundColor = image.backgroundColor
            data = secondPartData!!
        }

        return arrayOf(firstPart, secondPart)
    }
}