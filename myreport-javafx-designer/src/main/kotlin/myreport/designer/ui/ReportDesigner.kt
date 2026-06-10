package myreport.designer.myreport.designer.ui


import javafx.scene.canvas.Canvas
import javafx.scene.layout.Region
import myreport.designer.services.CompilerService
import myreport.designer.services.DesignService
import myreport.designer.services.WorkspaceService
import myreport.model.controls.DetailSection
import myreport.model.controls.PageFooterSection
import myreport.model.controls.PageHeaderSection
import myreport.model.controls.ReportHeaderSection
import myreport.renderer.ReportRenderer
import org.jfree.fx.FXGraphics2D
import java.awt.Color
import java.awt.Font
import java.awt.font.TextLayout

class ReportDesigner : Region() {
    private val g2fx: FXGraphics2D
    private val canvas: Canvas
    lateinit var compilerService: CompilerService
    lateinit var workspaceService: WorkspaceService
    lateinit var designerService: DesignService
    lateinit var report: myreport.model.Report
    lateinit var reportRenderer: ReportRenderer
    var pageNumber: Int = 0
    var dpi: Int = 0

    init {
        canvas = Canvas()

        canvas.widthProperty().bind(widthProperty())
        canvas.heightProperty().bind(heightProperty())

        g2fx = FXGraphics2D(canvas.graphicsContext2D)

        children.addAll(canvas)


        onMousePressed = {
            if (designerService.isDesign) {
                requestFocus()
                workspaceService.status("press:  ${it.x}, ${it.y}")
                designerService.mouseButtonPresss(it)
                println("onMousePressed ${it.x}, ${it.y}")
            }
        }

        onMouseDragged = {
            if (designerService.isDesign) {
                workspaceService.status("move:  ${it.x}, ${it.y}")
                designerService.mouseMove(it)
                println("onMouseMoved ${it.x}, ${it.y}")
            }
        }

        onMouseMoved = {
            if (designerService.isDesign) {
                workspaceService.status("move:  ${it.x}, ${it.y}")
                designerService.mouseMove(it)
                println("onMouseMoved ${it.x}, ${it.y}")
            }
        }

        onMouseReleased = {
            if (designerService.isDesign)
                designerService.buttonRelease(it)
            println("onMouseReleased ${it.x}, ${it.y}")
        }

        onKeyPressed = {
            if (designerService.isDesign)
                designerService.keyPressed(it)
        }

    }

    override fun layoutChildren() {
        canvas.relocate(0.0, 0.0)
        designerService.redrawReport(g2fx)
    }

    fun invalidate() {
       layoutChildren()
    }




}