package connectfour.chrome

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import kotlinx.browser.window
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Text

private const val FEEDBACK_MS = 3_200

@Composable
internal fun ConnectFourFeedback(message: String, onDismiss: () -> Unit) {
    DisposableEffect(message) {
        val timeoutId = window.setTimeout({
            onDismiss()
        }, FEEDBACK_MS)
        onDispose {
            window.clearTimeout(timeoutId)
        }
    }

    Div(attrs = {
        classes("connect-four-feedback-toast")
        attr("role", "status")
        attr("aria-live", "polite")
    }) {
        Text(message)
    }
}
