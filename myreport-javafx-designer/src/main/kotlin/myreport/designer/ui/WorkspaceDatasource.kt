package myreport.designer.ui

import javafx.application.Platform
import javafx.scene.Parent
import javafx.scene.control.Button
import javafx.scene.control.TextArea
import javafx.scene.control.ToolBar
import javafx.scene.layout.Region
import javafx.scene.layout.VBox
import myreport.designer.services.CompilerService

class WorkspaceDatasource : Region {
    private val _compiler: CompilerService
    private val _datasourceScriptEditor: TextArea
    private val _datasourceScriptErrors: TextArea

    constructor(script: String, compiler: CompilerService) : super() {
        heightProperty().addListener { _, _, newValue ->
            _datasourceScriptEditor.prefHeight = newValue.toDouble() / 2
        }

        widthProperty().addListener { _, _, newValue ->
            _datasourceScriptEditor.prefWidth = newValue.toDouble()
        }

        _datasourceScriptErrors = TextArea()
        _datasourceScriptEditor = TextArea(script)
        _compiler = compiler

        children.addAll(VBox(_datasourceScriptEditor, _datasourceScriptErrors, createToolbar()))
    }

    private fun createToolbar(): Parent {
        val toolBar = ToolBar()
        val buttonEvaluate = Button("Evaluate")
        buttonEvaluate.setOnAction {
            
            _datasourceScriptErrors.text = "Compiling ..."
            Platform.runLater {
                try {
                    var result = _compiler.evaluate(_datasourceScriptEditor.text)
                    if (result) {
                        _datasourceScriptErrors.text = ""
                        _datasourceScriptErrors.text = "Compilation succeeded!"
                    }
                    scriptEvaluated(_compiler.result!!)
                } catch (e: Exception) {
                    _datasourceScriptErrors.text = e.localizedMessage
                }
            }
        }

        toolBar.items.add(buttonEvaluate)
        return toolBar
    }

    val datasource: Array<Any>?
        get() {
            val compiled = _compiler.evaluate(_datasourceScriptEditor.text)
            if (compiled)
                return _compiler.result
            return null
        }

    var scriptEvaluated: (result: Array<Any>) -> Unit = {}

    fun evaluate() {
        _compiler.evaluate(_datasourceScriptEditor.text)
        scriptEvaluated(_compiler.result!!)
    }
}