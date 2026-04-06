package connectfour.chrome

import androidx.compose.runtime.Composable
import org.jetbrains.compose.web.dom.Button
import org.jetbrains.compose.web.dom.Text

@Composable
internal fun ConnectFourNewGameButton(onNewGame: () -> Unit) {
    Button(attrs = {
        classes("connect-four-new-game", "connect-four-new-game--fixed")
        attr("type", "button")
        onClick { onNewGame() }
    }) {
        Text("New game")
    }
}
