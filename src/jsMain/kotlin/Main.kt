import connectfour.app.ConnectFourApp
import org.jetbrains.compose.web.renderComposable

fun main() {
    renderComposable(rootElementId = "root") {
        ConnectFourApp()
    }
}
