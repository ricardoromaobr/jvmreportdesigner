package myreport.model.data

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlin.reflect.KProperty1

@Serializable
class PropertyDataField(
    @Transient
    val property: KProperty1<Any, *>? = null,
    @Transient
    private val getProperty: (PropertyDataField.(Any?) -> Any?)? = null,
) : Field() {
    override fun getValue(current: Any?, format:String): Any? {
        val value = getProperty?.invoke(this, if (current == null) defaultValue else current)
        return value
    }
}