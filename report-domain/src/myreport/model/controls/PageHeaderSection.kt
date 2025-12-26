package myreport.model.controls

import kotlinx.serialization.Serializable
import myreport.model.SectionType

@Serializable
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