package connectfour.board

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import connectfour.Player
import kotlin.js.asDynamic
import kotlinx.browser.window
import org.jetbrains.compose.web.attributes.ref
import org.jetbrains.compose.web.dom.Div
import org.w3c.dom.HTMLButtonElement
import org.w3c.dom.HTMLElement
import org.w3c.dom.events.Event

@Composable
internal fun ConnectFourDropAnimation(
    columnIndex: Int,
    columnRefs: Array<HTMLButtonElement?>,
    landingRow: Int,
    player: Player,
    onComplete: () -> Unit,
) {
    var topPx by remember { mutableStateOf<Double?>(null) }
    var pieceSizePx by remember { mutableStateOf(0.0) }
    var transitionActive by remember { mutableStateOf(false) }
    var pieceElement by remember { mutableStateOf<HTMLElement?>(null) }
    var completed by remember { mutableStateOf(false) }

    DisposableEffect(columnIndex, landingRow) {
        var phase2: Int? = null
        val phase1 = window.setTimeout({
            val btn = columnRefs[columnIndex] ?: return@setTimeout
            val cells = btn.querySelectorAll(".connect-four-cell")
            if (cells.length <= landingRow) return@setTimeout
            val first = cells.item(0) as? HTMLElement ?: return@setTimeout
            val land = cells.item(landingRow) as? HTMLElement ?: return@setTimeout
            val btnRect = btn.getBoundingClientRect()
            val fr = first.getBoundingClientRect()
            val lr = land.getBoundingClientRect()
            val ps = minOf(fr.width, fr.height) * 0.88
            val landCenterY = lr.top + lr.height / 2 - btnRect.top
            val firstCenterY = fr.top + fr.height / 2 - btnRect.top
            val startCenterY = firstCenterY - fr.height - ps * 0.35
            val startTop = startCenterY - ps / 2
            val endTop = landCenterY - ps / 2
            topPx = startTop
            pieceSizePx = ps
            phase2 = window.setTimeout({
                transitionActive = true
                topPx = endTop
            }, 20)
        }, 0)
        onDispose {
            window.clearTimeout(phase1)
            phase2?.let { window.clearTimeout(it) }
        }
    }

    DisposableEffect(pieceElement, transitionActive, completed) {
        val el = pieceElement
        var fallbackId: Int? = null
        val listener: (Event) -> Unit = listener@{ e ->
            val name = e.asDynamic().propertyName as? String
            if (name != "top") return@listener
            if (!completed) {
                completed = true
                onComplete()
            }
        }
        if (el != null && transitionActive && !completed) {
            el.addEventListener("transitionend", listener)
            fallbackId = window.setTimeout({
                if (!completed) {
                    completed = true
                    onComplete()
                }
            }, 500)
        }
        onDispose {
            if (el != null) {
                el.removeEventListener("transitionend", listener)
            }
            fallbackId?.let { window.clearTimeout(it) }
        }
    }

    Div(attrs = { classes("connect-four-drop-overlay") }) {
        val top = topPx
        if (top != null) {
            Div(attrs = {
                classes("connect-four-drop-piece")
                ref {
                    pieceElement = it as HTMLElement
                    onDispose { }
                }
                val topStr = "${top}px"
                val sizeStr = "${pieceSizePx}px"
                val transition =
                    if (transitionActive) "top 0.32s cubic-bezier(0.32, 0.72, 0, 1)" else "none"
                attr(
                    "style",
                    "top:$topStr;width:$sizeStr;height:$sizeStr;transition:$transition;",
                )
                when (player) {
                    Player.One -> classes("connect-four-piece", "connect-four-piece--one")
                    Player.Two -> classes("connect-four-piece", "connect-four-piece--two")
                }
            }) { }
        }
    }
}
