package myreport.designer.services

import javafx.scene.control.ToggleGroup
import javafx.scene.control.ToolBar
import myreport.designer.tools.BaseTool
import myreport.designer.ui.controlView.ControlViewBase

class ToolBoxService : IToolBoxService {

    override var selectedTool: BaseTool? = null

    fun buildToolBar(toolBar: ToolBar) {
        for (tool in toolsBox.values) {
            tool.buildToobar(toolBar)
        }
    }

    val toolsBox = mutableMapOf<String, BaseTool>()

    override fun addTool(tool: BaseTool) {
        toolsBox[tool.name] = tool
    }

    override fun setToolByConbrolView(controlView: ControlViewBase) {

        if (toolsBox.containsKey(controlView.defaultToolName))
            selectedTool = toolsBox[controlView.defaultToolName]
    }

    override fun setTool(tool: BaseTool) {
        selectedTool = tool
    }

    override fun setToolByName(toolName: String) {
        selectedTool = toolsBox[toolName]
        selectedTool?.createMode = true
    }

    override fun unselectTool() {
        selectedTool = null
    }
}