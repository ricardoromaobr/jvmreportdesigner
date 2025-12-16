package myreport.model.data

import kotlin.reflect.KMutableProperty
import kotlin.reflect.KMutableProperty1
import kotlin.reflect.full.*
import kotlin.reflect.typeOf

class FieldBuilder {

    companion object {

        fun createFields(obj: Any, name: String, fieldKind: FieldKind) {
            val rootObjectType = obj::class
             createFields(rootObjectType, name, fieldKind)
        }
    }
}

inline fun <reified T> membersOf() = T::class.declaredMembers.filter { member -> member is KMutableProperty<*> }


