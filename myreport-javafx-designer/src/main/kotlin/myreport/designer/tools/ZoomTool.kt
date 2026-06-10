package myreport.designer.tools

import javafx.scene.control.ToggleGroup
import javafx.scene.control.ToolBar
import myreport.designer.services.DesignService

class ZoomTool : BaseTool {

    constructor(designService: DesignService) : super(designService)

    override val name: String = "ZoomTool"

    override val isToolbarTool: Boolean
        get() = true

    override fun buildToobar(toolBar: ToolBar) {

    }
}