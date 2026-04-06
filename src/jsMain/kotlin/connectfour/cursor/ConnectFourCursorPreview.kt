package connectfour.cursor

import androidx.compose.runtime.Composable
import connectfour.Player
import org.jetbrains.compose.web.dom.Div

@Composable
internal fun ConnectFourCursorPreview(
    x: Double,
    y: Double,
    player: Player,
) {
    Div(attrs = {
        classes("connect-four-cursor-preview")
        attr("style", "left:${x}px;top:${y}px;")
    }) {
        Div(attrs = {
            when (player) {
                Player.One -> classes("connect-four-piece", "connect-four-piece--one")
                Player.Two -> classes("connect-four-piece", "connect-four-piece--two")
            }
        }) { }
    }
}
