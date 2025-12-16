package myreport.model.controls

import myreport.model.Size

interface IControlRenderer {
    fun render(context: Any, control: Control)
    fun measure(context: Any, control: Control) : Size
    var dpi: Float
    var designMode: Boolean
    fun breakOffControlAtMostAtHeight(context: Any, control: Control, height: Float) : Array<Control?>
}