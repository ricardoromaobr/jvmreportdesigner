package myreport.designer.ui.controlView

import myreport.model.controls.Control
import myreport.model.controls.Image
import myreport.renderer.ImageRenderer
import java.awt.Rectangle

class ImageView : ControlViewBase {

    override var controlModel: Control?
        get() = super.controlModel!!
        set(value) {
            super.controlModel = value
            if (image != value)
                image = value as Image
        }

    var imageRenderer: ImageRenderer? = null

    var image: Image? = null
        private set

    constructor(image: Image, parentSection: SectionView) : super(image) {
        this.parentSection = parentSection
        absoluteBounds = Rectangle(
            (parentSection.absoluteDrawingStartPoint.x + image.left).toInt(),
            (parentSection.absoluteDrawingStartPoint.y + image.top).toInt(),
            image.width.toInt(), image.height.toInt()
        )
        imageRenderer = ImageRenderer().apply { designMode = true }
    }

    override val defaultToolName: String
        get() = "RectTool"

    override fun render(g2d: java.awt.Graphics2D) {
        imageRenderer?.render(g2d, image!!)
        absoluteBounds = Rectangle(
            (parentSection!!.absoluteDrawingStartPoint.x + image!!.left).toInt(),
            (parentSection!!.absoluteDrawingStartPoint.y + image!!.top).toInt(),
            image!!.width.toInt(), image!!.height.toInt()
        )
    }

    override fun containsPoint(x: Double, y: Double): Boolean {
        return absoluteBounds!!.contains(x, y)
    }


}