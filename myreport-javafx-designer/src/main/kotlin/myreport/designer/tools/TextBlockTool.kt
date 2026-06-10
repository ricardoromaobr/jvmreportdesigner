package myreport.designer.tools

import myreport.designer.services.DesignService
import myreport.designer.ui.controlView.SectionView
import myreport.designer.ui.widgets.TextEditorDialog
import myreport.model.Point
import myreport.model.Size
import myreport.model.controls.TextBlock
import myreport.model.data.FieldKind

class TextBlockTool : RectTool {

    override val name: String = "TextBlockTool"

    override val toolbarImageName: String = "ToolText.png"

    override val isToolbarTool: Boolean
        get() = true


    constructor(designService: DesignService) : super(designService)

    override fun onDoubleClick() {
        val textEditorDialog = TextEditorDialog()
        //TODO: text editor
    }

    override fun createNewControl(sectionView: SectionView) {
        val startPoint = sectionView.pointInSectionByAbsolutePoint(designService!!.startPressPoint!!)
        val textBlock = TextBlock().apply {
            location = Point(startPoint.x.toFloat(), startPoint.y.toFloat())
            text = "text"
            fontName = "Helvetica"
            fontSize = 11f
            size = Size(70f, 14f)
            fieldKind = FieldKind.DATA
        }

        val textBlockView = sectionView.createControlView(textBlock)
        sectionView.section!!.controls.add(textBlock)
        textBlockView.parentSection = sectionView
        designService!!.selectedControl = textBlockView       
    }

}