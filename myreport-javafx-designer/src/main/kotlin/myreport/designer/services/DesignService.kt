package myreport.designer.services

import com.sun.javafx.cursor.CursorType
import com.sun.tools.javac.util.StringUtils
import javafx.scene.Cursor
import javafx.scene.control.ToggleGroup
import javafx.scene.control.ToolBar
import javafx.scene.input.KeyEvent
import javafx.scene.input.MouseEvent
import myreport.designer.tools.BaseTool
import myreport.designer.ui.controlView.ControlViewBase
import myreport.designer.ui.controlView.ControlViewFactory
import myreport.designer.ui.controlView.SectionView
import myreport.designer.ui.controlView.withSavedState
import myreport.model.Point
import myreport.model.Report
import myreport.model.controls.Image
import myreport.model.controls.Section
import myreport.model.controls.TextBlock
import myreport.model.data.FieldKind
import kotlin.io.path.name

class DesignService {
    var zoom: Double = 1.0
    var width: Float = 0.0f
    var height: Float = 0.0f

    var selectedTool: BaseTool?
        get() = toolBoxService.selectedTool
        set(value) {
            toolBoxService.selectedTool = value
        }

    var startPressPoint: java.awt.Point? = null
        private set

    var endPressPoint: java.awt.Point? = null
        private set

    var mousePoint: java.awt.Point? = null

    var previousMousePoint: java.awt.Point? = null
        private set

    var deltaPoint: java.awt.Point? = null
        private set

    var isPressed: Boolean = false
        private set

    var isMoving: Boolean = false
        private set

    var isDesign = false

    var render = false
        private set

    var workspaceService: IWorkspaceService
    lateinit var toolBoxService: ToolBoxService
    private var compilerService: CompilerService

    //events
    var onSelectedControlChanged: () -> Unit = {}
    var onReportDataFieldsRefreshed: () -> Unit = {}

    var onReportChanged: () -> Unit = {}

    var selectedControl: ControlViewBase? = null
        set(value) {
            field = value
            if (field != null)
                toolBoxService.setToolByConbrolView(field!!)
            else
                toolBoxService.unselectTool()

            onSelectedControlChanged()
        }

    private var controlViewFactory: ControlViewFactory
    var report: Report? = null
        set(value) {
            field = value
            initReport()
            onReportChanged()
        }

    var sectionViews: MutableList<SectionView> = mutableListOf()
        private set

    constructor(workspaceService: IWorkspaceService, compilerService: CompilerService, report: Report) {
        this.workspaceService = workspaceService
        this.compilerService = compilerService
        this.report = report
        controlViewFactory = ControlViewFactory(this)
        isDesign = true
        zoom = 1.0
        render = true
    }

    fun initReport() {
        //TODO: adjust to add group sections
        sectionViews = mutableListOf()
        report!!.sections.forEach { addSectionView(it) }

    }

    fun redrawReport(context: java.awt.Graphics2D) {

        context.scale(zoom, zoom)
        width = (report!!.width * zoom).toFloat()
        height = (report!!.height * zoom).toFloat()
        context.withSavedState {
            color = java.awt.Color(255, 255, 255, 255)
            context.fillRect(0, 0, width.toInt(), height.toInt())
        }

        if (selectedControl != null) selectedTool?.onBeforeDraw(context)

        sectionViews.forEach { it.render(context) }

        if (selectedControl != null) selectedTool?.onAfterDraw(context)
    }

    fun createTextBlockAtXY(text: String, fieldName: String, fieldKind: FieldKind, x: Float, y: Float) {
        var point = java.awt.Point((x / zoom).toInt(), (y / zoom).toInt())
        var sectionView = getSectionViewByXY(x, y)

        if (sectionView != null) {
            var localPoint = sectionView.pointInSectionByAbsolutePoint(point)
            toolBoxService.setToolByName("TextBlockTool")
            selectedTool!!.createNewControl(sectionView)
            var textBlock = selectedControl!!.controlModel as TextBlock
            textBlock.text = fieldName
            textBlock.fieldName = fieldName
            textBlock.fieldKind = fieldKind
            textBlock.location = Point(localPoint.x.toFloat(), localPoint.y.toFloat())
            selectedTool!!.createMode = false
        }
    }

    fun createImageAtXY(imageName: String, x: Float, y: Float) {
        var point = java.awt.Point((x / zoom).toInt(), (y / zoom).toInt())
        var sectionView = getSectionViewByXY(x, y)
        var localPoint = sectionView!!.pointInSectionByAbsolutePoint(point)
        toolBoxService.setToolByName("ImageTool")
        selectedTool!!.createNewControl(sectionView)
        var image = selectedControl!!.controlModel as Image
        image.location = Point(localPoint.x.toFloat(), localPoint.y.toFloat())
        selectedTool!!.createMode = false
    }

    fun getSectionViewByXY(x: Float, y: Float): SectionView? {
        var point = java.awt.Point((x / zoom).toInt(), (y / zoom).toInt())
        var sectionView: SectionView? = null

        for (retsectionView in sectionViews) {
            if (retsectionView.absoluteBounds!!.contains(point)) {
                if (retsectionView.headerAbsoluteBounds!!.contains(point)) {
                    selectedControl = retsectionView
                    continue
                }
                sectionView = retsectionView
                break
            }
        }

        return sectionView
    }

