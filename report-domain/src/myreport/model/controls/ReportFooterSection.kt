package myreport.model.controls

import kotlinx.serialization.Serializable
import myreport.model.SectionType

@Serializable
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