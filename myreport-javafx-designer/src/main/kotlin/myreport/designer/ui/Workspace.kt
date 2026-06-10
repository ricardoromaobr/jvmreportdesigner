package myreport.designer.ui

import javafx.geometry.Insets
import javafx.scene.Parent
import javafx.scene.control.Label
import javafx.scene.control.Menu
import javafx.scene.control.MenuBar
import javafx.scene.control.MenuItem
import javafx.scene.control.Tab
import javafx.scene.control.TabPane
import javafx.scene.control.ToolBar
import javafx.scene.layout.Priority
import javafx.scene.layout.Region
import javafx.scene.layout.VBox
import javafx.stage.Stage
import myreport.designer.myreport.designer.ui.ReportDesigner
import myreport.designer.services.CompilerService
import myreport.designer.services.DesignService
import myreport.designer.services.ToolBoxService
import myreport.designer.services.WorkspaceService
import myreport.designer.tools.ImageTool
import myreport.designer.tools.LineTool
import myreport.designer.tools.LineToolH
import myreport.designer.tools.LineToolV
import myreport.designer.tools.RectTool
import myreport.designer.tools.SectionTool
import myreport.designer.tools.SubreportTool
import myreport.designer.tools.TextBlockTool
import myreport.designer.tools.ZoomTool
import myreport.designer.ui.widgets.propertygrid.PropertyGrid
import myreport.model.Color
import myreport.model.PaperSizeType
import myreport.model.Report
import myreport.model.controls.DetailSection
import myreport.model.controls.PageFooterSection
import myreport.model.controls.PageHeaderSection
import myreport.model.controls.ReportHeaderSection
import myreport.model.engine.ReportEngine
import myreport.renderer.RegisterDefaultRenderers
import myreport.renderer.ReportRenderer

class Workspace : Region {
    private val code = """                     
           class GenerateDatasource {
                fun generate() : Any {
                
                    var datasource: Any? = null
                    
                    val parameters = mutableMapOf<String, Any>()
                    
                    %s  // datasource script came here 
                    
                    return arrayOf(datasource, parameters)
                }
           }      
                 
           var generator = GenerateDatasource()
           generator.generate()
        """.trimIndent()

    private val datasourceScript = """
            class Person {
                var name: String = ""
                var age: Int = 0
            }
        
            datasource = listOf(
                Person().apply {
                    name = "ricardo romao soares"
                    age = 50
                }, 
                Person().apply {
                    name = "Rogéria Silva"
                    age = 45
                }
            )
        """.trimIndent()

    val _stage: Stage
    var dpi: Float = 72f   // 72 dpi, pdf unit point

    val _toolBoxService = ToolBoxService()

    val _compilerService = CompilerService(code)
    val _report = Report().apply {
        paperSizeType = PaperSizeType.A4
    }

    // tabs of the workspace
    val _tabDesigner = Tab("Designer")
    val _tabPreview = Tab("Preview")
    val _tabScript = Tab("Datasource")
    val _workspaceService = WorkspaceService()
    val _designService = DesignService(_workspaceService, _compilerService, _report)

    val _reportDesigner = ReportDesigner().apply {
        prefWidth = (_report.width).toDouble()
        prefHeight = (_report.height).toDouble()
        this.designerService = _designService
        this.report = _report
        this.workspaceService = _workspaceService
        designerService.zoom = 1.0
        this.dpi = dpi
    }

    val _tabPane = TabPane()
    val _statusBar = ToolBar()
    val _statusBarText = Label()
    val _propertyGrid: PropertyGrid
    val _workspaceDatasource: WorkspaceDatasource
    val _workspaceReportPreview: WorkspaceReportPreview
    val _workspaceDesigner: WorkspaceDesigner
    val _menuBar: MenuBar
    val _toolBar: ToolBar

    constructor(stage: Stage) {
        _stage = stage
        //_tabPane.prefHeightProperty().bind(stage.heightProperty())
        _workspaceDatasource = WorkspaceDatasource(datasourceScript, _compilerService)
        _workspaceReportPreview = WorkspaceReportPreview(_report)


        _menuBar = createAMenuBar()
        montaToolboxService()
        _designService.toolBoxService = _toolBoxService
        _toolBar = createToolBar()

        _propertyGrid = PropertyGrid()
        _workspaceDesigner =
            WorkspaceDesigner(
                _designService,
                _compilerService,
                _workspaceService,
                _report,
                _propertyGrid,
                _toolBar,
                _reportDesigner
            )
        _workspaceDesigner.prefWidthProperty().bind(stage.widthProperty())

        _workspaceService.workspaceDesigner = _workspaceDesigner
        _workspaceService.workspacePreview = _workspaceReportPreview
        _workspaceService.statusBarText = _statusBarText
        _workspaceService.stage = _stage
        //_workspaceDatasource.evaluate()
        _report.dataSource = _workspaceDatasource.datasource!![0] as List<*>

        _workspaceDatasource.scriptEvaluated = {
            _workspaceDesigner.updateReportData(it)
        }

        _workspaceDesigner.updateReportData(_workspaceDatasource.datasource!!)

        val control = createContent(_stage)
        montaTabs()
        addDefaultSection()
        children.add(control)

        _designService.isDesign = true
        _designService.initReport()


    }

