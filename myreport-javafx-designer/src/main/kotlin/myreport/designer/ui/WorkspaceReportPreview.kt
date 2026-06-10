package myreport.designer.ui

import com.sun.prism.paint.Color
import javafx.scene.Group
import javafx.scene.control.Pagination
import javafx.scene.control.ScrollPane
import javafx.scene.control.Tab
import javafx.scene.control.ToolBar
import javafx.scene.layout.BackgroundFill
import javafx.scene.layout.Priority
import javafx.scene.layout.Region
import javafx.scene.layout.VBox
import myreport.model.Report
import myreport.model.engine.ReportEngine
import myreport.renderer.RegisterDefaultRenderers
import myreport.renderer.ReportRenderer

class WorkspaceReportPreview : VBox {

    private val _reportPreview: ReportPreview = ReportPreview()
    private val _previewToolBar: ToolBar = ToolBar()
    private val _report: Report
    private val _pagination = Pagination()
    private var _currentPageNumber: Int = 0
    private var _reportEngine: ReportEngine? = null
    private var _reportRenderer: ReportRenderer? = null

    fun reportChange() {
        _reportPreview.prefWidth = _report.width.toDouble()
        _reportPreview.prefHeight = _report.height.toDouble()
    }

    constructor(report: Report) : super() {
        _report = report
        reportChange()
        val scrollPane = ScrollPane(_reportPreview)
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED)
        VBox.setVgrow(scrollPane, Priority.ALWAYS)
        //_reportPreview.style = "-fx-background-color: yellow;"
        buildToolBar()
        children.addAll(_previewToolBar, scrollPane)
    }

    val previewToolBar: ToolBar get() = _previewToolBar
    val reportPreview: ReportPreview get() = _reportPreview
    var numberOfPage: Int = 0
        get() = field
        set(value) {
            field = value
            _pagination.pageCount = value
        }


    private fun buildToolBar() {

        _pagination.pageCount = numberOfPage
        _pagination.currentPageIndex = 0
        _pagination.maxPageIndicatorCount = 10

        _pagination.currentPageIndexProperty().addListener { _, _, newValue ->
            _currentPageNumber = newValue.toInt()
            if (_report.pages.size > 0) {
                _reportPreview.page = _report.pages[_currentPageNumber]
                invalidate()
            }

        }

        _previewToolBar.items.addAll(_pagination)
    }

    fun invalidate() {
        _reportPreview.invalidate()
    }


    fun setPage(pageNumber: Int) {
        _pagination.currentPageIndex = pageNumber
        _reportPreview.page = _report.pages[pageNumber - 1]
    }

    var reportEngine: ReportEngine
        get() = _reportEngine!!
        set(value) {
            _reportEngine = value
        }

}