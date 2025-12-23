package myreport.model

interface IExportToPdfService {
    fun exportToPdf(path: String, pages: List<Page>, reportRenderer: IReportRenderer): Array<Byte>
}