package myreport.designer.tools

import myreport.designer.services.DesignService
import myreport.designer.ui.controlView.SectionView
import myreport.model.LineMode
import myreport.model.Point
import myreport.model.controls.Line

class LineToolH : LineTool {

    constructor(designService: DesignService) : super(designService)

    override fun createLine(x: Float, y: Float): Line {
        val l = super.createLine(x, y)
        l.lineMode = LineMode.HORIZONTAL
        return l
    }

    override val name: String
        get() = "LineToolH"

    override val toolbarImageName: String
        get() = "ToolLineH.png"
}