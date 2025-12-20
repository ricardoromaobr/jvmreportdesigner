package myreport.model.data

import kotlin.reflect.KClass

abstract class Field {

    var name: String? = null

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