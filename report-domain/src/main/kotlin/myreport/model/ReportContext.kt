package myreport.model

import myreport.model.data.IDataSource
import javax.sql.DataSource

class ReportContext {
    val parameters = mutableMapOf<String, String>()
    var dataSource: DataSource? = null
    var currentPageIndex = 0
    var rowIndex = 0
    lateinit var reportMode: ReportMode
    var heightLeftOnCurrentPage = 0f
    var heightUsedOnCurrentPage = 0f
}