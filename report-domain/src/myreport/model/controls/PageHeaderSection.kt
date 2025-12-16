package myreport.model.controls

import myreport.model.SectionType

class PageHeaderSection : Section() {

    init {
        name = "Page Header"
        sectionType = SectionType.PAGE_HEADER
    }

    override fun createControl(): Control {
        val ds = PageHeaderSection()
        copyTo(ds)
        return ds
    }
}