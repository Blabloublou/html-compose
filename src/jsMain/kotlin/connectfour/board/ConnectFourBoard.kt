package connectfour.board

import androidx.compose.runtime.Composable
import connectfour.BoardState
import connectfour.GameConfig
import connectfour.Player
import org.jetbrains.compose.web.dom.Button
import org.jetbrains.compose.web.dom.Div

@Composable
internal fun ConnectFourBoard(
    boardState: BoardState,
    config: GameConfig,
    showCursorPreview: Boolean,
    onCursorEnter: () -> Unit,
    onCursorLeave: () -> Unit,
    onCursorMove: (Double, Double) -> Unit,
    onColumnClick: (Int) -> Unit,
) {
    Div(attrs = {
        classes("connect-four-board-wrap")
        if (showCursorPreview) {
            classes("connect-four-board-wrap--hide-cursor")
        }
        onMouseEnter { onCursorEnter() }
        onMouseLeave { onCursorLeave() }
        onMouseMove { event ->
            onCursorMove(event.clientX.toDouble(), event.clientY.toDouble())
        }
    }) {
        Div(attrs = {
            classes("connect-four-board")
            attr("style", boardGridStyle(config.cols))
        }) {
            for (col in 0 until config.cols) {
                Button(attrs = {
                    classes("connect-four-column")
                    attr("type", "button")
                    attr("aria-label", "Column ${col + 1}, drop a piece")
                    onClick { onColumnClick(col) }
                }) {
                    for (row in 0 until config.rows) {
                        ConnectFourCell(piece = boardState.cell(row, col))
                    }
                }
            }
        }
    }
}

private fun boardGridStyle(cols: Int): String =
    "grid-template-columns:repeat($cols, minmax(0, 1fr));"

@Composable
private fun ConnectFourCell(piece: Player?) {
    Div(attrs = { classes("connect-four-cell") }) {
        if (piece != null) {
            Div(attrs = {
                when (piece) {
                    Player.One -> classes("connect-four-piece", "connect-four-piece--one")
                    Player.Two -> classes("connect-four-piece", "connect-four-piece--two")
                }
            }) { }
        } else {
            Div(attrs = { classes("connect-four-hole") }) { }
        }
    }
}
