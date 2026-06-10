package myreport.designer.ui.controlView

import myreport.model.controls.Control
import java.awt.Rectangle


abstract class ControlViewBase {
    open var controlModel : Control? = null
    open var absoluteBounds: Rectangle? = null
    open var parentSection: SectionView? = null

    constructor(controlModel: Control) {
        this.controlModel = controlModel
    }

    abstract val defaultToolName: String
    abstract fun containsPoint(x: Double, y: Double): Boolean
    abstract fun render(g2d: java.awt.Graphics2D)
}