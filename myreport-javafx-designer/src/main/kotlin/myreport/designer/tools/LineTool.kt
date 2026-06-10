package myreport.designer.tools

import myreport.designer.services.DesignService
import myreport.designer.ui.controlView.SectionView
import myreport.model.LineMode
import myreport.model.Point
import myreport.model.controls.Line
import java.awt.Graphics2D
import kotlin.math.max
import kotlin.math.min


open class LineTool : BaseTool {
    private var currentSection: SectionView? = null
    protected var startPointHit: Boolean = false
    protected var endPointHit: Boolean = false
    protected var line: Line? = null
    private var lineDistance = 6

    constructor(designService: DesignService) : super(designService)

    override val name: String = "LineTool"
    override val isToolbarTool: Boolean = true
    override val toolbarImageName: String = "ToolLine.png"

    open protected fun createLine(x: Float, y: Float): Line {
        var l = Line().apply {
            location = Point(x, y)
            end = Point(x, y)
            lineMode = LineMode.ALL
        }
        return l
    }

    override fun createNewControl(sectionView: SectionView) {
        var startPoint = sectionView.pointInSectionByAbsolutePoint(designService!!.startPressPoint!!)
        line = createLine(startPoint.x.toFloat(), startPoint.y.toFloat())
        var lineView = sectionView.createControlView(line!!)
        sectionView.section!!.controls.add(line!!)
        lineView.parentSection = sectionView
        designService!!.selectedControl = lineView
    }

    override fun onMouseMove() {
        if (designService!!.isPressed) {
            var control = designService!!.selectedControl

            if (designService!!.isMoving && control != null) {
                var x = max(0, (line!!.location.x + designService!!.deltaPoint!!.x).toInt())
                var y = max(0, (line!!.location.y + designService!!.deltaPoint!!.y).toInt())
                var x1 = max(0, (line!!.end.x + designService!!.deltaPoint!!.x).toInt())
                var y1 = max(0, (line!!.end.y + designService!!.deltaPoint!!.y).toInt())

                x = min(x, control.parentSection!!.section!!.width.toInt())
                y = min(y, control.parentSection!!.section!!.height.toInt())
                x1 = min(x1, control.parentSection!!.section!!.width.toInt())
                y1 = min(y1, control.parentSection!!.section!!.height.toInt())

                if (startPointHit) {
                    when (line!!.lineMode) {
                        LineMode.VERTICAL -> line!!.location = Point(line!!.location.x, y.toFloat())
                        LineMode.HORIZONTAL -> line!!.location = Point(x.toFloat(), line!!.location.y)
                        else -> line!!.location = Point(x.toFloat(), y.toFloat())
                    }
                } else if (endPointHit) {
                    when (line!!.lineMode) {
                        LineMode.VERTICAL -> line!!.end = Point(line!!.end.x, y1.toFloat())
                        LineMode.HORIZONTAL -> line!!.end = Point(x1.toFloat(), line!!.end.y)
                        else -> line!!.end = Point(x1.toFloat(), y1.toFloat())
                    }
                } else {
                    line!!.location = Point(x.toFloat(), y.toFloat())
                    line!!.end = Point(x1.toFloat(), y1.toFloat())
                }

            }
        }
    }

    override fun onAfterDraw(context: Graphics2D) {
        if (designService != null && designService!!.selectedControl != null && designService!!.isDesign) {

            var p1 = designService!!
                .selectedControl!!
                .parentSection!!
                .absolutePointByLocalPoint(line!!.location.x, line!!.location.y)
            var p2 = designService!!
                .selectedControl!!
                .parentSection!!
                .absolutePointByLocalPoint(line!!.end.x, line!!.end.y)
            Gripper.drawGripper(context, p1)
            Gripper.drawGripper(context, p2)
        }
    }

    override fun onMouseDown() {
        currentSection = if (designService!!.selectedControl != null)
            designService!!.selectedControl!!.parentSection
        else null
        if (designService!!.selectedControl != null) {
            lineDistance = (6 - designService!!.zoom).toInt()
            line = designService!!.selectedControl!!.controlModel as Line
            var location  = line!!.location
            var startPoint = currentSection!!.pointInSectionByAbsolutePoint(designService!!.startPressPoint!!)
            var startDistance = Point(location.x - startPoint.x, location.y - startPoint.y)
            var endDistance = Point(x = line!!.end.x - startPoint.x, line!!.end.y - startPoint.y)

            if (startDistance.x < lineDistance &&
                startDistance.x > -lineDistance &&
                startDistance.y < lineDistance &&
                startDistance.y > -lineDistance) {
                startPointHit = true
            } else if (endDistance.x < lineDistance &&
                endDistance.x > -lineDistance &&
                endDistance.y < lineDistance &&
                endDistance.y > -lineDistance) {
                endPointHit = true
            }
        }

    }

    override fun onMouseUp() {
        startPointHit = false
        endPointHit = false
        createMode = false
    }

}