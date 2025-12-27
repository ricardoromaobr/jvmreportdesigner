package myreport.model.data

import kotlin.reflect.KClass

class ObjectDataSource<T> : IDataSource {

    private var _current: T? = null
    private var _data: Iterable<T>
    private var _enumerator: Iterator<T>
    private lateinit var _sortingFields: Array<String>
    private val _fields: MutableList<Field> = mutableListOf<Field>()
    private var _nextRes: Boolean
    private var _currentIndex: Int = -1
    var properties: MutableMap<String, Field> = mutableMapOf()

    constructor(data: Iterable<T>) {
        _data = data
        _enumerator = _data.iterator()
        _nextRes = _enumerator.hasNext()
        if (_enumerator.hasNext()) {
            _current = _enumerator.next()
        _currentIndex++
        }
        discoverFields()
    }

    override fun getValue(fieldName: String, format: String): String {
        val field = _fields.find { it.name == fieldName }
        val value = field?.getValue(_current, format)
        return value?.toString() ?: ""
    }

    override val currentRowIndex: Int
        get() = _currentIndex

    override fun applySort(sortingFields: Iterator<String>) {
        TODO("Not yet implemented")
    }

    override fun discoverFields(): Array<Field> {
        if (_current == null) return emptyArray()
        _fields.clear()
        _fields.addAll(FieldBuilder.createFields(_current as Any, "", FieldKind.DATA))
        _fields.forEach {
            properties[it.name!!] = it
        }
        return _fields.toTypedArray()
    }

    override fun containsField(fieldName: String): Boolean {
        return _fields.any { it.name == fieldName }
    }

    val current: Any?
        get() = _current

    fun reset() {
        _enumerator = _data.iterator()
        _nextRes = _enumerator.hasNext()
        _current = _enumerator.next()
        _currentIndex = 0
    }

    override fun moveNext(): Boolean {

        if (_nextRes) {
            _nextRes = _enumerator.hasNext()
            _current = _enumerator.next()
        }
        return _nextRes
    }

    val hasNext get() = _nextRes
}