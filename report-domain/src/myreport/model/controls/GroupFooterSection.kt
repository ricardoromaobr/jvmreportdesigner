package myreport.model.controls

import kotlinx.serialization.Serializable
import myreport.model.SectionType

@Serializable
class GroupFooterSection : Section() {
    init {
        name = "Group Footer"
        sectionType = SectionType.GROUP_FOOTER
    }
    override fun createControl(): Control {
        val gfs = GroupFooterSection()
        copyTo(gfs)
        return gfs
    }

}