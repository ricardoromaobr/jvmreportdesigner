package myreport.model.controls

import kotlinx.serialization.Serializable
import myreport.model.SectionType

@Serializable
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