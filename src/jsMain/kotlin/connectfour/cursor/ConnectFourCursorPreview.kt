package connectfour.cursor

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import connectfour.Player
import kotlinx.browser.window
import org.jetbrains.compose.web.dom.Div
import org.w3c.dom.events.Event

private const val CURSOR_PREVIEW_MIN_WIDTH_PX = 768

@Composable
internal fun rememberConnectFourCursorPreviewActive(whenPlaying: Boolean): Boolean {
    var wideEnough by remember {
        mutableStateOf(window.innerWidth >= CURSOR_PREVIEW_MIN_WIDTH_PX)
    }
    DisposableEffect(Unit) {
        val onResize = { _: Event ->
            wideEnough = window.innerWidth >= CURSOR_PREVIEW_MIN_WIDTH_PX
        }
        window.addEventListener("resize", onResize)
        onDispose {
            window.removeEventListener("resize", onResize)
        }
    }
    return wideEnough && whenPlaying
}

@Composable
internal fun ConnectFourCursorPreview(
    x: Double,
    y: Double,
    player: Player,
    visible: Boolean,
) {
    if (visible) {
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
}
