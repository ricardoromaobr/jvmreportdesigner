package myreport.designer.tools

import myreport.designer.services.DesignService
import myreport.model.LineMode
import myreport.model.controls.Line

class LineToolV: LineTool {

    constructor(designService: DesignService) : super(designService)

    override fun createLine(x: Float, y: Float): Line {
        val l = super.createLine(x, y)
        l.lineMode = LineMode.VERTICAL
        return l
    }

    override val name: String
        get() = "LineToolV"

    override val toolbarImageName: String
        get() = "ToolLineV.png"
}