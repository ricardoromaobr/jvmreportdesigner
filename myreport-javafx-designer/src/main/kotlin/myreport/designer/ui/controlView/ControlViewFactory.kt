package myreport.designer.ui.controlView

import myreport.designer.services.DesignService
import myreport.model.controls.Control
import myreport.model.controls.Image
import myreport.model.controls.Line
import myreport.model.controls.SubReport
import myreport.model.controls.TextBlock
import kotlin.reflect.KClass

class ControlViewFactory : IControlViewFactory {

    var designService: DesignService? = null
    val controlViews = mutableMapOf<KClass<*>, (control: Control, sectionView: SectionView) -> ControlViewBase>()

    constructor(designService: DesignService) {
        this.designService = designService
        controlViews[TextBlock::class] = { control, sectionView -> TextBlockView(control as TextBlock, sectionView) }
        controlViews[Line::class] = { control, sectionView -> LineView(control as Line, sectionView) }
        controlViews[Image::class] = { control, sectionView -> ImageView(control as Image, sectionView) }
        controlViews[SubReport::class] = { control, sectionView -> SubreportView(control as SubReport, sectionView) }
    }

    override fun createControlView(
        control: Control,
        sectionView: SectionView
    ): ControlViewBase {
        return controlViews[control::class]!!.invoke(control, sectionView)
    }
}