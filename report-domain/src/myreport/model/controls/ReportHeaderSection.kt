package myreport.model.controls

import myreport.model.SectionType

class ReportHeaderSection : Section() {
    init {
        name = "Report Header"
        sectionType = SectionType.REPORT_HEADER
    }

    override fun createControl(): Control {
        val rhs = ReportHeaderSection()
        copyTo(rhs)
        rhs.breakPageAfter = breakPageAfter
        return rhs
    }

    var breakPageAfter: Boolean = false
}