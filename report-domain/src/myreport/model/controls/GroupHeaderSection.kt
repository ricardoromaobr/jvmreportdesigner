package myreport.model.controls

import myreport.model.SectionType

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