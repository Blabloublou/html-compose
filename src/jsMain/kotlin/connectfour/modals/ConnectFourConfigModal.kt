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
    modalRowsText: String,
    modalColsText: String,
    modalWinText: String,
    modalFeedback: String?,
    showCancel: Boolean,
    onRowsTextChange: (String) -> Unit,
    onColsTextChange: (String) -> Unit,
    onWinTextChange: (String) -> Unit,
    onConfirm: () -> Unit,
    onCancel: () -> Unit,
) {
    val rowsParsed = modalRowsText.trim().toIntOrNull()
    val colsParsed = modalColsText.trim().toIntOrNull()
    val winLo = GameConfig.MIN_WIN_LENGTH
    val boardSide = if (rowsParsed != null && colsParsed != null) {
        maxOf(rowsParsed, colsParsed)
    } else {
        null
    }
    val winHiForField = boardSide ?: GameConfig.MAX_DIMENSION
    val boardTooSmall = boardSide != null && boardSide < winLo
    val canStart = GameConfig.parseOrNull(modalRowsText, modalColsText, modalWinText) != null

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
                Text(
                    "Rows, columns, and how many in a row to win. " +
                        "Rows and columns: ${GameConfig.MIN_DIMENSION}–${GameConfig.MAX_DIMENSION}. " +
                        "Minimum line length: $winLo (cannot exceed the longer board side).",
                )
            }

            Div(attrs = { classes("connect-four-modal-fields") }) {
                ConnectFourConfigField(
                    label = "Rows",
                    valueText = modalRowsText,
                    min = GameConfig.MIN_DIMENSION,
                    max = GameConfig.MAX_DIMENSION,
                    onTextChange = onRowsTextChange,
                )

                ConnectFourConfigField(
                    label = "Columns",
                    valueText = modalColsText,
                    min = GameConfig.MIN_DIMENSION,
                    max = GameConfig.MAX_DIMENSION,
                    onTextChange = onColsTextChange,
                )

                if (boardTooSmall) {
                    P(attrs = { classes("connect-four-modal-hint") }) {
                        Text(
                            "Increase rows or columns: you need a side of at least $winLo to play " +
                                "(currently $boardSide).",
                        )
                    }
                } else {
                    ConnectFourConfigField(
                        label = "Line length to win",
                        valueText = modalWinText,
                        min = winLo,
                        max = winHiForField,
                        onTextChange = onWinTextChange,
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
    valueText: String,
    min: Int,
    max: Int,
    onTextChange: (String) -> Unit,
) {
    Label(attrs = { classes("connect-four-field") }) {
        Span { Text("$label ") }
        Input(InputType.Number, attrs = {
            value(valueText)
            attr("min", "$min")
            attr("max", "$max")
            attr("inputmode", "numeric")
            onInput { event ->
                onTextChange(event.value?.toString().orEmpty())
            }
        })
    }
}
