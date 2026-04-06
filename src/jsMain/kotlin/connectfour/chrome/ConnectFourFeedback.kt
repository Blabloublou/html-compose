package connectfour.chrome

import androidx.compose.runtime.Composable
import org.jetbrains.compose.web.dom.P
import org.jetbrains.compose.web.dom.Text

@Composable
internal fun ConnectFourFeedback(message: String) {
    P(attrs = { classes("connect-four-feedback") }) {
        Text(message)
    }
}
