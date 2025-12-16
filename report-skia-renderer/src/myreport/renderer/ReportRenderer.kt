package myreport.renderer

import myreport.model.IReportRenderer
import myreport.model.Page
import myreport.model.Size
import myreport.model.controls.Control
import myreport.model.controls.IControlRenderer
import kotlin.Array
import kotlin.arrayOfNulls
import kotlin.reflect.KClass
import kotlin.reflect.typeOf

class ReportRenderer : IReportRenderer {

    private val _controlRenderers: MutableMap<KClass<*>, IControlRenderer> = mutableMapOf()

    override fun measureControl(control: Control) : Size? {
        val controlType = control::class

        if(_controlRenderers.containsKey(controlType)){
            val renderer = _controlRenderers[controlType] as IControlRenderer
            return renderer.measure(context,control);
        }
        return null
    }

    override fun renderControl(control: Control) {
        val controls: Array<Control?>  = arrayOfNulls(2)

        val controlType: KClass<*> = control::class

        if (_controlRenderers.containsKey(controlType)) {
            val renderer = _controlRenderers[controlType]
            renderer?.render (context, control)
        }
    }

    override fun breakOffControlAtMostAtHeight(control: Control, height: Float) : Array<Control?> {

        lateinit var controls: Array<Control?>
        val controlType: KClass<*> = control::class
        if (_controlRenderers.containsKey(controlType)) {
            val renderer = _controlRenderers[controlType]
           controls = renderer?.breakOffControlAtMostAtHeight (context, control, height)!!
        }
        return controls
    }

    override lateinit var context: Any

    override fun registerRenderer(type: KClass<*>, renderer: IControlRenderer) {
        _controlRenderers[type] = renderer
    }

    override fun renderPage(page: Page) {
        for(control in page.controls) {
            if (control.isVisible)
                renderControl(control)
        }
    }
}