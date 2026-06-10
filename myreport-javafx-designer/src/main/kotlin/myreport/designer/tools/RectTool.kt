package myreport.designer.tools

import com.sun.prism.BasicStroke
import myreport.designer.services.DesignService
import myreport.designer.ui.controlView.withSavedState
import myreport.model.Border
import myreport.model.Color
import myreport.model.Point
import myreport.model.Size
import java.awt.Graphics2D
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min


/**
 * Represents the corner grippers used for resizing controls in the designer.
 * Each gripper corresponds to a specific corner of the control's bounding rectangle.
 */
internal enum class GripperType {
    /** North-East corner gripper (top-right) */
    NE,

    /** North-West corner gripper (top-left) */
    NW,

    /** South-West corner gripper (bottom-left) */
    SW,

    /** South-East corner gripper (bottom-right) */
    SE
}

open class RectTool : BaseTool {
    private val gripSize = 10
    private var isResizing = false
    private var selectBoder: Border? = null
    private var gripperType: GripperType? = null

    constructor(designService: DesignService) : super(designService) {
        selectBoder = Border(1f)
        selectBoder!!.color = Color(0f, 0f, 0f, 255f)
    }

    override fun onMouseMove() {
        if (designService!!.isPressed &&
            designService!!.isMoving &&
            designService!!.selectedControl != null
        ) {
            var control = designService!!.selectedControl!!
            var location = control.controlModel!!.location

            if (designService!!.isMoving) {
                var (w, h, x, y) = listOf(0f, 0f, 0f, 0f)
                if (!isResizing) {
                    x = max(0f, (location.x + designService!!.deltaPoint!!.x))
                    y = max(0f, (location.y + designService!!.deltaPoint!!.y))
                    x = min(control.parentSection!!.section!!.width - control.controlModel!!.width, x)
                    y = min(control.parentSection!!.section!!.height - control.controlModel!!.height, y)
                    var point = Point(x, y)
                    control.controlModel!!.location = point
                } else {
                    when (gripperType) {
                        GripperType.NE -> {
                            w = min(
                                abs(control.controlModel!!.width + designService!!.deltaPoint!!.x),
                                control.parentSection!!.section!!.width
                            )
                            h = min(
                                abs(control.controlModel!!.height + designService!!.deltaPoint!!.y),
                                control.parentSection!!.section!!.height
                            )

                            y = max(location.y + designService!!.deltaPoint!!.y, 0f)
                            control.controlModel!!.size = Size(w, h)
                            control.controlModel!!.location = Point(location.x, y)
                        }

                        GripperType.SE -> {
                            w = min(
                                abs(control.controlModel!!.size.width + designService!!.deltaPoint!!.x),
                                control.parentSection!!.section!!.width
                            )
                            h = min(
                                abs(control.controlModel!!.size.height + designService!!.deltaPoint!!.y),
                                control.parentSection!!.section!!.height - control.controlModel!!.location.y
                            )
                            control.controlModel!!.size = Size(w, h)
                        }

                        GripperType.SW -> {
                            w = min(
                                abs(control.controlModel!!.size.width - designService!!.deltaPoint!!.x),
                                control.parentSection!!.section!!.width
                            )
                            h = min(
                                abs(control.controlModel!!.size.height + designService!!.deltaPoint!!.y),
                                control.parentSection!!.section!!.height - control.controlModel!!.location.y
                            )

                            x = max(location.x + designService!!.deltaPoint!!.x, 0f)
                            control.controlModel!!.size = Size(w, h)
                            control.controlModel!!.location = Point(x, location.y)
                        }

                        GripperType.NW -> {
                            w = min(
                                abs(control.controlModel!!.size.width - designService!!.deltaPoint!!.x),
                                control.parentSection!!.section!!.width
                            )
                            h = min(
                                abs(control.controlModel!!.size.height - designService!!.deltaPoint!!.y),
                                control.parentSection!!.section!!.height - control.controlModel!!.location.y
                            )

                            x = max(location.x + designService!!.deltaPoint!!.x, 0f)
                            y = max(location.y + designService!!.deltaPoint!!.y, 0f)
                            control.controlModel!!.size = Size(w, h)
                            control.controlModel!!.location = Point(x, y)
                        }

                        else -> {}
                    }
                }
            }
        }
    }

    override val name: String
        get() = "RectTool"

    override val isToolbarTool: Boolean
        get() = false

    override fun onAfterDraw(context: Graphics2D) {
        if (designService!!.selectedControl != null && designService!!.isDesign) {
            context.withSavedState {
                Gripper.drawInsideBorder(context, designService!!.selectedControl!!.absoluteBounds, selectBoder)
                Gripper.drawSelectBox(context, designService!!.selectedControl!!.absoluteBounds)
            }
        }
    }

    override fun onMouseDown() {
        if (designService?.selectedControl != null) {
            var control = designService!!.selectedControl!!
            var pointInSection = control.parentSection!!.pointInSectionByAbsolutePoint(designService!!.mousePoint!!)
            var location = control.controlModel!!.location
            isResizing = false
            if (pointInSection.y > location.y && pointInSection.y < location.y + gripSize) {
                if (pointInSection.x > location.x && location.x + gripSize > pointInSection.x) {
                    isResizing = true
                    gripperType = GripperType.NW
                } else if (pointInSection.x > location.x + control.controlModel!!.width - gripSize &&
                    location.x + control.controlModel!!.width > pointInSection.x
                ) {
                    isResizing = true
                    gripperType = GripperType.NE
                }
            } else if (pointInSection.y > location.y + control.controlModel!!.height - gripSize &&
                pointInSection.y < location.y + control.controlModel!!.height
            ) {
                if (pointInSection.x > location.x && location.x + gripSize > pointInSection.x) {
                    isResizing = true
                    gripperType = GripperType.SW
                } else if (pointInSection.x > location.x + control.controlModel!!.width - gripSize &&
                    location.x + control.controlModel!!.width > pointInSection.x
                ) {
                    isResizing = true
                    gripperType = GripperType.SE
                }
            }
        }
    }

    override fun onMouseUp() {
        isResizing = false
    }
}