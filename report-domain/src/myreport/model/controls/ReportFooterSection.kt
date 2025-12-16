package myreport.model.controls

import myreport.model.SectionType

class ReportFooterSection : Section() {
    init {
        name = "Report Footer"
        sectionType = SectionType.REPORT_FOOTER
    }
    override fun createControl(): Control {
        val rfs = ReportFooterSection()
        copyTo(rfs)
        return rfs
    }
}