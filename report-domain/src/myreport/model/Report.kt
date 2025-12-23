package myreport.model

import myreport.model.PaperSizes.Companion.paperSizes
import myreport.model.controls.Section
import myreport.model.data.Field
import myreport.model.data.IDataSource
import myreport.model.data.ObjectDataSource

class Report {
    var title: String? = null
    var dataScript: String? = null
    val sections: MutableList<Section> = mutableListOf()
    val pages: MutableList<Page> = mutableListOf()
    val parameters: MutableList<Field> = mutableListOf()
    val dataFields: MutableList<Field> = mutableListOf()
    val expressions: MutableList<Field> = mutableListOf()
    val groups: MutableList<Group> = mutableListOf()
    var unit: UnitType = UnitType.PT
    var pageSize: PaperSizeType = PaperSizeType.A4

    // it need to be the same unit of the unit property
    var margin: Thickness = Thickness(28f)

    val height: Float
        get() {
            val paperSize = paperSizes.filter { paperSize -> paperSize.sizeType == pageSize }.first()
            return paperSize.getHeight(unit)
        }

    val width: Float
        get() {
            val paperSize = paperSizes.filter { paperSize -> paperSize.sizeType == pageSize }.first()
            return paperSize.getWidth(unit)
        }

    val widthWithMargin: Float = width + margin.left + margin.right
    val heightWithMargin: Float = height + margin.top + margin.bottom

    var _source: Any? = null

    var dataSource: Any?
        get() = dataSource as? Any
        set(value) {
            _source = value
            if (value != null) {
                val kclass = ObjectDataSource::class
                val constructor = kclass.constructors.first()
                DataSource = constructor.call(value)
                fillFieldsFromDatasource()
            } else
                DataSource = null
        }

    fun addGroup(fieldName: String) {

    }

    internal var DataSource: IDataSource? = null

    fun fillFieldsFromDatasource() {
        dataFields.clear()
        check(DataSource == null, { "DataSouce can't be null while discovering data fields." })
        dataFields.addAll(DataSource!!.discoverFields())
    }

    fun copyToReport(r: Report) {
        r.title = title
        r.dataScript = dataScript
        r.DataSource = DataSource
        r.sections.addAll(sections)
        r.expressions.addAll(expressions)
        r.groups.addAll(groups)
        r.unit = unit
        r.pageSize = pageSize
    }
}




