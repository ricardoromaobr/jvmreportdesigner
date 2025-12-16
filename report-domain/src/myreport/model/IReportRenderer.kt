package myreport.model

import myreport.model.controls.Control
import myreport.model.controls.IControlRenderer
import kotlin.reflect.KClass

interface IReportRenderer {
    fun measureControl (control: Control) : Size?
    fun renderControl (control: Control)
    fun breakOffControlAtMostAtHeight(control:Control, height: Float) : Array<Control?>

    var context: Any
    fun registerRenderer(type: KClass<*>, renderer: IControlRenderer)
    fun renderPage(page: Page)
}