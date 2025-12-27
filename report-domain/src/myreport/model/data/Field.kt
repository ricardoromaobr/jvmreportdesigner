package myreport.model.data

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlin.reflect.KClass

@Serializable
sealed class Field {

    var name: String? = null

    @Transient
    var defaultValue: Any? = null

    var fieldType: KClass<*>? = null

    lateinit var fieldKind: FieldKind

    abstract fun getValue(current: Any?, format: String): Any?

    override fun toString(): String {
        return name!!
    }

}

enum class FieldKind {
    DATA,
    EXPRESSION,
    PARAMETER
}