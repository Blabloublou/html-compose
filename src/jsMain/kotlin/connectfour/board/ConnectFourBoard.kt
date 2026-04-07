package connectfour.board

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import connectfour.BoardState
import connectfour.Player
import kotlinx.browser.window
import org.jetbrains.compose.web.dom.Button
import org.jetbrains.compose.web.dom.Div

private const val DROP_MS_PER_ROW = 32
private const val DROP_MS_SETTLE = 72

@Composable
internal fun ConnectFourBoard(
    boardState: BoardState,
    pendingDrop: PendingDrop?,
    showCursorPreview: Boolean,
    onCursorEnter: () -> Unit,
    onCursorLeave: () -> Unit,
    onCursorMove: (Double, Double) -> Unit,
    onColumnClick: (Int) -> Unit,
    onDropAnimationEnd: () -> Unit,
) {
    val cfg = boardState.config
    var fallRow by remember { mutableStateOf<Int?>(null) }
    val onEnd by rememberUpdatedState(onDropAnimationEnd)

    DisposableEffect(pendingDrop) {
        val drop = pendingDrop
        if (drop == null) {
            fallRow = null
            return@DisposableEffect onDispose { }
        }
        fallRow = null
        val timeouts = mutableListOf<Int>()
        val landing = drop.landingRow
        for (r in 0..landing) {
            val delay = r * DROP_MS_PER_ROW
            timeouts.add(
                window.setTimeout({
                    fallRow = r
                }, delay),
            )
        }
        timeouts.add(
            window.setTimeout({
                onEnd()
            }, landing * DROP_MS_PER_ROW + DROP_MS_SETTLE),
        )
        onDispose {
            timeouts.forEach { window.clearTimeout(it) }
        }
    }

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
            attr("style", boardGridStyle(cfg.cols))
        }) {
            for (col in 0 until cfg.cols) {
                Button(attrs = {
                    classes("connect-four-column")
                    attr("type", "button")
                    attr("aria-label", "Column ${col + 1}, drop a piece")
                    onClick { onColumnClick(col) }
                }) {
                    for (row in 0 until cfg.rows) {
                        val placed = boardState.cell(row, col)
                        val falling =
                            pendingDrop?.takeIf { it.column == col && fallRow == row && placed == null }?.player
                        ConnectFourCell(piece = placed, fallingPiece = falling)
                    }
                }
            }
        }
    }
}

private fun boardGridStyle(cols: Int): String =
    "grid-template-columns:repeat($cols, minmax(0, 1fr));"

@Composable
private fun ConnectFourCell(piece: Player?, fallingPiece: Player?) {
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
            fallingPiece?.let { fp ->
                Div(attrs = {
                    classes("connect-four-piece", "connect-four-piece--falling-in-cell")
                    when (fp) {
                        Player.One -> classes("connect-four-piece--one")
                        Player.Two -> classes("connect-four-piece--two")
                    }
                }) { }
            }
        }
    }
}
