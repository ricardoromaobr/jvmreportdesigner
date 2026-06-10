package myreport.designer.ui.controlView

import myreport.model.controls.Control
import myreport.renderer.SubReportRenderer
import java.awt.Rectangle

class SubreportView : ControlViewBase {

    override var controlModel: Control?
        get() = super.controlModel
        set(value) {
            super.controlModel = value
        }

    var subreportRenderer : SubReportRenderer? = null
    var subreport: myreport.model.controls.SubReport? = null
    constructor(subreport: myreport.model.controls.SubReport, parentSection: SectionView) : super(subreport) {
        this.parentSection = parentSection
        absoluteBounds = Rectangle((parentSection.absoluteDrawingStartPoint.x.toInt() + subreport.left).toInt(),
            (parentSection.absoluteDrawingStartPoint.y + subreport.top).toInt(), subreport.width.toInt(), subreport.height.toInt())
        subreportRenderer = SubReportRenderer().apply { designMode = true }
    }

    override var defaultToolName: String = "SubreportTool"


    override fun render(g2d: java.awt.Graphics2D) {
        subreportRenderer?.render(g2d, subreport!!)
        absoluteBounds = Rectangle((parentSection!!.absoluteDrawingStartPoint.x + subreport!!.left).toInt(),
            (parentSection!!.absoluteDrawingStartPoint.y + subreport!!.top).toInt(), subreport!!.width.toInt(), subreport!!.height.toInt())
    }

    override fun containsPoint(x: Double, y: Double): Boolean {
        return absoluteBounds!!.contains(x, y)
    }
}