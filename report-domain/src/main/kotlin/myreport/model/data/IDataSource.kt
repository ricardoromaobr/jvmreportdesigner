package myreport.model.data


interface IDataSource  {
    fun getValue(fieldName: String, format: String): String
    val currentRowIndex: Int
    fun applySort (sortingFields: Iterator<String>)
    fun discoverFields() : Array<Field>
    fun containsField(fieldName: String): Boolean
    fun moveNext(): Boolean
    fun reset()
}