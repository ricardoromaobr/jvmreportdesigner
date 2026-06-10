package myreport.designer.tools

import myreport.designer.services.DesignService
import myreport.designer.ui.controlView.SectionView
import myreport.model.Point
import myreport.model.Size
import java.awt.Graphics2D

class SectionTool : BaseTool {

    var currentsection: SectionView? = null
    constructor(designService: DesignService) : super(designService)

    override fun onBeforeDraw(context: Graphics2D) {}
    override fun onMouseMove() {
        if (designService!!.isPressed) {
            if (designService!!.isMoving && designService!!.selectedControl != null) {
                var section = designService!!.selectedControl!! as SectionView
                section.controlModel!!.size = Size(section.controlModel!!.width,
                    section.controlModel!!.height + designService!!.deltaPoint!!.y)
            }
        }
    }

    override val name: String = "SectionTool"

    override fun onAfterDraw(context: Graphics2D) {
        if (currentsection != null)
            context.fillRect(
                currentsection!!.gripperAbsoluteBounds!!.x,
                currentsection!!.gripperAbsoluteBounds!!.y,
                currentsection!!.gripperAbsoluteBounds!!.width,
                currentsection!!.gripperAbsoluteBounds!!.height
            )
    }

    override fun onMouseDown() {
        currentsection = designService!!.selectedControl as SectionView?
    }

    override fun onMouseUp() {
        var y = 0.0
        var preveiousSection: SectionView? = null

        for (sectionView in designService!!.sectionViews) {
            if (y > 0) {
                sectionView.controlModel!!.location = Point(sectionView.controlModel!!.location.x,
                    preveiousSection!!.absoluteBounds!!.y.toFloat() ?: 0f)
                sectionView.sectionSpan = java.awt.Point(sectionView.controlModel!!.location.x.toInt(),
                    preveiousSection.absoluteBounds!!.y + preveiousSection.absoluteBounds!!.height)
                sectionView.invalidate()
                y += sectionView.controlModel!!.height
            } else
                y = (sectionView.controlModel!!.location.y + sectionView.controlModel!!.height).toDouble()

            preveiousSection = sectionView
        }

        designService!!.height = (preveiousSection!!.absoluteBounds!!.y +
                preveiousSection.absoluteBounds!!.height).toFloat()
    }

    override val isToolbarTool: Boolean  = false
}