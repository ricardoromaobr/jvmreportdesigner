package  myreport.designer


import javafx.application.Application
import javafx.application.Application.launch
import javafx.event.EventHandler
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.control.*
import javafx.scene.image.ImageView
import javafx.scene.input.KeyEvent
import javafx.scene.layout.HBox
import javafx.scene.layout.Priority
import javafx.scene.layout.StackPane
import javafx.scene.layout.VBox
import javafx.stage.Stage
import myreport.designer.myreport.designer.ui.ReportDesigner
import myreport.designer.services.CompilerService
import myreport.designer.services.DesignService
import myreport.designer.services.ToolBoxService
import myreport.designer.services.WorkspaceService
import myreport.designer.ui.Workspace
import myreport.designer.ui.widgets.propertygrid.PropertyGrid
import myreport.model.Color
import myreport.model.PaperSizeType
import myreport.model.Report
import myreport.model.controls.DetailSection
import myreport.model.controls.PageFooterSection
import myreport.model.controls.PageHeaderSection
import myreport.model.controls.ReportHeaderSection
import myreport.renderer.RegisterDefaultRenderers
import myreport.renderer.ReportRenderer
import org.openpdf.text.PageSize

class App : Application() {


    override fun start(stage: Stage) {
        stage.title = "MyReport Designer"

        val workspace = Workspace(stage)
        stage.scene = Scene(workspace, 1200.0, 800.0)
        // events
        stage.onCloseRequest = EventHandler { event ->
            println("Close Request")
        }

        stage.onHiding = EventHandler { event ->
            println("Hiding Request")
        }

        stage.onHidden = EventHandler { event ->
            println("Hidden Request")
        }

        stage.onShowing = EventHandler { event ->
            println("Showing Request")
        }

        stage.onShown = EventHandler { event ->
            println("Shown Request")
        }


        stage.addEventHandler(KeyEvent.KEY_PRESSED) { event ->
            println("Key pressed: $event")
            workspace.keyPressed(event)
        }




        stage.show()
    }

}

fun main(args: Array<String>): Unit {
    launch(App::class.java, *args)
}