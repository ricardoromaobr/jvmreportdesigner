package myreport.model.controls

import kotlinx.serialization.Serializable
import myreport.model.SectionType

@Serializable
class GroupHeaderSection : Section() {
    init {
        name = "Group Header"
        sectionType = SectionType.GROUP_HEADER
    }

    override fun createControl(): Control {
        val gs = GroupHeaderSection()
        copyTo(gs)
        return gs
    }
}