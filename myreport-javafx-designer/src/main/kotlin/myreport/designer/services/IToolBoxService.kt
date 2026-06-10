package myreport.designer.services

import myreport.designer.tools.BaseTool
import myreport.designer.ui.controlView.ControlViewBase

interface IToolBoxService {
    fun addTool(tool: BaseTool)
    var selectedTool: BaseTool?
    fun setToolByConbrolView(controlView: ControlViewBase)
    fun setTool(tool: BaseTool)
    fun setToolByName(toolName: String)
    fun unselectTool()
}