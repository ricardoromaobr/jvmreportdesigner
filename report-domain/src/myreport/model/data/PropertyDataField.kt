package myreport.model.data

import kotlin.reflect.KProperty1


class PropertyDataField(
    val property: KProperty1<Any, *>?,
    private val getProperty: PropertyDataField.(Any?) -> Any?
) : Field() {
    override fun getValue(current: Any?, format:String): Any? {
        val value = getProperty(if (current == null) defaultValue else current)
        return value
    }
}