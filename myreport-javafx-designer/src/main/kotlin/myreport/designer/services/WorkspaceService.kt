package myreport.designer.services


import com.sun.javafx.cursor.CursorType
import javafx.scene.Cursor
import javafx.scene.control.Label
import javafx.scene.control.ToolBar
import javafx.stage.Stage
import myreport.designer.ui.WorkspaceDesigner
import myreport.designer.ui.WorkspaceReportPreview

class WorkspaceService : IWorkspaceService {

    lateinit var workspacePreview: WorkspaceReportPreview
    lateinit var workspaceDesigner: WorkspaceDesigner
    lateinit var statusBarText: Label
    lateinit var stage: Stage

    override fun status(message: String) {
        statusBarText.text = message
    }

    override fun setCursor(cursor: Cursor) {
        stage.scene.cursor = cursor
    }

    override fun invalidateDesignArea() {
        workspaceDesigner.invalidate()
    }

    override fun invalidadetePreviewArea() {
        workspacePreview.invalidate()
    }

    override fun showInPropertGrid(control: Any) {
       workspaceDesigner.showInPropertGrid(control)
    }

}