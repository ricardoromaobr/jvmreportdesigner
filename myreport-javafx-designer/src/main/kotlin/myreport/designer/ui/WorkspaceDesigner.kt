package myreport.designer.ui

import javafx.scene.Group
import javafx.scene.control.ScrollPane
import javafx.scene.control.ToolBar
import javafx.scene.control.TreeCell
import javafx.scene.control.TreeItem
import javafx.scene.control.TreeView
import javafx.scene.input.ClipboardContent
import javafx.scene.input.DataFormat
import javafx.scene.input.Dragboard
import javafx.scene.input.TransferMode
import javafx.scene.layout.HBox
import javafx.scene.layout.Priority
import javafx.scene.layout.Region
import javafx.scene.layout.VBox
import javafx.util.Callback
import kotlinx.serialization.Serializable
import myreport.designer.myreport.designer.ui.ReportDesigner
import myreport.designer.services.CompilerService
import myreport.designer.services.DesignService
import myreport.designer.services.WorkspaceService
import myreport.designer.ui.widgets.propertygrid.PropertyGrid
import myreport.model.Report
import myreport.model.data.Field
import myreport.model.data.FieldBuilder
import myreport.model.data.FieldKind
import java.awt.Point

val DRAG_REPORT_DATA_FORMAT = DataFormat("Application/x-report-data")

class WorkspaceDesigner : Region {
    private val _reportDesigner: ReportDesigner
    private var _designerService: DesignService
    private var _workspaceService: WorkspaceService
    private var _report: myreport.model.Report

    private val _propertyGrid: PropertyGrid
    private val _toolBar: ToolBar
    private val _compilerService: CompilerService
    private val _workspaceArea: VBox
    private var _treeView: TreeView<ReportItem>? = null

    constructor(
        designerService: DesignService,
        compilerService: CompilerService,
        workspaceService: WorkspaceService,
        report: Report,
        propertyGrid: PropertyGrid,
        toolBar: ToolBar,
        reportDesigner: ReportDesigner
    ) : super() {

        _designerService = designerService
        _workspaceService = workspaceService
        _report = report
        _propertyGrid = propertyGrid
        _toolBar = toolBar
        _compilerService = compilerService
        _reportDesigner = reportDesigner
        _workspaceArea = VBox()
        children.add(createControls())

        heightProperty().addListener { _, _, newValue ->
            _workspaceArea.prefHeight = newValue.toDouble()
        }

        widthProperty().addListener { _, _, newValue ->
            _workspaceArea.prefWidth = newValue.toDouble()
        }

        _reportDesigner.onDragOver = {
            if (it.dragboard.hasString())
                it.acceptTransferModes(TransferMode.COPY)
            it.consume()
        }

        //
        _reportDesigner.onDragDropped = {
            val dropedSection = _designerService.getSectionViewByXY(it.x.toFloat(), it.y.toFloat())
            if (dropedSection == null) {
                it.isDropCompleted = false
            } else {
                val fieldName = it.dragboard.string
                val draggedField = field(fieldName)
                if (draggedField != null) {
                    if (draggedField is Field) {
                        _designerService.startPressPoint = Point(it.x.toInt(), it.y.toInt())
                        _designerService.createTextBlockAtXY(draggedField.name!!,
                            draggedField.name!!, draggedField.fieldKind,
                            it.x.toFloat(), it.y.toFloat())
                    }
                    it.isDropCompleted = true
                    it.consume()
                    invalidate()
                }

            }
        }

    }

    private fun field(fieldName: String?): Field? {
        var treeItem = _treeView?.root?.children?.filter {
            it.value.description == "Data" }?.first()

        treeItem = treeItem?.children?.filter { it.value.field?.name == fieldName }?.firstOrNull()

        if (treeItem == null) {
            treeItem = _treeView?.root?.children?.filter {
                it.value.description == "Parameters" }?.first()
            treeItem = treeItem?.children?.filter { it.value.field?.name == fieldName }?.firstOrNull()

            if (treeItem == null) {
                treeItem = _treeView?.root?.children?.filter {
                    it.value.description == "Expressions" }?.first()
                treeItem = treeItem?.children?.filter { it.value.field?.name == fieldName }?.firstOrNull()
            }
        }
        val draggedField = treeItem?.value?.field
        return draggedField
    }

