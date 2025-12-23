package myreport.renderer

import myreport.model.IRegisterDefaultRenderers
import myreport.model.IReportRenderer
import myreport.model.controls.DetailSection
import myreport.model.controls.Image
import myreport.model.controls.Line
import myreport.model.controls.PageFooterSection
import myreport.model.controls.PageHeaderSection
import myreport.model.controls.ReportFooterSection
import myreport.model.controls.ReportHeaderSection
import myreport.model.controls.TextBlock

class RegisterDefaultRenderers: IRegisterDefaultRenderers {
    override fun addRenderer(reportRenderer: IReportRenderer) {
        reportRenderer.registerRenderer(Line::class, LineRenderer())
        reportRenderer.registerRenderer(TextBlock::class, TextBlockRenderer())
        reportRenderer.registerRenderer(Image::class, ImageRenderer())
        val sectionRenderer = SectionRenderer()
        reportRenderer.registerRenderer(ReportHeaderSection::class, sectionRenderer)
        reportRenderer.registerRenderer(ReportFooterSection::class, sectionRenderer)
        reportRenderer.registerRenderer(PageHeaderSection::class, sectionRenderer)
        reportRenderer.registerRenderer(PageFooterSection::class, sectionRenderer)
        reportRenderer.registerRenderer(DetailSection::class, sectionRenderer)
    }
}