    fun montaToolboxService() {
        _toolBoxService.addTool(ZoomTool(_designService))
        _toolBoxService.addTool(LineTool(_designService))
        _toolBoxService.addTool(LineToolV(_designService))
        _toolBoxService.addTool(LineToolH(_designService))

        _toolBoxService.addTool(TextBlockTool(_designService))
        _toolBoxService.addTool(SubreportTool(_designService))
        _toolBoxService.addTool(SectionTool(_designService))
        _toolBoxService.addTool(ImageTool(_designService))
        _toolBoxService.addTool(RectTool(_designService))
    }

    private fun createContent(stage: Stage): Parent {
        return addContents(stage)
    }

    fun addContents(stage: Stage): Parent {
        val mainVBox = VBox()
        mainVBox.setPadding(Insets(0.0, 0.0, 20.0, 0.0)); // Extra 20px padding at the bottom

        mainVBox.prefWidthProperty().bind(stage.widthProperty())
        mainVBox.prefHeightProperty().bind(stage.heightProperty())
        _statusBarText.text = "Status Bar"
        _statusBar.items.add(_statusBarText)

        VBox.setVgrow(_tabPane, Priority.ALWAYS)
        //_tabPane.prefHeight = stage.height - _statusBar.height - _menuBar.height
        mainVBox.children.addAll(_menuBar, _tabPane, _statusBar)
        mainVBox.requestLayout()
        return mainVBox
    }

    private fun montaTabs() {
        _tabPane.tabs.add(_tabDesigner)
        _tabPane.tabs.add(_tabPreview)
        _tabPane.tabs.add(_tabScript)

        _tabDesigner.content = _workspaceDesigner
        _tabPreview.content = _workspaceReportPreview
        _tabScript.content = _workspaceDatasource

        _tabDesigner.isClosable = false
        _tabPreview.isClosable = false
        _tabScript.isClosable = false

        _tabPane.selectionModel.selectedItemProperty().addListener { _, _, tab ->
            when (tab) {
                _tabDesigner -> {
                    _designService.isDesign = true
                }

                _tabScript -> {}

                else -> {
                    _designService.isDesign = false
                    val reportRenderer = ReportRenderer()
                    reportRenderer.context = _workspaceReportPreview.reportPreview.g2d
                    _report.pages.clear()
                    val reportEngine = ReportEngine(_report, reportRenderer)
                    RegisterDefaultRenderers().addRenderer(reportRenderer)
                    _workspaceReportPreview.reportPreview.reportRenderer = reportRenderer
                    _workspaceReportPreview.reportEngine = reportEngine
                    _designService.refreshDataFieldsFromDataSource()
                    _workspaceReportPreview.reportEngine.process()
                    _workspaceReportPreview.numberOfPage = _designService.report?.pages!!.size
                    _workspaceReportPreview.setPage(1)
                    _workspaceReportPreview.invalidate()
                }
            }
        }
    }

    private fun addDefaultSection() {
        val reportHeader = ReportHeaderSection().apply {
            width = _report.width
            height = 100f
            backgroundColor = Color(255f, 255f, 255f, 255f)
        }
        val pageHeader = PageHeaderSection().apply {
            width = _report.width
            height = 100f
            backgroundColor = Color(255f, 255f, 255f, 255f)
        }
        val detail = DetailSection().apply {
            width = _report.width
            height = 100f
            backgroundColor = Color(255f, 255f, 255f, 255f)
        }
        val footPage = PageFooterSection().apply {
            width = _report.width
            height = 100f
            backgroundColor = Color(255f, 255f, 255f, 255f)
        }

        _report.sections.add(reportHeader)
        _report.sections.add(pageHeader)
        _report.sections.add(detail)
        _report.sections.add(footPage)

        _propertyGrid.setObject(detail)
    }


    fun createToolBar(): ToolBar {

        val toolBar = ToolBar()
        _toolBoxService.buildToolBar(toolBar)

        return toolBar
    }

    fun createAMenuBar(): MenuBar {
        val menuBar = MenuBar()
        val menu = Menu("File")
        menuBar.menus.add(menu)
        val menuItem1 = MenuItem("Save report")
        val menuItem2 = MenuItem("Load report")
        menu.items.add(menuItem1)
        menu.items.add(menuItem2)

        val os = System.getProperty("os.name")
        if (os.startsWith("Mac"))
            menuBar.useSystemMenuBarProperty().set(true)

        return menuBar
    }

    fun keyPressed(keyEvent: javafx.scene.input.KeyEvent) {
        _designService.keyPressed(keyEvent)
    }

}