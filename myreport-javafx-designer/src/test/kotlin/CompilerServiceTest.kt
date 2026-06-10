import myreport.designer.services.CompilerService
import org.junit.jupiter.api.Test
import kotlin.reflect.KProperty1
import kotlin.reflect.full.memberProperties

class CompilerServiceTest {

    @Test
    fun evaluate() {
        val code = """                     
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

        val datasourceScript = """
            datasource = listOf(
                object {
                    val name = "ricardo romao soares"
                    val idade = 50
                }, 
                object {
                    val name = "Rogéria Silva"
                    val idade = 45
                }
            )
        """.trimIndent()

        val compilerService = CompilerService(code)

        try {
            val a = compilerService.evaluate(datasourceScript)
            println(a)
            val  result = compilerService.result as Array<*>

            var datasource = result[0] as List<*>

            datasource.forEach { data ->
                val  p = data!!::class.memberProperties.find { it.name == "name" }
                val p1 = p as KProperty1<Any, Any>
                println(p1.get(data))
            }


        } catch (e: Exception) {
            println(e.message)
        }
    }
}