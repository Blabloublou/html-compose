package connectfour.modals

import androidx.compose.runtime.Composable
import connectfour.GameOver
import connectfour.Player
import org.jetbrains.compose.web.dom.Button
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.H2
import org.jetbrains.compose.web.dom.P
import org.jetbrains.compose.web.dom.Text

@Composable
internal fun ConnectFourGameOverModal(
    gameOver: GameOver,
    onReplay: () -> Unit,
    onNewGameSettings: () -> Unit,
) {
    val (title, subtitle) = when (gameOver) {
        is GameOver.Win -> Pair(
            if (gameOver.winner == Player.One) {
                "Red player wins"
            } else {
                "Yellow player wins"
            },
            null,
        )
        is GameOver.Draw -> Pair("Draw", "The board is full with no winner.")
    }

    Div(attrs = {
        classes("connect-four-modal-backdrop", "connect-four-modal-backdrop--gameover")
    }) {
        Div(attrs = {
            classes("connect-four-modal", "connect-four-modal--gameover")
            attr("role", "dialog")
            attr("aria-modal", "true")
            attr("aria-labelledby", "connect-four-gameover-title")
        }) {
            H2(attrs = {
                classes("connect-four-modal-title", "connect-four-gameover-title")
                attr("id", "connect-four-gameover-title")
            }) {
                Text(title)
            }

            subtitle?.let { line ->
                P(attrs = { classes("connect-four-modal-hint", "connect-four-gameover-subtitle") }) {
                    Text(line)
                }
            }

            Div(attrs = { classes("connect-four-modal-actions", "connect-four-modal-actions--split") }) {
                Button(attrs = {
                    classes("connect-four-modal-btn", "connect-four-modal-btn--secondary")
                    attr("type", "button")
                    onClick { onNewGameSettings() }
                }) {
                    Text("Settings")
                }
                Button(attrs = {
                    classes("connect-four-modal-btn", "connect-four-modal-btn--primary")
                    attr("type", "button")
                    onClick { onReplay() }
                }) {
                    Text("Play again")
                }
            }
        }
    }
}
