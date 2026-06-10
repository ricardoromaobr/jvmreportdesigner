package myreport.designer.ui.controlView

import myreport.model.controls.TextBlock
import myreport.renderer.TextBlockRenderer
import java.awt.Rectangle

class TextBlockView : ControlViewBase {

    override var controlModel: myreport.model.controls.Control?
        get() = super.controlModel
        set(value) {
            super.controlModel = value
            textBlock = value as TextBlock
        }


    var textBlock: TextBlock? = null
        private set

    var textBlockRenderer: TextBlockRenderer? = null

    constructor(textBlock: TextBlock, parentSection: SectionView) : super(textBlock) {
        this.parentSection = parentSection
        absoluteBounds = Rectangle(
            (parentSection.absoluteDrawingStartPoint.x.toInt() + textBlock.location.x).toInt(),
            (parentSection.absoluteDrawingStartPoint.y + textBlock.location.y).toInt(),
            textBlock.width.toInt(),
            textBlock.height.toInt()
        )

        textBlockRenderer = TextBlockRenderer().apply { designMode = true }
        this.textBlock = textBlock
    }

    //region implement abstract members

    override var defaultToolName: String = "TextBlockTool"

    override fun render(canvas: java.awt.Graphics2D) {
        textBlockRenderer?.render(canvas, textBlock!!)
        absoluteBounds = Rectangle(
            (parentSection!!.absoluteDrawingStartPoint.x.toInt() + textBlock!!.location.x).toInt(),
            (parentSection!!.absoluteDrawingStartPoint.y + textBlock!!.location.y).toInt(),
            textBlock!!.width.toInt(),
            textBlock!!.height.toInt()
        )
    }

    override fun containsPoint(x: Double, y: Double): Boolean {
        return absoluteBounds!!.contains(x, y)
    }
    //endregion
}
