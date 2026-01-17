package  myreport.designer


import javafx.application.Application
import javafx.application.Application.launch
import javafx.event.EventHandler
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.control.*
import javafx.scene.image.ImageView
import javafx.scene.input.KeyEvent
import javafx.scene.layout.StackPane
import javafx.scene.layout.VBox
import javafx.stage.Stage

class App : Application() {
    override fun start(stage: Stage) {
        stage.title = "MyReport Designer"
        stage.scene = Scene(createContent())
        stage.width = 1024.0
        stage.height = 800.0

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

            println ("Key pressed: $event")

            when (event.code.code) {
                27 -> stage.close()
                10 -> println ("Pressed  <enter>")
                else ->  println("Unrecognized key")

            }
        }


        stage.show()
    }

    private fun createContent(): Parent {


        return StackPane().apply {
            children.addAll(addContents())
        }
    }

    fun addContents() : Parent {

        val mainVBox = VBox()
        mainVBox.children.addAll(createAMenuBar())
        val tabPane = TabPane()

        val tabDesigner = Tab("Designer")
        val tabPreview = Tab("Preview")
        val tabScript = Tab("Script")
        tabPane.tabs.add(tabDesigner)
        tabPane.tabs.add(tabPreview)
        tabPane.tabs.add(tabScript)

        // designer
        val vbox = VBox()
        vbox.children.addAll( createToolBar())
        tabDesigner.content = vbox

        mainVBox.children.add(  tabPane)

        return mainVBox

    }

    fun createToolBar(): ToolBar {
        val toggleGroup = ToggleGroup()
        val toolBar = ToolBar()

        var toggle = ToggleButton()
        var input = javaClass.getResourceAsStream ("/images/ToolText.png")
        var image = javafx.scene.image.Image(input)
        var viewImage = ImageView(image)
        toggle.graphic = viewImage
        toggle.tooltip = Tooltip("TextBlock")
        toggle.toggleGroup = toggleGroup
        toolBar.items.addAll(toggle)

        toggle = ToggleButton()
        input = javaClass.getResourceAsStream ("/images/ToolImage.png")
        image = javafx.scene.image.Image(input)
        viewImage = ImageView(image)
        toggle.graphic = viewImage
        toggle.tooltip = Tooltip("Image")
        toggle.toggleGroup = toggleGroup
        toolBar.items.addAll(toggle)

        toggle = ToggleButton()
        input = javaClass.getResourceAsStream ("/images/ToolLineH.png")
        image = javafx.scene.image.Image(input)
        viewImage = ImageView(image)
        toggle.graphic = viewImage
        toggle.tooltip = Tooltip("Horizontal Line")
        toggle.toggleGroup = toggleGroup
        toolBar.items.addAll(toggle)

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
}

fun main(args: Array<String>): Unit {
   launch(App::class.java, *args)
}