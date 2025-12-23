package myreport.model.controls

import myreport.model.SectionType

abstract class Section : Control(), IResizable {

    lateinit var sectionType: SectionType

    override var canGrow: Boolean = false
    override var canShrink: Boolean = false

    var keepTogether = false
    var controls = mutableListOf<Control>()
    lateinit var name: String
    fun format() {}
    fun beforePrint() {}
    fun afterPrint() {}

    fun copyTo(s: Section) {
        copyBasicProperties(s)
        s.name = name
        s.canGrow = canGrow
        s.canShrink = canShrink
        s.keepTogether = keepTogether

        for (ctrl in controls)
            s.controls.add(ctrl.createControl())
    }
}