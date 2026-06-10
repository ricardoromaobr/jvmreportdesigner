package myreport.designer.services

import java.io.File
import java.net.URLClassLoader
import javax.script.ScriptEngine
import javax.script.ScriptEngineManager

class CompilerService {

    lateinit var codeTemplate: String

    constructor(codeTemplate: String) {
        this.codeTemplate = codeTemplate
    }

    private var _result: Array<Any>? = null

    val result: Array<Any>?
        get() = _result

    fun createScriptEngine() : ScriptEngine {
        // Pass the current thread classloader to ensure Maven dependencies are visible
        val manager = ScriptEngineManager(Thread.currentThread().contextClassLoader)
        val engine = manager.getEngineByExtension("kts")
            ?: throw IllegalStateException("Kotlin scripting engine not found")

        return engine
    }

    fun evaluate(datasourceStript: String) : Boolean {
        if (codeTemplate.isBlank())
            throw Exception("Code template is empty")

        if (datasourceStript.isBlank())
            throw Exception("Datasource stript is empty")

        val code = codeTemplate.format(datasourceStript)

        val engine = createScriptEngine()

        _result = engine.eval(code) as Array<Any>?

        return true
    }
}