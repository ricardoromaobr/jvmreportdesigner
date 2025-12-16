package myreport.model.controls

import kotlinx.serialization.Serializable
import myreport.model.BeforeControlProcessing
import myreport.model.Color
import myreport.model.Point
import myreport.model.ReportContext
import myreport.model.Size

@Serializable
abstract class Control {

    lateinit var templateControl: Control

    var backgroundColor = Color(1f, 1f, 1f, 0f)
    var location = Point(0f, 0f)
    var size = Size(0f, 0f)

    open var width: Float
        get() = size.width
        set(value) {
            size = Size(value, size.height)
        }

    open var height: Float
        get() = size.height
        set(value) {
            size = Size(size.width, value)
        }

    open var top: Float
        get() = location.y
        set(value) {
            location = Point(location.y, value)
        }

    open var left: Float
        get() = location.x
        set(value) {
            location = Point(location.x, value)
        }

    open val bottom: Float
        get() = location.y + size.height

    var isVisible = false

    fun measureBottomMarginFromSection(s: Section) {
    }

    abstract fun createControl(): Control

    internal fun copyBasicProperties(c: Control) {
        c.location = Point(location.x, location.y)
        c.size = Size(size.width, size.height)
        c.isVisible = isVisible
        c.backgroundColor = Color(backgroundColor.r, backgroundColor.g, backgroundColor.b,backgroundColor.a)
        c.templateControl = this
    }

    var beforeControlProcessing: BeforeControlProcessing? = null

    internal fun fireBeforeControlProcessing(rc: ReportContext, c: Control) {
        beforeControlProcessing?.invoke(rc, c)

    }
}