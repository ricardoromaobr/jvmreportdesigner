package myreport.designer.ui.controlView

import myreport.model.Report
import myreport.model.controls.Control
import myreport.model.controls.DetailSection
import myreport.model.controls.Section
import java.awt.Color
import java.awt.Graphics2D
import java.awt.Point
import java.awt.Rectangle


class SectionView : ControlViewBase {
    companion object {
        const val SECTION_HEADER_HEIGHT = 20.0f
        const val SECTION_GRIPPER_HEIGHT = 4.0f
        val BLACK_COLOR = java.awt.Color.BLACK
        val YELLOW_COLOR = java.awt.Color.YELLOW
        val SECTION_HEADER_COLOR = java.awt.Color(0.85f, 0.85f, 0.91f)
        val SECTION_HEADER_COLOR1 = java.awt.Color(0.56f, 0.56f, 0.61f)
    }

    var sectionGripperColor: java.awt.Color? = null
    var isCollapsed: Boolean
    var sectionSpan: java.awt.Point
    var absoluteDrawingStartPoint: java.awt.Point
    var headerAbsoluteBounds: java.awt.Rectangle? = null
    var gripperAbsoluteBounds: java.awt.Rectangle? = null

    var controlViewFactory: IControlViewFactory

    var parentReport: Report

    var allowCrossSectionControls: Boolean
        private set

    private var _controls = mutableListOf<ControlViewBase>()

    val Controls get() = _controls.toList()

    override var controlModel: Control?
        get() = super.controlModel!!
        set(value) {
            super.controlModel = value
            section = value as Section
        }

    override var defaultToolName: String = "SectionTool"

    var section: Section? = null

    var designCrossSectionControlsToAdd: MutableList<ControlViewBase>? = null
    var designCrossSectionControlsToRemove: MutableList<ControlViewBase>? = null

    constructor(
        parentReport: Report, controlViewFactory: IControlViewFactory, section: Section,
        sectionSpan: java.awt.Point
    ) : super(section) {

        designCrossSectionControlsToAdd = mutableListOf()
        designCrossSectionControlsToRemove = mutableListOf()

        this.parentReport = parentReport
        this.controlViewFactory = controlViewFactory

        if (section is DetailSection)
            allowCrossSectionControls = true
        else
            allowCrossSectionControls = false

        this.sectionSpan = sectionSpan
        _controls = mutableListOf()

        this.absoluteDrawingStartPoint = java.awt.Point(sectionSpan.x, sectionSpan.y)

        this.sectionGripperColor = SECTION_HEADER_COLOR1
        this.isCollapsed = false

        invalidate()
    }

    fun addControlView(control: ControlViewBase) {
        _controls.add(control)
    }

    fun createControlView(control: Control): ControlViewBase {
        var controlView = controlViewFactory.createControlView(control, this)
        addControlView(controlView)
        return controlView
    }

    fun removeControlView(controlView: ControlViewBase) {
        section?.controls?.remove(controlView.controlModel)
        _controls.remove(controlView)
    }

    fun addControls(controlsToAdd: List<Control>) = controlsToAdd.forEach { createControlView(it) }

    fun invalidate() {
        absoluteBounds = Rectangle(
            sectionSpan.x, sectionSpan.y, section!!.width.toInt(),
            (section!!.height + SECTION_HEADER_HEIGHT + SECTION_GRIPPER_HEIGHT).toInt()
        )

        gripperAbsoluteBounds = Rectangle(
            sectionSpan.x,
            (sectionSpan.y + section!!.height + SECTION_HEADER_HEIGHT).toInt(),
            section!!.width.toInt(), SECTION_GRIPPER_HEIGHT.toInt()
        )

        headerAbsoluteBounds = Rectangle(
            sectionSpan.x, sectionSpan.y, section!!.width.toInt(),
            SECTION_HEADER_HEIGHT.toInt()
        )

        absoluteDrawingStartPoint = java.awt.Point(
            absoluteBounds!!.x,
            (sectionSpan.y + SECTION_HEADER_HEIGHT).toInt()
        )
    }

    //region implement abstract members

    override fun containsPoint(x: Double, y: Double): Boolean {
        return absoluteBounds!!.contains(x, y)
    }

    fun pointInSectionByAbsolutePoint(absolutePoint: java.awt.Point): java.awt.Point {
        return pointInSectionByAbsolutePoint(absolutePoint.x.toFloat(), absolutePoint.y.toFloat())
    }

    fun pointInSectionByAbsolutePoint(x: Float, y: Float): java.awt.Point {
        return Point((x - absoluteDrawingStartPoint.x).toInt(), (y - absoluteDrawingStartPoint.y).toInt())
    }

    fun absolutePointByLocalPoint(x: Float, y: Float): java.awt.Point =
        Point((x + absoluteDrawingStartPoint.x).toInt(), (y + absoluteDrawingStartPoint.y).toInt())

    override fun render(g2d: Graphics2D) {
        invalidate()

        g2d.withSavedState {
            color = Color(
                section!!.backgroundColor.r / 255,
                section!!.backgroundColor.g / 255,
                section!!.backgroundColor.b / 255,
                section!!.backgroundColor.a / 255
            )
            fillRect(
                absoluteBounds!!.x, absoluteBounds!!.y,
                absoluteBounds!!.width, absoluteBounds!!.height
            )
        }

        g2d.withSavedState {

            color = Color.lightGray

            fillRect(
                headerAbsoluteBounds!!.x,
                headerAbsoluteBounds!!.y,
                headerAbsoluteBounds!!.width,
                headerAbsoluteBounds!!.height
            )
        }

        g2d.font = java.awt.Font("Tahoma", java.awt.Font.BOLD, 12)
        g2d.drawString(section!!.name, absoluteBounds!!.x + 3, absoluteBounds!!.y + g2d.fontMetrics.height)

        g2d.withSavedState {
            if (!sectionGripperHighlighted)
                color = sectionGripperColor
            else
                color = YELLOW_COLOR

            fillRect(
                gripperAbsoluteBounds!!.x,
                gripperAbsoluteBounds!!.y,
                gripperAbsoluteBounds!!.width,
                gripperAbsoluteBounds!!.height
            )
        }

        // translate the graphics context to the drawing start point
        val originalTransform = g2d.getTransform()
        g2d.translate(absoluteDrawingStartPoint.x, absoluteDrawingStartPoint.y)

        _controls.forEach { it.render(g2d) }
        // restore the original transform
        g2d.setTransform(originalTransform)
    }

    var sectionGripperHighlighted: Boolean = false
        get() = field
        set(value) {
            if (value != field) {
                field = value
                if (field)
                    sectionGripperColor = YELLOW_COLOR
                else
                    sectionGripperColor = SECTION_HEADER_COLOR1
            }
        }

    //endregion


}