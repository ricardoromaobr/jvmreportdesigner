import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicText
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import myreport.model.data.membersOf
import org.jetbrains.skia.Paint
import org.jetbrains.skia.Surface

@Composable
@Preview
fun App() {
    MaterialTheme {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            BasicText("Hello, World!")
            BasicText("outro")

            Canvas (modifier = Modifier
                .size(100.dp)
                .background(color = Color.Cyan)) {


                drawIntoCanvas { canvas ->
                    val skikoCanvas = canvas.nativeCanvas
                    skikoCanvas.drawCircle(50f,50f,20f, Paint().apply {
                        setStroke(true)
                        strokeWidth = 2f
                        color = org.jetbrains.skia.Color.MAGENTA
                    })

                }

            }

        }
    }
}

fun main() = application {

    var members = membersOf<Person>()

    Window(onCloseRequest = ::exitApplication) {
        App()
    }
}

class Person {
    var nome: String? = null
    var age: Int = 0
}