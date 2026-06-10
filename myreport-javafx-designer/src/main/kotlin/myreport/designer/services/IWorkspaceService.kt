package myreport.designer.services

import com.sun.javafx.cursor.CursorType
import javafx.scene.Cursor

interface IWorkspaceService {
    fun status(message: String)
    fun setCursor(cursor: Cursor)
    fun invalidateDesignArea()
    fun invalidadetePreviewArea()
    fun showInPropertGrid(control: Any)
}