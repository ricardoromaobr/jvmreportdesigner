package myreport.designer.ui

import javafx.scene.canvas.Canvas
import javafx.scene.layout.Region
import myreport.designer.ui.controlView.withSavedState
import myreport.model.Page
import myreport.renderer.ReportRenderer
import org.jfree.fx.FXGraphics2D

class ReportPreview : Region() {
    private val g2fx: FXGraphics2D
    private val _canvas: Canvas = Canvas()
    var page: Page? = null
    var reportRenderer: ReportRenderer? = null

    init {

        _canvas.widthProperty().bind(widthProperty())
        _canvas.heightProperty().bind(heightProperty())

        g2fx = FXGraphics2D(_canvas.graphicsContext2D)



        children.add(_canvas)
    }

    val g2d: FXGraphics2D get() = g2fx

    override fun layoutChildren() {
        _canvas.relocate(0.0, 0.0)
        drawPage()
    }

    fun drawPage() {

        if (page == null) return
        g2d.withSavedState {
            g2d.color = java.awt.Color.WHITE
            g2d.fillRect(0,0, width.toInt(), height.toInt())
        }

        g2d.color = java.awt.Color.BLACK
        g2d.drawString("Page test", 10, 800)
        reportRenderer?.renderPage(page!!)

    }

    fun invalidate() {
        layoutChildren()
    }
}