    private fun createControls(): VBox {
        // designer
        val scrollPane = ScrollPane(Group(_reportDesigner))
        HBox.setHgrow(scrollPane, Priority.ALWAYS)
        _treeView = createTreeViewToShowFieldsOfTheReportDatasource()
        val reportMetadata = VBox()
        reportMetadata.children.addAll(_treeView, _propertyGrid)
        val reportDesignerArea = HBox(scrollPane, reportMetadata)
        VBox.setVgrow(reportDesignerArea, Priority.ALWAYS)
        _workspaceArea.children.addAll(_toolBar, reportDesignerArea)
        return _workspaceArea
    }

    private fun createTreeViewToShowFieldsOfTheReportDatasource(): TreeView<ReportItem> {

        val treeView = TreeView<ReportItem>()
        val rootItem = ReportItem("Report")
        val dataItem = ReportItem("Data")
        val parametersItem = ReportItem("Parameters")
        val expressionsItem = ReportItem("Expressions")
        val groupsItem = ReportItem("Groups")
        val imageItem = ReportItem("Images")


        treeView.root = TreeItem(rootItem)
        treeView.root.children.addAll(
            TreeItem(dataItem),
            TreeItem(parametersItem),
            TreeItem(expressionsItem),
            TreeItem(groupsItem),
            TreeItem(imageItem)
        )

        var item = treeView.root.children.filter { it.value.description == "Expressions" }.first()
        createExpressionsFields(item)

        treeView.cellFactory = Callback {
            object : TreeCell<ReportItem>() {
                override fun updateItem(item: ReportItem?, empty: Boolean) {
                    super.updateItem(item, empty)
                    if (item?.field == null) {
                        text = item?.description
                        onDragDetected = null
                    } else {
                        text = item.field?.name
                        onDragDetected = {
                            val db: Dragboard = startDragAndDrop(TransferMode.COPY)
                            val content = ClipboardContent()
                            content.putString(item.field?.name)
                            db.setContent(content)
                            it.consume()
                        }
                    }
                }

            }
        }

        treeView.root.isExpanded = true

        return treeView
    }

    fun invalidate() {
        _reportDesigner.invalidate()
    }

    fun showInPropertGrid(control: Any) {
        _propertyGrid.setObject(control)
    }

    @Serializable
    class ReportItem(val description: String) {

        var field: Field? = null
    }

    fun updateReportData(data: Array<Any>) {
        _report.dataSource = data[0] as List<*>

        var treeItem: TreeItem<ReportItem>? = null

        val rootItem = _treeView?.root
        for (item in rootItem?.children!!) {
            if (item.value.description == "Data") {
                treeItem = item
                break
            }
        }


        treeItem?.children!!.clear()

        _report.dataFields.forEach {
            treeItem.children.add(TreeItem(ReportItem(it.name!!).apply { field = it }))
        }


        treeItem = rootItem.children.filter { it.value.description == "Parameters" }.first()
        treeItem.children.clear()
        val parameters = data[1] as Map<*, *>

        parameters.forEach {
            val newField = FieldBuilder.createFields(it.value!!, it.key.toString(), FieldKind.PARAMETER).single()
            treeItem.children.add(TreeItem(ReportItem(newField.name!!).apply { field = newField }))
        }


        layoutChildren()
    }

    fun createExpressionsFields(treeItem: TreeItem<ReportItem>) {
        var pageNumberField = FieldBuilder.createFields(0, "#PageNumber", FieldKind.EXPRESSION).single();
        pageNumberField.name = "#PageNumber";
        var numberOfPagesField = FieldBuilder.createFields(0, "#NumberOfPages", FieldKind.EXPRESSION).single();
        numberOfPagesField.name = "#NumberOfPages";
        var rowNumberField = FieldBuilder.createFields(0, "#RowNumber", FieldKind.EXPRESSION).single();
        rowNumberField.name = "#RowNumber";

        treeItem.children.addAll(
            TreeItem(ReportItem(pageNumberField.name!!).apply { field = pageNumberField }),
            TreeItem(ReportItem(numberOfPagesField.name!!).apply { field = numberOfPagesField }),
            TreeItem(ReportItem(rowNumberField.name!!).apply { field = rowNumberField })
        )
    }

}