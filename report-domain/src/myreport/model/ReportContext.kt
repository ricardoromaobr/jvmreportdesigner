package myreport.model

import myreport.model.data.IDataSource
import javax.sql.DataSource

class ReportContext {
    val parameters = mapOf<String, String>()
    lateinit var dataSource: DataSource
    var currentPageIndex = 0
    var rowIndex = 0
    lateinit var reportMode: ReportMode
    var heightLeftOnCurrentPage = 0
    var heightUsedOnCurrentPage = 0
}