    fun refreshDataFieldsFromDataSource() {
        report!!.fillFieldsFromDatasource()
        onReportDataFieldsRefreshed()
    }

    fun keyPressed(keyEvent: KeyEvent) {
        if (selectedControl != null)
            selectedTool!!.onKeyPressed(keyEvent)
    }

    fun deleteSelectedControl() {
        if (selectedControl != null) {
            selectedControl!!.parentSection!!.removeControlView(selectedControl!!)
            selectedControl!!.controlModel = null
            selectedControl = null
            workspaceService.invalidateDesignArea()
        }
    }

    fun mouseButtonPresss(mouseEvent: MouseEvent) {
        startPressPoint = java.awt.Point((mouseEvent.x / zoom).toInt(), (mouseEvent.y / zoom).toInt())
        isPressed = true
        isMoving = false

        if (!isMoving) {
            previousMousePoint = startPressPoint
            deltaPoint = java.awt.Point(0, 0)

            for (sectionView in sectionViews) {
                if (sectionView.absoluteBounds!!.contains(startPressPoint!!.x, startPressPoint!!.y)) {
                    if (sectionView.headerAbsoluteBounds!!.contains(startPressPoint!!.x, startPressPoint!!.y)) {
                        selectedControl = sectionView
                        selectedTool = null
                        continue
                    } else if (sectionView.gripperAbsoluteBounds!!.contains(startPressPoint!!.x, startPressPoint!!.y)) {
                        selectedControl = sectionView
                    } else {

                        if (selectedTool != null && selectedTool!!.createMode) {
                            selectedTool!!.createNewControl(sectionView)
                            selectedTool!!.createMode = false
                        } else {
                            selectedControl = null

                            for (controlView in sectionView.Controls) {

                                val matchControl = controlView.containsPoint(
                                    startPressPoint!!.x.toDouble(),
                                    startPressPoint!!.y.toDouble()
                                )

                                if (matchControl) {
                                    selectedControl = controlView
                                    break
                                }
                            }
                        }
                    }
                }
            }
        }

        if (selectedTool != null) {
            if (mouseEvent.clickCount == 1)
                selectedTool!!.onMouseDown()
            else
                selectedTool!!.onDoubleClick()
        }

        workspaceService.invalidateDesignArea()
    }

    fun load(path: String) {
        //TODO: LOAD REPORT FILE

        initReport()
        onReportChanged()
    }

    fun save(path: String) {
        if (report!!.title.isNullOrEmpty())
            report!!.title = java.nio.file.Paths.get(path).fileName.name
        //TODO: save report
        //report!!.save(path)
    }

    fun mouseMove(mouseEvent: MouseEvent) {
        mousePoint = java.awt.Point((mouseEvent.x / zoom).toInt(), (mouseEvent.y / zoom).toInt())
        isMoving = true
        if (selectedTool != null)
            selectedTool!!.onMouseMove()

        if (previousMousePoint != null)
            deltaPoint = java.awt.Point(
                -previousMousePoint!!.x + mousePoint!!.x,
                -previousMousePoint!!.y + mousePoint!!.y
            )

        if (!isPressed) {
            var isOnGripper = false
            for (sectionView in sectionViews) {
                if (sectionView.gripperAbsoluteBounds!!.contains(mousePoint!!)) {
                    sectionView.sectionGripperHighlighted = true
                    isOnGripper = true
                } else
                    sectionView.sectionGripperHighlighted = false
            }
            if (isOnGripper)
                workspaceService.setCursor(Cursor.V_RESIZE)
            else
                workspaceService.setCursor(Cursor.DEFAULT)
        }

        workspaceService.invalidateDesignArea()
        previousMousePoint = mousePoint
    }

    fun zoomChanged(zoom: Double) {
        this.zoom = zoom
        workspaceService.invalidateDesignArea()
    }

    fun buttonRelease(mouseEvent: MouseEvent) {
        endPressPoint = java.awt.Point((mouseEvent.x / zoom).toInt(), (mouseEvent.y / zoom).toInt())
        isPressed = false
        isMoving = false
        if (selectedTool != null)
            selectedTool!!.onMouseUp()
        if (selectedControl != null)
            workspaceService.showInPropertGrid(selectedControl!!.controlModel!!)
        workspaceService.invalidateDesignArea()
    }

    private fun addSectionView(section: Section) {
        var sectionSpan: java.awt.Point
        if (sectionViews.count() > 0) {
            var previousSectionView = sectionViews[sectionViews.count() - 1]
            sectionSpan = java.awt.Point(
                0,
                previousSectionView.absoluteBounds!!.y + previousSectionView.absoluteBounds!!.height
            )
        } else
            sectionSpan = java.awt.Point(0, 0)
        var sectionView = SectionView(report!!, controlViewFactory, section, sectionSpan)
        sectionViews.add(sectionView)
        height = (sectionView.absoluteBounds!!.y + sectionView.absoluteBounds!!.height).toFloat()
    }

    fun exportToPdf() {
        //TODO: export to PDF
        NotImplementedError()
    }
}