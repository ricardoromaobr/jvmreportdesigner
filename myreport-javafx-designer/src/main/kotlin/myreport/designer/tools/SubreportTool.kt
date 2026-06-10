package myreport.designer.tools

import myreport.designer.services.DesignService
import myreport.designer.ui.controlView.SectionView
import myreport.designer.ui.controlView.SubreportView
import myreport.model.Color
import myreport.model.Point
import myreport.model.Size
import myreport.model.controls.SubReport

class SubreportTool : RectTool {

    constructor(designService: DesignService) : super(designService)

    override val name: String = "SubreportTool"

    override val toolbarImageName: String
        get() = "ToolSubreport.png"

    override val isToolbarTool: Boolean = true

    override fun createNewControl(sectionView: SectionView) {
        var startPoint = sectionView.pointInSectionByAbsolutePoint(designService!!.startPressPoint!!)

        var subreport = SubReport().apply {
            location = Point(startPoint.x.toFloat(), startPoint.y.toFloat())
            size = Size(50f, 20f)
            backgroundColor = Color(0.5f, 0.5f, 0.5f)
        }

        val subreportView = sectionView.createControlView(subreport) as SubreportView
        sectionView.section!!.controls.add(subreport)
        subreportView.parentSection = sectionView
        designService!!.selectedControl = subreportView
    }
}