package myreport.designer.tools

import javafx.scene.control.Button
import javafx.scene.control.ToggleButton
import javafx.scene.control.ToggleGroup
import javafx.scene.control.ToolBar
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import myreport.designer.services.DesignService
import myreport.designer.ui.controlView.SectionView

abstract class BaseTool {
    val IMAGE_RESOURCE_PATH = "/images/"
    protected var designService: DesignService? = null

    constructor(designService: DesignService) {
        this.designService = designService
    }

    abstract val name: String
    open val toolbarImageName: String
        get() = ""

    abstract val isToolbarTool: Boolean
    var createMode: Boolean = false

    open fun createNewControl(sectionView: SectionView) {}

    //todo
    open fun buildToobar(toolBar: ToolBar) {
        if (isToolbarTool) {
            val toolItem = Button()
            val imageName = "$IMAGE_RESOURCE_PATH$toolbarImageName"
            val input = javaClass.getResourceAsStream(imageName)
            val image = Image(input)
            val imageView = ImageView(image)
            toolItem.graphic = imageView
            toolItem.text = name
            toolItem.setOnAction {
                designService!!.selectedControl = null
                designService!!.selectedTool = this
                createMode = true
            }

            toolBar.items.add(toolItem)
        }
    }

    open fun onBeforeDraw(context: java.awt.Graphics2D) {}
    open fun onAfterDraw(context: java.awt.Graphics2D) {}
    open fun onMouseDown() {}
    open fun onDoubleClick() {}
    open fun onMouseUp() {}
    open fun onMouseMove() {}

    //todo
    open fun onKeyPressed(keyEvent: KeyEvent) {
        when (keyEvent.code) {
            KeyCode.DELETE -> designService!!.deleteSelectedControl()
            else -> {}
        }

    }

}
