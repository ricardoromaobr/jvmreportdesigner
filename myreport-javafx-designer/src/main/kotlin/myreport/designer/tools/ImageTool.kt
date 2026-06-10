package myreport.designer.tools

import myreport.designer.services.DesignService
import myreport.designer.ui.controlView.ImageView
import myreport.designer.ui.controlView.SectionView
import myreport.model.Border
import myreport.model.Point
import myreport.model.controls.Image

class ImageTool : RectTool {
    constructor(designService: DesignService) : super(designService)
    override val name: String = "ImageTool"
    override val isToolbarTool: Boolean  = true
    override val toolbarImageName: String = "ToolImage.png"

    override fun createNewControl(sectionView: SectionView) {
        val startPoint = sectionView.pointInSectionByAbsolutePoint(designService!!.startPressPoint!!)
        val image = Image().apply {
            location = Point(startPoint.x.toFloat(), startPoint.y.toFloat())
            width = 50f
            height = 50f
            border = Border(1f, 1f, 1f, 1f)
            data = null
        }

        val imageView = sectionView.createControlView(image) as ImageView
        imageView.parentSection = sectionView
        designService!!.selectedControl = imageView
    }
}