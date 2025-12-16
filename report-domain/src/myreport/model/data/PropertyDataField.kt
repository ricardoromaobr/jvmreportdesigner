package myreport.model.data

import kotlin.reflect.KProperty1

class PropertyDataField(val property: KProperty1<out Any, *>) : Field() {

    override fun getValue(current: Any, format: String): String {

        val value = property.call(current)

        val validFormat =  if (format.isNullOrBlank()) "%s"  else format

        return String.format(validFormat, value)
    }
}