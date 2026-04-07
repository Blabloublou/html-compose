package connectfour.modals

import androidx.compose.runtime.Composable
import connectfour.GameConfig
import org.jetbrains.compose.web.attributes.InputType
import org.jetbrains.compose.web.dom.Button
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.H2
import org.jetbrains.compose.web.dom.Input
import org.jetbrains.compose.web.dom.Label
import org.jetbrains.compose.web.dom.P
import org.jetbrains.compose.web.dom.Span
import org.jetbrains.compose.web.dom.Text

@Composable
internal fun ConnectFourConfigModal(
    modalRows: Int,
    modalCols: Int,
    modalWin: Int,
    modalFeedback: String?,
    showCancel: Boolean,
    onRowsChange: (Int) -> Unit,
    onColsChange: (Int) -> Unit,
    onWinChange: (Int) -> Unit,
    onConfirm: () -> Unit,
    onCancel: () -> Unit,
) {
    val winHi = maxOf(modalRows, modalCols)
    val winLo = GameConfig.MIN_WIN_LENGTH
    val canStart = winHi >= winLo && modalWin in winLo..winHi

    Div(attrs = {
        classes("connect-four-modal-backdrop")
    }) {
        Div(attrs = {
            classes("connect-four-modal")
            attr("role", "dialog")
            attr("aria-modal", "true")
            attr("aria-labelledby", "connect-four-modal-title")
        }) {
            H2(attrs = {
                classes("connect-four-modal-title")
                attr("id", "connect-four-modal-title")
            }) {
                Text("Game settings")
            }

            P(attrs = { classes("connect-four-modal-hint") }) {
                Text("Rows, columns, and how many in a row to win.")
            }

            Div(attrs = { classes("connect-four-modal-fields") }) {
                ConnectFourConfigField(
                    label = "Rows",
                    currentValue = modalRows,
                    min = GameConfig.MIN_DIMENSION,
                    max = GameConfig.MAX_DIMENSION,
                    onValidChange = onRowsChange,
                )

                ConnectFourConfigField(
                    label = "Columns",
                    currentValue = modalCols,
                    min = GameConfig.MIN_DIMENSION,
                    max = GameConfig.MAX_DIMENSION,
                    onValidChange = onColsChange,
                )

                if (winLo > winHi) {
                    P(attrs = { classes("connect-four-modal-hint") }) {
                        Text(
                            "Increase rows or columns: you need a side of at least $winLo to play " +
                                "(currently $winHi).",
                        )
                    }
                } else {
                    ConnectFourConfigField(
                        label = "Line length to win",
                        currentValue = modalWin,
                        min = winLo,
                        max = winHi,
                        onValidChange = onWinChange,
                    )
                }
            }

            modalFeedback?.let { err ->
                P(attrs = { classes("connect-four-modal-error") }) {
                    Text(err)
                }
            }

            Div(attrs = {
                classes("connect-four-modal-actions")
                if (showCancel) {
                    classes("connect-four-modal-actions--split")
                }
            }) {
                if (showCancel) {
                    Button(attrs = {
                        classes("connect-four-modal-btn", "connect-four-modal-btn--secondary")
                        attr("type", "button")
                        onClick { onCancel() }
                    }) {
                        Text("Cancel")
                    }
                }
                Button(attrs = {
                    classes("connect-four-modal-btn", "connect-four-modal-btn--primary")
                    attr("type", "button")
                    if (!canStart) {
                        attr("disabled", "true")
                    }
                    onClick { if (canStart) onConfirm() }
                }) {
                    Text("Start")
                }
            }
        }
    }
}

@Composable
private fun ConnectFourConfigField(
    label: String,
    currentValue: Int,
    min: Int,
    max: Int,
    onValidChange: (Int) -> Unit,
) {
    Label(attrs = { classes("connect-four-field") }) {
        Span { Text("$label ") }
        Input(InputType.Number, attrs = {
            value(currentValue.toString())
            attr("min", "$min")
            attr("max", "$max")
            onInput { event ->
                val raw = event.value?.toString()?.trim().orEmpty()
                if (raw.isEmpty()) return@onInput
                val parsed = raw.toIntOrNull() ?: return@onInput
                if (parsed in min..max) {
                    onValidChange(parsed)
                }
            }
        })
    }
}
