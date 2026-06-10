package myreport.model.controls

import myreport.model.IReportRenderer
import myreport.model.PaperSize
import myreport.model.PaperSizeType
import myreport.model.Report
import myreport.model.engine.ReportEngine

class SubReport : Control(), IResizable {

    lateinit var report: Report
    override var canGrow: Boolean = true
    override var canShrink: Boolean = false
    var keepTogether: Boolean = false
    lateinit var engine: ReportEngine
    var finished: Boolean = false

    init {
        report = Report()
        canGrow = true
        report.paperSizeType = PaperSizeType.CUSTOM_SIZE
        report.setCustomPaperSize(PaperSize(PaperSizeType.CUSTOM_SIZE, width, 35f))
    }

    override fun createControl(): Control {
        var subreport = SubReport()
        copyBasicProperties(subreport)
        subreport.canGrow = canGrow
        subreport.canShrink = canShrink
        return subreport
    }

    fun processUpToPage(reportRenderer: IReportRenderer, height: Float) {
        engine.reportRenderer = reportRenderer
        engine.context?.heightLeftOnCurrentPage = height
        finished = engine.processReportPage()
    }

}