package connectfour.chrome

import androidx.compose.runtime.Composable
import org.jetbrains.compose.web.dom.Button
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Text

@Composable
internal fun ConnectFourFixedGameActions(
    onNewGame: () -> Unit,
    onUndoLastMove: () -> Unit,
    undoLastMoveEnabled: Boolean,
) {
    Div(attrs = { classes("connect-four-fixed-game-actions") }) {
        Button(attrs = {
            classes("connect-four-new-game")
            attr("type", "button")
            onClick { onNewGame() }
        }) {
            Text("New game")
        }
        Button(attrs = {
            classes("connect-four-new-game", "connect-four-undo-move")
            attr("type", "button")
            if (!undoLastMoveEnabled) {
                attr("disabled", "true")
            }
            onClick { if (undoLastMoveEnabled) onUndoLastMove() }
        }) {
            Text("Undo last move")
        }
    }
}
