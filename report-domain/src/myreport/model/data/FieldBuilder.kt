package myreport.model.data

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import kotlin.reflect.KClass
import kotlin.reflect.KMutableProperty
import kotlin.reflect.KMutableProperty1
import kotlin.reflect.KProperty1
import kotlin.reflect.full.*
import kotlin.reflect.typeOf

class FieldBuilder {

    companion object {

        fun createFields(type: KClass<*>, name: String, fieldKind: FieldKind, prefix: String? = null): List<Field> {
            val fields = mutableListOf<Field>()

            fillFields(type, fields, name, prefix, fieldKind)

            return fields
        }

        private fun fillFields(
            type: KClass<*>,
            fields: MutableList<Field>,
            name: String,
            prefix: String?,
            fieldKind: FieldKind
        ) {

            if (prefix != null && fields.count { field -> field.name!!.startsWith("$prefix.") } > 0)
                return

            if (isSimpletype(type)) {
                fields.add(
                    PropertyDataField(null) { value -> value }.apply {
                        this.name = name
                    }
                )
            } else {

                var properties = type.memberProperties
                var properName = if (prefix == null) "" else "$prefix."
                for (p in properties) {

                    if (isSimpletype(p.returnType.classifier as KClass<*>)) {
                        fields.add(createField(p, fieldKind, prefix))
                    } else {
                        fields.add(createField(p, fieldKind, prefix))

                        fillFields(
                            p.returnType.classifier as KClass<*>,
                            fields,
                            "",
                            p.name,
                            FieldKind.DATA
                        )
                    }
                }
            }
        }

        private fun createField(
            p: KProperty1<out Any, *>,
            fieldKind: FieldKind,
            prefix: String?
        ): Field {

            val field = PropertyDataField(p as KProperty1<Any, *>) { value ->
                var atualValue = value
                val properties = name?.split(".")
                if (properties != null && properties.isNotEmpty()) {
                    for (i in 0..properties.size  - 2) {
                        val property = atualValue!!::class.memberProperties.find { it.name == properties[i] }
                        atualValue = property?.call(atualValue)
                    }
                }

                property?.call(atualValue)
            }
            field.name = if (prefix == null) p.name else "$prefix.${p.name}"
            field.fieldKind = fieldKind
            return field
        }

        private fun isSimpletype(type: KClass<*>): Boolean =
            type == String::class || type == LocalDate::class || type == LocalDateTime::class || type == LocalTime::class || type.javaPrimitiveType != null

        fun createFields(obj: Any, name: String, fieldKind: FieldKind): List<Field> {
            val objClass: KClass<*> = obj::class
            val fields = createFields(objClass, name, fieldKind)
            return fields
        }

    }
}



