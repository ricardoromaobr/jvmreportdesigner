package myreport.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

import myreport.model.PaperSizes.Companion.paperSizes
import myreport.model.controls.DetailSection
import myreport.model.controls.PageHeaderSection
import myreport.model.controls.ReportHeaderSection
import myreport.model.controls.Section
import myreport.model.data.Field
import myreport.model.data.IDataSource
import myreport.model.data.ObjectDataSource


@Serializable
class Report {
    var title: String? = null
    var dataScript: String? = null

    val sections: MutableList<Section> = mutableListOf()

    @Transient
    val pages: MutableList<Page> = mutableListOf()

    val parameters: MutableList<Field> = mutableListOf()
    val dataFields: MutableList<Field> = mutableListOf()
    val expressions: MutableList<Field> = mutableListOf()
    val groups: MutableList<Group> = mutableListOf()
    var unit: UnitType = UnitType.PT

    var paperSizeType: PaperSizeType = PaperSizeType.A4
        set(value) {
            field = value

            if (value != PaperSizeType.CUSTOM_SIZE)
                paperSize = paperSizes.first { paperSize -> paperSize.paperSizeType == paperSizeType }
        }

    private var paperSize: PaperSize? = null

    /**
     * Set a custom paperSize
     */
    fun setCustomPaperSize(paperSize: PaperSize) {
        this.paperSize = paperSize
    }

    /**
     *   The margin ot the report page
     *   it need to be the same unit of the unit property
     */
    var margin: Thickness = Thickness(28f)

    /**
     *  The height of the report in the report unit
     */
    val height: Float
        get() = paperSize?.getHeight(unit)!!

    /**
     *  The widht of the report in the report unit
     */
    val width: Float
        get() = paperSize?.getWidth(unit)!!

    /**
     *  Width with the margin
     */
    val widthWithMargin: Float
        get() = width + margin.left + margin.right

    /**
     *  height with the margin
     */
    val heightWithMargin: Float
        get() = height + margin.top + margin.bottom

    @Transient
    private var _source: Any? = null

    var dataSource: Any?
        get() = _source as? Any
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

    @Transient
    internal var DataSource: IDataSource? = null

    fun fillFieldsFromDatasource() {
        dataFields.clear()
        check(DataSource != null) { "DataSouce can't be null while discovering data fields." }
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
        r.paperSizeType = paperSizeType
        r.parameters.addAll(parameters)
    }

    init {
        var section: Section = ReportHeaderSection()
        sections.add(section)
        section = PageHeaderSection()
        sections.add(section)
        section = DetailSection()
        sections.add(section)
    }
}




