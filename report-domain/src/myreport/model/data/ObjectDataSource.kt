package myreport.model.data

import kotlin.reflect.KClass

class ObjectDataSource<T> : IDataSource {

    override fun getValue(fieldName: String, format: String) {
        TODO("Not yet implemented")
    }

    override val currentRowIndex: Int

    override fun applySort(sortingFields: Iterator<String>) {
        TODO("Not yet implemented")
    }

    override fun discoverFields(): Array<Field> {
        TODO("Not yet implemented")
    }

    override fun containsField(fieldName: String) {
        TODO("Not yet implemented")
    }
}