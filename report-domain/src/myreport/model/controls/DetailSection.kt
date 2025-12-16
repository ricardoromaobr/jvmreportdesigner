package myreport.model.controls

import myreport.model.SectionType

class DetailSection : Section() {

    init {
        name = "Detail"
        keepTogether = false
        sectionType = SectionType.DETAILS
    }

    override fun createControl(): Control {
        val ds = DetailSection()
        copyTo(ds)
        return ds
    }

}