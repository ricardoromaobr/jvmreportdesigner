package myreport.model.controls

import kotlinx.serialization.Serializable
import myreport.model.SectionType

@Serializable
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