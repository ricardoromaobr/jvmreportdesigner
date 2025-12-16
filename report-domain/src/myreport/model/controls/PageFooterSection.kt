package myreport.model.controls

import myreport.model.SectionType

class PageFooterSection: Section() {
    init {
        name = "Page Footer"
        sectionType = SectionType.PAGE_FOOTER
    }
    override fun createControl(): Control {
        TODO("Not yet implemented")
    }
}