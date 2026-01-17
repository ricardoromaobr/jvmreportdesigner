package myreport.model.controls

import kotlinx.serialization.Serializable
import myreport.model.SectionType

@Serializable
class PageFooterSection: Section() {
    init {
        name = "Page Footer"
        sectionType = SectionType.PAGE_FOOTER
    }
    override fun createControl(): Control {
        val pfs = PageFooterSection()
        copyTo(pfs)
        return pfs
    }
}